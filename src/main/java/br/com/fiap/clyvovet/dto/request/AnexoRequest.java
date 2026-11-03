package br.com.fiap.clyvovet.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public class AnexoRequest {

    @NotBlank(message = "Informe o nome do arquivo.")
    @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres.")
    private String nomeArquivo;

    @NotBlank(message = "Informe o tipo do arquivo.")
    @Size(max = 50, message = "O tipo deve ter no máximo 50 caracteres.")
    private String tipoArquivo;

    @NotBlank(message = "Informe o link do arquivo.")
    @URL(message = "Informe um endereço válido, começando com http:// ou https://.")
    @Size(max = 255, message = "O link deve ter no máximo 255 caracteres.")
    private String url;

    @PositiveOrZero(message = "O tamanho não pode ser negativo.")
    @Max(value = 1_000_000, message = "O tamanho deve ser de no máximo 1.000.000 KB.")
    private Integer tamanhoKb;

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getTipoArquivo() {
        return tipoArquivo;
    }

    public void setTipoArquivo(String tipoArquivo) {
        this.tipoArquivo = tipoArquivo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getTamanhoKb() {
        return tamanhoKb;
    }

    public void setTamanhoKb(Integer tamanhoKb) {
        this.tamanhoKb = tamanhoKb;
    }
}
