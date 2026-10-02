package br.edu.tarefas;

import br.edu.tarefas.dao.CategoriaDao;
import br.edu.tarefas.dao.ConnectionFactory;
import br.edu.tarefas.dao.TarefaDao;
import java.sql.*;
import java.util.UUID;

public final class TesteIntegracaoQuatroTabelas {
    public static void main(String[] args) throws Exception {
        if (new CategoriaDao().listar().stream().noneMatch(c -> "Geral".equals(c.nome())))
            throw new AssertionError("Categoria Geral não encontrada.");

        try (Connection c = ConnectionFactory.abrir()) {
            c.setAutoCommit(false);
            try {
                long usuarioId = consultarId(c, "SELECT TOP (1) id FROM dbo.usuarios ORDER BY id");
                long tarefaExistente = consultarId(c, "SELECT TOP (1) id FROM dbo.tarefas ORDER BY id");
                if (new TarefaDao().buscar(tarefaExistente).orElseThrow().categoria() == null)
                    throw new AssertionError("Tarefa existente sem categoria no DAO.");

                String nome = "Teste-" + UUID.randomUUID().toString().substring(0, 8);
                long categoriaId;
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO dbo.categorias(nome,descricao) VALUES(?,N'Teste temporario')",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, nome); ps.executeUpdate();
                    categoriaId = chaveGerada(ps);
                }

                long tarefaId;
                try (PreparedStatement ps = c.prepareStatement("""
                        INSERT INTO dbo.tarefas(titulo,descricao,criador_id,responsavel_id,categoria_id,prazo,prioridade,status)
                        VALUES(N'Teste temporario',N'Teste de integracao',?,?,?,DATEADD(day,7,CAST(GETDATE() AS date)),N'MEDIA',N'PENDENTE')
                        """, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, usuarioId); ps.setLong(2, usuarioId); ps.setLong(3, categoriaId);
                    ps.executeUpdate(); tarefaId = chaveGerada(ps);
                }

                long comentarioId;
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO dbo.comentarios_tarefa(tarefa_id,autor_id,conteudo) VALUES(?,?,N'Comentario temporario')",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, tarefaId); ps.setLong(2, usuarioId); ps.executeUpdate();
                    comentarioId = chaveGerada(ps);
                }

                try (PreparedStatement ps = c.prepareStatement("""
                        SELECT COUNT(*) FROM dbo.tarefas t
                        JOIN dbo.categorias ca ON ca.id=t.categoria_id
                        JOIN dbo.comentarios_tarefa co ON co.tarefa_id=t.id
                        JOIN dbo.usuarios u ON u.id=co.autor_id
                        WHERE t.id=? AND ca.id=? AND co.id=? AND u.id=?
                        """)) {
                    ps.setLong(1, tarefaId); ps.setLong(2, categoriaId);
                    ps.setLong(3, comentarioId); ps.setLong(4, usuarioId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() || rs.getInt(1) != 1) throw new AssertionError("Relações entre tabelas inválidas.");
                    }
                }

                try (PreparedStatement ps = c.prepareStatement("DELETE FROM dbo.tarefas WHERE id=?")) {
                    ps.setLong(1, tarefaId); ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM dbo.comentarios_tarefa WHERE id=?")) {
                    ps.setLong(1, comentarioId);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        if (rs.getInt(1) != 0) throw new AssertionError("Exclusão em cascata dos comentários falhou.");
                    }
                }
                System.out.println("Integração aprovada: quatro tabelas, categoria e comentários.");
            } finally {
                c.rollback();
            }
        }
    }

    private static long consultarId(Connection c, String sql) throws SQLException {
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (!rs.next()) throw new AssertionError("Faltam dados iniciais para o teste.");
            return rs.getLong(1);
        }
    }

    private static long chaveGerada(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (!rs.next()) throw new SQLException("ID gerado não encontrado.");
            return rs.getLong(1);
        }
    }
}
