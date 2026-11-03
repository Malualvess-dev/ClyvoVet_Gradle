package br.com.fiap.clyvovet.mapper;

import br.com.fiap.clyvovet.dto.response.PetResponse;
import br.com.fiap.clyvovet.dto.response.RacaResponse;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.model.Raca;
import org.springframework.stereotype.Component;

@Component
public class PetMapper {

    public PetResponse paraResponse(Pet pet) {
        Raca raca = pet.getRaca();
        return new PetResponse(
                pet.getId(),
                pet.getNome(),
                raca.getEspecie().getNome(),
                raca.getNome(),
                raca.getPorte().getDescricao(),
                pet.getSexo(),
                pet.getDataNascimento(),
                pet.getPesoKg(),
                pet.getFotoUrl()
        );
    }

    public RacaResponse paraResponse(Raca raca) {
        return new RacaResponse(
                raca.getId(),
                raca.getNome(),
                raca.getEspecie().getNome(),
                raca.getPorte().getDescricao()
        );
    }
}
