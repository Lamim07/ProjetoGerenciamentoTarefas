package br.edu.tarefas.view;

import br.edu.tarefas.controller.AppController;
import br.edu.tarefas.model.Tarefa;
import br.edu.tarefas.model.Usuario;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public final class MainFrame extends JFrame {
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final AppController controller;
    private final Usuario usuario;
    private final JLabel titulo = Ui.titulo("Visão geral");
    private final JPanel centro = new JPanel(new BorderLayout(12, 12));
    private final JTable tabela = new JTable();
    private List<Tarefa> exibidas = List.of();
    private boolean criadas;

    public MainFrame(AppController controller, Usuario usuario) {
        super("Gerenciamento de Tarefas");
        this.controller = controller; this.usuario = usuario;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720); setMinimumSize(new Dimension(900, 580)); setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout()); root.setBackground(Ui.FUNDO);
        JPanel topo = new JPanel(new BorderLayout()); topo.setBackground(Color.WHITE);
        topo.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));
        JLabel marca = new JLabel("TAREFAS"); marca.setFont(new Font("Segoe UI", Font.BOLD, 22));
        marca.setForeground(Ui.AZUL); topo.add(marca, BorderLayout.WEST);
        JButton sair = Ui.botao("Sair", false); sair.addActionListener(e -> controller.sair());
        topo.add(Ui.linha(new JLabel(usuario.nome() + " · " + usuario.nomeUsuario()), sair), BorderLayout.EAST);
        root.add(topo, BorderLayout.NORTH);

        JPanel nav = new JPanel(); nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(new Color(245, 247, 251));
        nav.setBorder(BorderFactory.createEmptyBorder(30, 20, 0, 20));
        nav.setPreferredSize(new Dimension(230, 0));
        nav.add(new JLabel("NAVEGAÇÃO")); nav.add(Box.createVerticalStrut(20));
        nav.add(navButton("Visão geral", this::visaoGeral));
        nav.add(Box.createVerticalStrut(10));
        nav.add(navButton("Tarefas recebidas", () -> listar(false)));
        nav.add(Box.createVerticalStrut(10));
        nav.add(navButton("Tarefas criadas", () -> listar(true)));
        root.add(nav, BorderLayout.WEST);
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) abrirTarefa(selecionada(), true);
            }
        });
        centro.setBackground(Color.WHITE);
        centro.setBorder(BorderFactory.createEmptyBorder(26, 28, 26, 28));
        root.add(centro, BorderLayout.CENTER);
        setContentPane(root); visaoGeral();
    }

    private JButton navButton(String texto, Runnable acao) {
        JButton b = Ui.botao(texto, false);
        b.setMaximumSize(new Dimension(190, 42));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.addActionListener(e -> acao.run());
        return b;
    }

    private void visaoGeral() {
        centro.removeAll(); titulo.setText("Visão geral");
        centro.add(titulo, BorderLayout.NORTH);
        JPanel cards = new JPanel(new GridLayout(2, 2, 18, 18)); cards.setOpaque(false);
        JButton recebidas = Ui.botao("Tarefas recebidas  →", false);
        recebidas.addActionListener(e -> listar(false));
        JButton criadasBotao = Ui.botao("Tarefas criadas  →", false);
        criadasBotao.addActionListener(e -> listar(true));
        JButton nova = Ui.botao("+ Nova tarefa", true);
        nova.addActionListener(e -> abrirTarefa(null, false));
        cards.add(recebidas); cards.add(criadasBotao); cards.add(nova);
        cards.add(new JLabel("Organize, atribua e acompanhe suas tarefas."));
        centro.add(cards, BorderLayout.CENTER); atualizarTela();
    }

    private void listar(boolean criadas) {
        this.criadas = criadas;
        try { exibidas = criadas ? controller.tarefas().criadas(usuario) : controller.tarefas().recebidas(usuario); }
        catch (Exception ex) { Ui.erro(this, ex); return; }
        centro.removeAll();
        JPanel cabecalho = new JPanel(new BorderLayout()); cabecalho.setOpaque(false);
        titulo.setText(criadas ? "Tarefas criadas" : "Tarefas recebidas");
        cabecalho.add(titulo, BorderLayout.WEST);
        JButton nova = Ui.botao("+ Nova tarefa", true);
        nova.addActionListener(e -> abrirTarefa(null, false));
        cabecalho.add(nova, BorderLayout.EAST); centro.add(cabecalho, BorderLayout.NORTH);

        String[] colunas = {"Título", "Responsável", "Criador", "Prazo", "Prioridade", "Status"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        for (Tarefa t : exibidas) modelo.addRow(new Object[]{t.titulo(), t.responsavel().nome(),
                t.criador().nome(), DATA.format(t.prazo()), t.prioridade(), t.status()});
        tabela.setModel(modelo); tabela.setRowHeight(32); tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setAutoCreateRowSorter(true);
        tabela.getTableHeader().setReorderingAllowed(false);
        centro.add(new JScrollPane(tabela), BorderLayout.CENTER);
        JButton ver = Ui.botao("Visualizar", false); ver.addActionListener(e -> abrirTarefa(selecionada(), true));
        JButton editar = Ui.botao("Editar / status", false); editar.addActionListener(e -> abrirTarefa(selecionada(), false));
        JButton excluir = Ui.botao("Excluir", false); excluir.addActionListener(e -> excluirSelecionada());
        JButton atualizar = Ui.botao("Atualizar", false); atualizar.addActionListener(e -> listar(this.criadas));
        JPanel acoes = Ui.linha(ver, editar, excluir, atualizar);
        acoes.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        centro.add(acoes, BorderLayout.SOUTH); atualizarTela();
    }

    private Tarefa selecionada() {
        int row = tabela.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione uma tarefa."); return null; }
        return exibidas.get(tabela.convertRowIndexToModel(row));
    }

    private void abrirTarefa(Tarefa tarefa, boolean somenteLeitura) {
        if (tarefa == null && somenteLeitura) return;
        try {
            TaskDialog dialog = new TaskDialog(this, controller.tarefas(), usuario, tarefa, somenteLeitura);
            dialog.setVisible(true);
            if (dialog.foiSalva()) listar(criadas);
        } catch (Exception ex) { Ui.erro(this, ex); }
    }

    private void excluirSelecionada() {
        Tarefa t = selecionada(); if (t == null) return;
        if (t.criador().id() != usuario.id()) {
            JOptionPane.showMessageDialog(this, "Somente o criador pode excluir a tarefa."); return;
        }
        if (JOptionPane.showConfirmDialog(this, "Excluir a tarefa \"" + t.titulo() + "\"?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try { controller.tarefas().excluir(usuario, t); listar(criadas); }
        catch (Exception ex) { Ui.erro(this, ex); }
    }

    private void atualizarTela() { centro.revalidate(); centro.repaint(); }
}
