package br.com.fiap.clyvovet.model;

import java.math.BigDecimal;

public record Prontuario(String diagnostico, String prescricao, BigDecimal pesoAferidoKg) {
}
