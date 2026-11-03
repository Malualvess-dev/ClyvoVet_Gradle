package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.RelogioAjustavel;
import br.com.fiap.clyvovet.RelogioDeTesteConfig;
import br.com.fiap.clyvovet.dto.request.AtendimentoRequest;
import br.com.fiap.clyvovet.dto.request.SolicitacaoConsultaRequest;
import br.com.fiap.clyvovet.dto.response.ConsultaResponse;
import br.com.fiap.clyvovet.dto.response.HistoricoSaudeResponse;
import br.com.fiap.clyvovet.dto.response.PetResponse;
import br.com.fiap.clyvovet.dto.response.VeterinarioResponse;
import br.com.fiap.clyvovet.exception.RegraNegocioException;
import br.com.fiap.clyvovet.model.Modalidade;
import br.com.fiap.clyvovet.model.StatusConsulta;
import br.com.fiap.clyvovet.model.TipoPrescricao;
import br.com.fiap.clyvovet.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Os dois fluxos completos sobre o banco do grupo, criado pelo Flyway (dados do scripts.sql). */
@SpringBootTest
@Import(RelogioDeTesteConfig.class)
@Transactional
class FluxosDeNegocioTest {

    private static final LocalDate TERCA = LocalDate.of(2026, 10, 20);
    private static final LocalTime DEZ_HORAS = LocalTime.of(10, 0);

    @Autowired
    private AgendamentoService agendamentoService;
    @Autowired
    private AtendimentoService atendimentoService;
    @Autowired
    private ConsultaService consultaService;
    @Autowired
    private PetService petService;
    @Autowired
    private VeterinarioService veterinarioService;
    @Autowired
    private HistoricoSaudeService historicoSaudeService;
    @Autowired
    private NotificacaoService notificacaoService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RelogioAjustavel relogio;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long tutor;
    private Long veterinario;
    private PetResponse thor;
    private VeterinarioResponse drRoberto;

    @BeforeEach
    void prepararDadosDoGrupo() {
        relogio.ajustarPara(RelogioDeTesteConfig.SEGUNDA_AS_NOVE);
        tutor = idDoUsuario("joao.tutor@clyvo.com");
        veterinario = idDoUsuario("vet.roberto@clyvo.com");
        thor = petService.listarDoTutor(tutor).stream()
                .filter(pet -> pet.nome().equals("Thor")).findFirst().orElseThrow();
        drRoberto = veterinarioService.listar().stream()
                .filter(vet -> vet.nome().equals("Dr. Roberto Santos")).findFirst().orElseThrow();
    }

    @Test
    void fluxoDeAgendamentoReservaHorarioENotificaOsEnvolvidos() {
        long avisosDoVeterinario = notificacaoService.contarNaoLidas(veterinario);
        long avisosDoTutor = notificacaoService.contarNaoLidas(tutor);

        ConsultaResponse consulta = agendamentoService.solicitar(tutor, solicitacao(TERCA, DEZ_HORAS));

        assertThat(consulta.status()).isEqualTo(StatusConsulta.SOLICITADA);
        assertThat(consulta.modalidade()).isEqualTo(Modalidade.ONLINE);
        assertThat(agendamentoService.horariosDisponiveis(drRoberto.id(), TERCA)).doesNotContain(DEZ_HORAS);
        assertThat(notificacaoService.contarNaoLidas(veterinario)).isEqualTo(avisosDoVeterinario + 1);

        agendamentoService.confirmar(veterinario, consulta.id());

        assertThat(statusAtual(consulta)).isEqualTo(StatusConsulta.CONFIRMADA);
        assertThat(notificacaoService.contarNaoLidas(tutor)).isEqualTo(avisosDoTutor + 1);
        assertThat(consultaService.proximasDoTutor(tutor)).extracting(ConsultaResponse::id).contains(consulta.id());
    }

    @Test
    void consultaConfirmadaFicaComoAgendadaNoBancoDoGrupo() {
        ConsultaResponse consulta = agendamentoService.solicitar(tutor, solicitacao(TERCA, DEZ_HORAS));
        agendamentoService.confirmar(veterinario, consulta.id());
        entityManager.flush();

        String statusNoBanco = jdbcTemplate.queryForObject(
                "SELECT STATUS FROM TB_CONSULTA WHERE ID_CONSULTA = ?", String.class, consulta.id());

        assertThat(statusNoBanco).isEqualTo("AGENDADA");
    }

    @Test
    void petNaoPodeTerDuasConsultasAtivasNoMesmoDia() {
        agendamentoService.solicitar(tutor, solicitacao(TERCA, DEZ_HORAS));

        assertThatThrownBy(() -> agendamentoService.solicitar(tutor, solicitacao(TERCA, LocalTime.of(15, 0))))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Thor");
    }

