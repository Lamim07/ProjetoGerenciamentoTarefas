package br.edu.tarefas.model;

public enum StatusTarefa {
    PENDENTE("Pendente"), EM_ANDAMENTO("Em andamento"), CONCLUIDA("Concluída");
    private final String rotulo;
    StatusTarefa(String rotulo) { this.rotulo = rotulo; }
    @Override public String toString() { return rotulo; }
}
