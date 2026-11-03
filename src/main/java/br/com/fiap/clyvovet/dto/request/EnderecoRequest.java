package br.com.fiap.clyvovet.dto.request;

import br.com.fiap.clyvovet.dto.response.EnderecoResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class EnderecoRequest {

    @NotBlank(message = "Informe o CEP.")
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "Informe o CEP no formato 00000-000.")
    private String cep;

    @NotBlank(message = "Informe a rua ou avenida.")
    @Size(max = 120, message = "O logradouro deve ter no máximo 120 caracteres.")
    private String logradouro;

    @NotBlank(message = "Informe o número.")
    @Size(max = 10, message = "O número deve ter no máximo 10 caracteres.")
    private String numero;

    @Size(max = 50, message = "O complemento deve ter no máximo 50 caracteres.")
    private String complemento;

    @NotNull(message = "Selecione o bairro.")
    private Long bairroId;

    /** Preenche o formulário com o endereço já cadastrado. */
    public static EnderecoRequest de(EnderecoResponse endereco) {
        EnderecoRequest request = new EnderecoRequest();
        request.setCep(endereco.cep());
        request.setLogradouro(endereco.logradouro());
        request.setNumero(endereco.numero());
        request.setComplemento(endereco.complemento());
        request.setBairroId(endereco.bairroId());
        return request;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public Long getBairroId() {
        return bairroId;
    }

    public void setBairroId(Long bairroId) {
        this.bairroId = bairroId;
    }
}
