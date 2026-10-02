package br.edu.tarefas.model;

public record Categoria(long id, String nome, String descricao) {
    @Override public String toString() { return nome; }
}
