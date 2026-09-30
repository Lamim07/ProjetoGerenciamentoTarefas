package br.edu.tarefas.model;

public enum PrioridadeTarefa {
    BAIXA("Baixa"), MEDIA("Média"), ALTA("Alta");
    private final String rotulo;
    PrioridadeTarefa(String rotulo) { this.rotulo = rotulo; }
    @Override public String toString() { return rotulo; }
}
