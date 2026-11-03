package br.com.fiap.clyvovet.service.banco;

import br.com.fiap.clyvovet.dto.response.AuditoriaColeiraResponse;
import br.com.fiap.clyvovet.dto.response.LogErroResponse;
import br.com.fiap.clyvovet.dto.response.ResultadoProcedimento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Component
@Profile("oracle")
public class ProcedimentosOracle implements ProcedimentosDoBanco {

    static final String EXPORTAR_PETS_JSON = "PKG_PETS_JSON_API.PR_EXPORTAR_PETS_JSON";
    static final String RELATORIO_COLEIRA = "PKG_RELATORIOS_API.PR_RELATORIO_COLEIRA";
    private static final int TAMANHO_MAXIMO_MENSAGEM = 4000;

    private static final Logger LOG = LoggerFactory.getLogger(ProcedimentosOracle.class);

    private final JdbcTemplate jdbcTemplate;
    private final Clock relogio;

    public ProcedimentosOracle(JdbcTemplate jdbcTemplate, Clock relogio) {
        this.jdbcTemplate = jdbcTemplate;
        this.relogio = relogio;
    }

    @Override
    public boolean disponivel() {
        return true;
    }

    @Override
    public ResultadoProcedimento exportarPetsEmJson(String usuario) {
        ResultadoProcedimento resultado = executarComSaidaEmClob(EXPORTAR_PETS_JSON, usuario);
        if (!resultado.sucesso()) {
            return resultado;
        }
        // A procedure devolve o array em uma linha só; quebrar por pet facilita a leitura na tela.
        return ResultadoProcedimento.sucesso(EXPORTAR_PETS_JSON, resultado.conteudo().replace("},{", "},\n{"));
    }

    @Override
    public ResultadoProcedimento relatorioDaColeira(String usuario) {
        return executarComSaidaEmClob(RELATORIO_COLEIRA, usuario);
    }

    @Override
    public int idadeDoPet(LocalDate dataNascimento) {
        try {
            Integer idade = jdbcTemplate.queryForObject(
                    "SELECT PKG_RELATORIOS.FN_CALCULAR_IDADE_PET(?) FROM DUAL", Integer.class,
                    Date.valueOf(dataNascimento));
            return idade == null ? 0 : idade;
        } catch (DataAccessException erro) {
            LOG.warn("Falha ao calcular a idade no banco; usando o cálculo da aplicação.", erro);
            return Period.between(dataNascimento, LocalDate.now(relogio)).getYears();
        }
    }

    @Override
    public List<AuditoriaColeiraResponse> ultimasAuditoriasDaColeira() {
        return jdbcTemplate.query("""
                SELECT ID_AUDITORIA, OPERACAO, USUARIO, DATA_HORA,
                       NVL(ID_COLEIRA_NEW, ID_COLEIRA_OLD) AS ID_LEITURA, BATIMENTO_OLD, BATIMENTO_NEW
                  FROM TB_AUDITORIA_COLEIRA
                 ORDER BY ID_AUDITORIA DESC
                 FETCH FIRST 10 ROWS ONLY
                """, (linha, numero) -> new AuditoriaColeiraResponse(
                linha.getLong("ID_AUDITORIA"),
                linha.getString("OPERACAO"),
                linha.getString("USUARIO"),
                dataHora(linha, "DATA_HORA"),
                linha.getLong("ID_LEITURA"),
                linha.getBigDecimal("BATIMENTO_OLD"),
                linha.getBigDecimal("BATIMENTO_NEW")));
    }

    @Override
    public List<LogErroResponse> ultimosErros() {
        return jdbcTemplate.query("""
                SELECT ID_LOG_ERRO, NOME_PROCEDURE, NOME_USUARIO, DATA_ERRO, CODIGO_ERRO, MENSAGEM_ERRO
                  FROM TB_LOG_ERRO
                 ORDER BY ID_LOG_ERRO DESC
                 FETCH FIRST 5 ROWS ONLY
                """, (linha, numero) -> new LogErroResponse(
                linha.getLong("ID_LOG_ERRO"),
                linha.getString("NOME_PROCEDURE"),
                linha.getString("NOME_USUARIO"),
                dataHora(linha, "DATA_ERRO"),
                linha.getLong("CODIGO_ERRO"),
                linha.getString("MENSAGEM_ERRO")));
    }

    private ResultadoProcedimento executarComSaidaEmClob(String procedimento, String usuario) {
        try {
            String conteudo = jdbcTemplate.execute((Connection conexao) -> lerSaidaEmClob(conexao, procedimento));
            return ResultadoProcedimento.sucesso(procedimento, conteudo);
        } catch (DataAccessException erro) {
            SQLException causa = erro.getMostSpecificCause() instanceof SQLException sql ? sql : null;
            int codigo = causa == null ? 0 : causa.getErrorCode();
            String mensagem = causa == null ? erro.getMessage() : causa.getMessage();
            registrarErro(procedimento, usuario, codigo, mensagem);
            return ResultadoProcedimento.falha(procedimento, mensagem);
        }
    }

    private static String lerSaidaEmClob(Connection conexao, String procedimento) throws SQLException {
        try (CallableStatement chamada = conexao.prepareCall("{call " + procedimento + "(?)}")) {
            chamada.registerOutParameter(1, Types.CLOB);
            chamada.execute();
            Clob saida = chamada.getClob(1);
            if (saida == null) {
                return "";
            }
            try {
                return saida.getSubString(1, (int) saida.length());
            } finally {
                saida.free();
            }
        }
    }

    /** Mesma tabela de log usada pelo grupo de Banco de Dados para os erros das procedures. */
    private void registrarErro(String procedimento, String usuario, int codigo, String mensagem) {
        String mensagemLimitada = mensagem == null ? "Erro sem mensagem."
                : mensagem.substring(0, Math.min(mensagem.length(), TAMANHO_MAXIMO_MENSAGEM));
        try {
            jdbcTemplate.update("""
                    INSERT INTO TB_LOG_ERRO (ID_LOG_ERRO, NOME_PROCEDURE, NOME_USUARIO, DATA_ERRO, CODIGO_ERRO,
                                             MENSAGEM_ERRO)
                    VALUES (SEQ_LOG_ERRO.NEXTVAL, ?, ?, ?, ?, ?)
                    """, procedimento, usuario, Timestamp.valueOf(LocalDateTime.now(relogio)), codigo,
                    mensagemLimitada);
        } catch (DataAccessException erroAoRegistrar) {
            LOG.error("Não foi possível gravar o erro de {} em TB_LOG_ERRO.", procedimento, erroAoRegistrar);
        }
    }

    private static LocalDateTime dataHora(ResultSet linha, String coluna) throws SQLException {
        Timestamp valor = linha.getTimestamp(coluna);
        return valor == null ? null : valor.toLocalDateTime();
    }
}
