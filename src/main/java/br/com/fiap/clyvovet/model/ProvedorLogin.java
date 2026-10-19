package br.com.fiap.clyvovet.model;

import java.util.Locale;

public enum ProvedorLogin {
    LOCAL,
    GOOGLE,
    GITHUB;

    public static ProvedorLogin doRegistro(String registrationId) {
        return valueOf(registrationId.toUpperCase(Locale.ROOT));
    }
}
