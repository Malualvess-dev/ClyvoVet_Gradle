package br.com.fiap.clyvovet.security;

import br.com.fiap.clyvovet.model.Usuario;
import br.com.fiap.clyvovet.service.ContaService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class OAuth2UsuarioService extends DefaultOAuth2UserService {

    private final ContaService contaService;

    public OAuth2UsuarioService(ContaService contaService) {
        this.contaService = contaService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest requisicao) {
        OAuth2User usuarioDoProvedor = super.loadUser(requisicao);
        DadosDoProvedor dados = DadosDoProvedor.extrair(
                requisicao.getClientRegistration().getRegistrationId(), usuarioDoProvedor.getAttributes());
        Usuario usuario = contaService.obterOuCriarContaExterna(dados.nome(), dados.email(), dados.provedor());
        return UsuarioLogado.de(usuario, usuarioDoProvedor.getAttributes());
    }
}
