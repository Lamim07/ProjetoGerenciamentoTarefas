package br.edu.tarefas.service;

import br.edu.tarefas.dao.UsuarioDao;
import br.edu.tarefas.model.Usuario;
import br.edu.tarefas.util.Senhas;
import java.sql.SQLException;
import java.util.Arrays;

public final class AutenticacaoService {
    private final UsuarioDao dao = new UsuarioDao();

    public Usuario entrar(String login, char[] senha) throws SQLException {
        if (login == null || login.isBlank()) throw new IllegalArgumentException("Informe o usuário ou e-mail.");
        try {
            Usuario usuario = dao.buscarPorLogin(login.trim()).orElse(null);
            if (usuario == null || !Senhas.verificar(senha, usuario.senhaHash()))
                throw new IllegalArgumentException("Usuário/e-mail ou senha inválidos.");
            return usuario;
        } finally { Arrays.fill(senha, '\0'); }
    }

    public void cadastrar(String nome, String nomeUsuario, String email, char[] senha, char[] confirmacao) throws SQLException {
        try {
            nome = nome.trim(); nomeUsuario = nomeUsuario.trim(); email = email.trim();
            if (nome.isBlank() || nomeUsuario.isBlank() || email.isBlank())
                throw new IllegalArgumentException("Preencha todos os campos.");
            if (nome.length() > 120 || nomeUsuario.length() > 60 || email.length() > 180)
                throw new IllegalArgumentException("Um dos campos excede o tamanho permitido.");
            if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
                throw new IllegalArgumentException("Informe um e-mail válido.");
            if (senha.length < 8) throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres.");
            if (!Arrays.equals(senha, confirmacao)) throw new IllegalArgumentException("As senhas não coincidem.");
            dao.inserir(nome, nomeUsuario, email, Senhas.gerar(senha));
        } finally { Arrays.fill(senha, '\0'); Arrays.fill(confirmacao, '\0'); }
    }
}
