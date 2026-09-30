package br.edu.tarefas.model;

public record Usuario(long id, String nome, String nomeUsuario, String email, String senhaHash) {
    @Override public String toString() { return nome + " (" + nomeUsuario + ")"; }
}
