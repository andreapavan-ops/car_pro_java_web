package gui;

import util.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private SchermataCatalogo schermataCatalogo;
    private SchermataInventario schermataInventario;
    private SchermataOrdini schermataOrdini;
    private SchermataStatistiche schermataStatistiche;
    
    public MainFrame() {
        setTitle("CarPro - Gestionale Concessionaria");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Layout principale
        setLayout(new BorderLayout());
        
        // Sidebar a sinistra
        add(creaSidebar(), BorderLayout.WEST);
        
        // Pannello contenuto centrale
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);
        
        // Mostra catalogo di default
        mostraCatalogo();
    }
    
    private JPanel creaSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(45, 52, 54));
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        
        // Logo/Titolo
        JLabel logo = new JLabel("CarPro");
        logo.setFont(new Font("Arial", Font.BOLD, 28));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(30, 15, 50, 0));
        sidebar.add(logo);
        
        // Pulsanti menu
        sidebar.add(creaBottoneSidebar("🚗 Catalogo Auto", e -> mostraCatalogo()));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(creaBottoneSidebar("📦 Inventario", e -> mostraInventario()));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(creaBottoneSidebar("🛒 Ordini Fornitori", e -> mostraOrdini()));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(creaBottoneSidebar("📊 Vendite", e -> mostraVendite()));
        
        sidebar.add(Box.createVerticalGlue());
        
        return sidebar;
    }
    
    private JButton creaBottoneSidebar(String text, ActionListener action) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(200, 70));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setFont(new Font("Arial", Font.PLAIN, 18));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(99, 110, 114));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 0));
        btn.addActionListener(action);
        
        // Effetto hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(129, 140, 144));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(99, 110, 114));
            }
        });
        
        return btn;
    }
    
    private void mostraCatalogo() {
        contentPanel.removeAll();
        if (schermataCatalogo == null) {
            schermataCatalogo = new SchermataCatalogo();
        }
        contentPanel.add(schermataCatalogo, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void mostraInventario() {
        contentPanel.removeAll();
        if (schermataInventario == null) {
            schermataInventario = new SchermataInventario();
        }
        contentPanel.add(schermataInventario, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void mostraOrdini() {
        contentPanel.removeAll();
        if (schermataOrdini == null) {
            schermataOrdini = new SchermataOrdini();
        }
        contentPanel.add(schermataOrdini, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void mostraVendite() {
        contentPanel.removeAll();
        if (schermataStatistiche == null) {
            schermataStatistiche = new SchermataStatistiche();
        }
        contentPanel.add(schermataStatistiche, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    public static void main(String[] args) {
        // Inizializza il database prima di avviare l'applicazione
        System.out.println("=== AVVIO CARPRO ===");
        System.out.println("Inizializzazione database...");
        DatabaseConnection.inizializzaDatabase();
        
        // Avvia l'interfaccia grafica
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
            System.out.println("✅ CarPro avviato con successo!");
        });
    }
}