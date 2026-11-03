package br.com.fiap.clyvovet.service.banco;

import br.com.fiap.clyvovet.dto.response.AuditoriaColeiraResponse;
import br.com.fiap.clyvovet.dto.response.LogErroResponse;
import br.com.fiap.clyvovet.dto.response.ResultadoProcedimento;

import java.time.LocalDate;
import java.util.List;

/**
 * Objetos PL/SQL criados pelo grupo de Banco de Dados (packages, função e trigger).
 * No Oracle eles são executados de verdade; no H2 de desenvolvimento não existem.
 */
public interface ProcedimentosDoBanco {

    boolean disponivel();

    /** PKG_PETS_JSON_API.PR_EXPORTAR_PETS_JSON: todos os pets, com tutor, raça e espécie, em JSON. */
    ResultadoProcedimento exportarPetsEmJson(String usuario);

    /** PKG_RELATORIOS_API.PR_RELATORIO_COLEIRA: soma dos batimentos por pet e alerta, com subtotais. */
    ResultadoProcedimento relatorioDaColeira(String usuario);

    /** PKG_RELATORIOS.FN_CALCULAR_IDADE_PET: idade em anos completos. */
    int idadeDoPet(LocalDate dataNascimento);

    /** Registros gravados pelo trigger TRG_AUDITORIA_COLEIRA. */
    List<AuditoriaColeiraResponse> ultimasAuditoriasDaColeira();

    /** Erros das procedures registrados em TB_LOG_ERRO. */
    List<LogErroResponse> ultimosErros();
}
