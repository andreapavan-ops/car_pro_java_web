package gui;

import dao.AutoDAO;
import dao.MovimentoDAO;
import model.Auto;
import model.Movimento;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SchermataInventario extends JPanel {
    private JTable tableGiacenze;
    private JTable tableMovimenti;
    private DefaultTableModel modelGiacenze;
    private DefaultTableModel modelMovimenti;
    private AutoDAO autoDAO;
    private MovimentoDAO movimentoDAO;
    private JLabel lblAlertScorte;
    
    public SchermataInventario() {
        autoDAO = new AutoDAO();
        movimentoDAO = new MovimentoDAO();
        
        setLayout(new BorderLayout(0, 0));
        setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titolo = new JLabel("📦    Inventario e Movimenti");
        titolo.setFont(new Font("Arial", Font.BOLD, 28));
        titolo.setForeground(Color.WHITE);
        
        // Label per alert scorte minime
        lblAlertScorte = new JLabel();
        lblAlertScorte.setFont(new Font("Arial", Font.BOLD, 14));
        lblAlertScorte.setForeground(Color.YELLOW);
        
        headerPanel.add(titolo, BorderLayout.WEST);
        headerPanel.add(lblAlertScorte, BorderLayout.EAST);
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
        
        // Carica dati iniziali
        caricaGiacenze();
        caricaMovimenti();
        verificaScorteMinime();
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
        
        JScrollPane scrollPane = new JScrollPane(tableGiacenze);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Pulsanti gestione giacenze
        JPanel panelPulsanti = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelPulsanti.setBackground(Color.WHITE);
        
        JButton btnCarico = creaBottone("➕ Carico", new Color(46, 204, 113));
        JButton btnScarico = creaBottone("➖ Scarico", new Color(231, 76, 60));
        JButton btnAggiorna = creaBottone("🔄 Aggiorna", new Color(52, 152, 219));
        
        btnCarico.addActionListener(e -> mostraDialogMovimento("CARICO"));
        btnScarico.addActionListener(e -> mostraDialogMovimento("SCARICO"));
        btnAggiorna.addActionListener(e -> {
            caricaGiacenze();
            caricaMovimenti();
            verificaScorteMinime();
        });
        
        panelPulsanti.add(btnCarico);
        panelPulsanti.add(btnScarico);
        panelPulsanti.add(btnAggiorna);
        
        panel.add(panelPulsanti, BorderLayout.SOUTH);
        
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
        
        JScrollPane scrollPane = new JScrollPane(tableMovimenti);
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
        btn.setPreferredSize(new Dimension(150, 35));
        return btn;
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
    
    private void verificaScorteMinime() {
        List<Auto> listaAuto = autoDAO.getAll();
        int contaAllert = 0;
        
        for (Auto auto : listaAuto) {
            if (auto.getGiacenza() <= auto.getScortaMinima()) {
                contaAllert++;
            }
        }
        
        if (contaAllert > 0) {
            lblAlertScorte.setText("⚠️ " + contaAllert + " auto sotto scorta minima!");
        } else {
            lblAlertScorte.setText("✅ Tutte le giacenze OK");
        }
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
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        
        JLabel lblGiacenza = new JLabel("Giacenza attuale: " + giacenzaAttuale);
        lblGiacenza.setFont(new Font("Arial", Font.BOLD, 14));
        
        JTextField txtQuantita = new JTextField();
        JTextField txtCausale = new JTextField();
        
        dialog.add(lblGiacenza);
        dialog.add(new JLabel(""));
        dialog.add(new JLabel("Quantità:"));
        dialog.add(txtQuantita);
        dialog.add(new JLabel("Causale:"));
        dialog.add(txtCausale);
        
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
                    verificaScorteMinime();
                } else {
                    JOptionPane.showMessageDialog(dialog, "❌ Errore nel salvare il movimento!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "❌ Inserisci una quantità valida!");
            }
        });
        
        btnAnnulla.addActionListener(e -> dialog.dispose());
        
        dialog.add(btnSalva);
        dialog.add(btnAnnulla);
        
        dialog.setVisible(true);
    }
}