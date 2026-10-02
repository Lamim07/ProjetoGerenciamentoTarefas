package br.edu.tarefas.service;

import br.edu.tarefas.dao.CategoriaDao;
import br.edu.tarefas.model.Categoria;
import br.edu.tarefas.model.CategoriaResumo;
import br.edu.tarefas.model.Usuario;
import java.sql.SQLException;
import java.util.List;

public final class CategoriaService {
    private final CategoriaDao dao = new CategoriaDao();

    public List<CategoriaResumo> listar(Usuario ator) throws SQLException {
        exigirUsuario(ator);
        return dao.listarComContagem();
    }

    public void criar(Usuario ator, String nome, String descricao) throws SQLException {
        exigirUsuario(ator);
        nome = validarNome(nome);
        descricao = validarDescricao(descricao);
        if (nomeEmUso(nome, 0)) throw new IllegalArgumentException("Já existe uma categoria com esse nome.");
        dao.inserir(nome, descricao);
    }

    public void editar(Usuario ator, Categoria original, String nome, String descricao) throws SQLException {
        exigirUsuario(ator);
        if (original == null || !dao.existe(original.id())) throw new IllegalArgumentException("Categoria não encontrada.");
        nome = validarNome(nome);
        descricao = validarDescricao(descricao);
        if ("Geral".equalsIgnoreCase(original.nome()) && !"Geral".equals(nome))
            throw new IllegalArgumentException("O nome da categoria Geral não pode ser alterado.");
        if (nomeEmUso(nome, original.id())) throw new IllegalArgumentException("Já existe uma categoria com esse nome.");
        dao.atualizar(new Categoria(original.id(), nome, descricao));
    }

    public void excluir(Usuario ator, Categoria categoria) throws SQLException {
        exigirUsuario(ator);
        if (categoria == null) throw new IllegalArgumentException("Selecione uma categoria.");
        if ("Geral".equalsIgnoreCase(categoria.nome()))
            throw new IllegalArgumentException("A categoria Geral não pode ser excluída.");
        if (!dao.excluirSeVazia(categoria.id()))
            throw new IllegalArgumentException("Categoria com tarefas não pode ser excluída.");
    }

    private boolean nomeEmUso(String nome, long ignorarId) throws SQLException {
        return dao.listar().stream().anyMatch(c -> c.id() != ignorarId && c.nome().equalsIgnoreCase(nome));
    }

    private static String validarNome(String nome) {
        if (nome == null || nome.isBlank() || nome.trim().length() > 80)
            throw new IllegalArgumentException("Informe um nome de categoria com até 80 caracteres.");
        return nome.trim();
    }

    private static String validarDescricao(String descricao) {
        String valor = descricao == null ? "" : descricao.trim();
        if (valor.length() > 255) throw new IllegalArgumentException("A descrição deve ter até 255 caracteres.");
        return valor.isEmpty() ? null : valor;
    }

    private static void exigirUsuario(Usuario ator) {
        if (ator == null || ator.id() < 1) throw new SecurityException("Faça login para gerenciar categorias.");
    }
}
