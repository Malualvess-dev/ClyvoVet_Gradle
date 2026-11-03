package br.com.fiap.clyvovet.mapper;

import br.com.fiap.clyvovet.dto.response.AnexoResponse;
import br.com.fiap.clyvovet.dto.response.BairroResponse;
import br.com.fiap.clyvovet.dto.response.EnderecoResponse;
import br.com.fiap.clyvovet.dto.response.LembreteResponse;
import br.com.fiap.clyvovet.model.Anexo;
import br.com.fiap.clyvovet.model.Bairro;
import br.com.fiap.clyvovet.model.Endereco;
import br.com.fiap.clyvovet.model.Lembrete;
import org.springframework.stereotype.Component;

/** Conversões da área "Minha conta": lembretes, documentos e endereço. */
@Component
public class ContaMapper {

    public LembreteResponse paraResponse(Lembrete lembrete) {
        return new LembreteResponse(lembrete.getId(), lembrete.getTitulo(), lembrete.getDataHora(),
                lembrete.getAnotacao());
    }

    public AnexoResponse paraResponse(Anexo anexo) {
        return new AnexoResponse(anexo.getId(), anexo.getNomeArquivo(), anexo.getTipoArquivo(), anexo.getUrl(),
                anexo.getTamanhoKb(), anexo.getDataCriacao());
    }

    public EnderecoResponse paraResponse(Endereco endereco) {
        Bairro bairro = endereco.getBairro();
        return new EnderecoResponse(endereco.getLogradouro(), endereco.getNumero(), endereco.getComplemento(),
                endereco.getCep(), bairro.getId(), bairro.getDescricaoCompleta());
    }

    public BairroResponse paraResponse(Bairro bairro) {
        return new BairroResponse(bairro.getId(), bairro.getDescricaoCompleta());
    }
}
