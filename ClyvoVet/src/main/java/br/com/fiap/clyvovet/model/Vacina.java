package br.com.fiap.clyvovet.model;

import br.com.fiap.clyvovet.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "TB_CV_VACINA")
public class Vacina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_VACINA")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_PET", nullable = false)
    private Pet pet;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_CONSULTA", nullable = false)
    private Consulta consulta;

    @Column(name = "NOME", nullable = false, length = 80)
    private String nome;

    @Column(name = "DATA_APLICACAO", nullable = false)
    private LocalDate dataAplicacao;

    @Column(name = "PROXIMA_DOSE")
    private LocalDate proximaDose;

    protected Vacina() {
    }

    private Vacina(Consulta consulta, String nome, LocalDate dataAplicacao, LocalDate proximaDose) {
        this.pet = consulta.getPet();
        this.consulta = consulta;
        this.nome = nome.strip();
        this.dataAplicacao = dataAplicacao;
        this.proximaDose = proximaDose;
    }

    public static Vacina aplicadaEm(Consulta consulta, String nome, LocalDate proximaDose) {
        if (consulta.getStatus() != StatusConsulta.CONCLUIDA) {
            throw new RegraNegocioException("Vacinas só podem ser registradas em atendimentos concluídos.");
        }
        LocalDate dataAplicacao = consulta.getDataConclusao().toLocalDate();
        if (proximaDose != null && !proximaDose.isAfter(dataAplicacao)) {
            throw new RegraNegocioException("A próxima dose deve ser posterior à data de aplicação.");
        }
        return new Vacina(consulta, nome, dataAplicacao, proximaDose);
    }

    public Long getId() {
        return id;
    }

    public Pet getPet() {
        return pet;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public String getNome() {
        return nome;
    }

    public LocalDate getDataAplicacao() {
        return dataAplicacao;
    }

    public LocalDate getProximaDose() {
        return proximaDose;
    }
}
