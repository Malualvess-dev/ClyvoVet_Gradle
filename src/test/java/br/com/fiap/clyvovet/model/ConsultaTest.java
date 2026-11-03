package br.com.fiap.clyvovet.model;

import br.com.fiap.clyvovet.exception.RegraNegocioException;
import br.com.fiap.clyvovet.model.DadosDoAtendimento.DadosDaPrescricao;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultaTest {

    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 10, 19, 9, 0);
    private static final LocalDateTime DATA_DA_CONSULTA = LocalDateTime.of(2026, 10, 21, 10, 0);

    private final Raca labrador = new Raca(1L, "Labrador Retriever", Porte.GRANDE, new Especie(1L, "Cão"));
    private final Pet pet = new Pet(null, "Thor", labrador, Sexo.M, LocalDate.of(2021, 5, 10),
            new BigDecimal("30.00"), null);

    private Consulta novaConsulta() {
        return Consulta.solicitar(pet, null, DATA_DA_CONSULTA, Modalidade.PRESENCIAL, "Check-up anual", AGORA);
    }

    private static DadosDoAtendimento atendimento(String peso, LocalDate retorno, DadosDaPrescricao prescricao) {
        return new DadosDoAtendimento("Saudável", null, null, new BigDecimal(peso), new BigDecimal("38.5"),
                null, retorno, prescricao);
    }

    @Test
    void consultaSolicitadaPodeSerConfirmada() {
        Consulta consulta = novaConsulta();

        consulta.confirmar();

        assertThat(consulta.getStatus()).isEqualTo(StatusConsulta.CONFIRMADA);
    }

    @Test
    void recusaExigeJustificativa() {
        Consulta consulta = novaConsulta();

        assertThatThrownBy(() -> consulta.recusar("  "))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("justificativa");
        assertThat(consulta.getStatus()).isEqualTo(StatusConsulta.SOLICITADA);
    }

    @Test
    void consultaConfirmadaNaoPodeSerCanceladaComMenosDe24Horas() {
        Consulta consulta = novaConsulta();
        consulta.confirmar();

        assertThatThrownBy(() -> consulta.cancelar("Imprevisto no trabalho", DATA_DA_CONSULTA.minusHours(23)))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("24 horas");
    }

    @Test
    void consultaConfirmadaPodeSerCanceladaComAntecedencia() {
        Consulta consulta = novaConsulta();
        consulta.confirmar();

        consulta.cancelar("Imprevisto no trabalho", DATA_DA_CONSULTA.minusHours(25));

        assertThat(consulta.getStatus()).isEqualTo(StatusConsulta.CANCELADA);
        assertThat(consulta.getJustificativa()).isEqualTo("Imprevisto no trabalho");
    }

    @Test
    void atendimentoNaoPodeSerRegistradoAntesDoDiaDaConsulta() {
        Consulta consulta = novaConsulta();
        consulta.confirmar();

        assertThatThrownBy(() -> consulta.concluir(atendimento("31.00", null, null), DATA_DA_CONSULTA.minusDays(1)))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void concluirAtendimentoGeraProntuarioEAtualizaPesoDoPet() {
        Consulta consulta = novaConsulta();
        consulta.confirmar();
        DadosDaPrescricao vermifugo = new DadosDaPrescricao(TipoPrescricao.MEDICAMENTO, "Vermífugo", "Dose única");

        consulta.concluir(atendimento("31.40", LocalDate.of(2027, 10, 21), vermifugo), DATA_DA_CONSULTA);

        assertThat(consulta.getStatus()).isEqualTo(StatusConsulta.CONCLUIDA);
        assertThat(pet.getPesoKg()).isEqualByComparingTo("31.40");
        assertThat(consulta.getProntuario().getTemperatura()).isEqualByComparingTo("38.5");
        assertThat(consulta.getProntuario().getPrescricoes())
                .singleElement()
                .satisfies(prescricao -> assertThat(prescricao.getTipo()).isEqualTo(TipoPrescricao.MEDICAMENTO));
    }

    @Test
    void dataDeRetornoPrecisaSerDepoisDaConsulta() {
        Consulta consulta = novaConsulta();
        consulta.confirmar();
        DadosDoAtendimento retornoNoMesmoDia = atendimento("31.00", DATA_DA_CONSULTA.toLocalDate(), null);

        assertThatThrownBy(() -> consulta.concluir(retornoNoMesmoDia, DATA_DA_CONSULTA))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("retorno");
        assertThat(consulta.getStatus()).isEqualTo(StatusConsulta.CONFIRMADA);
    }

    @Test
    void consultaNaoConfirmadaNaoPodeSerConcluida() {
        Consulta consulta = novaConsulta();

        assertThatThrownBy(() -> consulta.concluir(atendimento("31.00", null, null), DATA_DA_CONSULTA))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Aguardando confirmação");
    }
}
