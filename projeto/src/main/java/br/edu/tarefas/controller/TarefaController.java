package br.edu.tarefas.controller;

import br.edu.tarefas.model.*;
import br.edu.tarefas.service.TarefaService;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public final class TarefaController {
    private final TarefaService service = new TarefaService();
    private final Usuario usuario;

    public TarefaController(Usuario usuario) { this.usuario = usuario; }
    public List<Usuario> usuarios() throws SQLException { return service.usuarios(); }
    public List<Categoria> categorias() throws SQLException { return service.categorias(); }
    public List<Tarefa> criadas() throws SQLException { return service.criadas(usuario); }
    public List<Tarefa> recebidas() throws SQLException { return service.recebidas(usuario); }
    public Tarefa buscar(long id) throws SQLException { return service.buscar(usuario, id); }

    public void criar(String titulo, String descricao, Usuario responsavel, Categoria categoria,
                      LocalDate prazo, PrioridadeTarefa prioridade) throws SQLException {
        service.criar(usuario, titulo, descricao, responsavel, categoria, prazo, prioridade);
    }

    public void editar(Tarefa original, String titulo, String descricao, Usuario responsavel,
                       Categoria categoria, LocalDate prazo, PrioridadeTarefa prioridade,
                       StatusTarefa status) throws SQLException {
        service.editar(usuario, original, titulo, descricao, responsavel, categoria, prazo, prioridade, status);
    }

    public void alterarStatus(Tarefa tarefa, StatusTarefa status) throws SQLException {
        service.alterarStatus(usuario, tarefa, status);
    }

    public void excluir(Tarefa tarefa) throws SQLException { service.excluir(usuario, tarefa); }
}
