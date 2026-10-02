package br.edu.tarefas;

import br.edu.tarefas.dao.ConnectionFactory;
import br.edu.tarefas.dao.UsuarioDao;
import br.edu.tarefas.model.*;
import br.edu.tarefas.controller.CategoriaController;
import br.edu.tarefas.controller.ComentarioController;
import br.edu.tarefas.controller.TarefaController;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.UUID;

public final class TesteFluxosAplicacao {
    public static void main(String[] args) throws Exception {
        Usuario usuario = new UsuarioDao().listar().stream().findFirst()
                .orElseThrow(() -> new AssertionError("Cadastre um usuário antes do teste."));
        CategoriaController categorias = new CategoriaController(usuario);
        TarefaController tarefas = new TarefaController(usuario);
        ComentarioController comentarios = new ComentarioController(usuario);
        String sufixo = UUID.randomUUID().toString().substring(0, 8);
        String nomeCategoria = "TesteServico-" + sufixo;
        String titulo = "TesteServicoTarefa-" + sufixo;
        Categoria categoria = null;
        Tarefa tarefa = null;
        try {
            categorias.criar(nomeCategoria, "Categoria temporária");
            categoria = categorias.listar().stream().map(CategoriaResumo::categoria)
                    .filter(c -> c.nome().equals(nomeCategoria)).findFirst().orElseThrow();
            categorias.editar(categoria, nomeCategoria + " Editada", "Descrição alterada");
            long categoriaId = categoria.id();
            categoria = categorias.listar().stream().map(CategoriaResumo::categoria)
                    .filter(c -> c.id() == categoriaId).findFirst().orElseThrow();

            tarefas.criar(titulo, "Descrição temporária", usuario, categoria,
                    LocalDate.now().plusDays(7), PrioridadeTarefa.MEDIA);
            tarefa = tarefas.criadas().stream().filter(t -> t.titulo().equals(titulo))
                    .findFirst().orElseThrow();
            if (tarefa.categoria().id() != categoria.id()) throw new AssertionError("Categoria da tarefa incorreta.");
            try {
                categorias.excluir(categoria);
                throw new AssertionError("Categoria em uso foi excluída.");
            } catch (IllegalArgumentException esperado) {
                // Categoria vinculada a tarefa deve permanecer disponível.
            }

            comentarios.publicar(tarefa, "Comentário temporário");
            ComentarioTarefa comentario = comentarios.listar(tarefa).stream()
                    .filter(c -> c.conteudo().equals("Comentário temporário"))
                    .findFirst().orElseThrow();
            comentarios.excluir(comentario);
            comentarios.publicar(tarefa, "Comentário para testar cascata");

            tarefas.alterarStatus(tarefa, StatusTarefa.CONCLUIDA);
            tarefa = tarefas.buscar(tarefa.id());
            if (tarefa.status() != StatusTarefa.CONCLUIDA) throw new AssertionError("Status não atualizado.");
            tarefas.editar(tarefa, titulo, "Descrição editada", usuario, categoria,
                    tarefa.prazo(), tarefa.prioridade(), tarefa.status());
            tarefa = tarefas.buscar(tarefa.id());
            if (!"Descrição editada".equals(tarefa.descricao())) throw new AssertionError("Edição não persistida.");

            long tarefaId = tarefa.id();
            tarefas.excluir(tarefa); tarefa = null;
            try (Connection c = ConnectionFactory.abrir();
                 PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM dbo.comentarios_tarefa WHERE tarefa_id=?")) {
                ps.setLong(1, tarefaId);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) != 0) throw new AssertionError("Comentário não excluído com a tarefa.");
                }
            }
            categorias.excluir(categoria); categoria = null;
            System.out.println("Fluxos aprovados: categorias, tarefas e comentários via controllers/serviços/DAOs.");
        } finally {
            if (tarefa != null) {
                try { tarefas.excluir(tarefa); } catch (Exception ignored) { }
            }
            if (categoria != null) {
                try { categorias.excluir(categoria); } catch (Exception ignored) { }
            }
        }
    }
}
