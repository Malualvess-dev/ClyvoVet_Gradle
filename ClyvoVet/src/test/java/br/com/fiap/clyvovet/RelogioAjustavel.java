package br.com.fiap.clyvovet;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class RelogioAjustavel extends Clock {

    private static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");

    private Instant instante;

    public RelogioAjustavel(LocalDateTime inicio) {
        ajustarPara(inicio);
    }

    public void ajustarPara(LocalDateTime dataHora) {
        this.instante = dataHora.atZone(FUSO).toInstant();
    }

    @Override
    public ZoneId getZone() {
        return FUSO;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return Clock.fixed(instante, zone);
    }

    @Override
    public Instant instant() {
        return instante;
    }
}
