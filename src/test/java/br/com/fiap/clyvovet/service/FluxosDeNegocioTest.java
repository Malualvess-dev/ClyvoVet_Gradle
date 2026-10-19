package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.RelogioAjustavel;
import br.com.fiap.clyvovet.RelogioDeTesteConfig;
import br.com.fiap.clyvovet.dto.request.AtendimentoRequest;
import br.com.fiap.clyvovet.dto.request.SolicitacaoConsultaRequest;
import br.com.fiap.clyvovet.dto.response.ConsultaResponse;
import br.com.fiap.clyvovet.dto.response.PetResponse;
import br.com.fiap.clyvovet.dto.response.VacinaResponse;
import br.com.fiap.clyvovet.dto.response.VeterinarioResponse;
import br.com.fiap.clyvovet.exception.RegraNegocioException;
import br.com.fiap.clyvovet.model.StatusConsulta;
import br.com.fiap.clyvovet.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    private VacinaService vacinaService;
    @Autowired
    private NotificacaoService notificacaoService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RelogioAjustavel relogio;

    private Long tutora;
    private Long veterinaria;
    private PetResponse thor;
    private VeterinarioResponse draAna;

    @BeforeEach
    void prepararDadosDeDemonstracao() {
        relogio.ajustarPara(RelogioDeTesteConfig.SEGUNDA_AS_NOVE);
        tutora = idDoUsuario("carla@clyvovet.com");
        veterinaria = idDoUsuario("ana.vet@clyvovet.com");
        thor = petService.listarDoTutor(tutora).stream()
                .filter(pet -> pet.nome().equals("Thor")).findFirst().orElseThrow();
        draAna = veterinarioService.listar().stream()
                .filter(veterinario -> veterinario.nome().equals("Dra. Ana Souza")).findFirst().orElseThrow();
    }

    @Test
    void fluxoDeAgendamentoReservaHorarioENotificaOsEnvolvidos() {
        ConsultaResponse consulta = agendamentoService.solicitar(tutora, solicitacao(TERCA, DEZ_HORAS));

        assertThat(consulta.status()).isEqualTo(StatusConsulta.SOLICITADA);
        assertThat(agendamentoService.horariosDisponiveis(draAna.id(), TERCA)).doesNotContain(DEZ_HORAS);
        assertThat(notificacaoService.contarNaoLidas(veterinaria)).isEqualTo(1);

        agendamentoService.confirmar(veterinaria, consulta.id());

        assertThat(statusAtual(consulta)).isEqualTo(StatusConsulta.CONFIRMADA);
        assertThat(notificacaoService.contarNaoLidas(tutora)).isEqualTo(1);
        assertThat(consultaService.proximasDoTutor(tutora)).extracting(ConsultaResponse::id).contains(consulta.id());
    }

    @Test
    void petNaoPodeTerDuasConsultasAtivasNoMesmoDia() {
        agendamentoService.solicitar(tutora, solicitacao(TERCA, DEZ_HORAS));

        assertThatThrownBy(() -> agendamentoService.solicitar(tutora, solicitacao(TERCA, LocalTime.of(15, 0))))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Thor");
    }

    @Test
    void cancelamentoDeConsultaConfirmadaRespeitaAntecedencia() {
        ConsultaResponse consulta = agendamentoService.solicitar(tutora, solicitacao(TERCA, DEZ_HORAS));
        agendamentoService.confirmar(veterinaria, consulta.id());
        relogio.ajustarPara(LocalDateTime.of(2026, 10, 19, 11, 0));

        assertThatThrownBy(() -> agendamentoService.cancelar(tutora, consulta.id(), "Imprevisto no trabalho"))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void veterinarioNaoAcessaConsultaDeOutroProfissional() {
        ConsultaResponse consulta = agendamentoService.solicitar(tutora, solicitacao(TERCA, DEZ_HORAS));
        Long outroVeterinario = idDoUsuario("bruno.vet@clyvovet.com");

        assertThatThrownBy(() -> agendamentoService.confirmar(outroVeterinario, consulta.id()))
                .hasMessageContaining("não encontrada");
    }

    @Test
    void fluxoDeAtendimentoConcluiProntuarioERegistraVacina() {
        ConsultaResponse consulta = agendamentoService.solicitar(tutora, solicitacao(TERCA, DEZ_HORAS));
        agendamentoService.confirmar(veterinaria, consulta.id());
        relogio.ajustarPara(LocalDateTime.of(TERCA, LocalTime.of(10, 40)));

        atendimentoService.registrar(veterinaria, consulta.id(), atendimento("V8 (polivalente)", TERCA.plusDays(21)));

        assertThat(statusAtual(consulta)).isEqualTo(StatusConsulta.CONCLUIDA);
        assertThat(petService.detalharDoTutor(tutora, thor.id()).pesoKg()).isEqualByComparingTo("32.10");
        assertThat(consultaService.historicoClinico(thor.id())).extracting(ConsultaResponse::id).contains(consulta.id());
        assertThat(vacinaService.reforcosProximos(tutora)).extracting(VacinaResponse::nome).contains("V8 (polivalente)");
    }

    @Test
    void atendimentoExigeConsultaConfirmada() {
        ConsultaResponse consulta = agendamentoService.solicitar(tutora, solicitacao(TERCA, DEZ_HORAS));
        relogio.ajustarPara(LocalDateTime.of(TERCA, LocalTime.of(10, 40)));

        assertThatThrownBy(() -> atendimentoService.registrar(veterinaria, consulta.id(), atendimento(null, null)))
                .isInstanceOf(RegraNegocioException.class);
    }

    private SolicitacaoConsultaRequest solicitacao(LocalDate data, LocalTime horario) {
        SolicitacaoConsultaRequest request = new SolicitacaoConsultaRequest();
        request.setPetId(thor.id());
        request.setVeterinarioId(draAna.id());
        request.setData(data);
        request.setHorario(horario);
        request.setMotivo("Vacinação e check-up");
        return request;
    }

    private AtendimentoRequest atendimento(String vacina, LocalDate proximaDose) {
        AtendimentoRequest request = new AtendimentoRequest();
        request.setDiagnostico("Animal saudável.");
        request.setPesoAferidoKg(new BigDecimal("32.10"));
        request.setVacina(vacina);
        request.setProximaDose(proximaDose);
        return request;
    }

    private StatusConsulta statusAtual(ConsultaResponse consulta) {
        return consultaService.detalharDoVeterinario(veterinaria, consulta.id()).status();
    }

    private Long idDoUsuario(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow().getId();
    }
}
