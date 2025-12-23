package gui.components;

import model.Movimento;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Componente per visualizzare i movimenti di magazzino in una tabella
 */
public class TabellaMovimenti extends JPanel {
    
    private JTable table;
    private DefaultTableModel tableModel;
    private java.time.format.DateTimeFormatter dateFormat;
    
    private static final String[] COLONNE = {
        "ID", "Data", "Tipo", "Auto", "Quantità", "Note"
    };
    
    public TabellaMovimenti() {
        dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        inizializzaComponenti();
    }
    
    /**
     * Inizializza i componenti della tabella
     */
    private void inizializzaComponenti() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Modello tabella non editabile
        tableModel = new DefaultTableModel(COLONNE, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Creazione tabella
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);
        
        // Larghezza colonne
        table.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(130);  // Data
        table.getColumnModel().getColumn(2).setPreferredWidth(100);  // Tipo
        table.getColumnModel().getColumn(3).setPreferredWidth(200);  // Auto
        table.getColumnModel().getColumn(4).setPreferredWidth(80);   // Quantità
        table.getColumnModel().getColumn(5).setPreferredWidth(250);  // Note
        
        // Renderer per centrare alcune colonne
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        
        // Renderer personalizzato per la colonna "Tipo"
        table.getColumnModel().getColumn(2).setCellRenderer(new TipoMovimentoRenderer());
        
        // Renderer personalizzato per la colonna "Quantità"
        table.getColumnModel().getColumn(4).setCellRenderer(new QuantitaRenderer());
        
        // Header personalizzato
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(52, 58, 64));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        
        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Carica e visualizza la lista di movimenti
     */
    public void caricaMovimenti(List<Movimento> movimenti) {
        tableModel.setRowCount(0); // Pulisce la tabella
        
        if (movimenti == null || movimenti.isEmpty()) {
            return;
        }
        
        for (Movimento mov : movimenti) {
            Object[] riga = new Object[6];
            
            riga[0] = mov.getId();
            riga[1] = mov.getDataMovimento().format(dateFormat);
            riga[2] = mov.getTipoMovimento();
            
            // Auto - serve un AutoDAO per recuperare i dettagli
            // Per ora mostriamo solo l'ID, puoi migliorare con una query
            riga[3] = "Auto ID: " + mov.getAutoId();
            
            riga[4] = mov.getQuantita();
            riga[5] = mov.getCausale() != null ? mov.getCausale() : "";
            
            tableModel.addRow(riga);
        }
    }
    
    /**
     * Filtra i movimenti per tipo
     */
    public void filtraPerTipo(String tipo) {
    }
    
    /**
     * Pulisce la tabella
     */
    public void pulisci() {
        tableModel.setRowCount(0);
    }
    
    /**
     * Ottiene il movimento selezionato
     */
    public int getMovimentoSelezionatoId() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            return (int) tableModel.getValueAt(selectedRow, 0);
        }
        return -1;
    }
    
    /**
     * Renderer personalizzato per la colonna "Tipo"
     */
    private class TipoMovimentoRenderer extends DefaultTableCellRenderer {
        
        public TipoMovimentoRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
            
            if (!isSelected) {
                String tipo = value.toString().toUpperCase();
                
                switch (tipo) {
                    case "CARICO":
                    case "ENTRATA":
                        c.setBackground(new Color(212, 237, 218));
                        c.setForeground(new Color(21, 87, 36));
                        break;
                    case "SCARICO":
                    case "USCITA":
                    case "VENDITA":
                        c.setBackground(new Color(248, 215, 218));
                        c.setForeground(new Color(114, 28, 36));
                        break;
                    case "RESO":
                        c.setBackground(new Color(255, 243, 205));
                        c.setForeground(new Color(133, 100, 4));
                        break;
                    default:
                        c.setBackground(new Color(207, 226, 243));
                        c.setForeground(new Color(12, 84, 96));
                }
                
                if (c instanceof JLabel) {
                    ((JLabel) c).setText(" " + tipo + " ");
                }
            } else {
                c.setBackground(table.getSelectionBackground());
                c.setForeground(table.getSelectionForeground());
            }
            
            return c;
        }
    }
    
    /**
     * Renderer personalizzato per la colonna "Quantità"
     */
    private class QuantitaRenderer extends DefaultTableCellRenderer {
        
        public QuantitaRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
            
            if (!isSelected && value instanceof Integer) {
                int quantita = (int) value;
                String tipo = tableModel.getValueAt(row, 2).toString().toUpperCase();
                
                // Colora in base al tipo di movimento
                if (tipo.equals("CARICO") || tipo.equals("ENTRATA") || tipo.equals("RESO")) {
                    ((JLabel) c).setText("+" + quantita);
                    c.setForeground(new Color(40, 167, 69));
                } else if (tipo.equals("SCARICO") || tipo.equals("USCITA") || tipo.equals("VENDITA")) {
                    ((JLabel) c).setText("-" + quantita);
                    c.setForeground(new Color(220, 53, 69));
                } else {
                    ((JLabel) c).setText(String.valueOf(quantita));
                    c.setForeground(Color.BLACK);
                }
                
                c.setBackground(Color.WHITE);
                ((JLabel) c).setFont(new Font("Segoe UI", Font.BOLD, 13));
            } else if (isSelected) {
                c.setBackground(table.getSelectionBackground());
                c.setForeground(table.getSelectionForeground());
            }
            
            return c;
        }
    }
    
    /**
     * Esporta i dati in CSV
     */
    public String esportaCSV() {
        StringBuilder csv = new StringBuilder();
        
        // Header
        for (int i = 0; i < COLONNE.length; i++) {
            csv.append(COLONNE[i]);
            if (i < COLONNE.length - 1) {
                csv.append(";");
            }
        }
        csv.append("\n");
        
        // Dati
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            for (int col = 0; col < tableModel.getColumnCount(); col++) {
                Object value = tableModel.getValueAt(row, col);
                csv.append(value != null ? value.toString() : "");
                if (col < tableModel.getColumnCount() - 1) {
                    csv.append(";");
                }
            }
            csv.append("\n");
        }
        
        return csv.toString();
    }
    
    /**
     * Restituisce il numero di righe nella tabella
     */
    public int getNumeroRighe() {
        return tableModel.getRowCount();
    }
}