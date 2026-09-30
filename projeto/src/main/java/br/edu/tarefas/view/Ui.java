package br.edu.tarefas.view;

import java.awt.*;
import javax.swing.*;

final class Ui {
    static final Color AZUL = new Color(49, 93, 220);
    static final Color ESCURO = new Color(32, 48, 71);
    static final Color FUNDO = new Color(238, 242, 248);
    private Ui() { }

    static JButton botao(String texto, boolean primario) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);
        if (primario) { b.setBackground(AZUL); b.setForeground(Color.WHITE); }
        return b;
    }

    static JLabel titulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 25));
        label.setForeground(ESCURO);
        return label;
    }

    static JPanel linha(JComponent... componentes) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        p.setOpaque(false);
        for (JComponent componente : componentes) p.add(componente);
        return p;
    }

    static void erro(Component parent, Exception e) {
        JOptionPane.showMessageDialog(parent, e.getMessage(), "Atenção", JOptionPane.ERROR_MESSAGE);
    }
}
