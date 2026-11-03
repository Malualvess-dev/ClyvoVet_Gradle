package br.com.fiap.clyvovet.service.banco;

import br.com.fiap.clyvovet.dto.response.AuditoriaColeiraResponse;
import br.com.fiap.clyvovet.dto.response.LogErroResponse;
import br.com.fiap.clyvovet.dto.response.ResultadoProcedimento;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

/** Usado com o H2 (perfil dev e testes), onde os packages PL/SQL do Oracle não existem. */
@Component
@Profile("!oracle")
public class ProcedimentosIndisponiveis implements ProcedimentosDoBanco {

    static final String MENSAGEM = "Disponível apenas com o banco Oracle (perfil oracle).";

    private final Clock relogio;

    public ProcedimentosIndisponiveis(Clock relogio) {
        this.relogio = relogio;
    }

    @Override
    public boolean disponivel() {
        return false;
    }

    @Override
    public ResultadoProcedimento exportarPetsEmJson(String usuario) {
        return ResultadoProcedimento.falha(ProcedimentosOracle.EXPORTAR_PETS_JSON, MENSAGEM);
    }

    @Override
    public ResultadoProcedimento relatorioDaColeira(String usuario) {
        return ResultadoProcedimento.falha(ProcedimentosOracle.RELATORIO_COLEIRA, MENSAGEM);
    }

    @Override
    public int idadeDoPet(LocalDate dataNascimento) {
        return Period.between(dataNascimento, LocalDate.now(relogio)).getYears();
    }

    @Override
    public List<AuditoriaColeiraResponse> ultimasAuditoriasDaColeira() {
        return List.of();
    }

    @Override
    public List<LogErroResponse> ultimosErros() {
        return List.of();
    }
}
