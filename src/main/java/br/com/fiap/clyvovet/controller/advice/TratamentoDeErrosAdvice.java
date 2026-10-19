package br.com.fiap.clyvovet.controller.advice;

import br.com.fiap.clyvovet.controller.Mensagens;
import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.exception.RegraNegocioException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.net.URI;
import java.util.Optional;

@ControllerAdvice
public class TratamentoDeErrosAdvice {

    private static final String PAGINA_PADRAO = "/painel";

    @ExceptionHandler(RegraNegocioException.class)
    public String regraDeNegocioVioladaEm(RegraNegocioException excecao, HttpServletRequest requisicao) {
        RequestContextUtils.getOutputFlashMap(requisicao).put(Mensagens.ERRO, excecao.getMessage());
        return "redirect:" + paginaDeOrigem(requisicao);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String recursoNaoEncontrado(RecursoNaoEncontradoException excecao, Model model) {
        model.addAttribute("mensagem", excecao.getMessage());
        return "error/404";
    }

    private static String paginaDeOrigem(HttpServletRequest requisicao) {
        return Optional.ofNullable(requisicao.getHeader(HttpHeaders.REFERER))
                .map(URI::create)
                .filter(origem -> requisicao.getServerName().equals(origem.getHost()))
                .map(TratamentoDeErrosAdvice::caminhoComParametros)
                .orElse(PAGINA_PADRAO);
    }

    private static String caminhoComParametros(URI origem) {
        String caminho = origem.getRawPath();
        return origem.getRawQuery() == null ? caminho : caminho + "?" + origem.getRawQuery();
    }
}
