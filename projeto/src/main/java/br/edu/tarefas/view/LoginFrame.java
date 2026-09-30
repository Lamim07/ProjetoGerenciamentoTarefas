package br.edu.tarefas.view;

import br.edu.tarefas.controller.AppController;
import java.awt.*;
import javax.swing.*;

public final class LoginFrame extends JFrame {
    private final JTextField login = new JTextField();
    private final JPasswordField senha = new JPasswordField();
    private final JLabel mensagem = new JLabel(" ");

    public LoginFrame(AppController controller) {
        super("Gerenciamento de Tarefas — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 560); setLocationRelativeTo(null); setMinimumSize(new Dimension(740, 500));
        JPanel root = new JPanel(new GridLayout(1, 2));
        JPanel marca = new JPanel(new GridBagLayout());
        marca.setBackground(new Color(36, 58, 112));
        JLabel nome = new JLabel("<html><center><h1>TAREFAS</h1>Cadastro e gerenciamento<br><br>Organize, atribua e acompanhe.</center></html>");
        nome.setForeground(Color.WHITE); nome.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        marca.add(nome); root.add(marca);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(8, 42, 8, 42);
        g.gridy = 0; form.add(Ui.titulo("Bem-vindo de volta"), g);
        g.gridy++; form.add(new JLabel("Entre com sua conta para continuar."), g);
        g.gridy++; form.add(new JLabel("Usuário ou e-mail"), g);
        g.gridy++; form.add(login, g);
        g.gridy++; form.add(new JLabel("Senha"), g);
        g.gridy++; form.add(senha, g);
        mensagem.setForeground(new Color(163, 51, 58));
        g.gridy++; form.add(mensagem, g);
        JButton entrar = Ui.botao("Entrar", true);
        entrar.addActionListener(e -> {
            try { controller.entrar(controller.autenticacao().entrar(login.getText(), senha.getPassword())); }
            catch (Exception ex) { senha.setText(""); mensagem.setText(ex.getMessage()); }
        });
        getRootPane().setDefaultButton(entrar);
        g.gridy++; form.add(entrar, g);
        JButton cadastrar = Ui.botao("Cadastrar usuário", false);
        cadastrar.addActionListener(e -> new RegisterDialog(this, controller).setVisible(true));
        g.gridy++; form.add(cadastrar, g);
        root.add(form); setContentPane(root);
    }
}
