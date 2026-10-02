package br.edu.tarefas.model;

import java.time.LocalDateTime;

public record ComentarioTarefa(long id, Tarefa tarefa, Usuario autor,
                              String conteudo, LocalDateTime criadoEm) { }
