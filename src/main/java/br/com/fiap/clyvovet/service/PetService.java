package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.request.PetRequest;
import br.com.fiap.clyvovet.dto.response.PetResponse;
import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.mapper.PetMapper;
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
    private final PetMapper petMapper;

    public PetService(PetRepository petRepository, TutorService tutorService, PetMapper petMapper) {
        this.petRepository = petRepository;
        this.tutorService = tutorService;
        this.petMapper = petMapper;
    }

    public PetResponse cadastrar(Long usuarioTutorId, PetRequest request) {
        Tutor tutor = tutorService.buscarPorUsuario(usuarioTutorId);
        Pet pet = petRepository.save(new Pet(tutor, request.getNome(), request.getEspecie(), request.getRaca(),
                request.getDataNascimento(), request.getPesoKg()));
        return petMapper.paraResponse(pet);
    }

    @Transactional(readOnly = true)
    public List<PetResponse> listarDoTutor(Long usuarioTutorId) {
        return petRepository.findByTutor_Usuario_IdOrderByNomeAsc(usuarioTutorId).stream()
                .map(petMapper::paraResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PetResponse detalharDoTutor(Long usuarioTutorId, Long petId) {
        return petMapper.paraResponse(buscarDoTutor(usuarioTutorId, petId));
    }

    Pet buscarDoTutor(Long usuarioTutorId, Long petId) {
        return petRepository.findByIdAndTutor_Usuario_Id(petId, usuarioTutorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado."));
    }
}
