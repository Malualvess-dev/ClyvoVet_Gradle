package br.com.fiap.clyvovet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PerfilVeterinarioForm {

    @NotBlank(message = "Informe seu CRMV.")
    @Pattern(regexp = "^CRMV-[A-Z]{2} \\d{3,6}$", message = "Use o formato CRMV-UF 12345, ex.: CRMV-SP 12345.")
    private String crmv;

    @NotBlank(message = "Informe sua especialidade.")
    @Size(max = 100, message = "A especialidade deve ter no máximo 100 caracteres.")
    private String especialidade;

    @NotBlank(message = "Informe seu telefone.")
    @Pattern(regexp = Telefone.FORMATO, message = Telefone.MENSAGEM)
    private String telefone;

    public String getCrmv() {
        return crmv;
    }

    public void setCrmv(String crmv) {
        this.crmv = crmv;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
