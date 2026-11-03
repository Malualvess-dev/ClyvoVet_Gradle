package br.com.fiap.clyvovet.model;

final class Texto {

    private Texto() {
    }

    /** Campos opcionais dos formulários: texto em branco vira nulo no banco. */
    static String opcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.strip();
    }
}
