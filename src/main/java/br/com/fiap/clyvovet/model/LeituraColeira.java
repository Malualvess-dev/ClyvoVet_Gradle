package br.com.fiap.clyvovet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Leitura enviada pela coleira inteligente (IoT): localização e batimentos do pet. */
@Entity
@Table(name = "TB_COLEIRA")
public class LeituraColeira {

    static final BigDecimal BATIMENTO_MINIMO_NORMAL = new BigDecimal("50");
    static final BigDecimal BATIMENTO_MAXIMO_NORMAL = new BigDecimal("140");
    private static final int SEM_ALERTA = 0;
    private static final int COM_ALERTA = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_COLEIRA")
    @SequenceGenerator(name = "SEQ_COLEIRA", sequenceName = "SEQ_COLEIRA", allocationSize = 1)
    @Column(name = "ID_COLEIRA")
    private Long id;

    @Column(name = "DATA_HORA", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "LATITUDE", nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "LONGITUDE", nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "BATIMENTO_BPM", nullable = false, precision = 5, scale = 1)
    private BigDecimal batimentoBpm;

    @Column(name = "ALERTA", nullable = false)
    private Integer alerta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_PET", nullable = false)
    private Pet pet;

    protected LeituraColeira() {
    }

    public LeituraColeira(Pet pet, LocalDateTime dataHora, BigDecimal latitude, BigDecimal longitude,
                          BigDecimal batimentoBpm) {
        this.pet = pet;
        this.dataHora = dataHora;
        this.latitude = latitude;
        this.longitude = longitude;
        this.batimentoBpm = batimentoBpm;
        this.alerta = batimentoForaDoNormal(batimentoBpm) ? COM_ALERTA : SEM_ALERTA;
    }

    private static boolean batimentoForaDoNormal(BigDecimal batimento) {
        return batimento.compareTo(BATIMENTO_MINIMO_NORMAL) < 0 || batimento.compareTo(BATIMENTO_MAXIMO_NORMAL) > 0;
    }

    public boolean isAlerta() {
        return alerta == COM_ALERTA;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public BigDecimal getBatimentoBpm() {
        return batimentoBpm;
    }

    public Pet getPet() {
        return pet;
    }
}
