package br.com.fiap.clyvovet.controller.api;

import br.com.fiap.clyvovet.dto.api.ErroApiResponse;
import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.exception.RegraNegocioException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Erros da API em JSON ({status, mensagem, campos}), para o aplicativo exibir a mensagem.
 * Tem prioridade sobre o tratamento das telas web, que redireciona para páginas HTML.
 */
@RestControllerAdvice(basePackageClasses = TratamentoDeErrosDaApi.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TratamentoDeErrosDaApi {

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroApiResponse> regraDeNegocio(RegraNegocioException excecao) {
        return erro(HttpStatus.BAD_REQUEST, excecao.getMessage());
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroApiResponse> naoEncontrado(RecursoNaoEncontradoException excecao) {
        return erro(HttpStatus.NOT_FOUND, excecao.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroApiResponse> acessoNegado(AccessDeniedException excecao) {
        return erro(HttpStatus.FORBIDDEN, excecao.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroApiResponse> dadosInvalidos(MethodArgumentNotValidException excecao) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError erro : excecao.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(erro.getField(), erro.getDefaultMessage());
        }
        String mensagem = campos.isEmpty() ? "Confira os dados enviados." : String.join("\n", campos.values());
        return ResponseEntity.badRequest()
                .body(new ErroApiResponse(HttpStatus.BAD_REQUEST.value(), mensagem, campos));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    public ResponseEntity<ErroApiResponse> requisicaoMalFormada(Exception excecao) {
        return erro(HttpStatus.BAD_REQUEST, "Dados em formato inválido. Confira datas (aaaa-mm-dd) e números.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroApiResponse> conflito(DataIntegrityViolationException excecao) {
        return erro(HttpStatus.CONFLICT, "O banco de dados recusou a operação: há registros ligados a este item.");
    }

    private static ResponseEntity<ErroApiResponse> erro(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(ErroApiResponse.de(status.value(), mensagem));
    }
}
