package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.request.PerfilTutorRequest;
import br.com.fiap.clyvovet.exception.RecursoNaoEncontradoException;
import br.com.fiap.clyvovet.exception.RegraNegocioException;
import br.com.fiap.clyvovet.model.Perfil;
import br.com.fiap.clyvovet.model.Tutor;
import br.com.fiap.clyvovet.model.Usuario;
import br.com.fiap.clyvovet.repository.TutorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TutorService {

    private final TutorRepository tutorRepository;
    private final ContaService contaService;

    public TutorService(TutorRepository tutorRepository, ContaService contaService) {
        this.tutorRepository = tutorRepository;
        this.contaService = contaService;
    }

    public Usuario tornarTutor(Long usuarioId, PerfilTutorRequest request) {
        String cpf = request.getCpf().replaceAll("\\D", "");
        if (tutorRepository.existsByCpf(cpf)) {
            throw new RegraNegocioException("Este CPF já está vinculado a outro tutor.");
        }
        Usuario usuario = contaService.buscar(usuarioId);
        usuario.definirPerfil(Perfil.TUTOR);
        tutorRepository.save(new Tutor(usuario, cpf, request.getTelefone()));
        return usuario;
    }

    @Transactional(readOnly = true)
    public Tutor buscarPorUsuario(Long usuarioId) {
        return tutorRepository.findByUsuario_Id(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Perfil de tutor não encontrado."));
    }
}
