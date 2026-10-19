package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.PetForm;
import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.model.Tutor;
import br.com.fiap.clyvovet.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PetService {

    private final PetRepository petRepository;
    private final TutorService tutorService;

    public PetService(PetRepository petRepository, TutorService tutorService) {
        this.petRepository = petRepository;
        this.tutorService = tutorService;
    }

    public Pet cadastrar(Long usuarioTutorId, PetForm form) {
        Tutor tutor = tutorService.buscarPorUsuario(usuarioTutorId);
        return petRepository.save(new Pet(tutor, form.getNome(), form.getEspecie(), form.getRaca(),
                form.getDataNascimento(), form.getPesoKg()));
    }

    @Transactional(readOnly = true)
    public List<Pet> listarDoTutor(Long usuarioTutorId) {
        return petRepository.findByTutor_Usuario_IdOrderByNomeAsc(usuarioTutorId);
    }

    @Transactional(readOnly = true)
    public Pet buscarDoTutor(Long usuarioTutorId, Long petId) {
        return petRepository.findByIdAndTutor_Usuario_Id(petId, usuarioTutorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado."));
    }
}
