package br.com.fiap.clyvovet.model;

import br.com.fiap.clyvovet.exception.RegraNegocioException;
import br.com.fiap.clyvovet.model.conversor.SimNaoConverter;
import br.com.fiap.clyvovet.model.conversor.StatusConsultaConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegrasDoModeloTest {

    private static final LocalDateTime DATA_DA_CONSULTA = LocalDateTime.of(2026, 10, 21, 10, 0);

    private final Raca labrador = new Raca(1L, "Labrador Retriever", Porte.GRANDE, new Especie(1L, "Cão"));
    private final Pet thor = new Pet(null, "Thor", labrador, Sexo.M, LocalDate.of(2021, 5, 10),
            new BigDecimal("30.00"), null);
    private final Veterinario veterinario = new Veterinario(
            Usuario.comSenha("Dr. Roberto Santos", "vet@teste.com", "hash", DATA_DA_CONSULTA),
            "CRMV-SP-12345", "Clínica Geral", "(11) 98888-0001", LocalDate.of(1980, 1, 15));

    @ParameterizedTest
    @CsvSource({"SOLICITADA,SOLICITADA", "CONFIRMADA,AGENDADA", "RECUSADA,RECUSADA", "CANCELADA,CANCELADA",
            "CONCLUIDA,REALIZADA"})
    void statusUsaOsValoresDoModeloDoBanco(StatusConsulta status, String valorNoBanco) {
        StatusConsultaConverter conversor = new StatusConsultaConverter();

        assertThat(conversor.convertToDatabaseColumn(status)).isEqualTo(valorNoBanco);
        assertThat(conversor.convertToEntityAttribute(valorNoBanco)).isEqualTo(status);
    }

    @Test
    void colunaLidaGuardaSouN() {
        SimNaoConverter conversor = new SimNaoConverter();

        assertThat(conversor.convertToDatabaseColumn(true)).isEqualTo("S");
        assertThat(conversor.convertToDatabaseColumn(false)).isEqualTo("N");
        assertThat(conversor.convertToEntityAttribute("S")).isTrue();
        assertThat(conversor.convertToEntityAttribute("N")).isFalse();
    }

    @Test
    void notificacaoLongaEhResumidaParaCaberNaColuna() {
        Notificacao notificacao = new Notificacao(null, "a".repeat(300), DATA_DA_CONSULTA);

        assertThat(notificacao.getMensagem()).hasSize(Notificacao.TAMANHO_MAXIMO_MENSAGEM).endsWith("…");
        assertThat(notificacao.isLida()).isFalse();
    }

    @ParameterizedTest
    @CsvSource({"85.0,false", "145.5,true", "45.0,true", "140.0,false"})
    void leituraDaColeiraForaDaFaixaNormalGeraAlerta(BigDecimal batimento, boolean alerta) {
        LeituraColeira leitura = new LeituraColeira(thor, DATA_DA_CONSULTA, BigDecimal.ZERO, BigDecimal.ZERO,
                batimento);

        assertThat(leitura.isAlerta()).isEqualTo(alerta);
    }

    @Test
    void vacinaSoPodeSerRegistradaEmAtendimentoConcluido() {
        Consulta consulta = Consulta.solicitar(thor, veterinario, DATA_DA_CONSULTA, Modalidade.PRESENCIAL,
                "Vacinação", DATA_DA_CONSULTA.minusDays(2));
        consulta.confirmar();

        assertThatThrownBy(() -> HistoricoSaude.vacinaAplicada(consulta, "V10", DATA_DA_CONSULTA.toLocalDate(), null))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("concluídos");
    }

    @Test
    void proximaDoseDaVacinaPrecisaSerPosteriorAAplicacao() {
        Consulta consulta = consultaConcluida();
        LocalDate hoje = DATA_DA_CONSULTA.toLocalDate();

        assertThatThrownBy(() -> HistoricoSaude.vacinaAplicada(consulta, "V10", hoje, hoje))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("próxima dose");
    }

    @Test
    void vacinaAplicadaEntraNoHistoricoDeSaudeDoPet() {
        Consulta consulta = consultaConcluida();
        LocalDate hoje = DATA_DA_CONSULTA.toLocalDate();

        HistoricoSaude vacina = HistoricoSaude.vacinaAplicada(consulta, " Antirrábica ", hoje, hoje.plusYears(1));

        assertThat(vacina.getEvento()).isEqualTo("Antirrábica");
        assertThat(vacina.getPet()).isSameAs(thor);
        assertThat(vacina.getProximaDose()).isEqualTo(hoje.plusYears(1));
        assertThat(vacina.getDetalhes()).contains("Dr. Roberto Santos");
    }

    private Consulta consultaConcluida() {
        Consulta consulta = Consulta.solicitar(thor, veterinario, DATA_DA_CONSULTA, Modalidade.PRESENCIAL,
                "Vacinação", DATA_DA_CONSULTA.minusDays(2));
        consulta.confirmar();
        consulta.concluir(new DadosDoAtendimento("Saudável", null, null, new BigDecimal("31.00"), null, null, null,
                null), DATA_DA_CONSULTA);
        return consulta;
    }
}
