package br.edu.tarefas.controller;

import br.edu.tarefas.model.Usuario;
import br.edu.tarefas.service.AutenticacaoService;
import br.edu.tarefas.service.TarefaService;
import br.edu.tarefas.view.LoginFrame;
import br.edu.tarefas.view.MainFrame;

public final class AppController {
    private final AutenticacaoService autenticacao = new AutenticacaoService();
    private final TarefaService tarefas = new TarefaService();
    private LoginFrame loginFrame;
    private MainFrame mainFrame;

    public void iniciar() { mostrarLogin(); }
    public AutenticacaoService autenticacao() { return autenticacao; }
    public TarefaService tarefas() { return tarefas; }

    public void mostrarLogin() {
        if (mainFrame != null) { mainFrame.dispose(); mainFrame = null; }
        loginFrame = new LoginFrame(this);
        loginFrame.setVisible(true);
    }

    public void entrar(Usuario usuario) {
        if (loginFrame != null) { loginFrame.dispose(); loginFrame = null; }
        mainFrame = new MainFrame(this, usuario);
        mainFrame.setVisible(true);
    }

    public void sair() { mostrarLogin(); }
}
