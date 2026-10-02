package br.edu.tarefas.dao;

import br.edu.tarefas.model.Categoria;
import br.edu.tarefas.model.CategoriaResumo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class CategoriaDao {
    public List<CategoriaResumo> listarComContagem() throws SQLException {
        String sql = """
            SELECT c.id,c.nome,c.descricao,COUNT_BIG(t.id) AS quantidade
            FROM dbo.categorias c LEFT JOIN dbo.tarefas t ON t.categoria_id=c.id
            GROUP BY c.id,c.nome,c.descricao ORDER BY c.nome
            """;
        List<CategoriaResumo> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.abrir();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(new CategoriaResumo(mapear(rs), rs.getLong("quantidade")));
        }
        return lista;
    }

    public List<Categoria> listar() throws SQLException {
        List<Categoria> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.abrir();
             PreparedStatement ps = c.prepareStatement("SELECT id,nome,descricao FROM dbo.categorias ORDER BY nome");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public boolean existe(long id) throws SQLException {
        try (Connection c = ConnectionFactory.abrir();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM dbo.categorias WHERE id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public void inserir(String nome, String descricao) throws SQLException {
        try (Connection c = ConnectionFactory.abrir();
             PreparedStatement ps = c.prepareStatement("INSERT INTO dbo.categorias(nome,descricao) VALUES(?,?)")) {
            ps.setString(1, nome);
            ps.setString(2, descricao);
            ps.executeUpdate();
        }
    }

    public void atualizar(Categoria categoria) throws SQLException {
        try (Connection c = ConnectionFactory.abrir();
             PreparedStatement ps = c.prepareStatement("UPDATE dbo.categorias SET nome=?,descricao=? WHERE id=?")) {
            ps.setString(1, categoria.nome());
            ps.setString(2, categoria.descricao());
            ps.setLong(3, categoria.id());
            if (ps.executeUpdate() != 1) throw new SQLException("Categoria não encontrada.");
        }
    }

    public boolean excluirSeVazia(long id) throws SQLException {
        String sql = """
            DELETE FROM dbo.categorias
            WHERE id=? AND nome<>N'Geral'
              AND NOT EXISTS(SELECT 1 FROM dbo.tarefas WHERE categoria_id=?)
            """;
        try (Connection c = ConnectionFactory.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id); ps.setLong(2, id);
            return ps.executeUpdate() == 1;
        }
    }

    private static Categoria mapear(ResultSet rs) throws SQLException {
        return new Categoria(rs.getLong("id"), rs.getString("nome"), rs.getString("descricao"));
    }
}
