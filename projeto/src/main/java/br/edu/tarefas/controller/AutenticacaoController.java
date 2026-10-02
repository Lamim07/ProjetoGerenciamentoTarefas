package br.edu.tarefas.controller;

import br.edu.tarefas.model.Usuario;
import br.edu.tarefas.service.AutenticacaoService;
import java.sql.SQLException;

public final class AutenticacaoController {
    private final AutenticacaoService service = new AutenticacaoService();

    public Usuario entrar(String login, char[] senha) throws SQLException {
        return service.entrar(login, senha);
    }

    public void cadastrar(String nome, String usuario, String email, char[] senha, char[] confirmacao)
            throws SQLException {
        service.cadastrar(nome, usuario, email, senha, confirmacao);
    }
}
