package br.edu.tarefas.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionFactory {
    private ConnectionFactory() { }

    public static Connection abrir() throws SQLException {
        String url = System.getenv().getOrDefault("TAREFAS_DB_URL",
                "jdbc:sqlserver://localhost:1433;databaseName=GerenciamentoTarefas;encrypt=true;trustServerCertificate=true");
        String usuario = System.getenv("TAREFAS_DB_USER");
        String senha = System.getenv("TAREFAS_DB_PASSWORD");
        if (usuario == null || usuario.isBlank()) {
            throw new SQLException("Defina TAREFAS_DB_USER e TAREFAS_DB_PASSWORD para acessar o SQL Server.");
        }
        return DriverManager.getConnection(url, usuario, senha == null ? "" : senha);
    }
}
