package gui;

import dao.VenditaDAO;
import dao.AutoDAO;
import dao.OrdineDAO;
import model.Vendita;
import model.Auto;
import model.Ordine;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class SchermataStatistiche extends JPanel {
    private JTable tableVendite;
    private DefaultTableModel modelVendite;
    private VenditaDAO venditaDAO;
    private AutoDAO autoDAO;
    private OrdineDAO ordineDAO;
    
    // Pannelli statistiche
    private JLabel lblTotaleVendite;
    private JLabel lblNumeroVendite;
    private JLabel lblScorteBasse;
    private JLabel lblTotaleOrdini;
    private JLabel lblMargine;
    
    public SchermataStatistiche() {
        venditaDAO = new VenditaDAO();
        autoDAO = new AutoDAO();
        ordineDAO = new OrdineDAO();
        
        setLayout(new BorderLayout(0, 0));
        setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 146, 70));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titolo = new JLabel("Vendite e Statistiche");
        titolo.setFont(new Font("Arial", Font.BOLD, 28));
        titolo.setForeground(Color.WHITE);

        headerPanel.add(titolo, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Pannello principale con split
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(80);
        splitPane.setResizeWeight(0.1);
        
        // Pannello statistiche (sopra)
        JPanel panelStatistiche = creaPannelloStatistiche();
        splitPane.setTopComponent(panelStatistiche);
        
        // Pannello vendite (sotto)
        JPanel panelVendite = creaPannelloVendite();
        splitPane.setBottomComponent(panelVendite);

        add(splitPane, BorderLayout.CENTER);

        // Footer con pulsanti stile Catalogo
        JPanel panelFooter = new JPanel(new GridLayout(1, 4, 0, 0));
        panelFooter.setBackground(new Color(206, 43, 55)); // Rosso bandiera italiana
        panelFooter.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        Color coloreSidebar = new Color(45, 52, 54);
        JPanel btnNuova = creaBottoneConIcona("+", "Nuova Vendita", coloreSidebar, e -> mostraDialogNuovaVendita());
        JPanel btnElimina = creaBottoneConIcona("🗑️", "Elimina", coloreSidebar, e -> eliminaVendita());
        JPanel btnAggiorna = creaBottoneConIcona("🔄", "Aggiorna", coloreSidebar, e -> {
            caricaVendite();
            aggiornaStatistiche();
        });
        JPanel btnEsporta = creaBottoneConIcona("📄", "Esporta Report", coloreSidebar, e -> esportaReport());

        panelFooter.add(btnNuova);
        panelFooter.add(btnElimina);
        panelFooter.add(btnAggiorna);
        panelFooter.add(btnEsporta);

        add(panelFooter, BorderLayout.SOUTH);

        // Carica dati iniziali
        caricaVendite();
        aggiornaStatistiche();
    }
    
    private JPanel creaPannelloStatistiche() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.setBackground(Color.WHITE);

        // Barra orizzontale con sfondo grigio medio - 5 statistiche
        JPanel barraStats = new JPanel(new GridLayout(1, 5, 0, 0));
        barraStats.setBackground(new Color(100, 110, 115));
        barraStats.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 90, 95), 2),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // Stat 1: Totale Vendite
        JPanel stat1 = creaStatItem("💰", "Vendite");
        lblTotaleVendite = (JLabel) stat1.getComponent(1);
        barraStats.add(stat1);

        // Stat 2: Numero Vendite (con icona auto)
        JPanel stat2 = creaStatItem("🚗", "N° Vendite");
        lblNumeroVendite = (JLabel) stat2.getComponent(1);
        barraStats.add(stat2);

        // Stat 3: Scorte Basse (cliccabile per dettagli)
        JPanel stat3 = creaStatItem("⚠️", "Scorte Basse");
        lblScorteBasse = (JLabel) stat3.getComponent(1);
        stat3.setCursor(new Cursor(Cursor.HAND_CURSOR));
        stat3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mostraDettagliScorteBasse();
            }
        });
        barraStats.add(stat3);

        // Stat 4: Totale Ordini
        JPanel stat4 = creaStatItem("📦", "Ordini");
        lblTotaleOrdini = (JLabel) stat4.getComponent(1);
        barraStats.add(stat4);

        // Stat 5: Margine
        JPanel stat5 = creaStatItem("💹", "Margine");
        lblMargine = (JLabel) stat5.getComponent(1);
        barraStats.add(stat5);

        panel.add(barraStats, BorderLayout.CENTER);

        return panel;
    }

    private JPanel creaStatItem(String icona, String etichetta) {
        JPanel item = new JPanel();
        item.setLayout(new BorderLayout(0, 2));
        item.setBackground(new Color(100, 110, 115));

        // Riga superiore: icona + etichetta
        JPanel rigaSup = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        rigaSup.setBackground(new Color(100, 110, 115));

        JLabel lblIcona = new JLabel(icona);
        lblIcona.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18)); // Stessa dimensione sidebar
        // Colora l'icona di warning in giallo
        if (icona.equals("⚠️") || icona.equals("⚠")) {
            lblIcona.setText("\u26A0"); // Usa solo il simbolo warning senza variante
            lblIcona.setForeground(new Color(255, 200, 0)); // Giallo
        }
        rigaSup.add(lblIcona);

        JLabel lblEtichetta = new JLabel(etichetta);
        lblEtichetta.setFont(new Font("Arial", Font.BOLD, 14)); // Testo più grande
        lblEtichetta.setForeground(Color.BLACK); // Colore nero
        rigaSup.add(lblEtichetta);

        item.add(rigaSup, BorderLayout.NORTH);

        // Riga inferiore: valore (bianco, centrato)
        JLabel lblValore = new JLabel("0", SwingConstants.CENTER);
        lblValore.setFont(new Font("Arial", Font.BOLD, 18)); // Valore più grande
        lblValore.setForeground(Color.WHITE);
        item.add(lblValore, BorderLayout.CENTER);

        return item;
    }
    
    private JPanel creaPannelloVendite() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Titolo sezione
        JLabel lblTitolo = new JLabel("🧾 Elenco Vendite");
        lblTitolo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitolo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(lblTitolo, BorderLayout.NORTH);
        
        // Tabella vendite
        String[] colonneVendite = {"ID", "Data", "Auto", "Cliente", "Prezzo €", "Metodo Pagamento"};
        modelVendite = new DefaultTableModel(colonneVendite, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableVendite = new JTable(modelVendite);
        tableVendite.setRowHeight(28);
        tableVendite.setFont(new Font("Arial", Font.PLAIN, 13));
        tableVendite.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

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
        for (int i = 0; i < tableVendite.getColumnCount(); i++) {
            tableVendite.getColumnModel().getColumn(i).setCellRenderer(zebraRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(tableVendite);
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
    
    private void caricaVendite() {
        modelVendite.setRowCount(0);
        List<Vendita> listaVendite = venditaDAO.getAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Vendita vendita : listaVendite) {
            Auto auto = autoDAO.getById(vendita.getAutoId());
            String nomeAuto = auto != null ? auto.getMarca() + " " + auto.getModello() : "N/D";
            String nomeCliente = vendita.getNomeCompletoCliente() != null && !vendita.getNomeCompletoCliente().trim().isEmpty() 
                ? vendita.getNomeCompletoCliente() : "N/D";
            String metodoPagamento = vendita.getMetodoPagamento() != null ? vendita.getMetodoPagamento() : "N/D";
            
            Object[] row = {
                vendita.getId(),
                vendita.getDataVendita().format(formatter),
                nomeAuto,
                nomeCliente,
                String.format(Locale.ITALIAN, "%,.2f", vendita.getPrezzoVendita()),
                metodoPagamento
            };
            modelVendite.addRow(row);
        }
    }
    
    private void aggiornaStatistiche() {
        List<Vendita> listaVendite = venditaDAO.getAll();
        List<Ordine> listaOrdini = ordineDAO.getAll();
        List<Auto> listaAuto = autoDAO.getAll();

        // Calcola statistiche vendite
        double totaleVendite = 0.0;
        int numeroVendite = listaVendite.size();

        for (Vendita vendita : listaVendite) {
            totaleVendite += vendita.getPrezzoVendita();
        }

        // Calcola scorte basse (giacenza < scorta minima)
        int scorteBasse = 0;
        for (Auto auto : listaAuto) {
            if (auto.getGiacenza() < auto.getScortaMinima()) {
                scorteBasse++;
            }
        }

        // Calcola totale ordini
        double totaleOrdini = 0.0;
        for (Ordine ordine : listaOrdini) {
            totaleOrdini += ordine.getTotale();
        }

        // Calcola margine stimato (vendite - ordini)
        double margine = totaleVendite - totaleOrdini;

        // Aggiorna le label
        lblTotaleVendite.setText(String.format(Locale.ITALIAN, "%,.2f €", totaleVendite));
        lblNumeroVendite.setText(String.valueOf(numeroVendite));
        lblScorteBasse.setText(String.valueOf(scorteBasse));
        lblTotaleOrdini.setText(String.format(Locale.ITALIAN, "%,.2f €", totaleOrdini));
        lblMargine.setText(String.format(Locale.ITALIAN, "%,.2f €", margine));

        // Cambia colore scorte basse in base al valore
        if (scorteBasse > 0) {
            lblScorteBasse.setForeground(new Color(255, 100, 100)); // Rosso se ci sono scorte basse
        } else {
            lblScorteBasse.setForeground(new Color(100, 255, 100)); // Verde se tutto ok
        }

        // Cambia colore margine in base al valore
        if (margine > 0) {
            lblMargine.setForeground(Color.WHITE);
        } else if (margine < 0) {
            lblMargine.setForeground(new Color(255, 200, 200));
        }
    }

    private void mostraDettagliScorteBasse() {
        List<Auto> listaAuto = autoDAO.getAll();
        StringBuilder dettagli = new StringBuilder();
        int count = 0;

        for (Auto auto : listaAuto) {
            if (auto.getGiacenza() < auto.getScortaMinima()) {
                int mancanti = auto.getScortaMinima() - auto.getGiacenza();
                dettagli.append(String.format("• %s %s → %d (min: %d) - mancano %d\n",
                    auto.getMarca(), auto.getModello(),
                    auto.getGiacenza(), auto.getScortaMinima(), mancanti));
                count++;
            }
        }

        if (count == 0) {
            JOptionPane.showMessageDialog(this,
                "Tutte le scorte sono sufficienti!",
                "Scorte OK",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Modelli con scorta insufficiente: " + count + "\n\n" + dettagli.toString(),
                "⚠️ Scorte Basse",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void mostraDialogNuovaVendita() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nuova Vendita", true);
        dialog.setSize(500, 480);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(10, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JComboBox<String> cmbAuto = new JComboBox<>();
        List<Auto> listaAuto = autoDAO.getAll();
        for (Auto a : listaAuto) {
            if (a.getGiacenza() > 0) {  // Solo auto disponibili
                cmbAuto.addItem(a.getId() + " - " + a.getMarca() + " " + a.getModello() + " (Disp: " + a.getGiacenza() + ")");
            }
        }

        JTextField txtNome = new JTextField();
        JTextField txtCognome = new JTextField();
        JTextField txtCodiceFiscale = new JTextField();
        JTextField txtTelefono = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtPrezzo = new JTextField();
        JComboBox<String> cmbMetodoPagamento = new JComboBox<>(new String[]{"Contanti", "Carta", "Bonifico", "Finanziamento"});
        JTextField txtData = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        // Listener per auto-compilare il prezzo quando si seleziona un'auto
        cmbAuto.addActionListener(e -> {
            String autoStr = (String) cmbAuto.getSelectedItem();
            if (autoStr != null) {
                int autoId = Integer.parseInt(autoStr.split(" - ")[0]);
                Auto auto = autoDAO.getById(autoId);
                if (auto != null) {
                    txtPrezzo.setText(String.format(Locale.ITALIAN, "%,.2f", auto.getPrezzo()));
                }
            }
        });

        panel.add(new JLabel("Auto:"));
        panel.add(cmbAuto);
        panel.add(new JLabel("Nome Cliente:"));
        panel.add(txtNome);
        panel.add(new JLabel("Cognome Cliente:"));
        panel.add(txtCognome);
        panel.add(new JLabel("Codice Fiscale:"));
        panel.add(txtCodiceFiscale);
        panel.add(new JLabel("Telefono:"));
        panel.add(txtTelefono);
        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);
        panel.add(new JLabel("Prezzo Vendita €:"));
        panel.add(txtPrezzo);
        panel.add(new JLabel("Metodo Pagamento:"));
        panel.add(cmbMetodoPagamento);
        panel.add(new JLabel("Data (gg/mm/aaaa):"));
        panel.add(txtData);

        JButton btnSalva = new JButton("💾 Salva");
        JButton btnAnnulla = new JButton("Annulla");

        btnSalva.addActionListener(e -> {
            try {
                String autoStr = (String) cmbAuto.getSelectedItem();
                if (autoStr == null) {
                    JOptionPane.showMessageDialog(dialog, "⚠️ Seleziona un'auto!");
                    return;
                }

                int autoId = Integer.parseInt(autoStr.split(" - ")[0]);
                String nomeCliente = txtNome.getText();
                String cognomeCliente = txtCognome.getText();
                double prezzoVendita = Double.parseDouble(txtPrezzo.getText().replace(".", "").replace(",", "."));

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate dataVendita = LocalDate.parse(txtData.getText(), formatter);

                // Verifica giacenza
                Auto auto = autoDAO.getById(autoId);
                if (auto != null && auto.getGiacenza() <= 0) {
                    JOptionPane.showMessageDialog(dialog, "❌ Auto non disponibile!");
                    return;
                }

                // Crea vendita
                Vendita vendita = new Vendita();
                vendita.setAutoId(autoId);
                vendita.setNomeCliente(nomeCliente);
                vendita.setCognomeCliente(cognomeCliente);
                vendita.setCodiceFiscale(txtCodiceFiscale.getText());
                vendita.setTelefono(txtTelefono.getText());
                vendita.setEmail(txtEmail.getText());
                vendita.setPrezzoVendita(prezzoVendita);
                vendita.setMetodoPagamento((String) cmbMetodoPagamento.getSelectedItem());
                vendita.setDataVendita(dataVendita);

                if (venditaDAO.inserisci(vendita)) {
                    // Aggiorna giacenza auto (decrementa di 1)
                    if (auto != null) {
                        auto.setGiacenza(auto.getGiacenza() - 1);
                        autoDAO.aggiorna(auto);
                    }

                    JOptionPane.showMessageDialog(dialog, "✅ Vendita registrata con successo!");
                    dialog.dispose();
                    caricaVendite();
                    aggiornaStatistiche();
                } else {
                    JOptionPane.showMessageDialog(dialog, "❌ Errore nel salvare la vendita!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "❌ Errore: " + ex.getMessage());
            }
        });

        btnAnnulla.addActionListener(e -> dialog.dispose());

        panel.add(btnSalva);
        panel.add(btnAnnulla);

        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void eliminaVendita() {
        int selectedRow = tableVendite.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Seleziona una vendita da eliminare!");
            return;
        }
        
        int conferma = JOptionPane.showConfirmDialog(
            this,
            "Sei sicuro di voler eliminare questa vendita?\n(La giacenza NON verrà ripristinata automaticamente)",
            "Conferma Eliminazione",
            JOptionPane.YES_NO_OPTION
        );
        
        if (conferma == JOptionPane.YES_OPTION) {
            int id = (int) modelVendite.getValueAt(selectedRow, 0);
            if (venditaDAO.elimina(id)) {
                JOptionPane.showMessageDialog(this, "✅ Vendita eliminata con successo!");
                caricaVendite();
                aggiornaStatistiche();
            }
        }
    }
    
    /**
     * Metodo pubblico per aggiornare i dati della schermata.
     * Chiamato automaticamente quando si passa a questa schermata.
     */
    public void refresh() {
        caricaVendite();
        aggiornaStatistiche();
    }

    private void esportaReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== REPORT VENDITE E STATISTICHE ===\n\n");
        report.append("Data generazione: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");
        
        report.append("STATISTICHE GENERALI:\n");
        report.append("- Totale Vendite: ").append(lblTotaleVendite.getText()).append("\n");
        report.append("- Numero Vendite: ").append(lblNumeroVendite.getText()).append("\n");
        report.append("- Scorte Basse: ").append(lblScorteBasse.getText()).append(" modelli\n");
        report.append("- Totale Ordini: ").append(lblTotaleOrdini.getText()).append("\n");
        report.append("- Margine: ").append(lblMargine.getText()).append("\n\n");
        
        report.append("DETTAGLIO VENDITE:\n");
        report.append("ID | Data | Auto | Cliente | Prezzo | Metodo Pagamento\n");
        report.append("-----------------------------------------------------------\n");
        
        for (int i = 0; i < modelVendite.getRowCount(); i++) {
            for (int j = 0; j < modelVendite.getColumnCount(); j++) {
                report.append(modelVendite.getValueAt(i, j)).append(" | ");
            }
            report.append("\n");
        }
        
        // Mostra report in un dialog
        JTextArea textArea = new JTextArea(report.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700, 500));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Report Vendite", JOptionPane.INFORMATION_MESSAGE);
    }
}