package br.com.fiap.clyvovet.mapper;

import br.com.fiap.clyvovet.dto.response.VeterinarioResponse;
import br.com.fiap.clyvovet.model.Veterinario;
import org.springframework.stereotype.Component;

@Component
public class VeterinarioMapper {

    public VeterinarioResponse paraResponse(Veterinario veterinario) {
        return new VeterinarioResponse(
                veterinario.getId(),
                veterinario.getNome(),
                veterinario.getCrmv(),
                veterinario.getEspecialidade()
        );
    }
}
