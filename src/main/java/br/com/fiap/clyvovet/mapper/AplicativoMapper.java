package br.com.fiap.clyvovet.mapper;

import br.com.fiap.clyvovet.dto.api.ConsultaApiResponse;
import br.com.fiap.clyvovet.dto.api.PerfilApiResponse;
import br.com.fiap.clyvovet.dto.api.PetApiResponse;
import br.com.fiap.clyvovet.dto.api.RacaApiResponse;
import br.com.fiap.clyvovet.dto.api.VeterinarioApiResponse;
import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.Perfil;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.model.Raca;
import br.com.fiap.clyvovet.model.StatusConsulta;
import br.com.fiap.clyvovet.model.Tutor;
import br.com.fiap.clyvovet.model.Usuario;
import br.com.fiap.clyvovet.model.Veterinario;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/** Converte as entidades para o formato JSON que as telas do aplicativo mobile já esperavam. */
@Component
public class AplicativoMapper {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORARIO = DateTimeFormatter.ofPattern("HH:mm");

    private final Clock relogio;

    public AplicativoMapper(Clock relogio) {
        this.relogio = relogio;
    }

    public PerfilApiResponse perfil(Usuario usuario, Tutor tutor, Veterinario veterinario) {
        String tipo = usuario.getPerfil() == Perfil.VETERINARIO ? "veterinario" : "tutor";
        return new PerfilApiResponse(
                texto(usuario.getId()),
                tipo,
                usuario.getNome(),
                usuario.getEmail(),
                tutor == null ? null : tutor.getCpf(),
                tutor == null ? null : tutor.getTelefone(),
                dataDe(tutor != null ? tutor.getDataNascimento()
                        : veterinario == null ? null : veterinario.getDataNascimento()),
                veterinario == null ? null : veterinario.getCrmv(),
                veterinario == null ? null : veterinario.getTelefone(),
                veterinario == null ? null : veterinario.getEspecialidade()
        );
    }

    public PetApiResponse pet(Pet pet) {
        Raca raca = pet.getRaca();
        Tutor tutor = pet.getTutor();
        return new PetApiResponse(
                texto(pet.getId()),
                texto(tutor.getUsuario().getId()),
                tutor.getNome(),
                pet.getNome(),
                raca.getEspecie().getNome(),
                texto(raca.getId()),
                raca.getNome(),
                raca.getPorte().getDescricao(),
                pet.getSexo().getDescricao(),
                pet.getSexo().name(),
                pet.getDataNascimento().toString(),
                idade(pet.getDataNascimento()),
                pet.getPesoKg(),
                peso(pet.getPesoKg()),
                pet.getObservacoes()
        );
    }

    public RacaApiResponse raca(Raca raca) {
        return new RacaApiResponse(texto(raca.getId()), raca.getNome(), raca.getEspecie().getNome(),
                raca.getPorte().getDescricao());
    }

    public VeterinarioApiResponse veterinario(Veterinario veterinario) {
        return new VeterinarioApiResponse(texto(veterinario.getId()), veterinario.getNome(), veterinario.getCrmv(),
                veterinario.getEspecialidade());
    }

    public ConsultaApiResponse consulta(Consulta consulta) {
        Pet pet = consulta.getPet();
        Tutor tutor = pet.getTutor();
        LocalDateTime dataHora = consulta.getDataHora();
        return new ConsultaApiResponse(
                texto(consulta.getId()),
                texto(tutor.getUsuario().getId()),
                texto(pet.getId()),
                pet.getNome(),
                tutor.getNome(),
                tutor.getTelefone(),
                texto(consulta.getVeterinario().getId()),
                consulta.getVeterinario().getNome(),
                dataHora.toString(),
                dataHora.format(DATA),
                dataHora.format(HORARIO),
                consulta.getModalidade().getDescricao(),
                consulta.getMotivo(),
                statusDoAplicativo(consulta.getStatus()),
                consulta.getJustificativa(),
                consulta.getProntuario() == null ? null : consulta.getProntuario().getDiagnostico(),
                consulta.podeSerCancelada(LocalDateTime.now(relogio))
        );
    }

    static String statusDoAplicativo(StatusConsulta status) {
        return switch (status) {
            case SOLICITADA -> "solicitada";
            case CONFIRMADA -> "confirmada";
            case RECUSADA -> "recusada";
            case CANCELADA -> "cancelada";
            case CONCLUIDA -> "realizada";
        };
    }

    String idade(LocalDate dataNascimento) {
        Period periodo = Period.between(dataNascimento, LocalDate.now(relogio));
        if (periodo.getYears() > 0) {
            return periodo.getYears() + (periodo.getYears() == 1 ? " ano" : " anos");
        }
        if (periodo.getMonths() > 0) {
            return periodo.getMonths() + (periodo.getMonths() == 1 ? " mês" : " meses");
        }
        return "Menos de 1 mês";
    }

    static String peso(BigDecimal pesoKg) {
        return pesoKg == null ? null : pesoKg.stripTrailingZeros().toPlainString().replace('.', ',');
    }

    private static String dataDe(LocalDate data) {
        return data == null ? null : data.toString();
    }

    private static String texto(Long id) {
        return id == null ? null : String.valueOf(id);
    }
}
