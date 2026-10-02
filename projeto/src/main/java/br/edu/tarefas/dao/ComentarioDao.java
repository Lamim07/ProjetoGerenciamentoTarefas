package br.edu.tarefas.dao;

import br.edu.tarefas.model.ComentarioTarefa;
import br.edu.tarefas.model.Tarefa;
import br.edu.tarefas.model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class ComentarioDao {
    public List<ComentarioTarefa> listar(Tarefa tarefa) throws SQLException {
        String sql = """
            SELECT co.id,co.conteudo,co.criado_em,
                   u.id AS autor_id,u.nome,u.nome_usuario,u.email
            FROM dbo.comentarios_tarefa co
            JOIN dbo.usuarios u ON u.id=co.autor_id
            WHERE co.tarefa_id=? ORDER BY co.criado_em,co.id
            """;
        List<ComentarioTarefa> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, tarefa.id());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuario autor = new Usuario(rs.getLong("autor_id"), rs.getString("nome"),
                            rs.getString("nome_usuario"), rs.getString("email"), "");
                    lista.add(new ComentarioTarefa(rs.getLong("id"), tarefa, autor,
                            rs.getString("conteudo"), rs.getTimestamp("criado_em").toLocalDateTime()));
                }
            }
        }
        return lista;
    }

    public void inserir(long tarefaId, long autorId, String conteudo) throws SQLException {
        String sql = """
            INSERT INTO dbo.comentarios_tarefa(tarefa_id,autor_id,conteudo)
            SELECT t.id,?,? FROM dbo.tarefas t
            WHERE t.id=? AND (t.criador_id=? OR t.responsavel_id=?)
            """;
        try (Connection c = ConnectionFactory.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, autorId); ps.setString(2, conteudo); ps.setLong(3, tarefaId);
            ps.setLong(4, autorId); ps.setLong(5, autorId);
            if (ps.executeUpdate() != 1) throw new SQLException("Tarefa indisponível para comentário.");
        }
    }

    public boolean excluirDoAutor(long comentarioId, long autorId) throws SQLException {
        try (Connection c = ConnectionFactory.abrir();
             PreparedStatement ps = c.prepareStatement("DELETE FROM dbo.comentarios_tarefa WHERE id=? AND autor_id=?")) {
            ps.setLong(1, comentarioId); ps.setLong(2, autorId);
            return ps.executeUpdate() == 1;
        }
    }
}
