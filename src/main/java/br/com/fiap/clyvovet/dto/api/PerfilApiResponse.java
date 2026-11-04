package br.com.fiap.clyvovet.dto.api;

/**
 * Perfil do usuário no formato que o aplicativo mobile já usava (UsuarioPerfil).
 * Campos de tutor ou de veterinário ficam nulos conforme o tipo.
 */
public record PerfilApiResponse(
        String uid,
        String tipo,
        String nome,
        String email,
        String cpf,
        String whatsapp,
        String dataNascimento,
        String crmv,
        String telefone,
        String especialidade
) {
}
