package br.edu.tarefas.view;

import br.edu.tarefas.model.Categoria;
import br.edu.tarefas.model.CategoriaResumo;
import br.edu.tarefas.controller.CategoriaController;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public final class CategoryDialog extends JDialog {
    private final CategoriaController controller;
    private final JTable tabela = new JTable();
    private final JTextField nome = new JTextField();
    private final JTextArea descricao = new JTextArea(4, 20);
    private List<CategoriaResumo> exibidas = List.of();
    private Categoria emEdicao;
    private boolean modificada;

    public CategoryDialog(JFrame owner, CategoriaController controller) {
        super(owner, "Gerenciamento de categorias", true);
        this.controller = controller;
        setSize(850, 560); setMinimumSize(new Dimension(750, 480)); setLocationRelativeTo(owner);

        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
        root.add(Ui.titulo("Categorias"), BorderLayout.NORTH);

        JPanel conteudo = new JPanel(new GridLayout(1, 2, 20, 0));
        JPanel esquerda = new JPanel(new BorderLayout(8, 8));
        esquerda.add(new JLabel("Categorias cadastradas"), BorderLayout.NORTH);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(30);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) carregarSelecionada();
        });
        esquerda.add(new JScrollPane(tabela), BorderLayout.CENTER);
        JButton nova = Ui.botao("Nova", true);
        nova.addActionListener(e -> limparFormulario());
        JButton excluir = Ui.botao("Excluir", false);
        excluir.addActionListener(e -> excluirSelecionada());
        esquerda.add(Ui.linha(nova, excluir), BorderLayout.SOUTH);
        conteudo.add(esquerda);

        JPanel direita = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTHWEST; g.insets = new Insets(6, 0, 6, 0);
        g.gridy = 0; direita.add(new JLabel("Nome"), g);
        g.gridy++; direita.add(nome, g);
        g.gridy++; direita.add(new JLabel("Descrição (opcional)"), g);
        descricao.setLineWrap(true); descricao.setWrapStyleWord(true);
        g.gridy++; g.weighty = 1; g.fill = GridBagConstraints.BOTH;
        direita.add(new JScrollPane(descricao), g);
        JButton cancelar = Ui.botao("Cancelar", false);
        cancelar.addActionListener(e -> limparFormulario());
        JButton salvar = Ui.botao("Salvar", true);
        salvar.addActionListener(e -> salvar());
        g.gridy++; g.weighty = 0; g.fill = GridBagConstraints.HORIZONTAL;
        direita.add(Ui.linha(cancelar, salvar), g);
        conteudo.add(direita);
        root.add(conteudo, BorderLayout.CENTER);
        JLabel aviso = new JLabel("A categoria Geral e categorias com tarefas não podem ser excluídas.");
        aviso.setForeground(new Color(100, 116, 139));
        root.add(aviso, BorderLayout.SOUTH);
        setContentPane(root);
        atualizar();
    }

    public boolean foiModificada() { return modificada; }

    private void atualizar() {
        try {
            exibidas = controller.listar();
            DefaultTableModel modelo = new DefaultTableModel(new String[]{"Nome", "Descrição", "Tarefas"}, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            for (CategoriaResumo item : exibidas) modelo.addRow(new Object[]{item.categoria().nome(),
                    item.categoria().descricao(), item.quantidadeTarefas()});
            tabela.setModel(modelo);
            tabela.getColumnModel().getColumn(2).setMaxWidth(80);
        } catch (Exception e) { Ui.erro(this, e); }
    }

    private void carregarSelecionada() {
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= exibidas.size()) return;
        emEdicao = exibidas.get(row).categoria();
        nome.setText(emEdicao.nome());
        descricao.setText(emEdicao.descricao() == null ? "" : emEdicao.descricao());
    }

    private void limparFormulario() {
        tabela.clearSelection();
        emEdicao = null; nome.setText(""); descricao.setText(""); nome.requestFocusInWindow();
    }

    private void salvar() {
        try {
            if (emEdicao == null) controller.criar(nome.getText(), descricao.getText());
            else controller.editar(emEdicao, nome.getText(), descricao.getText());
            modificada = true; atualizar(); limparFormulario();
        } catch (Exception e) { Ui.erro(this, e); }
    }

    private void excluirSelecionada() {
        if (emEdicao == null) { JOptionPane.showMessageDialog(this, "Selecione uma categoria."); return; }
        if (JOptionPane.showConfirmDialog(this, "Excluir a categoria \"" + emEdicao.nome() + "\"?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try { controller.excluir(emEdicao); modificada = true; atualizar(); limparFormulario(); }
        catch (Exception e) { Ui.erro(this, e); }
    }
}
