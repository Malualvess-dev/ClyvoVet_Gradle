package br.com.fiap.clyvovet.mapper;

import br.com.fiap.clyvovet.dto.response.PetResponse;
import br.com.fiap.clyvovet.model.Pet;
import org.springframework.stereotype.Component;

@Component
public class PetMapper {

    public PetResponse paraResponse(Pet pet) {
        return new PetResponse(
                pet.getId(),
                pet.getNome(),
                pet.getEspecie(),
                pet.getRaca(),
                pet.getDataNascimento(),
                pet.getPesoKg()
        );
    }
}
