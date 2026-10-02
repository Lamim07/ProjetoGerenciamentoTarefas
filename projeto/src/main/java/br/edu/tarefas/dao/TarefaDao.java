package br.edu.tarefas.dao;

import br.edu.tarefas.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class TarefaDao {
    private static final String SELECT = """
        SELECT t.id,t.titulo,t.descricao,t.prazo,t.prioridade,t.status,t.criada_em,
               c.id AS c_id,c.nome AS c_nome,c.nome_usuario AS c_usuario,c.email AS c_email,
               r.id AS r_id,r.nome AS r_nome,r.nome_usuario AS r_usuario,r.email AS r_email,
               ca.id AS categoria_id,ca.nome AS categoria_nome,ca.descricao AS categoria_descricao
        FROM dbo.tarefas t
        JOIN dbo.usuarios c ON c.id=t.criador_id
        JOIN dbo.usuarios r ON r.id=t.responsavel_id
        JOIN dbo.categorias ca ON ca.id=t.categoria_id
        """;

    public List<Tarefa> listarCriadas(long usuarioId) throws SQLException {
        return listar(SELECT + " WHERE t.criador_id=? ORDER BY t.criada_em DESC", usuarioId);
    }

    public List<Tarefa> listarRecebidas(long usuarioId) throws SQLException {
        return listar(SELECT + " WHERE t.responsavel_id=? ORDER BY t.criada_em DESC", usuarioId);
    }

    private List<Tarefa> listar(String sql, long id) throws SQLException {
        List<Tarefa> tarefas = new ArrayList<>();
        try (Connection c = ConnectionFactory.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tarefas.add(mapear(rs));
            }
        }
        return tarefas;
    }

    public Optional<Tarefa> buscar(long id) throws SQLException {
        try (Connection c = ConnectionFactory.abrir(); PreparedStatement ps = c.prepareStatement(SELECT + " WHERE t.id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public void inserir(Tarefa t) throws SQLException {
        String sql = "INSERT INTO dbo.tarefas(titulo,descricao,criador_id,responsavel_id,categoria_id,prazo,prioridade,status) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection c = ConnectionFactory.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, t.titulo()); ps.setString(2, t.descricao());
            ps.setLong(3, t.criador().id()); ps.setLong(4, t.responsavel().id());
            ps.setLong(5, t.categoria().id()); ps.setDate(6, Date.valueOf(t.prazo()));
            ps.setString(7, t.prioridade().name()); ps.setString(8, t.status().name()); ps.executeUpdate();
        }
    }

    public void atualizarDados(Tarefa t) throws SQLException {
        String sql = "UPDATE dbo.tarefas SET titulo=?,descricao=?,responsavel_id=?,categoria_id=?,prazo=?,prioridade=?,status=? WHERE id=? AND criador_id=?";
        try (Connection c = ConnectionFactory.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, t.titulo()); ps.setString(2, t.descricao());
            ps.setLong(3, t.responsavel().id()); ps.setLong(4, t.categoria().id());
            ps.setDate(5, Date.valueOf(t.prazo())); ps.setString(6, t.prioridade().name());
            ps.setString(7, t.status().name()); ps.setLong(8, t.id()); ps.setLong(9, t.criador().id());
            if (ps.executeUpdate() != 1) throw new SQLException("Tarefa não encontrada para edição.");
        }
    }

    public void atualizarStatus(long tarefaId, long responsavelId, StatusTarefa status) throws SQLException {
        String sql = "UPDATE dbo.tarefas SET status=? WHERE id=? AND responsavel_id=?";
        try (Connection c = ConnectionFactory.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status.name()); ps.setLong(2, tarefaId); ps.setLong(3, responsavelId);
            if (ps.executeUpdate() != 1) throw new SQLException("Tarefa não encontrada para atualização de status.");
        }
    }

    public void excluir(long tarefaId, long criadorId) throws SQLException {
        try (Connection c = ConnectionFactory.abrir();
             PreparedStatement ps = c.prepareStatement("DELETE FROM dbo.tarefas WHERE id=? AND criador_id=?")) {
            ps.setLong(1, tarefaId); ps.setLong(2, criadorId);
            if (ps.executeUpdate() != 1) throw new SQLException("Tarefa não encontrada para exclusão.");
        }
    }

    private static Tarefa mapear(ResultSet rs) throws SQLException {
        Usuario c = new Usuario(rs.getLong("c_id"), rs.getString("c_nome"), rs.getString("c_usuario"),
                rs.getString("c_email"), "");
        Usuario r = new Usuario(rs.getLong("r_id"), rs.getString("r_nome"), rs.getString("r_usuario"),
                rs.getString("r_email"), "");
        Categoria categoria = new Categoria(rs.getLong("categoria_id"), rs.getString("categoria_nome"),
                rs.getString("categoria_descricao"));
        return new Tarefa(rs.getLong("id"), rs.getString("titulo"), rs.getString("descricao"), c, r,
                categoria, rs.getDate("prazo").toLocalDate(), PrioridadeTarefa.valueOf(rs.getString("prioridade")),
                StatusTarefa.valueOf(rs.getString("status")), rs.getTimestamp("criada_em").toLocalDateTime());
    }
}
