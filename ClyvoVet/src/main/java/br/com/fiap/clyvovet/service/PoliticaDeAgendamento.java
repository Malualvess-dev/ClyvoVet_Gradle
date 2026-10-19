package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.exception.RegraNegocioException;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

@Component
public class PoliticaDeAgendamento {

    static final LocalTime PRIMEIRO_HORARIO = LocalTime.of(8, 0);
    static final LocalTime ULTIMO_HORARIO = LocalTime.of(17, 30);
    static final Duration DURACAO_DA_CONSULTA = Duration.ofMinutes(30);
    static final Duration ANTECEDENCIA_MINIMA = Duration.ofHours(1);

    private final Clock relogio;

    public PoliticaDeAgendamento(Clock relogio) {
        this.relogio = relogio;
    }

    public List<LocalTime> horariosDeAtendimento(LocalDate data) {
        if (data.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return List.of();
        }
        LocalDateTime limite = LocalDateTime.now(relogio).plus(ANTECEDENCIA_MINIMA);
        return Stream.iterate(PRIMEIRO_HORARIO, horario -> !horario.isAfter(ULTIMO_HORARIO),
                        horario -> horario.plus(DURACAO_DA_CONSULTA))
                .filter(horario -> LocalDateTime.of(data, horario).isAfter(limite))
                .toList();
    }

    public void validar(LocalDateTime dataHora) {
        if (dataHora.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new RegraNegocioException("A clínica não realiza atendimentos aos domingos.");
        }
        if (!horariosDeAtendimento(dataHora.toLocalDate()).contains(dataHora.toLocalTime())) {
            throw new RegraNegocioException(
                    "Escolha um horário entre 08:00 e 17:30, em intervalos de 30 minutos "
                            + "e com pelo menos 1 hora de antecedência.");
        }
    }
}
