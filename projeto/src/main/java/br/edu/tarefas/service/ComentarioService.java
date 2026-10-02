package br.edu.tarefas.service;

import br.edu.tarefas.dao.ComentarioDao;
import br.edu.tarefas.dao.TarefaDao;
import br.edu.tarefas.model.ComentarioTarefa;
import br.edu.tarefas.model.Tarefa;
import br.edu.tarefas.model.Usuario;
import java.sql.SQLException;
import java.util.List;

public final class ComentarioService {
    private final ComentarioDao comentarios = new ComentarioDao();
    private final TarefaDao tarefas = new TarefaDao();

    public List<ComentarioTarefa> listar(Usuario ator, Tarefa tarefa) throws SQLException {
        return comentarios.listar(exigirParticipante(ator, tarefa));
    }

    public void publicar(Usuario ator, Tarefa tarefa, String conteudo) throws SQLException {
        Tarefa atual = exigirParticipante(ator, tarefa);
        if (conteudo == null || conteudo.isBlank() || conteudo.trim().length() > 2000)
            throw new IllegalArgumentException("Escreva um comentário de até 2000 caracteres.");
        comentarios.inserir(atual.id(), ator.id(), conteudo.trim());
    }

    public void excluir(Usuario ator, ComentarioTarefa comentario) throws SQLException {
        if (ator == null || comentario == null || comentario.autor().id() != ator.id())
            throw new SecurityException("Somente o autor pode excluir o comentário.");
        if (!comentarios.excluirDoAutor(comentario.id(), ator.id()))
            throw new IllegalArgumentException("Comentário não encontrado.");
    }

    private Tarefa exigirParticipante(Usuario ator, Tarefa tarefa) throws SQLException {
        if (ator == null || tarefa == null) throw new SecurityException("Acesso à tarefa não autorizado.");
        Tarefa atual = tarefas.buscar(tarefa.id()).orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada."));
        if (atual.criador().id() != ator.id() && atual.responsavel().id() != ator.id())
            throw new SecurityException("Apenas criador e responsável podem acessar comentários.");
        return atual;
    }
}
