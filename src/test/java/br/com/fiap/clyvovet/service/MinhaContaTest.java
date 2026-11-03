package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.RelogioDeTesteConfig;
import br.com.fiap.clyvovet.dto.request.AnexoRequest;
import br.com.fiap.clyvovet.dto.request.EnderecoRequest;
import br.com.fiap.clyvovet.dto.request.LeituraColeiraRequest;
import br.com.fiap.clyvovet.dto.request.LembreteRequest;
import br.com.fiap.clyvovet.dto.response.AnexoResponse;
import br.com.fiap.clyvovet.dto.response.EnderecoResponse;
import br.com.fiap.clyvovet.dto.response.LeituraColeiraResponse;
import br.com.fiap.clyvovet.dto.response.LembreteResponse;
import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** CRUD das tabelas de apoio do modelo do grupo: agenda, anexos, endereço e coleira. */
@SpringBootTest
@Import(RelogioDeTesteConfig.class)
@Transactional
class MinhaContaTest {

    @Autowired
    private LembreteService lembreteService;
    @Autowired
    private AnexoService anexoService;
    @Autowired
    private EnderecoService enderecoService;
    @Autowired
    private ColeiraService coleiraService;
    @Autowired
    private NotificacaoService notificacaoService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    private Long joao;
    private Long lucas;

    @BeforeEach
    void identificarUsuarios() {
        joao = usuarioRepository.findByEmail("joao.tutor@clyvo.com").orElseThrow().getId();
        lucas = usuarioRepository.findByEmail("lucas.tutor@clyvo.com").orElseThrow().getId();
    }

    @Test
    void lembreteCompletaOCicloDeCadastroAlteracaoEExclusao() {
        LembreteResponse criado = lembreteService.criar(joao,
                lembrete("Vermífugo do Thor", LocalDateTime.of(2026, 11, 10, 9, 0)));

        assertThat(lembreteService.listar(joao)).extracting(LembreteResponse::titulo).contains("Vermífugo do Thor");

        lembreteService.atualizar(joao, criado.id(), lembrete("Vermífugo e banho", LocalDateTime.of(2026, 11, 11, 9, 0)));
        assertThat(lembreteService.detalhar(joao, criado.id()).titulo()).isEqualTo("Vermífugo e banho");

        lembreteService.excluir(joao, criado.id());
        assertThat(lembreteService.listar(joao)).extracting(LembreteResponse::id).doesNotContain(criado.id());
    }

    @Test
    void usuarioNaoAlteraLembreteDeOutraPessoa() {
        LembreteResponse doJoao = lembreteService.criar(joao, lembrete("Retorno", LocalDateTime.of(2026, 11, 10, 9, 0)));

        assertThatThrownBy(() -> lembreteService.excluir(lucas, doJoao.id()))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void documentoPodeSerAdicionadoERemovido() {
        AnexoRequest request = new AnexoRequest();
        request.setNomeArquivo("hemograma_thor.pdf");
        request.setTipoArquivo("application/pdf");
        request.setUrl("https://clyvo.com/docs/hemograma_thor.pdf");
        request.setTamanhoKb(850);

        AnexoResponse anexo = anexoService.adicionar(joao, request);
        assertThat(anexoService.listar(joao)).extracting(AnexoResponse::nomeArquivo).contains("hemograma_thor.pdf");

        anexoService.excluir(joao, anexo.id());
        assertThat(anexoService.listar(joao)).extracting(AnexoResponse::id).doesNotContain(anexo.id());
    }

    @Test
    void enderecoDoGrupoEhAtualizadoSemCriarOutro() {
        EnderecoResponse atual = enderecoService.buscar(joao).orElseThrow();
        EnderecoRequest request = EnderecoRequest.de(atual);
        request.setNumero("200");
        request.setCep("05422000");

        EnderecoResponse salvo = enderecoService.salvar(joao, request);

        assertThat(salvo.numero()).isEqualTo("200");
        assertThat(salvo.cep()).isEqualTo("05422-000");
        assertThat(enderecoService.buscar(joao)).get().extracting(EnderecoResponse::numero).isEqualTo("200");
    }

    @Test
    void leituraComBatimentoAltoGeraAlertaENotificaOTutor() {
        long avisosAntes = notificacaoService.contarNaoLidas(joao);
        LeituraColeiraRequest request = new LeituraColeiraRequest();
        request.setPetId(1L);
        request.setBatimentoBpm(new BigDecimal("150.0"));

        LeituraColeiraResponse leitura = coleiraService.registrarLeitura(request);

        assertThat(leitura.alerta()).isTrue();
        assertThat(leitura.petNome()).isEqualTo("Thor");
        assertThat(notificacaoService.contarNaoLidas(joao)).isEqualTo(avisosAntes + 1);
        assertThat(coleiraService.ultimasLeituras(1L)).extracting(LeituraColeiraResponse::id).contains(leitura.id());
    }

    private static LembreteRequest lembrete(String titulo, LocalDateTime dataHora) {
        LembreteRequest request = new LembreteRequest();
        request.setTitulo(titulo);
        request.setDataHora(dataHora);
        request.setAnotacao("Criado pelo teste.");
        return request;
    }
}
