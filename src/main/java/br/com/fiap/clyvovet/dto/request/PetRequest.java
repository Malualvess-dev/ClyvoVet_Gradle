package br.com.fiap.clyvovet.dto.request;

import br.com.fiap.clyvovet.model.Sexo;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PetRequest {

    @NotBlank(message = "Informe o nome do pet.")
    @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres.")
    private String nome;

    @NotNull(message = "Selecione a raça.")
    private Long racaId;

    @NotNull(message = "Selecione o sexo.")
    private Sexo sexo;

    @NotNull(message = "Informe a data de nascimento.")
    @PastOrPresent(message = "A data de nascimento não pode estar no futuro.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataNascimento;

    @NotNull(message = "Informe o peso.")
    @DecimalMin(value = "0.01", message = "O peso deve ser de pelo menos 0,01 kg.")
    @DecimalMax(value = "150.0", message = "O peso deve ser de no máximo 150 kg.")
    @Digits(integer = 3, fraction = 2, message = "Use no máximo duas casas decimais.")
    private BigDecimal pesoKg;

    @URL(message = "Informe um endereço válido, começando com http:// ou https://.")
    @Size(max = 255, message = "O link da foto deve ter no máximo 255 caracteres.")
    private String fotoUrl;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getRacaId() {
        return racaId;
    }

    public void setRacaId(Long racaId) {
        this.racaId = racaId;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public BigDecimal getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(BigDecimal pesoKg) {
        this.pesoKg = pesoKg;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }
}
