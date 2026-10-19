package br.com.fiap.clyvovet.model;

import br.com.fiap.clyvovet.exception.RegraNegocioException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultaTest {

    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 10, 19, 9, 0);
    private static final LocalDateTime DATA_DA_CONSULTA = LocalDateTime.of(2026, 10, 21, 10, 0);

    private final Pet pet = new Pet(null, "Thor", Especie.CACHORRO, "Golden",
            LocalDate.of(2021, 3, 15), new BigDecimal("30.00"));

    private Consulta novaConsulta() {
        return Consulta.solicitar(pet, null, DATA_DA_CONSULTA, "Check-up anual", AGORA);
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
        Prontuario prontuario = new Prontuario("Saudável", null, new BigDecimal("31.00"));

        assertThatThrownBy(() -> consulta.concluir(prontuario, DATA_DA_CONSULTA.minusDays(1)))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void concluirAtendimentoAtualizaPesoDoPet() {
        Consulta consulta = novaConsulta();
        consulta.confirmar();

        consulta.concluir(new Prontuario("Saudável", "Vermífugo", new BigDecimal("31.40")), DATA_DA_CONSULTA);

        assertThat(consulta.getStatus()).isEqualTo(StatusConsulta.CONCLUIDA);
        assertThat(pet.getPesoKg()).isEqualByComparingTo("31.40");
    }

    @Test
    void consultaNaoConfirmadaNaoPodeSerConcluida() {
        Consulta consulta = novaConsulta();
        Prontuario prontuario = new Prontuario("Saudável", null, new BigDecimal("31.00"));

        assertThatThrownBy(() -> consulta.concluir(prontuario, DATA_DA_CONSULTA))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Aguardando confirmação");
    }
}
