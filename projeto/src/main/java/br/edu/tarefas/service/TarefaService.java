package br.edu.tarefas.service;

import br.edu.tarefas.dao.TarefaDao;
import br.edu.tarefas.dao.UsuarioDao;
import br.edu.tarefas.model.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class TarefaService {
    private final TarefaDao tarefas = new TarefaDao();
    private final UsuarioDao usuarios = new UsuarioDao();

    public List<Usuario> usuarios() throws SQLException { return usuarios.listar(); }
    public List<Tarefa> criadas(Usuario u) throws SQLException { return tarefas.listarCriadas(u.id()); }
    public List<Tarefa> recebidas(Usuario u) throws SQLException { return tarefas.listarRecebidas(u.id()); }

    public void criar(Usuario criador, String titulo, String descricao, Usuario responsavel,
                      LocalDate prazo, PrioridadeTarefa prioridade) throws SQLException {
        validar(titulo, descricao, responsavel, prazo, prioridade);
        tarefas.inserir(new Tarefa(0, titulo.trim(), descricao.trim(), criador, responsavel,
                prazo, prioridade, StatusTarefa.PENDENTE, LocalDateTime.now()));
    }

    public void editar(Usuario ator, Tarefa original, String titulo, String descricao,
                       Usuario responsavel, LocalDate prazo, PrioridadeTarefa prioridade,
                       StatusTarefa status) throws SQLException {
        if (original.criador().id() != ator.id()) throw new SecurityException("Somente o criador pode editar os dados da tarefa.");
        validar(titulo, descricao, responsavel, prazo, prioridade);
        if (status == null) throw new IllegalArgumentException("Selecione o status.");
        if (original.responsavel().id() != ator.id() && status != original.status())
            throw new SecurityException("Somente o responsável pode alterar o status.");
        tarefas.atualizarDados(new Tarefa(original.id(), titulo.trim(), descricao.trim(), ator,
                responsavel, prazo, prioridade, status, original.criadaEm()));
    }

    public void alterarStatus(Usuario ator, Tarefa tarefa, StatusTarefa status) throws SQLException {
        if (tarefa.responsavel().id() != ator.id()) throw new SecurityException("Somente o responsável pode alterar o status.");
        if (status == null) throw new IllegalArgumentException("Selecione o status.");
        tarefas.atualizarStatus(tarefa.id(), ator.id(), status);
    }

    public void excluir(Usuario ator, Tarefa tarefa) throws SQLException {
        if (tarefa.criador().id() != ator.id()) throw new SecurityException("Somente o criador pode excluir a tarefa.");
        tarefas.excluir(tarefa.id(), ator.id());
    }

    private void validar(String titulo, String descricao, Usuario responsavel, LocalDate prazo,
                         PrioridadeTarefa prioridade) throws SQLException {
        if (titulo == null || titulo.isBlank() || titulo.trim().length() > 180)
            throw new IllegalArgumentException("Informe um título de até 180 caracteres.");
        if (descricao == null || descricao.isBlank()) throw new IllegalArgumentException("Informe a descrição.");
        if (responsavel == null || usuarios.listar().stream().noneMatch(u -> u.id() == responsavel.id()))
            throw new IllegalArgumentException("Selecione um responsável válido.");
        if (prazo == null) throw new IllegalArgumentException("Informe o prazo.");
        if (prioridade == null) throw new IllegalArgumentException("Selecione a prioridade.");
    }
}
