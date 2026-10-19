package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.RelogioAjustavel;
import br.com.fiap.clyvovet.exception.RegraNegocioException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PoliticaDeAgendamentoTest {

    private static final LocalDateTime SEGUNDA_AS_NOVE = LocalDateTime.of(2026, 10, 19, 9, 0);

    private final PoliticaDeAgendamento politica = new PoliticaDeAgendamento(new RelogioAjustavel(SEGUNDA_AS_NOVE));

    @Test
    void diaUtilFuturoPossuiVinteHorarios() {
        assertThat(politica.horariosDeAtendimento(LocalDate.of(2026, 10, 20)))
                .hasSize(20)
                .startsWith(LocalTime.of(8, 0))
                .endsWith(LocalTime.of(17, 30));
    }

    @Test
    void horariosDeHojeRespeitamAntecedenciaMinima() {
        assertThat(politica.horariosDeAtendimento(SEGUNDA_AS_NOVE.toLocalDate()))
                .first()
                .isEqualTo(LocalTime.of(10, 30));
    }

    @Test
    void domingoNaoPossuiAtendimento() {
        LocalDateTime domingo = LocalDateTime.of(2026, 10, 25, 10, 0);

        assertThat(politica.horariosDeAtendimento(domingo.toLocalDate())).isEmpty();
        assertThatThrownBy(() -> politica.validar(domingo))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("domingos");
    }

    @Test
    void horarioForaDoIntervaloDeTrintaMinutosEhRecusado() {
        assertThatThrownBy(() -> politica.validar(LocalDateTime.of(2026, 10, 20, 10, 15)))
                .isInstanceOf(RegraNegocioException.class);
    }
}
