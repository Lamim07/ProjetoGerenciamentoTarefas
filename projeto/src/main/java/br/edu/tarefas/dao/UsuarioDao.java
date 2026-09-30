package br.edu.tarefas.dao;

import br.edu.tarefas.model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class UsuarioDao {
    public Optional<Usuario> buscarPorLogin(String login) throws SQLException {
        String sql = "SELECT id,nome,nome_usuario,email,senha_hash FROM dbo.usuarios WHERE nome_usuario=? OR email=?";
        try (Connection c = ConnectionFactory.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login); ps.setString(2, login);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public List<Usuario> listar() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection c = ConnectionFactory.abrir();
             PreparedStatement ps = c.prepareStatement("SELECT id,nome,nome_usuario,email,senha_hash FROM dbo.usuarios ORDER BY nome");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) usuarios.add(mapear(rs));
        }
        return usuarios;
    }

    public void inserir(String nome, String nomeUsuario, String email, String senhaHash) throws SQLException {
        String sql = "INSERT INTO dbo.usuarios(nome,nome_usuario,email,senha_hash) VALUES(?,?,?,?)";
        try (Connection c = ConnectionFactory.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nome); ps.setString(2, nomeUsuario); ps.setString(3, email); ps.setString(4, senhaHash);
            ps.executeUpdate();
        }
    }

    static Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(rs.getLong("id"), rs.getString("nome"), rs.getString("nome_usuario"),
                rs.getString("email"), rs.getString("senha_hash"));
    }
}
