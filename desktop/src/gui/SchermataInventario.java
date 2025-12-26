package gui;

import dao.AutoDAO;
import dao.MovimentoDAO;
import model.Auto;
import model.Movimento;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SchermataInventario extends JPanel {
    private JTable tableGiacenze;
    private JTable tableMovimenti;
    private DefaultTableModel modelGiacenze;
    private DefaultTableModel modelMovimenti;
    private AutoDAO autoDAO;
    private MovimentoDAO movimentoDAO;

    public SchermataInventario() {
        autoDAO = new AutoDAO();
        movimentoDAO = new MovimentoDAO();

        setLayout(new BorderLayout(0, 0));
        setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 146, 70));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titolo = new JLabel("Inventario Auto");
        titolo.setFont(new Font("Arial", Font.BOLD, 28));
        titolo.setForeground(Color.WHITE);

        headerPanel.add(titolo, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);
        
        // Split panel per dividere giacenze e movimenti
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);
        
        // Pannello giacenze (sopra)
        JPanel panelGiacenze = creaPannelloGiacenze();
        splitPane.setTopComponent(panelGiacenze);
        
        // Pannello movimenti (sotto)
        JPanel panelMovimenti = creaPannelloMovimenti();
        splitPane.setBottomComponent(panelMovimenti);

        add(splitPane, BorderLayout.CENTER);

        // Footer con pulsanti stile Catalogo
        JPanel panelPulsanti = new JPanel(new GridLayout(1, 3, 0, 0));
        panelPulsanti.setBackground(new Color(206, 43, 55)); // Rosso bandiera italiana
        panelPulsanti.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        Color coloreSidebar = new Color(45, 52, 54);
        JPanel btnCarico = creaBottoneConIcona("+", "Carico", coloreSidebar, e -> mostraDialogMovimento("CARICO"));
        JPanel btnScarico = creaBottoneConIcona("-", "Scarico", coloreSidebar, e -> mostraDialogMovimento("SCARICO"));
        JPanel btnAggiorna = creaBottoneConIcona("🔄", "Aggiorna", coloreSidebar, e -> {
            caricaGiacenze();
            caricaMovimenti();
                    });

        panelPulsanti.add(btnCarico);
        panelPulsanti.add(btnScarico);
        panelPulsanti.add(btnAggiorna);

        add(panelPulsanti, BorderLayout.SOUTH);

        // Carica dati iniziali
        caricaGiacenze();
        caricaMovimenti();
            }
    
    private JPanel creaPannelloGiacenze() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Titolo sezione
        JLabel lblTitolo = new JLabel("📊 Stato Giacenze");
        lblTitolo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitolo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(lblTitolo, BorderLayout.NORTH);
        
        // Tabella giacenze
        String[] colonneGiacenze = {"ID", "Marca", "Modello", "Giacenza", "Scorta Min", "Stato"};
        modelGiacenze = new DefaultTableModel(colonneGiacenze, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableGiacenze = new JTable(modelGiacenze);
        tableGiacenze.setRowHeight(28);
        tableGiacenze.setFont(new Font("Arial", Font.PLAIN, 13));
        tableGiacenze.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        // Renderer per righe zebrate
        Color rigaPari = Color.WHITE;
        Color rigaDispari = new Color(245, 245, 245);
        DefaultTableCellRenderer zebraRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? rigaPari : rigaDispari);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        };
        for (int i = 0; i < tableGiacenze.getColumnCount(); i++) {
            tableGiacenze.getColumnModel().getColumn(i).setCellRenderer(zebraRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(tableGiacenze);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    
    private JPanel creaPannelloMovimenti() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Titolo sezione
        JLabel lblTitolo = new JLabel("📋 Storico Movimenti");
        lblTitolo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitolo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(lblTitolo, BorderLayout.NORTH);
        
        // Tabella movimenti
        String[] colonneMovimenti = {"ID", "Data/Ora", "Auto", "Tipo", "Quantità", "Causale", "Giac.Prec", "Giac.Succ"};
        modelMovimenti = new DefaultTableModel(colonneMovimenti, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableMovimenti = new JTable(modelMovimenti);
        tableMovimenti.setRowHeight(28);
        tableMovimenti.setFont(new Font("Arial", Font.PLAIN, 13));
        tableMovimenti.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        // Renderer per righe zebrate
        Color rigaPari = Color.WHITE;
        Color rigaDispari = new Color(245, 245, 245);
        DefaultTableCellRenderer zebraRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? rigaPari : rigaDispari);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        };
        for (int i = 0; i < tableMovimenti.getColumnCount(); i++) {
            tableMovimenti.getColumnModel().getColumn(i).setCellRenderer(zebraRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(tableMovimenti);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    
    private JPanel creaBottoneConIcona(String icona, String testo, Color coloreIcona, java.awt.event.ActionListener action) {
        JPanel panel = new JPanel() {
            private boolean showShadow = false;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (showShadow) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(206, 43, 55));
        panel.setOpaque(false);

        JLabel lblIcona = new JLabel(icona);
        lblIcona.setOpaque(true);
        lblIcona.setBackground(coloreIcona);
        lblIcona.setForeground(Color.WHITE);
        // Per + e - usa font più grande e bold
        if (icona.equals("+") || icona.equals("-")) {
            lblIcona.setFont(new Font("Arial", Font.BOLD, 28));
        } else {
            lblIcona.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        }
        lblIcona.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcona.setPreferredSize(new Dimension(35, 35));
        lblIcona.setMinimumSize(new Dimension(35, 35));
        lblIcona.setMaximumSize(new Dimension(35, 35));
        lblIcona.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(lblIcona);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        JLabel lblTesto = new JLabel("<html><span>" + testo + "</span></html>");
        lblTesto.setForeground(Color.WHITE);
        lblTesto.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        lblTesto.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTesto.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblTesto);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(null);
            }
            public void mouseEntered(MouseEvent e) {
                lblTesto.setText("<html><u>" + testo + "</u></html>");
                try {
                    java.lang.reflect.Method m = panel.getClass().getMethod("setShadow", boolean.class);
                    m.invoke(panel, true);
                } catch (Exception ex) {}
            }
            public void mouseExited(MouseEvent e) {
                lblTesto.setText("<html><span>" + testo + "</span></html>");
                try {
                    java.lang.reflect.Method m = panel.getClass().getMethod("setShadow", boolean.class);
                    m.invoke(panel, false);
                } catch (Exception ex) {}
            }
        });

        return panel;
    }
    
    private void caricaGiacenze() {
        modelGiacenze.setRowCount(0);
        List<Auto> listaAuto = autoDAO.getAll();
        
        for (Auto auto : listaAuto) {
            String stato;
            if (auto.getGiacenza() <= 0) {
                stato = "⚠️ ESAURITO";
            } else if (auto.getGiacenza() <= auto.getScortaMinima()) {
                stato = "⚠️ SCORTA BASSA";
            } else {
                stato = "✅ OK";
            }
            
            Object[] row = {
                auto.getId(),
                auto.getMarca(),
                auto.getModello(),
                auto.getGiacenza(),
                auto.getScortaMinima(),
                stato
            };
            modelGiacenze.addRow(row);
        }
    }
    
    private void caricaMovimenti() {
        modelMovimenti.setRowCount(0);
        List<Movimento> listaMovimenti = movimentoDAO.getAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Movimento mov : listaMovimenti) {
            Auto auto = autoDAO.getById(mov.getAutoId());
            String nomeAuto = auto != null ? auto.getMarca() + " " + auto.getModello() : "N/D";
            
            Object[] row = {
                mov.getId(),
                mov.getDataMovimento().format(formatter),
                nomeAuto,
                mov.getTipoMovimento(),
                mov.getQuantita(),
                mov.getCausale() != null ? mov.getCausale() : "",
                mov.getGiacenzaPrecedente(),
                mov.getGiacenzaSuccessiva()
            };
            modelMovimenti.addRow(row);
        }
    }
    
