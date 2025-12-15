package gui;

import dao.OrdineDAO;
import dao.FornitoreDAO;
import dao.AutoDAO;
import model.Ordine;
import model.Fornitore;
import model.Auto;
import model.RigaOrdine;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SchermataOrdini extends JPanel {
    private JTable tableOrdini;
    private JTable tableDettaglio;
    private DefaultTableModel modelOrdini;
    private DefaultTableModel modelDettaglio;
    private OrdineDAO ordineDAO;
    private FornitoreDAO fornitoreDAO;
    private AutoDAO autoDAO;
    private JLabel lblStatistiche;
    
    public SchermataOrdini() {
        ordineDAO = new OrdineDAO();
        fornitoreDAO = new FornitoreDAO();
        autoDAO = new AutoDAO();
        
        setLayout(new BorderLayout(0, 0));
        setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(155, 89, 182));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titolo = new JLabel("🛒    Ordini Fornitori");
        titolo.setFont(new Font("Arial", Font.BOLD, 28));
        titolo.setForeground(Color.WHITE);
        
        lblStatistiche = new JLabel();
        lblStatistiche.setFont(new Font("Arial", Font.BOLD, 14));
        lblStatistiche.setForeground(Color.WHITE);
        
        headerPanel.add(titolo, BorderLayout.WEST);
        headerPanel.add(lblStatistiche, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Split panel per dividere ordini e dettaglio
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);
        
        // Pannello ordini (sopra)
        JPanel panelOrdini = creaPannelloOrdini();
        splitPane.setTopComponent(panelOrdini);
        
        // Pannello dettaglio ordine (sotto)
        JPanel panelDettaglio = creaPannelloDettaglio();
        splitPane.setBottomComponent(panelDettaglio);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Carica dati iniziali
        caricaOrdini();
        aggiornaStatistiche();
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
        
        // Listener per mostrare dettaglio quando si seleziona un ordine
        tableOrdini.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostraDettaglioOrdine();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableOrdini);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Pulsanti gestione ordini
        JPanel panelPulsanti = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelPulsanti.setBackground(Color.WHITE);
        
        JButton btnNuovo = creaBottone("➕ Nuovo Ordine", new Color(46, 204, 113));
        JButton btnModifica = creaBottone("✏️ Modifica Stato", new Color(52, 152, 219));
        JButton btnElimina = creaBottone("❌ Elimina", new Color(231, 76, 60));
        JButton btnAggiorna = creaBottone("🔄 Aggiorna", new Color(149, 165, 166));
        
        btnNuovo.addActionListener(e -> mostraDialogNuovoOrdine());
        btnModifica.addActionListener(e -> mostraDialogModificaStato());
        btnElimina.addActionListener(e -> eliminaOrdine());
        btnAggiorna.addActionListener(e -> {
            caricaOrdini();
            aggiornaStatistiche();
        });
        
        panelPulsanti.add(btnNuovo);
        panelPulsanti.add(btnModifica);
        panelPulsanti.add(btnElimina);
        panelPulsanti.add(btnAggiorna);
        
        panel.add(panelPulsanti, BorderLayout.SOUTH);
        
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
        
        JScrollPane scrollPane = new JScrollPane(tableDettaglio);
        panel.add(scrollPane, BorderLayout.CENTER);
        
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
                String.format("%.2f", ordine.getTotale()),
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
                String.format("%.2f", riga.getPrezzoUnitario()),
                String.format("%.2f", riga.getSubtotale())
            };
            modelDettaglio.addRow(row);
        }
    }
    
    private void aggiornaStatistiche() {
        List<Ordine> listaOrdini = ordineDAO.getAll();
        int ordiniPendenti = 0;
        double totaleOrdini = 0.0;
        
        for (Ordine ordine : listaOrdini) {
            if ("PENDENTE".equals(ordine.getStato()) || "IN_LAVORAZIONE".equals(ordine.getStato())) {
                ordiniPendenti++;
            }
            totaleOrdini += ordine.getTotale();
        }
        
        lblStatistiche.setText(String.format("Ordini totali: %d | Pendenti: %d | Totale: %.2f €", 
            listaOrdini.size(), ordiniPendenti, totaleOrdini));
    }
    
    private void mostraDialogNuovoOrdine() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nuovo Ordine", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);
        
        // Pannello superiore - dati ordine
        JPanel panelDati = new JPanel(new GridLayout(3, 2, 10, 10));
        panelDati.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JComboBox<String> cmbFornitore = new JComboBox<>();
        List<Fornitore> fornitori = fornitoreDAO.getAll();
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
        
        // Pannello centrale - righe ordine
        JPanel panelRighe = new JPanel(new BorderLayout());
        panelRighe.setBorder(BorderFactory.createTitledBorder("Righe Ordine"));
        
        String[] colonneRighe = {"Auto", "Quantità", "Prezzo Unit."};
        DefaultTableModel modelRighe = new DefaultTableModel(colonneRighe, 0);
        JTable tableRighe = new JTable(modelRighe);
        JScrollPane scrollRighe = new JScrollPane(tableRighe);
        panelRighe.add(scrollRighe, BorderLayout.CENTER);
        
        // Pannello per aggiungere righe
        JPanel panelAggiungiRiga = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> cmbAuto = new JComboBox<>();
        List<Auto> listaAuto = autoDAO.getAll();
        for (Auto a : listaAuto) {
            cmbAuto.addItem(a.getId() + " - " + a.getMarca() + " " + a.getModello());
        }
        
        JTextField txtQuantita = new JTextField(5);
        JTextField txtPrezzo = new JTextField(8);
        JButton btnAggiungiRiga = new JButton("➕ Aggiungi");
        
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
        
        panelAggiungiRiga.add(new JLabel("Auto:"));
        panelAggiungiRiga.add(cmbAuto);
        panelAggiungiRiga.add(new JLabel("Qnt:"));
        panelAggiungiRiga.add(txtQuantita);
        panelAggiungiRiga.add(new JLabel("Prezzo:"));
        panelAggiungiRiga.add(txtPrezzo);
        panelAggiungiRiga.add(btnAggiungiRiga);
        
        panelRighe.add(panelAggiungiRiga, BorderLayout.SOUTH);
        dialog.add(panelRighe, BorderLayout.CENTER);
        
        // Pannello pulsanti
        JPanel panelPulsanti = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalva = new JButton("💾 Salva Ordine");
        JButton btnAnnulla = new JButton("Annulla");
        
        btnSalva.addActionListener(e -> {
            try {
                if (modelRighe.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(dialog, "⚠️ Aggiungi almeno una riga all'ordine!");
                    return;
                }
                
                String fornitoreStr = (String) cmbFornitore.getSelectedItem();
                int fornitoreId = Integer.parseInt(fornitoreStr.split(" - ")[0]);
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate dataOrdine = LocalDate.parse(txtData.getText(), formatter);
                
                // Calcola totale
                double totale = 0.0;
                for (int i = 0; i < modelRighe.getRowCount(); i++) {
                    int quantita = (int) modelRighe.getValueAt(i, 1);
                    double prezzoUnit = (double) modelRighe.getValueAt(i, 2);
                    totale += quantita * prezzoUnit;
                }
                
                // Crea ordine
                Ordine ordine = new Ordine();
                ordine.setFornitoreId(fornitoreId);
                ordine.setDataOrdine(dataOrdine);
                ordine.setStato("PENDENTE");
                ordine.setNote(txtNote.getText());
                ordine.setTotale(totale);
                
                // Inserisci ordine e recupera l'ID generato
                int ordineId = ordineDAO.inserisci(ordine);
                
                if (ordineId > 0) {
                    // Inserisci le righe ordine
                    boolean tutteRigheInserite = true;
                    for (int i = 0; i < modelRighe.getRowCount(); i++) {
                        String autoStr = (String) modelRighe.getValueAt(i, 0);
                        int autoId = Integer.parseInt(autoStr.split(" - ")[0]);
                        int quantita = (int) modelRighe.getValueAt(i, 1);
                        double prezzoUnit = (double) modelRighe.getValueAt(i, 2);
                        
                        RigaOrdine riga = new RigaOrdine(ordineId, autoId, quantita, prezzoUnit);
                        if (!ordineDAO.inserisciRiga(riga)) {
                            tutteRigheInserite = false;
                        }
                    }
                    
                    if (tutteRigheInserite) {
                        JOptionPane.showMessageDialog(dialog, "✅ Ordine creato con successo!");
                        dialog.dispose();
                        caricaOrdini();
                        aggiornaStatistiche();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "⚠️ Ordine creato ma alcune righe non sono state salvate!");
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog, "❌ Errore nel salvare l'ordine!");
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
        
        String[] stati = {"PENDENTE", "IN_LAVORAZIONE", "COMPLETATO", "ANNULLATO"};
        String nuovoStato = (String) JOptionPane.showInputDialog(
            this,
            "Seleziona il nuovo stato:",
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
                if (ordineDAO.aggiorna(ordine)) {
                    JOptionPane.showMessageDialog(this, "✅ Stato aggiornato con successo!");
                    caricaOrdini();
                    aggiornaStatistiche();
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
        
        int conferma = JOptionPane.showConfirmDialog(
            this,
            "Sei sicuro di voler eliminare questo ordine e tutte le sue righe?",
            "Conferma Eliminazione",
            JOptionPane.YES_NO_OPTION
        );
        
        if (conferma == JOptionPane.YES_OPTION) {
            int id = (int) modelOrdini.getValueAt(selectedRow, 0);
            
            // Elimina prima le righe, poi l'ordine
            ordineDAO.eliminaRighe(id);
            
            if (ordineDAO.elimina(id)) {
                JOptionPane.showMessageDialog(this, "✅ Ordine eliminato con successo!");
                caricaOrdini();
                aggiornaStatistiche();
                modelDettaglio.setRowCount(0);
            }
        }
    }
}