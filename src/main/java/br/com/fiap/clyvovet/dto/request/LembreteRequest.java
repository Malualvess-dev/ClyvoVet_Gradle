package br.com.fiap.clyvovet.dto.request;

import br.com.fiap.clyvovet.dto.response.LembreteResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class LembreteRequest {

    @NotBlank(message = "Informe o título.")
    @Size(max = 85, message = "O título deve ter no máximo 85 caracteres.")
    private String titulo;

    @NotNull(message = "Informe a data e o horário.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dataHora;

    @Size(max = 2500, message = "A anotação deve ter no máximo 2500 caracteres.")
    private String anotacao;

    /** Preenche o formulário de edição com os dados atuais do lembrete. */
    public static LembreteRequest de(LembreteResponse lembrete) {
        LembreteRequest request = new LembreteRequest();
        request.setTitulo(lembrete.titulo());
        request.setDataHora(lembrete.dataHora());
        request.setAnotacao(lembrete.anotacao());
        return request;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getAnotacao() {
        return anotacao;
    }

    public void setAnotacao(String anotacao) {
        this.anotacao = anotacao;
    }
}
