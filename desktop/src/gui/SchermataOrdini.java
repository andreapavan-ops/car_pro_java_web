package gui;

import dao.OrdineDAO;
import dao.FornitoreDAO;
import dao.AutoDAO;
import model.Ordine;
import model.Fornitore;
import model.Auto;
import model.RigaOrdine;
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

public class SchermataOrdini extends JPanel {
    private JTable tableFornitori;
    private JTable tableOrdini;
    private JTable tableDettaglio;
    private DefaultTableModel modelFornitori;
    private DefaultTableModel modelOrdini;
    private DefaultTableModel modelDettaglio;
    private OrdineDAO ordineDAO;
    private FornitoreDAO fornitoreDAO;
    private AutoDAO autoDAO;

    public SchermataOrdini() {
        ordineDAO = new OrdineDAO();
        fornitoreDAO = new FornitoreDAO();
        autoDAO = new AutoDAO();

        setLayout(new BorderLayout(0, 0));
        setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 146, 70));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titolo = new JLabel("Gestione Ordini e Fornitori");
        titolo.setFont(new Font("Arial", Font.BOLD, 28));
        titolo.setForeground(Color.WHITE);

        headerPanel.add(titolo, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);
        
        // Split panel superiore (Fornitori e Ordini)
        JSplitPane splitPaneSuperiore = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPaneSuperiore.setDividerLocation(250);
        splitPaneSuperiore.setResizeWeight(0.35);
        
        // Pannello fornitori (in alto)
        JPanel panelFornitori = creaPannelloFornitori();
        splitPaneSuperiore.setTopComponent(panelFornitori);
        
        // Split panel inferiore (Ordini e Dettaglio)
        JSplitPane splitPaneInferiore = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPaneInferiore.setDividerLocation(250);
        splitPaneInferiore.setResizeWeight(0.5);
        
        // Pannello ordini (centro)
        JPanel panelOrdini = creaPannelloOrdini();
        splitPaneInferiore.setTopComponent(panelOrdini);
        
        // Pannello dettaglio ordine (sotto)
        JPanel panelDettaglio = creaPannelloDettaglio();
        splitPaneInferiore.setBottomComponent(panelDettaglio);
        
        splitPaneSuperiore.setBottomComponent(splitPaneInferiore);

        add(splitPaneSuperiore, BorderLayout.CENTER);

        // Footer con pulsanti stile Catalogo
        JPanel panelFooter = new JPanel(new GridLayout(1, 5, 0, 0));
        panelFooter.setBackground(new Color(206, 43, 55)); // Rosso bandiera italiana
        panelFooter.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        Color coloreSidebar = new Color(45, 52, 54);
        JPanel btnNuovoFornitore = creaBottoneConIcona("👥", "Nuovo Fornitore", coloreSidebar, e -> mostraDialogNuovoFornitore());
        JPanel btnNuovoOrdine = creaBottoneConIcona("📦", "Nuovo Ordine", coloreSidebar, e -> mostraDialogNuovoOrdine());
        JPanel btnModificaStato = creaBottoneConIcona("✏️", "Modifica Stato", coloreSidebar, e -> mostraDialogModificaStato());
        JPanel btnElimina = creaBottoneConIcona("🗑️", "Elimina", coloreSidebar, e -> eliminaOrdine());
        JPanel btnAggiorna = creaBottoneConIcona("🔄", "Aggiorna", coloreSidebar, e -> {
            caricaFornitori();
            caricaOrdini();
        });

        panelFooter.add(btnNuovoFornitore);
        panelFooter.add(btnNuovoOrdine);
        panelFooter.add(btnModificaStato);
        panelFooter.add(btnElimina);
        panelFooter.add(btnAggiorna);

        add(panelFooter, BorderLayout.SOUTH);

        // Carica dati iniziali
        caricaFornitori();
        caricaOrdini();
    }
    
    private JPanel creaPannelloFornitori() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Titolo sezione
        JLabel lblTitolo = new JLabel("👥 Fornitori");
        lblTitolo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitolo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(lblTitolo, BorderLayout.NORTH);
        
        // Tabella fornitori
        String[] colonneFornitori = {"ID", "Nome", "Note", "Telefono", "Email", "Indirizzo", "P.IVA"};
        modelFornitori = new DefaultTableModel(colonneFornitori, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableFornitori = new JTable(modelFornitori);
        tableFornitori.setRowHeight(28);
        tableFornitori.setFont(new Font("Arial", Font.PLAIN, 13));
        tableFornitori.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

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
        for (int i = 0; i < tableFornitori.getColumnCount(); i++) {
            tableFornitori.getColumnModel().getColumn(i).setCellRenderer(zebraRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(tableFornitori);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    
    private JPanel creaPannelloOrdini() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Titolo sezione
        JLabel lblTitolo = new JLabel("📋 Elenco Ordini");
        lblTitolo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitolo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(lblTitolo, BorderLayout.NORTH);
        
        // Tabella ordini
        String[] colonneOrdini = {"ID", "Data", "Fornitore", "Stato", "Totale €", "Note"};
        modelOrdini = new DefaultTableModel(colonneOrdini, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableOrdini = new JTable(modelOrdini);
        tableOrdini.setRowHeight(28);
        tableOrdini.setFont(new Font("Arial", Font.PLAIN, 13));
        tableOrdini.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

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
        for (int i = 0; i < tableOrdini.getColumnCount(); i++) {
            tableOrdini.getColumnModel().getColumn(i).setCellRenderer(zebraRenderer);
        }

        // Listener per mostrare dettaglio quando si seleziona un ordine
        tableOrdini.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostraDettaglioOrdine();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableOrdini);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    
    private JPanel creaPannelloDettaglio() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Titolo sezione
        JLabel lblTitolo = new JLabel("📦 Dettaglio Ordine");
        lblTitolo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitolo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(lblTitolo, BorderLayout.NORTH);
        
        // Tabella dettaglio righe ordine
        String[] colonneDettaglio = {"Auto", "Quantità", "Prezzo Unit. €", "Subtotale €"};
        modelDettaglio = new DefaultTableModel(colonneDettaglio, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableDettaglio = new JTable(modelDettaglio);
        tableDettaglio.setRowHeight(28);
        tableDettaglio.setFont(new Font("Arial", Font.PLAIN, 13));
        tableDettaglio.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

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
        for (int i = 0; i < tableDettaglio.getColumnCount(); i++) {
            tableDettaglio.getColumnModel().getColumn(i).setCellRenderer(zebraRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(tableDettaglio);
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
        lblIcona.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
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
    
    private void caricaFornitori() {
        modelFornitori.setRowCount(0);
        List<Fornitore> listaFornitori = fornitoreDAO.getAll();
        
        for (Fornitore fornitore : listaFornitori) {
            Object[] row = {
                fornitore.getId(),
                fornitore.getNome(),
                fornitore.getNote() != null ? fornitore.getNote() : "",
                fornitore.getTelefono() != null ? fornitore.getTelefono() : "",
                fornitore.getEmail() != null ? fornitore.getEmail() : "",
                fornitore.getIndirizzo() != null ? fornitore.getIndirizzo() : "",
                fornitore.getPartitaIva() != null ? fornitore.getPartitaIva() : ""
            };
            modelFornitori.addRow(row);
        }
    }
    
    private void caricaOrdini() {
        modelOrdini.setRowCount(0);
        List<Ordine> listaOrdini = ordineDAO.getAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Ordine ordine : listaOrdini) {
            Fornitore fornitore = fornitoreDAO.getById(ordine.getFornitoreId());
            String nomeFornitore = fornitore != null ? fornitore.getNome() : "N/D";
            
            Object[] row = {
                ordine.getId(),
                ordine.getDataOrdine().format(formatter),
                nomeFornitore,
                ordine.getStato(),
                String.format(Locale.ITALIAN, "%,.2f", ordine.getTotale()),
                ordine.getNote() != null ? ordine.getNote() : ""
            };
            modelOrdini.addRow(row);
        }
    }
    
    private void mostraDettaglioOrdine() {
        modelDettaglio.setRowCount(0);
        
        int selectedRow = tableOrdini.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        int idOrdine = (int) modelOrdini.getValueAt(selectedRow, 0);
        List<RigaOrdine> righe = ordineDAO.getRigheOrdine(idOrdine);
        
        for (RigaOrdine riga : righe) {
            Auto auto = autoDAO.getById(riga.getAutoId());
            String nomeAuto = auto != null ? auto.getMarca() + " " + auto.getModello() : "N/D";
            
            Object[] row = {
                nomeAuto,
                riga.getQuantita(),
                String.format(Locale.ITALIAN, "%,.2f", riga.getPrezzoUnitario()),
                String.format(Locale.ITALIAN, "%,.2f", riga.getSubtotale())
            };
            modelDettaglio.addRow(row);
        }
    }
    
// ========== GESTIONE FORNITORI ==========
    
    private void mostraDialogNuovoFornitore() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nuovo Fornitore", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panelDati = new JPanel(new GridLayout(7, 2, 10, 10));
        panelDati.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField txtNome = new JTextField();
        JTextField txtContatto = new JTextField();
        JTextField txtTelefono = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtIndirizzo = new JTextField();
        JTextField txtPartitaIva = new JTextField();
        
        panelDati.add(new JLabel("Nome *:"));
        panelDati.add(txtNome);
        panelDati.add(new JLabel("Note:"));
        panelDati.add(txtContatto);
        panelDati.add(new JLabel("Telefono:"));
        panelDati.add(txtTelefono);
        panelDati.add(new JLabel("Email:"));
        panelDati.add(txtEmail);
        panelDati.add(new JLabel("Indirizzo:"));
        panelDati.add(txtIndirizzo);
        panelDati.add(new JLabel("Partita IVA:"));
        panelDati.add(txtPartitaIva);
        panelDati.add(new JLabel(""));
        panelDati.add(new JLabel("* Campo obbligatorio"));
        
        dialog.add(panelDati, BorderLayout.CENTER);
        
        // Pannello pulsanti
        JPanel panelPulsanti = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalva = new JButton("💾 Salva");
        JButton btnAnnulla = new JButton("Annulla");
        
        btnSalva.setBackground(new Color(46, 204, 113));
        btnSalva.setForeground(Color.WHITE);
        btnSalva.setFont(new Font("Arial", Font.BOLD, 14));
        
        btnSalva.addActionListener(e -> {
            String nome = txtNome.getText().trim();
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "⚠️ Il nome è obbligatorio!");
                return;
            }
            
            Fornitore fornitore = new Fornitore();
            fornitore.setNome(nome);
            fornitore.setNote(txtContatto.getText().trim());
            fornitore.setTelefono(txtTelefono.getText().trim());
            fornitore.setEmail(txtEmail.getText().trim());
            fornitore.setIndirizzo(txtIndirizzo.getText().trim());
            fornitore.setPartitaIva(txtPartitaIva.getText().trim());
            
            if (fornitoreDAO.inserisci(fornitore)) {
                JOptionPane.showMessageDialog(dialog, "✅ Fornitore creato con successo!");
                dialog.dispose();
                caricaFornitori();
                            } else {
                JOptionPane.showMessageDialog(dialog, "❌ Errore nel salvare il fornitore!");
            }
        });
        
        btnAnnulla.addActionListener(e -> dialog.dispose());
        
        panelPulsanti.add(btnSalva);
        panelPulsanti.add(btnAnnulla);
        dialog.add(panelPulsanti, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void mostraDialogModificaFornitore() {
        int selectedRow = tableFornitori.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Seleziona un fornitore da modificare!");
            return;
        }
        
        int idFornitore = (int) modelFornitori.getValueAt(selectedRow, 0);
        Fornitore fornitore = fornitoreDAO.getById(idFornitore);
        
        if (fornitore == null) {
            JOptionPane.showMessageDialog(this, "❌ Fornitore non trovato!");
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Modifica Fornitore", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panelDati = new JPanel(new GridLayout(7, 2, 10, 10));
        panelDati.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField txtNome = new JTextField(fornitore.getNome());
        JTextField txtContatto = new JTextField(fornitore.getNote() != null ? fornitore.getNote() : "");
        JTextField txtTelefono = new JTextField(fornitore.getTelefono() != null ? fornitore.getTelefono() : "");
        JTextField txtEmail = new JTextField(fornitore.getEmail() != null ? fornitore.getEmail() : "");
        JTextField txtIndirizzo = new JTextField(fornitore.getIndirizzo() != null ? fornitore.getIndirizzo() : "");
        JTextField txtPartitaIva = new JTextField(fornitore.getPartitaIva() != null ? fornitore.getPartitaIva() : "");
        
        panelDati.add(new JLabel("Nome *:"));
        panelDati.add(txtNome);
        panelDati.add(new JLabel("Note:"));
        panelDati.add(txtContatto);
        panelDati.add(new JLabel("Telefono:"));
        panelDati.add(txtTelefono);
        panelDati.add(new JLabel("Email:"));
        panelDati.add(txtEmail);
        panelDati.add(new JLabel("Indirizzo:"));
        panelDati.add(txtIndirizzo);
        panelDati.add(new JLabel("Partita IVA:"));
        panelDati.add(txtPartitaIva);
        panelDati.add(new JLabel(""));
        panelDati.add(new JLabel("* Campo obbligatorio"));
        
        dialog.add(panelDati, BorderLayout.CENTER);
        
        // Pannello pulsanti
        JPanel panelPulsanti = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalva = new JButton("💾 Salva Modifiche");
        JButton btnAnnulla = new JButton("Annulla");
        
        btnSalva.setBackground(new Color(52, 152, 219));
        btnSalva.setForeground(Color.WHITE);
        btnSalva.setFont(new Font("Arial", Font.BOLD, 14));
        
        btnSalva.addActionListener(e -> {
            String nome = txtNome.getText().trim();
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "⚠️ Il nome è obbligatorio!");
                return;
            }
            
            fornitore.setNome(nome);
            fornitore.setNote(txtContatto.getText().trim());
            fornitore.setTelefono(txtTelefono.getText().trim());
            fornitore.setEmail(txtEmail.getText().trim());
            fornitore.setIndirizzo(txtIndirizzo.getText().trim());
            fornitore.setPartitaIva(txtPartitaIva.getText().trim());
            
            if (fornitoreDAO.aggiorna(fornitore)) {
                JOptionPane.showMessageDialog(dialog, "✅ Fornitore aggiornato con successo!");
                dialog.dispose();
                caricaFornitori();
                caricaOrdini();
                            } else {
                JOptionPane.showMessageDialog(dialog, "❌ Errore nell'aggiornare il fornitore!");
            }
        });
        
        btnAnnulla.addActionListener(e -> dialog.dispose());
        
        panelPulsanti.add(btnSalva);
        panelPulsanti.add(btnAnnulla);
        dialog.add(panelPulsanti, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void eliminaFornitore() {
        int selectedRow = tableFornitori.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Seleziona un fornitore da eliminare!");
            return;
        }
        
        int id = (int) modelFornitori.getValueAt(selectedRow, 0);
        String nome = (String) modelFornitori.getValueAt(selectedRow, 1);
        
        // Controlla se ci sono ordini associati
        List<Ordine> ordini = ordineDAO.getAll();
        boolean haOrdini = ordini.stream().anyMatch(o -> o.getFornitoreId() == id);
        
        if (haOrdini) {
            JOptionPane.showMessageDialog(this, 
                "⚠️ Impossibile eliminare il fornitore.\nCi sono ordini associati a questo fornitore!",
                "Errore",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int conferma = JOptionPane.showConfirmDialog(
            this,
            "Sei sicuro di voler eliminare il fornitore '" + nome + "'?",
            "Conferma Eliminazione",
            JOptionPane.YES_NO_OPTION
        );
        
        if (conferma == JOptionPane.YES_OPTION) {
            if (fornitoreDAO.elimina(id)) {
                JOptionPane.showMessageDialog(this, "✅ Fornitore eliminato con successo!");
                caricaFornitori();
                            } else {
                JOptionPane.showMessageDialog(this, "❌ Errore nell'eliminare il fornitore!");
            }
        }
    }
    
    // ========== GESTIONE ORDINI ==========
    
    private void mostraDialogNuovoOrdine() {
        List<Fornitore> fornitori = fornitoreDAO.getAll();
        if (fornitori.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "⚠️ Nessun fornitore disponibile!\nCrea prima un fornitore.",
                "Attenzione",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nuovo Ordine", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(750, 550);
        dialog.setLocationRelativeTo(this);
        
        JPanel panelDati = new JPanel(new GridLayout(3, 2, 10, 10));
        panelDati.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JComboBox<String> cmbFornitore = new JComboBox<>();
        for (Fornitore f : fornitori) {
            cmbFornitore.addItem(f.getId() + " - " + f.getNome());
        }
        
        JTextField txtData = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        JTextField txtNote = new JTextField();
        
        panelDati.add(new JLabel("Fornitore:"));
        panelDati.add(cmbFornitore);
        panelDati.add(new JLabel("Data (gg/mm/aaaa):"));
        panelDati.add(txtData);
        panelDati.add(new JLabel("Note:"));
        panelDati.add(txtNote);
        
        dialog.add(panelDati, BorderLayout.NORTH);
        
        JPanel panelRighe = new JPanel(new BorderLayout());
        panelRighe.setBorder(BorderFactory.createTitledBorder("Righe Ordine"));
        
        String[] colonneRighe = {"Auto", "Quantità", "Prezzo Unit."};
        DefaultTableModel modelRighe = new DefaultTableModel(colonneRighe, 0);
        JTable tableRighe = new JTable(modelRighe);
        JScrollPane scrollRighe = new JScrollPane(tableRighe);
        panelRighe.add(scrollRighe, BorderLayout.CENTER);
        
        JPanel panelAggiungiRiga = new JPanel(new GridLayout(2, 4, 10, 10));
        panelAggiungiRiga.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelAggiungiRiga.setBackground(new Color(240, 248, 255)); // Sfondo azzurro chiaro

        JComboBox<String> cmbAuto = new JComboBox<>();
        List<Auto> listaAuto = autoDAO.getAll();
        for (Auto a : listaAuto) {
            cmbAuto.addItem(a.getId() + " - " + a.getMarca() + " " + a.getModello());
        }

        JTextField txtQuantita = new JTextField();
        JTextField txtPrezzo = new JTextField();
        JButton btnAggiungiRiga = new JButton("➕ AGGIUNGI RIGA");
        btnAggiungiRiga.setBackground(new Color(46, 204, 113));
        btnAggiungiRiga.setForeground(Color.WHITE);
        btnAggiungiRiga.setFont(new Font("Arial", Font.BOLD, 14));

        // Prima riga: etichette
        panelAggiungiRiga.add(new JLabel("Auto:"));
        panelAggiungiRiga.add(new JLabel("Quantità:"));
        panelAggiungiRiga.add(new JLabel("Prezzo Unit. €:"));
        panelAggiungiRiga.add(new JLabel("")); // Placeholder

        // Seconda riga: campi input e pulsante
        panelAggiungiRiga.add(cmbAuto);
        panelAggiungiRiga.add(txtQuantita);
        panelAggiungiRiga.add(txtPrezzo);
        panelAggiungiRiga.add(btnAggiungiRiga);
        
        btnAggiungiRiga.addActionListener(e -> {
            try {
                String autoSelezionata = (String) cmbAuto.getSelectedItem();
                int quantita = Integer.parseInt(txtQuantita.getText());
                double prezzo = Double.parseDouble(txtPrezzo.getText());
                
                Object[] riga = {autoSelezionata, quantita, prezzo};
                modelRighe.addRow(riga);
                
                txtQuantita.setText("");
                txtPrezzo.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "❌ Inserisci valori validi!");
            }
        });

        panelRighe.add(panelAggiungiRiga, BorderLayout.NORTH);
        dialog.add(panelRighe, BorderLayout.CENTER);

        // Pannello pulsanti
        JPanel panelPulsanti = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalva = new JButton("💾 Salva Ordine");
        JButton btnAnnulla = new JButton("Annulla");

        btnSalva.setBackground(new Color(46, 204, 113));
        btnSalva.setForeground(Color.WHITE);
        btnSalva.setFont(new Font("Arial", Font.BOLD, 14));

        btnSalva.addActionListener(e -> {
            if (modelRighe.getRowCount() == 0) {
                JOptionPane.showMessageDialog(dialog, "⚠️ Aggiungi almeno una riga all'ordine!");
                return;
            }

            try {
                // Parse data
                String dataStr = txtData.getText().trim();
                LocalDate dataOrdine;
                try {
                    dataOrdine = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "⚠️ Formato data non valido! Usa gg/mm/aaaa");
                    return;
                }

                // Estrai ID fornitore
                String fornitoreStr = (String) cmbFornitore.getSelectedItem();
                int fornitoreId = Integer.parseInt(fornitoreStr.split(" - ")[0]);

                // Calcola totale
                double totale = 0.0;
                for (int i = 0; i < modelRighe.getRowCount(); i++) {
                    int qnt = (int) modelRighe.getValueAt(i, 1);
                    double prezzo = (double) modelRighe.getValueAt(i, 2);
                    totale += qnt * prezzo;
                }

                // Crea ordine
                Ordine ordine = new Ordine();
                ordine.setFornitoreId(fornitoreId);
                ordine.setDataOrdine(dataOrdine);
                ordine.setStato("PENDENTE");
                ordine.setTotale(totale);
                ordine.setNote(txtNote.getText().trim());

                int ordineId = ordineDAO.inserisci(ordine);

                if (ordineId > 0) {
                    // Inserisci righe ordine
                    for (int i = 0; i < modelRighe.getRowCount(); i++) {
                        String autoStr = (String) modelRighe.getValueAt(i, 0);
                        int autoId = Integer.parseInt(autoStr.split(" - ")[0]);
                        int qnt = (int) modelRighe.getValueAt(i, 1);
                        double prezzo = (double) modelRighe.getValueAt(i, 2);

                        RigaOrdine riga = new RigaOrdine(ordineId, autoId, qnt, prezzo);
                        ordineDAO.inserisciRiga(riga);
                    }

                    JOptionPane.showMessageDialog(dialog, "✅ Ordine creato con successo!");
                    dialog.dispose();
                    caricaOrdini();
                                    } else {
                    JOptionPane.showMessageDialog(dialog, "❌ Errore nel creare l'ordine!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "❌ Errore: " + ex.getMessage());
            }
        });

        btnAnnulla.addActionListener(e -> dialog.dispose());

        panelPulsanti.add(btnSalva);
        panelPulsanti.add(btnAnnulla);
        dialog.add(panelPulsanti, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void mostraDialogModificaStato() {
        int selectedRow = tableOrdini.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Seleziona un ordine da modificare!");
            return;
        }

        int idOrdine = (int) modelOrdini.getValueAt(selectedRow, 0);
        String statoAttuale = (String) modelOrdini.getValueAt(selectedRow, 3);

        String[] stati = {"PENDENTE", "IN_LAVORAZIONE", "SPEDITO", "CONSEGNATO", "ANNULLATO"};

        String nuovoStato = (String) JOptionPane.showInputDialog(
            this,
            "Seleziona il nuovo stato dell'ordine:",
            "Modifica Stato Ordine",
            JOptionPane.QUESTION_MESSAGE,
            null,
            stati,
            statoAttuale
        );

        if (nuovoStato != null && !nuovoStato.equals(statoAttuale)) {
            Ordine ordine = ordineDAO.getById(idOrdine);
            if (ordine != null) {
                ordine.setStato(nuovoStato);

                // Se consegnato, imposta data consegna
                if ("CONSEGNATO".equals(nuovoStato)) {
                    ordine.setDataConsegna(LocalDate.now());
                }

                if (ordineDAO.aggiorna(ordine)) {
                    JOptionPane.showMessageDialog(this, "✅ Stato aggiornato con successo!");
                    caricaOrdini();
                                    } else {
                    JOptionPane.showMessageDialog(this, "❌ Errore nell'aggiornare lo stato!");
                }
            }
        }
    }

    private void eliminaOrdine() {
        int selectedRow = tableOrdini.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Seleziona un ordine da eliminare!");
            return;
        }

        int idOrdine = (int) modelOrdini.getValueAt(selectedRow, 0);
        String dataOrdine = (String) modelOrdini.getValueAt(selectedRow, 1);
        String fornitore = (String) modelOrdini.getValueAt(selectedRow, 2);

        int conferma = JOptionPane.showConfirmDialog(
            this,
            "Sei sicuro di voler eliminare l'ordine del " + dataOrdine + " da " + fornitore + "?\n" +
            "Verranno eliminate anche tutte le righe dell'ordine.",
            "Conferma Eliminazione",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (conferma == JOptionPane.YES_OPTION) {
            // Prima elimina le righe dell'ordine
            ordineDAO.eliminaRighe(idOrdine);

            // Poi elimina l'ordine
            if (ordineDAO.elimina(idOrdine)) {
                JOptionPane.showMessageDialog(this, "✅ Ordine eliminato con successo!");
                caricaOrdini();
                modelDettaglio.setRowCount(0); // Pulisce il dettaglio
                            } else {
                JOptionPane.showMessageDialog(this, "❌ Errore nell'eliminare l'ordine!");
            }
        }
    }
}