    @Test
    void cancelamentoDeConsultaConfirmadaRespeitaAntecedencia() {
        ConsultaResponse consulta = agendamentoService.solicitar(tutor, solicitacao(TERCA, DEZ_HORAS));
        agendamentoService.confirmar(veterinario, consulta.id());
        relogio.ajustarPara(LocalDateTime.of(2026, 10, 19, 11, 0));

        assertThatThrownBy(() -> agendamentoService.cancelar(tutor, consulta.id(), "Imprevisto no trabalho"))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void veterinarioNaoAcessaConsultaDeOutroProfissional() {
        ConsultaResponse consulta = agendamentoService.solicitar(tutor, solicitacao(TERCA, DEZ_HORAS));
        Long outroVeterinario = idDoUsuario("vet.camila@clyvo.com");

        assertThatThrownBy(() -> agendamentoService.confirmar(outroVeterinario, consulta.id()))
                .hasMessageContaining("não encontrada");
    }

    @Test
    void fluxoDeAtendimentoGeraProntuarioPrescricaoEVacina() {
        ConsultaResponse consulta = agendamentoService.solicitar(tutor, solicitacao(TERCA, DEZ_HORAS));
        agendamentoService.confirmar(veterinario, consulta.id());
        relogio.ajustarPara(LocalDateTime.of(TERCA, LocalTime.of(10, 40)));

        atendimentoService.registrar(veterinario, consulta.id(), atendimento("V8 (polivalente)", TERCA.plusDays(21)));
        entityManager.flush();

        assertThat(statusAtual(consulta)).isEqualTo(StatusConsulta.CONCLUIDA);
        assertThat(petService.detalharDoTutor(tutor, thor.id()).pesoKg()).isEqualByComparingTo("32.10");
        assertThat(consultaService.historicoClinico(thor.id()))
                .filteredOn(atendida -> atendida.id().equals(consulta.id()))
                .singleElement()
                .satisfies(atendida -> {
                    assertThat(atendida.prontuario().diagnostico()).isEqualTo("Animal saudável.");
                    assertThat(atendida.prontuario().prescricoes()).hasSize(1);
                });
        assertThat(historicoSaudeService.reforcosProximos(tutor))
                .extracting(HistoricoSaudeResponse::evento)
                .contains("V8 (polivalente)");
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM TB_PRONTUARIO WHERE ID_CONSULTA = ?", Integer.class, consulta.id()))
                .isEqualTo(1);
    }

    @Test
    void atendimentoExigeConsultaConfirmada() {
        ConsultaResponse consulta = agendamentoService.solicitar(tutor, solicitacao(TERCA, DEZ_HORAS));
        relogio.ajustarPara(LocalDateTime.of(TERCA, LocalTime.of(10, 40)));

        assertThatThrownBy(() -> atendimentoService.registrar(veterinario, consulta.id(), atendimento(null, null)))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void historicoDoGrupoApareceParaOTutor() {
        assertThat(consultaService.historicoClinico(thor.id())).isNotEmpty();
        assertThat(historicoSaudeService.doPet(thor.id()))
                .extracting(HistoricoSaudeResponse::evento)
                .contains("Vacina Múltipla V10", "Vacina Antirrábica");
    }

    private SolicitacaoConsultaRequest solicitacao(LocalDate data, LocalTime horario) {
        SolicitacaoConsultaRequest request = new SolicitacaoConsultaRequest();
        request.setPetId(thor.id());
        request.setVeterinarioId(drRoberto.id());
        request.setData(data);
        request.setHorario(horario);
        request.setModalidade(Modalidade.ONLINE);
        request.setMotivo("Vacinação e check-up");
        return request;
    }

    private AtendimentoRequest atendimento(String vacina, LocalDate proximaDose) {
        AtendimentoRequest request = new AtendimentoRequest();
        request.setDiagnostico("Animal saudável.");
        request.setPesoAferidoKg(new BigDecimal("32.10"));
        request.setTemperatura(new BigDecimal("38.6"));
        request.setTipoPrescricao(TipoPrescricao.MEDICAMENTO);
        request.setPrescricao("Vermífugo amplo espectro");
        request.setPosologia("Dose única");
        request.setVacina(vacina);
        request.setProximaDose(proximaDose);
        return request;
    }

    private StatusConsulta statusAtual(ConsultaResponse consulta) {
        return consultaService.detalharDoVeterinario(veterinario, consulta.id()).status();
    }

    private Long idDoUsuario(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow().getId();
    }
}
