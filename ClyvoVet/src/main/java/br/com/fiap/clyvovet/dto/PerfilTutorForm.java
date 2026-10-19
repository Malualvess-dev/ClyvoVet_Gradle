package br.com.fiap.clyvovet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CPF;

public class PerfilTutorForm {

    @NotBlank(message = "Informe seu CPF.")
    @CPF(message = "Informe um CPF válido.")
    private String cpf;

    @NotBlank(message = "Informe seu telefone.")
    @Pattern(regexp = Telefone.FORMATO, message = Telefone.MENSAGEM)
    private String telefone;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
