package br.edu.tarefas.controller;

import br.edu.tarefas.model.Categoria;
import br.edu.tarefas.model.CategoriaResumo;
import br.edu.tarefas.model.Usuario;
import br.edu.tarefas.service.CategoriaService;
import java.sql.SQLException;
import java.util.List;

public final class CategoriaController {
    private final CategoriaService service = new CategoriaService();
    private final Usuario usuario;

    public CategoriaController(Usuario usuario) { this.usuario = usuario; }
    public List<CategoriaResumo> listar() throws SQLException { return service.listar(usuario); }
    public void criar(String nome, String descricao) throws SQLException { service.criar(usuario, nome, descricao); }
    public void editar(Categoria categoria, String nome, String descricao) throws SQLException {
        service.editar(usuario, categoria, nome, descricao);
    }
    public void excluir(Categoria categoria) throws SQLException { service.excluir(usuario, categoria); }
}
