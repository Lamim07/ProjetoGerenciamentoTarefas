package br.edu.tarefas;

import br.edu.tarefas.util.Senhas;

public final class TesteBasico {
    public static void main(String[] args) throws Exception {
        String hash = Senhas.gerar("senhaDeTeste123".toCharArray());
        if (!Senhas.verificar("senhaDeTeste123".toCharArray(), hash))
            throw new AssertionError("Senha correta rejeitada.");
        if (Senhas.verificar("senhaIncorreta".toCharArray(), hash))
            throw new AssertionError("Senha incorreta aceita.");
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        System.out.println("Teste básico aprovado: senhas e driver JDBC.");
    }
}
