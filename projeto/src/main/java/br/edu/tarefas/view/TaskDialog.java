package br.edu.tarefas.view;

import br.edu.tarefas.model.*;
import br.edu.tarefas.controller.TarefaController;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import javax.swing.*;

public final class TaskDialog extends JDialog {
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private final JTextField titulo = new JTextField();
    private final JTextArea descricao = new JTextArea(5, 30);
    private final JComboBox<Usuario> responsavel = new JComboBox<>();
    private final JComboBox<Categoria> categoria = new JComboBox<>();
    private final JTextField prazo = new JTextField();
    private final JComboBox<PrioridadeTarefa> prioridade = new JComboBox<>(PrioridadeTarefa.values());
    private final JComboBox<StatusTarefa> status = new JComboBox<>(StatusTarefa.values());
    private boolean salva;

    public TaskDialog(JFrame owner, TarefaController controller, Usuario ator, Tarefa tarefa,
                      boolean somenteLeitura) throws Exception {
        super(owner, tarefa == null ? "Nova tarefa" : somenteLeitura ? "Visualizar tarefa" : "Editar tarefa", true);
        setSize(700, 700); setMinimumSize(new Dimension(590, 600)); setLocationRelativeTo(owner);
        List<Usuario> usuarios = controller.usuarios();
        for (Usuario u : usuarios) responsavel.addItem(u);
        List<Categoria> categorias = controller.categorias();
        for (Categoria c : categorias) categoria.addItem(c);
        if (tarefa == null) {
            selecionarUsuario(ator.id());
            categorias.stream().filter(c -> "Geral".equalsIgnoreCase(c.nome())).findFirst()
                    .ifPresent(c -> selecionarCategoria(c.id()));
        }
        else {
            titulo.setText(tarefa.titulo()); descricao.setText(tarefa.descricao());
            selecionarUsuario(tarefa.responsavel().id());
            selecionarCategoria(tarefa.categoria().id());
            prazo.setText(DATA.format(tarefa.prazo())); prioridade.setSelectedItem(tarefa.prioridade());
            status.setSelectedItem(tarefa.status());
        }
        boolean criador = tarefa == null || tarefa.criador().id() == ator.id();
        boolean podeStatus = tarefa != null && tarefa.responsavel().id() == ator.id();
        boolean editarDados = !somenteLeitura && criador;
        titulo.setEditable(editarDados); descricao.setEditable(editarDados);
        responsavel.setEnabled(editarDados); categoria.setEnabled(editarDados);
        prazo.setEditable(editarDados); prioridade.setEnabled(editarDados);
        status.setEnabled(!somenteLeitura && podeStatus);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(5, 0, 5, 0);
        g.gridy = 0; form.add(Ui.titulo(tarefa == null ? "Nova tarefa" : "Detalhes da tarefa"), g);
        campo(form, g, "Título", titulo);
        descricao.setLineWrap(true); descricao.setWrapStyleWord(true);
        campo(form, g, "Descrição", new JScrollPane(descricao));
        campo(form, g, "Responsável / destinatário", responsavel);
        campo(form, g, "Categoria", categoria);
        campo(form, g, "Prazo (dd/mm/aaaa)", prazo);
        campo(form, g, "Prioridade", prioridade);
        campo(form, g, "Status", status);
        JButton cancelar = Ui.botao(somenteLeitura ? "Fechar" : "Cancelar", false);
        cancelar.addActionListener(e -> dispose());
        JButton salvar = Ui.botao("Salvar", true);
        salvar.setVisible(!somenteLeitura && (criador || podeStatus));
        salvar.addActionListener(e -> {
            try {
                if (tarefa == null) {
                    controller.criar(titulo.getText(), descricao.getText(),
                            (Usuario) responsavel.getSelectedItem(), (Categoria) categoria.getSelectedItem(),
                            lerPrazo(), (PrioridadeTarefa) prioridade.getSelectedItem());
                } else if (criador) {
                    controller.editar(tarefa, titulo.getText(), descricao.getText(),
                            (Usuario) responsavel.getSelectedItem(), (Categoria) categoria.getSelectedItem(), lerPrazo(),
                            (PrioridadeTarefa) prioridade.getSelectedItem(), (StatusTarefa) status.getSelectedItem());
                } else {
                    controller.alterarStatus(tarefa, (StatusTarefa) status.getSelectedItem());
                }
                salva = true; dispose();
            } catch (Exception ex) { Ui.erro(this, ex); }
        });
        g.gridy++; form.add(Ui.linha(cancelar, salvar), g);
        add(new JScrollPane(form));
    }

    public boolean foiSalva() { return salva; }

    private LocalDate lerPrazo() {
        try { return LocalDate.parse(prazo.getText().trim(), DATA); }
        catch (DateTimeParseException e) { throw new IllegalArgumentException("Informe o prazo no formato dd/mm/aaaa."); }
    }

    private void selecionarUsuario(long id) {
        for (int i = 0; i < responsavel.getItemCount(); i++) {
            if (responsavel.getItemAt(i).id() == id) { responsavel.setSelectedIndex(i); return; }
        }
    }

    private void selecionarCategoria(long id) {
        for (int i = 0; i < categoria.getItemCount(); i++) {
            if (categoria.getItemAt(i).id() == id) { categoria.setSelectedIndex(i); return; }
        }
    }

    private static void campo(JPanel p, GridBagConstraints g, String rotulo, JComponent componente) {
        g.gridy++; p.add(new JLabel(rotulo), g);
        g.gridy++; p.add(componente, g);
    }
}
