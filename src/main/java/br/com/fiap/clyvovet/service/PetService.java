package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.request.PetRequest;
import br.com.fiap.clyvovet.dto.response.PetResponse;
import br.com.fiap.clyvovet.dto.response.RacaResponse;
import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.mapper.PetMapper;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.model.Raca;
import br.com.fiap.clyvovet.model.Tutor;
import br.com.fiap.clyvovet.repository.PetRepository;
import br.com.fiap.clyvovet.repository.RacaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PetService {

    private final PetRepository petRepository;
    private final RacaRepository racaRepository;
    private final TutorService tutorService;
    private final PetMapper petMapper;

    public PetService(PetRepository petRepository, RacaRepository racaRepository, TutorService tutorService,
                      PetMapper petMapper) {
        this.petRepository = petRepository;
        this.racaRepository = racaRepository;
        this.tutorService = tutorService;
        this.petMapper = petMapper;
    }

    public PetResponse cadastrar(Long usuarioTutorId, PetRequest request) {
        Tutor tutor = tutorService.buscarPorUsuario(usuarioTutorId);
        Raca raca = racaRepository.findById(request.getRacaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Raça não encontrada."));
        Pet pet = petRepository.save(new Pet(tutor, request.getNome(), raca, request.getSexo(),
                request.getDataNascimento(), request.getPesoKg(), request.getFotoUrl()));
        return petMapper.paraResponse(pet);
    }

    @Transactional(readOnly = true)
    public List<PetResponse> listarDoTutor(Long usuarioTutorId) {
        return paraResponse(petRepository.findByTutor_Usuario_IdOrderByNomeAsc(usuarioTutorId));
    }

    @Transactional(readOnly = true)
    public List<PetResponse> listarTodos() {
        return paraResponse(petRepository.findAllByOrderByNomeAsc());
    }

    @Transactional(readOnly = true)
    public PetResponse detalharDoTutor(Long usuarioTutorId, Long petId) {
        return petMapper.paraResponse(buscarDoTutor(usuarioTutorId, petId));
    }

    @Transactional(readOnly = true)
    public List<RacaResponse> listarRacas() {
        return racaRepository.findAllByOrderByEspecie_NomeAscNomeAsc().stream()
                .map(petMapper::paraResponse)
                .toList();
    }

    Pet buscarDoTutor(Long usuarioTutorId, Long petId) {
        return petRepository.findByIdAndTutor_Usuario_Id(petId, usuarioTutorId)
                .orElseThrow(PetService::petNaoEncontrado);
    }

    Pet buscar(Long petId) {
        return petRepository.findById(petId).orElseThrow(PetService::petNaoEncontrado);
    }

    private List<PetResponse> paraResponse(List<Pet> pets) {
        return pets.stream().map(petMapper::paraResponse).toList();
    }

    private static RecursoNaoEncontradoException petNaoEncontrado() {
        return new RecursoNaoEncontradoException("Pet não encontrado.");
    }
}
