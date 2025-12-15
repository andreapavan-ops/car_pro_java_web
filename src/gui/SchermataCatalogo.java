package gui;

import dao.AutoDAO;
import model.Auto;
import gui.components.GrigliaAuto;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class SchermataCatalogo extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private AutoDAO autoDAO;
    private GrigliaAuto grigliaAuto;
    private JToggleButton btnVisualizzazione;
    
    public SchermataCatalogo() {
        autoDAO = new AutoDAO();
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);
        setBorder(null);
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 146, 70)); 
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        JLabel titolo = new JLabel("🚗    Catalogo Auto");
        titolo.setFont(new Font("Arial", Font.BOLD, 28));
        titolo.setForeground(Color.WHITE);
        headerPanel.add(titolo);
        add(headerPanel, BorderLayout.NORTH);        
        
        // Pannello centrale con CardLayout
        JPanel panelCentrale = new JPanel(new CardLayout());
        
        // Tabella
        String[] colonne = {"ID", "Marca", "Modello", "Targa", "Anno", "Prezzo €", "Giacenza", "Scorta Min"};
        tableModel = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(table);
        panelCentrale.add(scrollPane, "TABELLA");
        
        // Griglia
        grigliaAuto = new GrigliaAuto();
        grigliaAuto.setOnAutoClickListener(auto -> {
            JOptionPane.showMessageDialog(this, 
                "Auto selezionata:\n" + 
                auto.getMarca() + " " + auto.getModello() + "\n" +
                "Prezzo: €" + auto.getPrezzo() + "\n" +
                "Giacenza: " + auto.getGiacenza());
        });
        panelCentrale.add(grigliaAuto, "GRIGLIA");
        
        add(panelCentrale, BorderLayout.CENTER);
        
        // Pannello pulsanti
        JPanel panelPulsanti = new JPanel(new GridLayout(1, 5, 0, 0));
        panelPulsanti.setBackground(new Color(206, 43, 55));
        panelPulsanti.setBorder(BorderFactory.createEmptyBorder(20, 5, 10, 5));

        JButton btnAggiungi = creaBottone("➕ Aggiungi Auto", new Color(206, 43, 55));
        JButton btnModifica = creaBottone("🛠️ Modifica", new Color(206, 43, 55));
        JButton btnElimina = creaBottone("❌ Elimina", new Color(206, 43, 55));
        JButton btnAggiorna = creaBottone("🔄 Aggiorna", new Color(206, 43, 55));
        
        btnVisualizzazione = new JToggleButton("📊 Vista Griglia");
        btnVisualizzazione.setBackground(new Color(206, 43, 55));
        btnVisualizzazione.setForeground(Color.WHITE);
        btnVisualizzazione.setFont(new Font("Arial", Font.BOLD, 16));
        btnVisualizzazione.setFocusPainted(false);
        btnVisualizzazione.setBorderPainted(false);
        btnVisualizzazione.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnAggiungi.addActionListener(e -> mostraDialogAggiungi());
        btnModifica.addActionListener(e -> mostraDialogModifica());
        btnElimina.addActionListener(e -> eliminaAuto());
        btnAggiorna.addActionListener(e -> caricaAuto());
        
        btnVisualizzazione.addActionListener(e -> {
            CardLayout cl = (CardLayout) panelCentrale.getLayout();
            if (btnVisualizzazione.isSelected()) {
                cl.show(panelCentrale, "GRIGLIA");
                btnVisualizzazione.setText("📋 Vista Tabella");
            } else {
                cl.show(panelCentrale, "TABELLA");
                btnVisualizzazione.setText("📊 Vista Griglia");
            }
        });
        
        panelPulsanti.add(btnAggiungi);
        panelPulsanti.add(btnModifica);
        panelPulsanti.add(btnElimina);
        panelPulsanti.add(btnAggiorna);
        panelPulsanti.add(btnVisualizzazione);
        
        add(panelPulsanti, BorderLayout.SOUTH);
        
        // Carica dati iniziali
        caricaAuto();
    }
    
    private void caricaAuto() {
        tableModel.setRowCount(0);
        List<Auto> listaAuto = autoDAO.getAll();
        
        for (Auto auto : listaAuto) {
            Object[] row = {
                auto.getId(),
                auto.getMarca(),
                auto.getModello(),
                auto.getTarga(),
                auto.getAnno(),
                String.format("%.2f", auto.getPrezzo()),
                auto.getGiacenza(),
                auto.getScortaMinima()
            };
            tableModel.addRow(row);
        }
        
        grigliaAuto.caricaAuto(listaAuto);
    }

    private JButton creaBottone(String testo, Color colore) {
        JButton btn = new JButton(testo);
        btn.setBackground(colore);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }    
    
    private void mostraDialogAggiungi() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Aggiungi Auto", true);
        dialog.setLayout(new GridLayout(9, 2, 10, 10));
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        
        JTextField txtMarca = new JTextField();
        JTextField txtModello = new JTextField();
        JTextField txtTarga = new JTextField();
        JTextField txtAnno = new JTextField();
        JTextField txtPrezzo = new JTextField();
        JTextField txtGiacenza = new JTextField("0");
        JTextField txtScortaMin = new JTextField("5");
        
        // Campo immagine con pulsante sfoglia
        JTextField txtImmagine = new JTextField();
        txtImmagine.setEditable(false);
        JButton btnSfogliaImmagine = new JButton("📁 Sfoglia");
        btnSfogliaImmagine.addActionListener(e -> {
            String nomeFile = selezionaImmagine(dialog);
            if (nomeFile != null) {
                txtImmagine.setText(nomeFile);
            }
        });
        
        JPanel panelImmagine = new JPanel(new BorderLayout(5, 0));
        panelImmagine.add(txtImmagine, BorderLayout.CENTER);
        panelImmagine.add(btnSfogliaImmagine, BorderLayout.EAST);
        
        dialog.add(new JLabel("Marca:"));
        dialog.add(txtMarca);
        dialog.add(new JLabel("Modello:"));
        dialog.add(txtModello);
        dialog.add(new JLabel("Targa:"));
        dialog.add(txtTarga);
        dialog.add(new JLabel("Anno:"));
        dialog.add(txtAnno);
        dialog.add(new JLabel("Prezzo €:"));
        dialog.add(txtPrezzo);
        dialog.add(new JLabel("Giacenza:"));
        dialog.add(txtGiacenza);
        dialog.add(new JLabel("Scorta Minima:"));
        dialog.add(txtScortaMin);
        dialog.add(new JLabel("Immagine:"));
        dialog.add(panelImmagine);
        
        JButton btnSalva = new JButton("Salva");
        JButton btnAnnulla = new JButton("Annulla");
        
        btnSalva.addActionListener(e -> {
            try {
                Auto auto = new Auto(
                    txtMarca.getText(),
                    txtModello.getText(),
                    txtTarga.getText(),
                    Integer.parseInt(txtAnno.getText()),
                    Double.parseDouble(txtPrezzo.getText()),
                    Integer.parseInt(txtGiacenza.getText()),
                    Integer.parseInt(txtScortaMin.getText()),
                    txtImmagine.getText().isEmpty() ? null : txtImmagine.getText()
                );
                
                if (autoDAO.inserisci(auto)) {
                    JOptionPane.showMessageDialog(dialog, "✅ Auto aggiunta con successo!");
                    dialog.dispose();
                    caricaAuto();
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
    
    private void mostraDialogModifica() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Seleziona un'auto da modificare!");
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        Auto autoCorrente = autoDAO.getById(id);
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Modifica Auto", true);
        dialog.setLayout(new GridLayout(9, 2, 10, 10));
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        
        JTextField txtMarca = new JTextField((String) tableModel.getValueAt(selectedRow, 1));
        JTextField txtModello = new JTextField((String) tableModel.getValueAt(selectedRow, 2));
        JTextField txtTarga = new JTextField((String) tableModel.getValueAt(selectedRow, 3));
        JTextField txtAnno = new JTextField(String.valueOf(tableModel.getValueAt(selectedRow, 4)));
        JTextField txtPrezzo = new JTextField((String) tableModel.getValueAt(selectedRow, 5));
        JTextField txtGiacenza = new JTextField(String.valueOf(tableModel.getValueAt(selectedRow, 6)));
        JTextField txtScortaMin = new JTextField(String.valueOf(tableModel.getValueAt(selectedRow, 7)));
        
        // Campo immagine con pulsante sfoglia
        JTextField txtImmagine = new JTextField(autoCorrente != null && autoCorrente.getImmagine() != null ? autoCorrente.getImmagine() : "");
        txtImmagine.setEditable(false);
        JButton btnSfogliaImmagine = new JButton("📁 Sfoglia");
        btnSfogliaImmagine.addActionListener(e -> {
            String nomeFile = selezionaImmagine(dialog);
            if (nomeFile != null) {
                txtImmagine.setText(nomeFile);
            }
        });
        
        JPanel panelImmagine = new JPanel(new BorderLayout(5, 0));
        panelImmagine.add(txtImmagine, BorderLayout.CENTER);
        panelImmagine.add(btnSfogliaImmagine, BorderLayout.EAST);
        
        dialog.add(new JLabel("Marca:"));
        dialog.add(txtMarca);
        dialog.add(new JLabel("Modello:"));
        dialog.add(txtModello);
        dialog.add(new JLabel("Targa:"));
        dialog.add(txtTarga);
        dialog.add(new JLabel("Anno:"));
        dialog.add(txtAnno);
        dialog.add(new JLabel("Prezzo €:"));
        dialog.add(txtPrezzo);
        dialog.add(new JLabel("Giacenza:"));
        dialog.add(txtGiacenza);
        dialog.add(new JLabel("Scorta Minima:"));
        dialog.add(txtScortaMin);
        dialog.add(new JLabel("Immagine:"));
        dialog.add(panelImmagine);
        
        JButton btnSalva = new JButton("Salva");
        JButton btnAnnulla = new JButton("Annulla");
        
        btnSalva.addActionListener(e -> {
            try {
                Auto auto = new Auto(
                    txtMarca.getText(),
                    txtModello.getText(),
                    txtTarga.getText(),
                    Integer.parseInt(txtAnno.getText()),
                    Double.parseDouble(txtPrezzo.getText().replace(",", ".")),
                    Integer.parseInt(txtGiacenza.getText()),
                    Integer.parseInt(txtScortaMin.getText()),
                    txtImmagine.getText().isEmpty() ? null : txtImmagine.getText()
                );
                auto.setId(id);
                
                if (autoDAO.aggiorna(auto)) {
                    JOptionPane.showMessageDialog(dialog, "✅ Auto modificata con successo!");
                    dialog.dispose();
                    caricaAuto();
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
    
    /**
     * Apre un file chooser per selezionare un'immagine e la copia in resources/images/
     */
    private String selezionaImmagine(JDialog parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleziona un'immagine");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Immagini", "jpg", "jpeg", "png", "gif"));
        
        int result = fileChooser.showOpenDialog(parent);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File fileSelezionato = fileChooser.getSelectedFile();
            
            try {
                // Crea la cartella resources/images se non esiste
                File dirImages = new File("resources/images");
                if (!dirImages.exists()) {
                    dirImages.mkdirs();
                }
                
                // Copia il file nella cartella resources/images
                String nomeFile = fileSelezionato.getName();
                File destinazione = new File(dirImages, nomeFile);
                
                Files.copy(fileSelezionato.toPath(), destinazione.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                JOptionPane.showMessageDialog(parent, 
                    "✅ Immagine caricata con successo!\n" +
                    "File: " + nomeFile, 
                    "Successo", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                return nomeFile;
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, 
                    "❌ Errore durante il caricamento dell'immagine:\n" + e.getMessage(), 
                    "Errore", 
                    JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        
        return null;
    }
    
    private void eliminaAuto() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Seleziona un'auto da eliminare!");
            return;
        }
        
        int conferma = JOptionPane.showConfirmDialog(
            this,
            "Sei sicuro di voler eliminare questa auto?",
            "Conferma Eliminazione",
            JOptionPane.YES_NO_OPTION
        );
        
        if (conferma == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            if (autoDAO.elimina(id)) {
                JOptionPane.showMessageDialog(this, "✅ Auto eliminata con successo!");
                caricaAuto();
            }
        }
    }
}