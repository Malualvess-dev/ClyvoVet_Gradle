package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.RelogioAjustavel;
import br.com.fiap.clyvovet.RelogioDeTesteConfig;
import br.com.fiap.clyvovet.dto.AtendimentoForm;
import br.com.fiap.clyvovet.dto.SolicitacaoConsultaForm;
import br.com.fiap.clyvovet.exception.RegraNegocioException;
import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.model.StatusConsulta;
import br.com.fiap.clyvovet.model.Veterinario;
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
    private Pet thor;
    private Veterinario draAna;

    @BeforeEach
    void prepararDadosDeDemonstracao() {
        relogio.ajustarPara(RelogioDeTesteConfig.SEGUNDA_AS_NOVE);
        tutora = idDoUsuario("carla@clyvovet.com");
        veterinaria = idDoUsuario("ana.vet@clyvovet.com");
        thor = petService.listarDoTutor(tutora).stream()
                .filter(pet -> pet.getNome().equals("Thor")).findFirst().orElseThrow();
        draAna = veterinarioService.listar().stream()
                .filter(veterinario -> veterinario.getUsuario().getId().equals(veterinaria)).findFirst().orElseThrow();
    }

    @Test
    void fluxoDeAgendamentoReservaHorarioENotificaOsEnvolvidos() {
        Consulta consulta = agendamentoService.solicitar(tutora, solicitacao(TERCA, DEZ_HORAS));

        assertThat(consulta.getStatus()).isEqualTo(StatusConsulta.SOLICITADA);
        assertThat(agendamentoService.horariosDisponiveis(draAna.getId(), TERCA)).doesNotContain(DEZ_HORAS);
        assertThat(notificacaoService.contarNaoLidas(veterinaria)).isEqualTo(1);

        agendamentoService.confirmar(veterinaria, consulta.getId());

        assertThat(consulta.getStatus()).isEqualTo(StatusConsulta.CONFIRMADA);
        assertThat(notificacaoService.contarNaoLidas(tutora)).isEqualTo(1);
        assertThat(consultaService.proximasDoTutor(tutora)).extracting(Consulta::getId).contains(consulta.getId());
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
        Consulta consulta = agendamentoService.solicitar(tutora, solicitacao(TERCA, DEZ_HORAS));
        agendamentoService.confirmar(veterinaria, consulta.getId());
        relogio.ajustarPara(LocalDateTime.of(2026, 10, 19, 11, 0));

        assertThatThrownBy(() -> agendamentoService.cancelar(tutora, consulta.getId(), "Imprevisto no trabalho"))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void veterinarioNaoAcessaConsultaDeOutroProfissional() {
        Consulta consulta = agendamentoService.solicitar(tutora, solicitacao(TERCA, DEZ_HORAS));
        Long outroVeterinario = idDoUsuario("bruno.vet@clyvovet.com");

        assertThatThrownBy(() -> agendamentoService.confirmar(outroVeterinario, consulta.getId()))
                .hasMessageContaining("não encontrada");
    }

    @Test
    void fluxoDeAtendimentoConcluiProntuarioERegistraVacina() {
        Consulta consulta = agendamentoService.solicitar(tutora, solicitacao(TERCA, DEZ_HORAS));
        agendamentoService.confirmar(veterinaria, consulta.getId());
        relogio.ajustarPara(LocalDateTime.of(TERCA, LocalTime.of(10, 40)));

        atendimentoService.registrar(veterinaria, consulta.getId(), atendimento("V8 (polivalente)", TERCA.plusDays(21)));

        assertThat(consulta.getStatus()).isEqualTo(StatusConsulta.CONCLUIDA);
        assertThat(thor.getPesoKg()).isEqualByComparingTo("32.10");
        assertThat(consultaService.historicoClinico(thor.getId())).extracting(Consulta::getId).contains(consulta.getId());
        assertThat(vacinaService.reforcosProximos(tutora)).extracting(vacina -> vacina.getNome()).contains("V8 (polivalente)");
    }

    @Test
    void atendimentoExigeConsultaConfirmada() {
        Consulta consulta = agendamentoService.solicitar(tutora, solicitacao(TERCA, DEZ_HORAS));
        relogio.ajustarPara(LocalDateTime.of(TERCA, LocalTime.of(10, 40)));

        assertThatThrownBy(() -> atendimentoService.registrar(veterinaria, consulta.getId(), atendimento(null, null)))
                .isInstanceOf(RegraNegocioException.class);
    }

    private SolicitacaoConsultaForm solicitacao(LocalDate data, LocalTime horario) {
        SolicitacaoConsultaForm form = new SolicitacaoConsultaForm();
        form.setPetId(thor.getId());
        form.setVeterinarioId(draAna.getId());
        form.setData(data);
        form.setHorario(horario);
        form.setMotivo("Vacinação e check-up");
        return form;
    }

    private AtendimentoForm atendimento(String vacina, LocalDate proximaDose) {
        AtendimentoForm form = new AtendimentoForm();
        form.setDiagnostico("Animal saudável.");
        form.setPesoAferidoKg(new BigDecimal("32.10"));
        form.setVacina(vacina);
        form.setProximaDose(proximaDose);
        return form;
    }

    private Long idDoUsuario(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow().getId();
    }
}
