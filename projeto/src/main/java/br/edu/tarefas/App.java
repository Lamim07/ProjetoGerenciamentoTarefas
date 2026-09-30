package br.edu.tarefas;

import br.edu.tarefas.controller.AppController;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class App {
    private App() { }
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> new AppController().iniciar());
    }
}
