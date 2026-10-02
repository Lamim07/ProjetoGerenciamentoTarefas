package br.edu.tarefas.controller;

import br.edu.tarefas.model.ComentarioTarefa;
import br.edu.tarefas.model.Tarefa;
import br.edu.tarefas.model.Usuario;
import br.edu.tarefas.service.ComentarioService;
import java.sql.SQLException;
import java.util.List;

public final class ComentarioController {
    private final ComentarioService service = new ComentarioService();
    private final Usuario usuario;

    public ComentarioController(Usuario usuario) { this.usuario = usuario; }
    public List<ComentarioTarefa> listar(Tarefa tarefa) throws SQLException { return service.listar(usuario, tarefa); }
    public void publicar(Tarefa tarefa, String conteudo) throws SQLException {
        service.publicar(usuario, tarefa, conteudo);
    }
    public void excluir(ComentarioTarefa comentario) throws SQLException { service.excluir(usuario, comentario); }
}
