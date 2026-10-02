package br.edu.tarefas.view;

import br.edu.tarefas.model.ComentarioTarefa;
import br.edu.tarefas.model.Tarefa;
import br.edu.tarefas.model.Usuario;
import br.edu.tarefas.controller.ComentarioController;
import br.edu.tarefas.controller.TarefaController;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public final class TaskDetailsDialog extends JDialog {
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final JFrame owner;
    private final TarefaController tarefas;
    private final ComentarioController comentarios;
    private final Usuario usuario;
    private Tarefa tarefa;
    private final JLabel titulo = Ui.titulo("");
    private final JLabel metadados = new JLabel();
    private final JLabel categoria = new JLabel();
    private final JLabel prioridade = new JLabel();
    private final JLabel status = new JLabel();
    private final JTextArea descricao = new JTextArea();
    private final JTextArea novoComentario = new JTextArea(3, 25);
    private final JPanel listaComentarios = new JPanel();
    private boolean tarefaAlterada;

    public TaskDetailsDialog(JFrame owner, TarefaController tarefas, ComentarioController comentarios,
                             Usuario usuario, Tarefa tarefa) throws Exception {
        super(owner, "Detalhes e comentários", true);
        this.owner = owner; this.tarefas = tarefas; this.comentarios = comentarios;
        this.usuario = usuario; this.tarefa = tarefas.buscar(tarefa.id());
        setSize(1000, 640); setMinimumSize(new Dimension(850, 540)); setLocationRelativeTo(owner);

        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
        JPanel cabecalho = new JPanel(new BorderLayout(0, 8));
        cabecalho.add(titulo, BorderLayout.NORTH);
        metadados.setForeground(new Color(100, 116, 139));
        cabecalho.add(metadados, BorderLayout.SOUTH);
        root.add(cabecalho, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(1, 2, 20, 0));
        JPanel detalhes = new JPanel(new BorderLayout(8, 8));
        detalhes.add(new JLabel("Descrição"), BorderLayout.NORTH);
        descricao.setEditable(false); descricao.setLineWrap(true); descricao.setWrapStyleWord(true);
        descricao.setBackground(Color.WHITE);
        detalhes.add(new JScrollPane(descricao), BorderLayout.CENTER);
        JPanel dados = new JPanel(new GridLayout(3, 1, 0, 6));
        dados.add(categoria); dados.add(prioridade); dados.add(status);
        JButton editar = Ui.botao(this.tarefa.criador().id() == usuario.id()
                ? "Editar / alterar status" : "Alterar status", true);
        editar.addActionListener(e -> editarTarefa());
        JPanel rodapeDetalhes = new JPanel(new BorderLayout(0, 12));
        rodapeDetalhes.add(dados, BorderLayout.CENTER);
        rodapeDetalhes.add(editar, BorderLayout.SOUTH);
        detalhes.add(rodapeDetalhes, BorderLayout.SOUTH);
        centro.add(detalhes);

        JPanel conversa = new JPanel(new BorderLayout(8, 8));
        conversa.add(new JLabel("Comentários"), BorderLayout.NORTH);
        listaComentarios.setLayout(new BoxLayout(listaComentarios, BoxLayout.Y_AXIS));
        listaComentarios.setBackground(Color.WHITE);
        conversa.add(new JScrollPane(listaComentarios), BorderLayout.CENTER);
        novoComentario.setLineWrap(true); novoComentario.setWrapStyleWord(true);
        JPanel publicar = new JPanel(new BorderLayout(6, 6));
        publicar.add(new JLabel("Novo comentário"), BorderLayout.NORTH);
        publicar.add(new JScrollPane(novoComentario), BorderLayout.CENTER);
        JButton enviar = Ui.botao("Publicar", true);
        enviar.addActionListener(e -> publicar());
        publicar.add(enviar, BorderLayout.SOUTH);
        conversa.add(publicar, BorderLayout.SOUTH);
        centro.add(conversa);
        root.add(centro, BorderLayout.CENTER);
        JButton fechar = Ui.botao("Fechar", false); fechar.addActionListener(e -> dispose());
        root.add(Ui.linha(fechar), BorderLayout.SOUTH);
        setContentPane(root);
        atualizarDados(); atualizarComentarios();
    }

    public boolean tarefaFoiAlterada() { return tarefaAlterada; }

    private void atualizarDados() {
        titulo.setText(tarefa.titulo());
        metadados.setText("Criada por " + tarefa.criador().nome() + "  ·  Responsável: "
                + tarefa.responsavel().nome() + "  ·  Prazo: " + DATA.format(tarefa.prazo()));
        descricao.setText(tarefa.descricao()); descricao.setCaretPosition(0);
        categoria.setText("Categoria: " + tarefa.categoria().nome());
        prioridade.setText("Prioridade: " + tarefa.prioridade());
        status.setText("Status: " + tarefa.status());
    }

    private void atualizarComentarios() {
        try {
            listaComentarios.removeAll();
            var itens = comentarios.listar(tarefa);
            if (itens.isEmpty()) listaComentarios.add(new JLabel("Ainda não há comentários."));
            for (ComentarioTarefa item : itens) {
                JPanel cartao = new JPanel(new BorderLayout(4, 5));
                cartao.setBackground(Color.WHITE);
                cartao.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 227, 237)),
                        BorderFactory.createEmptyBorder(10, 5, 10, 5)));
                JLabel autor = new JLabel(item.autor().nome() + "  ·  " + DATA_HORA.format(item.criadoEm()));
                autor.setForeground(new Color(82, 100, 125));
                JPanel topo = new JPanel(new BorderLayout()); topo.setOpaque(false);
                topo.add(autor, BorderLayout.WEST);
                if (item.autor().id() == usuario.id()) {
                    JButton excluir = Ui.botao("Excluir", false);
                    excluir.addActionListener(e -> excluir(item));
                    topo.add(excluir, BorderLayout.EAST);
                }
                cartao.add(topo, BorderLayout.NORTH);
                JTextArea texto = new JTextArea(item.conteudo());
                texto.setEditable(false); texto.setLineWrap(true); texto.setWrapStyleWord(true);
                texto.setRows(2); texto.setColumns(28); texto.setFocusable(false);
                texto.setBackground(Color.WHITE);
                cartao.add(texto, BorderLayout.CENTER);
                cartao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
                listaComentarios.add(cartao);
            }
            listaComentarios.revalidate(); listaComentarios.repaint();
        } catch (Exception e) { Ui.erro(this, e); }
    }

    private void publicar() {
        try {
            comentarios.publicar(tarefa, novoComentario.getText());
            novoComentario.setText(""); atualizarComentarios();
        } catch (Exception e) { Ui.erro(this, e); }
    }

    private void excluir(ComentarioTarefa item) {
        if (JOptionPane.showConfirmDialog(this, "Excluir este comentário?", "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try { comentarios.excluir(item); atualizarComentarios(); }
        catch (Exception e) { Ui.erro(this, e); }
    }

    private void editarTarefa() {
        try {
            TaskDialog dialog = new TaskDialog(owner, tarefas, usuario, tarefa, false);
            dialog.setVisible(true);
            if (dialog.foiSalva()) {
                tarefa = tarefas.buscar(tarefa.id());
                tarefaAlterada = true; atualizarDados(); atualizarComentarios();
            }
        } catch (Exception e) { Ui.erro(this, e); }
    }
}
