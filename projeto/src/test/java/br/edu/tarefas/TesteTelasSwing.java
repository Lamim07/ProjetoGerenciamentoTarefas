package br.edu.tarefas;

import br.edu.tarefas.controller.AppController;
import br.edu.tarefas.controller.CategoriaController;
import br.edu.tarefas.controller.ComentarioController;
import br.edu.tarefas.controller.TarefaController;
import br.edu.tarefas.dao.TarefaDao;
import br.edu.tarefas.model.Tarefa;
import br.edu.tarefas.model.Usuario;
import br.edu.tarefas.view.CategoryDialog;
import br.edu.tarefas.view.MainFrame;
import br.edu.tarefas.view.TaskDetailsDialog;
import br.edu.tarefas.view.TaskDialog;
import javax.swing.SwingUtilities;

public final class TesteTelasSwing {
    public static void main(String[] args) throws Exception {
        Tarefa tarefa = new TarefaDao().buscar(primeiraTarefaId()).orElseThrow();
        Usuario usuario = tarefa.criador();
        AppController controller = new AppController();
        TarefaController tarefas = new TarefaController(usuario);
        CategoriaController categoriasController = new CategoriaController(usuario);
        ComentarioController comentarios = new ComentarioController(usuario);
        final Exception[] falha = new Exception[1];
        SwingUtilities.invokeAndWait(() -> {
            MainFrame principal = null;
            try {
                principal = new MainFrame(controller, usuario);
                CategoryDialog categorias = new CategoryDialog(principal, categoriasController);
                categorias.dispose();
                TaskDialog formulario = new TaskDialog(principal, tarefas, usuario, null, false);
                formulario.dispose();
                TaskDetailsDialog detalhes = new TaskDetailsDialog(principal, tarefas,
                        comentarios, usuario, tarefa);
                detalhes.dispose();
            } catch (Exception e) {
                falha[0] = e;
            } finally {
                if (principal != null) principal.dispose();
            }
        });
        if (falha[0] != null) throw falha[0];
        System.out.println("Telas Swing inicializadas com sucesso.");
    }

    private static long primeiraTarefaId() throws Exception {
        try (var c = br.edu.tarefas.dao.ConnectionFactory.abrir();
             var ps = c.prepareStatement("SELECT TOP (1) id FROM dbo.tarefas ORDER BY id");
             var rs = ps.executeQuery()) {
            if (!rs.next()) throw new AssertionError("Cadastre uma tarefa antes do teste de telas.");
            return rs.getLong(1);
        }
    }
}
