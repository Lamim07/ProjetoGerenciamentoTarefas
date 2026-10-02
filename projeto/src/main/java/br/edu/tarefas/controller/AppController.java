package br.edu.tarefas.controller;

import br.edu.tarefas.model.Usuario;
import br.edu.tarefas.view.LoginFrame;
import br.edu.tarefas.view.MainFrame;

public final class AppController {
    private final AutenticacaoController autenticacao = new AutenticacaoController();
    private TarefaController tarefas;
    private CategoriaController categorias;
    private ComentarioController comentarios;
    private LoginFrame loginFrame;
    private MainFrame mainFrame;

    public void iniciar() { mostrarLogin(); }
    public AutenticacaoController autenticacao() { return autenticacao; }
    public TarefaController tarefas() { return tarefas; }
    public CategoriaController categorias() { return categorias; }
    public ComentarioController comentarios() { return comentarios; }

    public void mostrarLogin() {
        if (mainFrame != null) { mainFrame.dispose(); mainFrame = null; }
        loginFrame = new LoginFrame(this);
        loginFrame.setVisible(true);
    }

    public void entrar(Usuario usuario) {
        if (loginFrame != null) { loginFrame.dispose(); loginFrame = null; }
        tarefas = new TarefaController(usuario);
        categorias = new CategoriaController(usuario);
        comentarios = new ComentarioController(usuario);
        mainFrame = new MainFrame(this, usuario);
        mainFrame.setVisible(true);
    }

    public void sair() {
        tarefas = null; categorias = null; comentarios = null;
        mostrarLogin();
    }
}
