package br.edu.tarefas.view;

import br.edu.tarefas.controller.AppController;
import java.awt.*;
import javax.swing.*;

public final class RegisterDialog extends JDialog {
    private final JTextField nome = new JTextField();
    private final JTextField usuario = new JTextField();
    private final JTextField email = new JTextField();
    private final JPasswordField senha = new JPasswordField();
    private final JPasswordField confirmacao = new JPasswordField();

    public RegisterDialog(JFrame owner, AppController controller) {
        super(owner, "Cadastro de usuário", true);
        setSize(600, 480); setLocationRelativeTo(owner);
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(5, 0, 5, 0);
        g.gridy = 0; form.add(Ui.titulo("Criar conta"), g);
        campo(form, g, "Nome completo", nome);
        campo(form, g, "Nome de usuário", usuario);
        campo(form, g, "E-mail", email);
        campo(form, g, "Senha", senha);
        campo(form, g, "Confirmar senha", confirmacao);
        JButton voltar = Ui.botao("Voltar", false); voltar.addActionListener(e -> dispose());
        JButton cadastrar = Ui.botao("Cadastrar", true);
        cadastrar.addActionListener(e -> {
            try {
                controller.autenticacao().cadastrar(nome.getText(), usuario.getText(), email.getText(),
                        senha.getPassword(), confirmacao.getPassword());
                JOptionPane.showMessageDialog(this, "Cadastro concluído. Faça login para continuar.");
                dispose();
            } catch (Exception ex) {
                senha.setText(""); confirmacao.setText(""); Ui.erro(this, ex);
            }
        });
        g.gridy++; form.add(Ui.linha(voltar, cadastrar), g);
        add(new JScrollPane(form));
    }

    private static void campo(JPanel p, GridBagConstraints g, String rotulo, JComponent componente) {
        g.gridy++; p.add(new JLabel(rotulo), g);
        g.gridy++; p.add(componente, g);
    }
}
