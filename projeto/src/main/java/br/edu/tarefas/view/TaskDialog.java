package br.edu.tarefas.view;

import br.edu.tarefas.model.*;
import br.edu.tarefas.service.TarefaService;
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
    private final JTextField prazo = new JTextField();
    private final JComboBox<PrioridadeTarefa> prioridade = new JComboBox<>(PrioridadeTarefa.values());
    private final JComboBox<StatusTarefa> status = new JComboBox<>(StatusTarefa.values());
    private boolean salva;

    public TaskDialog(JFrame owner, TarefaService service, Usuario ator, Tarefa tarefa,
                      boolean somenteLeitura) throws Exception {
        super(owner, tarefa == null ? "Nova tarefa" : somenteLeitura ? "Visualizar tarefa" : "Editar tarefa", true);
        setSize(680, 660); setMinimumSize(new Dimension(590, 550)); setLocationRelativeTo(owner);
        List<Usuario> usuarios = service.usuarios();
        for (Usuario u : usuarios) responsavel.addItem(u);
        if (tarefa == null) selecionarUsuario(ator.id());
        else {
            titulo.setText(tarefa.titulo()); descricao.setText(tarefa.descricao());
            selecionarUsuario(tarefa.responsavel().id());
            prazo.setText(DATA.format(tarefa.prazo())); prioridade.setSelectedItem(tarefa.prioridade());
            status.setSelectedItem(tarefa.status());
        }
        boolean criador = tarefa == null || tarefa.criador().id() == ator.id();
        boolean podeStatus = tarefa != null && tarefa.responsavel().id() == ator.id();
        boolean editarDados = !somenteLeitura && criador;
        titulo.setEditable(editarDados); descricao.setEditable(editarDados);
        responsavel.setEnabled(editarDados); prazo.setEditable(editarDados); prioridade.setEnabled(editarDados);
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
                    service.criar(ator, titulo.getText(), descricao.getText(),
                            (Usuario) responsavel.getSelectedItem(), lerPrazo(), (PrioridadeTarefa) prioridade.getSelectedItem());
                } else if (criador) {
                    service.editar(ator, tarefa, titulo.getText(), descricao.getText(),
                            (Usuario) responsavel.getSelectedItem(), lerPrazo(),
                            (PrioridadeTarefa) prioridade.getSelectedItem(), (StatusTarefa) status.getSelectedItem());
                } else {
                    service.alterarStatus(ator, tarefa, (StatusTarefa) status.getSelectedItem());
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

    private static void campo(JPanel p, GridBagConstraints g, String rotulo, JComponent componente) {
        g.gridy++; p.add(new JLabel(rotulo), g);
        g.gridy++; p.add(componente, g);
    }
}
