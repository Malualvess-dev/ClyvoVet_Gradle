package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.api.PetApiRequest;
import br.com.fiap.clyvovet.dto.api.PetApiResponse;
import br.com.fiap.clyvovet.dto.api.RacaApiResponse;
import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.exception.RegraNegocioException;
import br.com.fiap.clyvovet.mapper.AplicativoMapper;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.model.Raca;
import br.com.fiap.clyvovet.model.Tutor;
import br.com.fiap.clyvovet.repository.ConsultaRepository;
import br.com.fiap.clyvovet.repository.HistoricoSaudeRepository;
import br.com.fiap.clyvovet.repository.LeituraColeiraRepository;
import br.com.fiap.clyvovet.repository.PetRepository;
import br.com.fiap.clyvovet.repository.RacaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Pets pelo aplicativo: o tutor gerencia os seus; o veterinário vê todos os pacientes. */
@Service
@Transactional
public class AplicativoPetService {

    private final PetService petService;
    private final TutorService tutorService;
    private final PetRepository petRepository;
    private final RacaRepository racaRepository;
    private final ConsultaRepository consultaRepository;
    private final HistoricoSaudeRepository historicoSaudeRepository;
    private final LeituraColeiraRepository leituraColeiraRepository;
    private final AplicativoMapper mapper;

    public AplicativoPetService(PetService petService, TutorService tutorService, PetRepository petRepository,
                                RacaRepository racaRepository, ConsultaRepository consultaRepository,
                                HistoricoSaudeRepository historicoSaudeRepository,
                                LeituraColeiraRepository leituraColeiraRepository, AplicativoMapper mapper) {
        this.petService = petService;
        this.tutorService = tutorService;
        this.petRepository = petRepository;
        this.racaRepository = racaRepository;
        this.consultaRepository = consultaRepository;
        this.historicoSaudeRepository = historicoSaudeRepository;
        this.leituraColeiraRepository = leituraColeiraRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<PetApiResponse> listarDoTutor(Long usuarioTutorId) {
        return petRepository.findByTutor_Usuario_IdOrderByNomeAsc(usuarioTutorId).stream().map(mapper::pet).toList();
    }

    @Transactional(readOnly = true)
    public List<PetApiResponse> listarTodos() {
        return petRepository.findAllByOrderByNomeAsc().stream().map(mapper::pet).toList();
    }

    @Transactional(readOnly = true)
    public List<RacaApiResponse> listarRacas() {
        return racaRepository.findAllByOrderByEspecie_NomeAscNomeAsc().stream().map(mapper::raca).toList();
    }

    public PetApiResponse cadastrar(Long usuarioTutorId, PetApiRequest request) {
        Tutor tutor = tutorService.buscarPorUsuario(usuarioTutorId);
        Pet pet = new Pet(tutor, request.nome(), raca(request.racaId()), request.sexo(), request.dataNascimento(),
                request.pesoKg(), null);
        pet.registrarObservacoes(request.observacoes());
        return mapper.pet(petRepository.save(pet));
    }

    public PetApiResponse atualizar(Long usuarioTutorId, Long petId, PetApiRequest request) {
        Pet pet = petService.buscarDoTutor(usuarioTutorId, petId);
        pet.atualizarDados(request.nome(), raca(request.racaId()), request.sexo(), request.dataNascimento(),
                request.pesoKg());
        pet.registrarObservacoes(request.observacoes());
        return mapper.pet(pet);
    }

    /** Só exclui pets sem histórico: consultas, vacinas e leituras da coleira ficam ligadas ao pet no banco. */
    public void excluir(Long usuarioTutorId, Long petId) {
        Pet pet = petService.buscarDoTutor(usuarioTutorId, petId);
        if (consultaRepository.existsByPet_Id(petId) || historicoSaudeRepository.existsByPet_Id(petId)
                || leituraColeiraRepository.existsByPet_Id(petId)) {
            throw new RegraNegocioException(pet.getNome()
                    + " já possui consultas ou histórico registrados e não pode ser excluído.");
        }
        petRepository.delete(pet);
    }

    private Raca raca(Long racaId) {
        return racaRepository.findById(racaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Raça não encontrada."));
    }
}
