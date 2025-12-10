package gui;

import dao.AutoDAO;
import model.Auto;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SchermataCatalogo extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private AutoDAO autoDAO;
    
    public SchermataCatalogo() {
        autoDAO = new AutoDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);
        
        // Header
        JLabel titolo = new JLabel("🚗 Catalogo Auto");
        titolo.setFont(new Font("Arial", Font.BOLD, 28));
        add(titolo, BorderLayout.NORTH);
        
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
        add(scrollPane, BorderLayout.CENTER);
        
        // Pannello pulsanti
        JPanel panelPulsanti = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelPulsanti.setBackground(Color.WHITE);
        
        JButton btnAggiungi = creaBottone("➕ Aggiungi Auto", new Color(46, 204, 113));
        JButton btnModifica = creaBottone("✏️ Modifica", new Color(52, 152, 219));
        JButton btnElimina = creaBottone("🗑️ Elimina", new Color(231, 76, 60));
        JButton btnAggiorna = creaBottone("🔄 Aggiorna", new Color(149, 165, 166));
        
        btnAggiungi.addActionListener(e -> mostraDialogAggiungi());
        btnModifica.addActionListener(e -> mostraDialogModifica());
        btnElimina.addActionListener(e -> eliminaAuto());
        btnAggiorna.addActionListener(e -> caricaAuto());
        
        panelPulsanti.add(btnAggiungi);
        panelPulsanti.add(btnModifica);
        panelPulsanti.add(btnElimina);
        panelPulsanti.add(btnAggiorna);
        
        add(panelPulsanti, BorderLayout.SOUTH);
        
        // Carica dati iniziali
        caricaAuto();
    }
    
    private JButton creaBottone(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 40));
        return btn;
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
    }
    
    private void mostraDialogAggiungi() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Aggiungi Auto", true);
        dialog.setLayout(new GridLayout(8, 2, 10, 10));
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JTextField txtMarca = new JTextField();
        JTextField txtModello = new JTextField();
        JTextField txtTarga = new JTextField();
        JTextField txtAnno = new JTextField();
        JTextField txtPrezzo = new JTextField();
        JTextField txtGiacenza = new JTextField("0");
        JTextField txtScortaMin = new JTextField("5");
        
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
                    Integer.parseInt(txtScortaMin.getText())
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
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Modifica Auto", true);
        dialog.setLayout(new GridLayout(8, 2, 10, 10));
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JTextField txtMarca = new JTextField((String) tableModel.getValueAt(selectedRow, 1));
        JTextField txtModello = new JTextField((String) tableModel.getValueAt(selectedRow, 2));
        JTextField txtTarga = new JTextField((String) tableModel.getValueAt(selectedRow, 3));
        JTextField txtAnno = new JTextField(String.valueOf(tableModel.getValueAt(selectedRow, 4)));
        JTextField txtPrezzo = new JTextField((String) tableModel.getValueAt(selectedRow, 5));
        JTextField txtGiacenza = new JTextField(String.valueOf(tableModel.getValueAt(selectedRow, 6)));
        JTextField txtScortaMin = new JTextField(String.valueOf(tableModel.getValueAt(selectedRow, 7)));
        
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
                    Integer.parseInt(txtScortaMin.getText())
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