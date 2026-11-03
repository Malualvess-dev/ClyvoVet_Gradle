package br.com.fiap.clyvovet.model;

import br.com.fiap.clyvovet.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Evento de saúde do pet (vacinas, exames, procedimentos). Vacinas podem ter data de reforço. */
@Entity
@Table(name = "TB_HISTORICO_SAUDE")
public class HistoricoSaude {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_HISTORICO_SAUDE")
    @SequenceGenerator(name = "SEQ_HISTORICO_SAUDE", sequenceName = "SEQ_HISTORICO_SAUDE", allocationSize = 1)
    @Column(name = "ID_HISTORICO_SAUDE")
    private Long id;

    @Column(name = "EVENTO", nullable = false, length = 100)
    private String evento;

    @Column(name = "DATA_EVENTO", nullable = false)
    private LocalDate dataEvento;

    @Column(name = "DETALHES", length = 200)
    private String detalhes;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_PET", nullable = false)
    private Pet pet;

    @Column(name = "PROXIMA_DOSE")
    private LocalDate proximaDose;

    @ManyToOne
    @JoinColumn(name = "ID_CONSULTA")
    private Consulta consulta;

    protected HistoricoSaude() {
    }

    private HistoricoSaude(Consulta consulta, String evento, LocalDate dataEvento, String detalhes,
                           LocalDate proximaDose) {
        this.consulta = consulta;
        this.pet = consulta.getPet();
        this.evento = evento.strip();
        this.dataEvento = dataEvento;
        this.detalhes = detalhes;
        this.proximaDose = proximaDose;
    }

    public static HistoricoSaude vacinaAplicada(Consulta consulta, String vacina, LocalDate dataAplicacao,
                                                LocalDate proximaDose) {
        if (consulta.getStatus() != StatusConsulta.CONCLUIDA) {
            throw new RegraNegocioException("Vacinas só podem ser registradas em atendimentos concluídos.");
        }
        if (proximaDose != null && !proximaDose.isAfter(dataAplicacao)) {
            throw new RegraNegocioException("A próxima dose deve ser posterior à data de aplicação.");
        }
        String detalhes = "Aplicada por %s no atendimento de %s."
                .formatted(consulta.getVeterinario().getNome(), consulta.getDataHora().format(DATA));
        return new HistoricoSaude(consulta, vacina, dataAplicacao, detalhes, proximaDose);
    }

    public Long getId() {
        return id;
    }

    public String getEvento() {
        return evento;
    }

    public LocalDate getDataEvento() {
        return dataEvento;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public Pet getPet() {
        return pet;
    }

    public LocalDate getProximaDose() {
        return proximaDose;
    }

    public Consulta getConsulta() {
        return consulta;
    }
}