/**
     * Metodo pubblico per aggiornare i dati della schermata.
     * Chiamato automaticamente quando si passa a questa schermata.
     */
    public void refresh() {
        caricaGiacenze();
        caricaMovimenti();
            }
    
    private void mostraDialogMovimento(String tipo) {
        int selectedRow = tableGiacenze.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Seleziona un'auto dalla tabella!");
            return;
        }

        int idAuto = (int) modelGiacenze.getValueAt(selectedRow, 0);
        String marca = (String) modelGiacenze.getValueAt(selectedRow, 1);
        String modello = (String) modelGiacenze.getValueAt(selectedRow, 2);
        int giacenzaAttuale = (int) modelGiacenze.getValueAt(selectedRow, 3);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            tipo + " - " + marca + " " + modello, true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblGiacenza = new JLabel("Giacenza attuale: " + giacenzaAttuale);
        lblGiacenza.setFont(new Font("Arial", Font.BOLD, 14));

        JTextField txtQuantita = new JTextField();
        JTextField txtCausale = new JTextField();

        panel.add(lblGiacenza);
        panel.add(new JLabel(""));
        panel.add(new JLabel("Quantità:"));
        panel.add(txtQuantita);
        panel.add(new JLabel("Causale:"));
        panel.add(txtCausale);

        JButton btnSalva = new JButton("Salva");
        JButton btnAnnulla = new JButton("Annulla");

        btnSalva.addActionListener(e -> {
            try {
                int quantita = Integer.parseInt(txtQuantita.getText());

                if (quantita <= 0) {
                    JOptionPane.showMessageDialog(dialog, "❌ La quantità deve essere maggiore di zero!");
                    return;
                }

                // Verifica scorte per scarico
                if (tipo.equals("SCARICO") && quantita > giacenzaAttuale) {
                    JOptionPane.showMessageDialog(dialog,
                        "❌ Quantità non disponibile! Giacenza attuale: " + giacenzaAttuale);
                    return;
                }

                // Calcola nuova giacenza
                int nuovaGiacenza;
                if (tipo.equals("CARICO")) {
                    nuovaGiacenza = giacenzaAttuale + quantita;
                } else {
                    nuovaGiacenza = giacenzaAttuale - quantita;
                }

                // Crea movimento con il costruttore corretto
                Movimento movimento = new Movimento(idAuto, tipo, quantita, txtCausale.getText());
                movimento.setGiacenzaPrecedente(giacenzaAttuale);
                movimento.setGiacenzaSuccessiva(nuovaGiacenza);

                if (movimentoDAO.inserisci(movimento)) {
                    // Aggiorna giacenza auto
                    Auto auto = autoDAO.getById(idAuto);
                    if (auto != null) {
                        auto.setGiacenza(nuovaGiacenza);
                        autoDAO.aggiorna(auto);
                    }

                    JOptionPane.showMessageDialog(dialog, "✅ Movimento registrato con successo!");
                    dialog.dispose();
                    caricaGiacenze();
                    caricaMovimenti();
                                    } else {
                    JOptionPane.showMessageDialog(dialog, "❌ Errore nel salvare il movimento!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "❌ Inserisci una quantità valida!");
            }
        });

        btnAnnulla.addActionListener(e -> dialog.dispose());

        panel.add(btnSalva);
        panel.add(btnAnnulla);

        dialog.add(panel);
        dialog.setVisible(true);
    }
}