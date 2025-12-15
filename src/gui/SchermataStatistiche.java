package gui;

import dao.VenditaDAO;
import dao.AutoDAO;
import dao.OrdineDAO;
import model.Vendita;
import model.Auto;
import model.Ordine;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SchermataStatistiche extends JPanel {
    private JTable tableVendite;
    private DefaultTableModel modelVendite;
    private VenditaDAO venditaDAO;
    private AutoDAO autoDAO;
    private OrdineDAO ordineDAO;
    
    // Pannelli statistiche
    private JLabel lblTotaleVendite;
    private JLabel lblNumeroVendite;
    private JLabel lblMediaVendita;
    private JLabel lblAutoVendute;
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
        headerPanel.setBackground(new Color(230, 126, 34));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titolo = new JLabel("📊    Vendite e Statistiche");
        titolo.setFont(new Font("Arial", Font.BOLD, 28));
        titolo.setForeground(Color.WHITE);
        
        headerPanel.add(titolo, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Pannello principale con split
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(200);
        splitPane.setResizeWeight(0.3);
        
        // Pannello statistiche (sopra)
        JPanel panelStatistiche = creaPannelloStatistiche();
        splitPane.setTopComponent(panelStatistiche);
        
        // Pannello vendite (sotto)
        JPanel panelVendite = creaPannelloVendite();
        splitPane.setBottomComponent(panelVendite);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Carica dati iniziali
        caricaVendite();
        aggiornaStatistiche();
    }
    
    private JPanel creaPannelloStatistiche() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        panel.setBackground(Color.WHITE);
        
        // Titolo sezione
        JLabel lblTitolo = new JLabel("📈 Dashboard");
        lblTitolo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitolo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(lblTitolo, BorderLayout.NORTH);
        
        // Griglia con le card statistiche
        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        gridPanel.setBackground(Color.WHITE);
        
        // Card 1: Totale Vendite
        JPanel card1 = creaCardStatistica("💰 Totale Vendite", "0.00 €", new Color(46, 204, 113));
        lblTotaleVendite = (JLabel) ((JPanel) card1.getComponent(1)).getComponent(0);
        gridPanel.add(card1);
        
        // Card 2: Numero Vendite
        JPanel card2 = creaCardStatistica("🛒 N° Vendite", "0", new Color(52, 152, 219));
        lblNumeroVendite = (JLabel) ((JPanel) card2.getComponent(1)).getComponent(0);
        gridPanel.add(card2);
        
        // Card 3: Media Vendita
        JPanel card3 = creaCardStatistica("📊 Media Vendita", "0.00 €", new Color(155, 89, 182));
        lblMediaVendita = (JLabel) ((JPanel) card3.getComponent(1)).getComponent(0);
        gridPanel.add(card3);
        
        // Card 4: Auto Vendute
        JPanel card4 = creaCardStatistica("🚗 Auto Vendute", "0", new Color(241, 196, 15));
        lblAutoVendute = (JLabel) ((JPanel) card4.getComponent(1)).getComponent(0);
        gridPanel.add(card4);
        
        // Card 5: Totale Ordini
        JPanel card5 = creaCardStatistica("📦 Totale Ordini", "0.00 €", new Color(231, 76, 60));
        lblTotaleOrdini = (JLabel) ((JPanel) card5.getComponent(1)).getComponent(0);
        gridPanel.add(card5);
        
        // Card 6: Margine Stimato
        JPanel card6 = creaCardStatistica("💹 Margine", "0.00 €", new Color(26, 188, 156));
        lblMargine = (JLabel) ((JPanel) card6.getComponent(1)).getComponent(0);
        gridPanel.add(card6);
        
        panel.add(gridPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel creaCardStatistica(String titolo, String valore, Color colore) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(5, 5));
        card.setBackground(colore);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(colore.darker(), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel lblTitolo = new JLabel(titolo);
        lblTitolo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitolo.setForeground(Color.WHITE);
        card.add(lblTitolo, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setBackground(colore);
        JLabel lblValore = new JLabel(valore);
        lblValore.setFont(new Font("Arial", Font.BOLD, 24));
        lblValore.setForeground(Color.WHITE);
        centerPanel.add(lblValore);
        card.add(centerPanel, BorderLayout.CENTER);
        
        return card;
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
        
        JScrollPane scrollPane = new JScrollPane(tableVendite);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Pulsanti gestione vendite
        JPanel panelPulsanti = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelPulsanti.setBackground(Color.WHITE);
        
        JButton btnNuova = creaBottone("➕ Nuova Vendita", new Color(46, 204, 113));
        JButton btnElimina = creaBottone("❌ Elimina", new Color(231, 76, 60));
        JButton btnAggiorna = creaBottone("🔄 Aggiorna", new Color(149, 165, 166));
        JButton btnEsporta = creaBottone("📄 Esporta Report", new Color(52, 152, 219));
        
        btnNuova.addActionListener(e -> mostraDialogNuovaVendita());
        btnElimina.addActionListener(e -> eliminaVendita());
        btnAggiorna.addActionListener(e -> {
            caricaVendite();
            aggiornaStatistiche();
        });
        btnEsporta.addActionListener(e -> esportaReport());
        
        panelPulsanti.add(btnNuova);
        panelPulsanti.add(btnElimina);
        panelPulsanti.add(btnAggiorna);
        panelPulsanti.add(btnEsporta);
        
        panel.add(panelPulsanti, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JButton creaBottone(String testo, Color colore) {
        JButton btn = new JButton(testo);
        btn.setBackground(colore);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 35));
        return btn;
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
                String.format("%.2f", vendita.getPrezzoVendita()),
                metodoPagamento
            };
            modelVendite.addRow(row);
        }
    }
    
    private void aggiornaStatistiche() {
        List<Vendita> listaVendite = venditaDAO.getAll();
        List<Ordine> listaOrdini = ordineDAO.getAll();
        
        // Calcola statistiche vendite
        double totaleVendite = 0.0;
        int numeroVendite = listaVendite.size();
        int autoVendute = numeroVendite; // Ogni vendita = 1 auto
        
        for (Vendita vendita : listaVendite) {
            totaleVendite += vendita.getPrezzoVendita();
        }
        
        double mediaVendita = numeroVendite > 0 ? totaleVendite / numeroVendite : 0.0;
        
        // Calcola totale ordini
        double totaleOrdini = 0.0;
        for (Ordine ordine : listaOrdini) {
            totaleOrdini += ordine.getTotale();
        }
        
        // Calcola margine stimato (vendite - ordini)
        double margine = totaleVendite - totaleOrdini;
        
        // Aggiorna le label
        lblTotaleVendite.setText(String.format("%.2f €", totaleVendite));
        lblNumeroVendite.setText(String.valueOf(numeroVendite));
        lblMediaVendita.setText(String.format("%.2f €", mediaVendita));
        lblAutoVendute.setText(String.valueOf(autoVendute));
        lblTotaleOrdini.setText(String.format("%.2f €", totaleOrdini));
        lblMargine.setText(String.format("%.2f €", margine));
        
        // Cambia colore margine in base al valore
        if (margine > 0) {
            lblMargine.setForeground(Color.WHITE);
        } else if (margine < 0) {
            lblMargine.setForeground(new Color(255, 200, 200));
        }
    }
    
    private void mostraDialogNuovaVendita() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nuova Vendita", true);
        dialog.setLayout(new GridLayout(9, 2, 10, 10));
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        
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
                    txtPrezzo.setText(String.format("%.2f", auto.getPrezzo()));
                }
            }
        });
        
        dialog.add(new JLabel("Auto:"));
        dialog.add(cmbAuto);
        dialog.add(new JLabel("Nome Cliente:"));
        dialog.add(txtNome);
        dialog.add(new JLabel("Cognome Cliente:"));
        dialog.add(txtCognome);
        dialog.add(new JLabel("Codice Fiscale:"));
        dialog.add(txtCodiceFiscale);
        dialog.add(new JLabel("Telefono:"));
        dialog.add(txtTelefono);
        dialog.add(new JLabel("Email:"));
        dialog.add(txtEmail);
        dialog.add(new JLabel("Prezzo Vendita €:"));
        dialog.add(txtPrezzo);
        dialog.add(new JLabel("Metodo Pagamento:"));
        dialog.add(cmbMetodoPagamento);
        dialog.add(new JLabel("Data (gg/mm/aaaa):"));
        dialog.add(txtData);
        
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
                double prezzoVendita = Double.parseDouble(txtPrezzo.getText());
                
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
        
        dialog.add(btnSalva);
        dialog.add(btnAnnulla);
        
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
    
    private void esportaReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== REPORT VENDITE E STATISTICHE ===\n\n");
        report.append("Data generazione: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");
        
        report.append("STATISTICHE GENERALI:\n");
        report.append("- Totale Vendite: ").append(lblTotaleVendite.getText()).append("\n");
        report.append("- Numero Vendite: ").append(lblNumeroVendite.getText()).append("\n");
        report.append("- Media Vendita: ").append(lblMediaVendita.getText()).append("\n");
        report.append("- Auto Vendute: ").append(lblAutoVendute.getText()).append("\n");
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