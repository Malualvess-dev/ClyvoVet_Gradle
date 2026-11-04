package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.api.CadastroTutorApiRequest;
import br.com.fiap.clyvovet.dto.api.CadastroVeterinarioApiRequest;
import br.com.fiap.clyvovet.dto.api.PerfilApiResponse;
import br.com.fiap.clyvovet.dto.request.CadastroContaRequest;
import br.com.fiap.clyvovet.dto.request.PerfilTutorRequest;
import br.com.fiap.clyvovet.dto.request.PerfilVeterinarioRequest;
import br.com.fiap.clyvovet.mapper.AplicativoMapper;
import br.com.fiap.clyvovet.model.Usuario;
import br.com.fiap.clyvovet.repository.TutorRepository;
import br.com.fiap.clyvovet.repository.VeterinarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * Conta e cadastro feitos pelo aplicativo mobile. Usa as mesmas regras do cadastro web
 * (ContaService, TutorService e VeterinarioService) em uma única transação.
 */
@Service
@Transactional
public class AplicativoContaService {

    private final ContaService contaService;
    private final TutorService tutorService;
    private final VeterinarioService veterinarioService;
    private final TutorRepository tutorRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final AplicativoMapper mapper;

    public AplicativoContaService(ContaService contaService, TutorService tutorService,
                                  VeterinarioService veterinarioService, TutorRepository tutorRepository,
                                  VeterinarioRepository veterinarioRepository, AplicativoMapper mapper) {
        this.contaService = contaService;
        this.tutorService = tutorService;
        this.veterinarioService = veterinarioService;
        this.tutorRepository = tutorRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public PerfilApiResponse perfil(Long usuarioId) {
        Usuario usuario = contaService.buscar(usuarioId);
        return mapper.perfil(usuario,
                tutorRepository.findByUsuario_Id(usuarioId).orElse(null),
                veterinarioRepository.findByUsuario_Id(usuarioId).orElse(null));
    }

    public PerfilApiResponse cadastrarTutor(CadastroTutorApiRequest request) {
        Usuario usuario = contaService.criarContaComSenha(conta(request.nome(), request.email(), request.senha()));
        PerfilTutorRequest perfil = new PerfilTutorRequest();
        perfil.setCpf(request.cpf());
        perfil.setTelefone(request.whatsapp());
        perfil.setDataNascimento(request.dataNascimento());
        tutorService.tornarTutor(usuario.getId(), perfil);
        return perfil(usuario.getId());
    }

    public PerfilApiResponse cadastrarVeterinario(CadastroVeterinarioApiRequest request) {
        Usuario usuario = contaService.criarContaComSenha(conta(request.nome(), request.email(), request.senha()));
        PerfilVeterinarioRequest perfil = new PerfilVeterinarioRequest();
        perfil.setCrmv(request.crmv().strip().toUpperCase(Locale.ROOT));
        perfil.setTelefone(request.telefone());
        perfil.setEspecialidade(request.especialidade());
        perfil.setDataNascimento(request.dataNascimento());
        veterinarioService.tornarVeterinario(usuario.getId(), perfil);
        return perfil(usuario.getId());
    }

    private static CadastroContaRequest conta(String nome, String email, String senha) {
        CadastroContaRequest conta = new CadastroContaRequest();
        conta.setNome(nome);
        conta.setEmail(email);
        conta.setSenha(senha);
        conta.setConfirmacaoSenha(senha);
        return conta;
    }
}
