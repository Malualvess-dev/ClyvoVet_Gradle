package br.com.fiap.clyvovet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Locale;

public class PerfilVeterinarioRequest {

    /** Mesmo formato exigido pela constraint CK_VETERINARIO_CRMV do banco. */
    @NotBlank(message = "Informe seu CRMV.")
    @Pattern(regexp = "^CRMV-[A-Z]{2}-\\d{5}$", message = "Use o formato CRMV-UF-12345, ex.: CRMV-SP-12345.")
    private String crmv;

    @NotBlank(message = "Informe sua especialidade.")
    @Size(max = 100, message = "A especialidade deve ter no máximo 100 caracteres.")
    private String especialidade;

    @NotBlank(message = "Informe seu telefone.")
    @Pattern(regexp = Telefone.FORMATO, message = Telefone.MENSAGEM)
    private String telefone;

    @NotNull(message = "Informe sua data de nascimento.")
    @Past(message = "A data de nascimento deve estar no passado.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataNascimento;

    public String getCrmv() {
        return crmv;
    }

    public void setCrmv(String crmv) {
        this.crmv = crmv == null ? null : crmv.strip().toUpperCase(Locale.ROOT);
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

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
}
