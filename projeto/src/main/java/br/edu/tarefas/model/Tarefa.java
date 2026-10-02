package br.edu.tarefas.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record Tarefa(long id, String titulo, String descricao, Usuario criador,
                     Usuario responsavel, Categoria categoria, LocalDate prazo, PrioridadeTarefa prioridade,
                     StatusTarefa status, LocalDateTime criadaEm) { }
