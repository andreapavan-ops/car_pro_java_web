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
    private SchermataImmagini schermataImmagini; // ✅ NUOVO
    
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
        sidebar.setBackground(new Color(45, 52, 54, 204)); // 20% più trasparente (255 - 51 = 204)
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        
        // Logo/Titolo
        JLabel logo = new JLabel("CarPro");
        logo.setFont(new Font("Arial", Font.BOLD, 28));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(15, 20, 30, 0));
        sidebar.add(logo);
        
        // Pulsanti menu
        sidebar.add(creaBottoneSidebar("\uD83D\uDE97", "Catalogo Auto", e -> mostraCatalogo()));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(creaBottoneSidebar("\uD83D\uDCE6", "Inventario", e -> mostraInventario()));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(creaBottoneSidebar("\uD83D\uDED2", "Ordini Fornitori", e -> mostraOrdini()));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(creaBottoneSidebar("\uD83D\uDCCA", "Vendite", e -> mostraVendite()));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(creaBottoneSidebar("\uD83D\uDDBC", "Immagini", e -> mostraImmagini()));
        
        sidebar.add(Box.createVerticalGlue());

        // Logo in basso a sinistra
        JLabel lblLogo = new JLabel();
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblLogo.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 0));
        java.io.File fileLogo = new java.io.File("resources/images/logo.jpeg");
        if (fileLogo.exists()) {
            ImageIcon icon = new ImageIcon(fileLogo.getAbsolutePath());
            // Ridimensiona il logo a 80x60 mantenendo le proporzioni
            int maxW = 80;
            int maxH = 60;
            int imgW = icon.getIconWidth();
            int imgH = icon.getIconHeight();
            double ratio = Math.min((double) maxW / imgW, (double) maxH / imgH);
            int newW = (int) (imgW * ratio);
            int newH = (int) (imgH * ratio);
            Image img = icon.getImage().getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(img));
        }
        sidebar.add(lblLogo);

        return sidebar;
    }
    
    private JPanel creaBottoneSidebar(String icona, String testo, ActionListener action) {
        // Pannello contenitore per l'ombra
        JPanel containerPanel = new JPanel() {
            private boolean showShadow = false;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (showShadow) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Ombra sfumata sotto e a destra
                    int shadowSize = 4;
                    for (int i = 0; i < shadowSize; i++) {
                        int alpha = 50 - (i * 12);
                        if (alpha < 0) alpha = 0;
                        g2d.setColor(new Color(0, 0, 0, alpha));
                        g2d.fillRoundRect(i + 2, i + 2, getWidth() - i - 2, getHeight() - i - 2, 5, 5);
                    }
                    g2d.dispose();
                }
            }

            @SuppressWarnings("unused")
            public void setShadow(boolean show) {
                this.showShadow = show;
                repaint();
            }
        };
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.X_AXIS));
        containerPanel.setBackground(new Color(45, 52, 54, 204));
        containerPanel.setOpaque(false);
        containerPanel.setMaximumSize(new Dimension(200, 50));
        containerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        containerPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 5));
        containerPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Quadrato con icona (stesso colore sidebar)
        Color coloreSidebar = new Color(45, 52, 54);
        JLabel lblIcona = new JLabel(icona);
        lblIcona.setOpaque(true);
        lblIcona.setBackground(coloreSidebar);
        lblIcona.setForeground(Color.WHITE);
        lblIcona.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lblIcona.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcona.setPreferredSize(new Dimension(35, 35));
        lblIcona.setMinimumSize(new Dimension(35, 35));
        lblIcona.setMaximumSize(new Dimension(35, 35));

        // Testo
        JLabel lblTesto = new JLabel(testo);
        lblTesto.setForeground(Color.WHITE);
        lblTesto.setFont(new Font("Arial", Font.BOLD, 14));
        lblTesto.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        containerPanel.add(lblIcona);
        containerPanel.add(lblTesto);

        containerPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                action.actionPerformed(null);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Sottolineatura testo
                lblTesto.setText("<html><u>" + testo + "</u></html>");
                // Attiva ombra sfumata
                try {
                    java.lang.reflect.Method m = containerPanel.getClass().getMethod("setShadow", boolean.class);
                    m.invoke(containerPanel, true);
                } catch (Exception e) {}
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                // Rimuovi sottolineatura
                lblTesto.setText(testo);
                // Disattiva ombra
                try {
                    java.lang.reflect.Method m = containerPanel.getClass().getMethod("setShadow", boolean.class);
                    m.invoke(containerPanel, false);
                } catch (Exception e) {}
            }
        });

        return containerPanel;
    }
    
    private void mostraCatalogo() {
        contentPanel.removeAll();
        if (schermataCatalogo == null) {
            schermataCatalogo = new SchermataCatalogo();
        } else {
            schermataCatalogo.refresh(); // Aggiorna dati dal database
        }
        contentPanel.add(schermataCatalogo, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void mostraInventario() {
        contentPanel.removeAll();
        if (schermataInventario == null) {
            schermataInventario = new SchermataInventario();
        } else {
            schermataInventario.refresh(); // Aggiorna dati dal database
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
        } else {
            schermataStatistiche.refresh(); // Aggiorna dati dal database
        }
        contentPanel.add(schermataStatistiche, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void mostraImmagini() {
        contentPanel.removeAll();
        if (schermataImmagini == null) {
            schermataImmagini = new SchermataImmagini();
        }
        contentPanel.add(schermataImmagini, BorderLayout.CENTER);
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