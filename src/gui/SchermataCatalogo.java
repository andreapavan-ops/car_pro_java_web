package gui;

import dao.AutoDAO;
import model.Auto;
import gui.components.GrigliaAuto;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
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
        
        // ✅ DOPPIO CLIC SULLA TABELLA - Apre dialog con slideshow
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        int id = (int) tableModel.getValueAt(row, 0);
                        Auto auto = autoDAO.getById(id);
                        if (auto != null) {
                            mostraDialogDettaglio(auto);
                        }
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        panelCentrale.add(scrollPane, "TABELLA");
        
        // Griglia
        grigliaAuto = new GrigliaAuto();
        grigliaAuto.setOnAutoClickListener(auto -> mostraDialogDettaglio(auto));
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
        btnVisualizzazione.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
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
    
    // ============================================
    // 🎬 DIALOG DETTAGLIO CON SLIDESHOW
    // ============================================
    private void mostraDialogDettaglio(Auto auto) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Dettaglio Auto", true);
        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        
        // Carica tutte le immagini disponibili
        List<String> immagini = caricaImmaginiAuto(auto);
        final int[] indiceCorrente = {0};
        
        // === PANNELLO SINISTRO: SLIDESHOW ===
        JPanel panelSinistro = new JPanel(new BorderLayout());
        panelSinistro.setBackground(Color.WHITE);
        panelSinistro.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));
        
        // Immagine principale
        JLabel lblImmagine = new JLabel();
        lblImmagine.setHorizontalAlignment(SwingConstants.CENTER);
        lblImmagine.setPreferredSize(new Dimension(500, 400));
        lblImmagine.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        lblImmagine.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblImmagine.setToolTipText("Clicca per ingrandire");
        
        // Pannello controlli slideshow
        JPanel panelControlli = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelControlli.setBackground(Color.WHITE);
        
        JButton btnPrecedente = new JButton("◀ Precedente");
        JButton btnSuccessiva = new JButton("Successiva ▶");
        JLabel lblContatore = new JLabel();
        lblContatore.setFont(new Font("Arial", Font.BOLD, 14));
        
        btnPrecedente.setFont(new Font("Arial", Font.PLAIN, 13));
        btnSuccessiva.setFont(new Font("Arial", Font.PLAIN, 13));
        btnPrecedente.setFocusPainted(false);
        btnSuccessiva.setFocusPainted(false);
        
        // Funzione per mostrare l'immagine corrente
        Runnable mostraImmagine = () -> {
            if (immagini.isEmpty()) {
                lblImmagine.setText("🖼️ Nessuna immagine disponibile");
                lblImmagine.setIcon(null);
                lblImmagine.setFont(new Font("Arial", Font.BOLD, 18));
                lblImmagine.setForeground(Color.GRAY);
                lblContatore.setText("0 / 0");
                return;
            }
            
            String nomeImmagine = immagini.get(indiceCorrente[0]);
            File fileImmagine = new File("resources/images/" + nomeImmagine);
            
            if (fileImmagine.exists()) {
                ImageIcon icon = new ImageIcon(fileImmagine.getAbsolutePath());
                Image img = icon.getImage().getScaledInstance(480, 380, Image.SCALE_SMOOTH);
                lblImmagine.setIcon(new ImageIcon(img));
                lblImmagine.setText("");
            } else {
                lblImmagine.setIcon(null);
                lblImmagine.setText("🖼️ Immagine non trovata");
                lblImmagine.setFont(new Font("Arial", Font.BOLD, 18));
                lblImmagine.setForeground(Color.GRAY);
            }
            
            lblContatore.setText((indiceCorrente[0] + 1) + " / " + immagini.size());
        };
        
        mostraImmagine.run(); // Mostra prima immagine
        
        btnPrecedente.addActionListener(e -> {
            if (!immagini.isEmpty()) {
                indiceCorrente[0] = (indiceCorrente[0] - 1 + immagini.size()) % immagini.size();
                mostraImmagine.run();
            }
        });
        
        btnSuccessiva.addActionListener(e -> {
            if (!immagini.isEmpty()) {
                indiceCorrente[0] = (indiceCorrente[0] + 1) % immagini.size();
                mostraImmagine.run();
            }
        });
        
        // Click per ingrandire
        lblImmagine.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!immagini.isEmpty()) {
                    mostraImmagineFullscreen(dialog, immagini.get(indiceCorrente[0]));
                }
            }
        });
        
        panelControlli.add(btnPrecedente);
        panelControlli.add(lblContatore);
        panelControlli.add(btnSuccessiva);
        
        panelSinistro.add(lblImmagine, BorderLayout.CENTER);
        panelSinistro.add(panelControlli, BorderLayout.SOUTH);
        
        // === PANNELLO DESTRO: DETTAGLI ===
        JPanel panelDestro = new JPanel();
        panelDestro.setLayout(new BoxLayout(panelDestro, BoxLayout.Y_AXIS));
        panelDestro.setBackground(Color.WHITE);
        panelDestro.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));
        
        // Titolo
        JLabel lblTitolo = new JLabel(auto.getMarca() + " " + auto.getModello());
        lblTitolo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitolo.setForeground(new Color(0, 146, 70));
        lblTitolo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(350, 2));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Dettagli
        JPanel panelInfo = new JPanel(new GridLayout(8, 2, 10, 15));
        panelInfo.setBackground(Color.WHITE);
        panelInfo.setMaximumSize(new Dimension(350, 300));
        panelInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        Font fontLabel = new Font("Arial", Font.BOLD, 14);
        Font fontValue = new Font("Arial", Font.PLAIN, 14);
        
        panelInfo.add(creaLabel("🚗 Marca:", fontLabel));
        panelInfo.add(creaLabel(auto.getMarca(), fontValue));
        
        panelInfo.add(creaLabel("🏎️ Modello:", fontLabel));
        panelInfo.add(creaLabel(auto.getModello(), fontValue));
        
        panelInfo.add(creaLabel("🔖 Targa:", fontLabel));
        panelInfo.add(creaLabel(auto.getTarga(), fontValue));
        
        panelInfo.add(creaLabel("📅 Anno:", fontLabel));
        panelInfo.add(creaLabel(String.valueOf(auto.getAnno()), fontValue));
        
        panelInfo.add(creaLabel("💰 Prezzo:", fontLabel));
        JLabel lblPrezzo = creaLabel(String.format("€ %.2f", auto.getPrezzo()), fontValue);
        lblPrezzo.setForeground(new Color(206, 43, 55));
        lblPrezzo.setFont(new Font("Arial", Font.BOLD, 16));
        panelInfo.add(lblPrezzo);
        
        panelInfo.add(creaLabel("📦 Giacenza:", fontLabel));
        JLabel lblGiacenza = creaLabel(String.valueOf(auto.getGiacenza()), fontValue);
        if (auto.getGiacenza() <= auto.getScortaMinima()) {
            lblGiacenza.setForeground(Color.RED);
            lblGiacenza.setFont(new Font("Arial", Font.BOLD, 14));
        }
        panelInfo.add(lblGiacenza);
        
        panelInfo.add(creaLabel("⚠️ Scorta Min:", fontLabel));
        panelInfo.add(creaLabel(String.valueOf(auto.getScortaMinima()), fontValue));
        
        panelInfo.add(creaLabel("🖼️ Immagini:", fontLabel));
        panelInfo.add(creaLabel(String.valueOf(immagini.size()), fontValue));
        
        // Descrizione
        JTextArea txtDescrizione = new JTextArea(
            "Questa è un'auto " + auto.getMarca() + " " + auto.getModello() + 
            " del " + auto.getAnno() + ".\n\n" +
            "Caratteristiche:\n" +
            "• Design moderno ed elegante\n" +
            "• Prestazioni elevate\n" +
            "• Tecnologia all'avanguardia\n" +
            "• Comfort superiore\n\n" +
            "Contattaci per maggiori informazioni o per prenotare un test drive!"
        );
        txtDescrizione.setFont(new Font("Arial", Font.PLAIN, 12));
        txtDescrizione.setLineWrap(true);
        txtDescrizione.setWrapStyleWord(true);
        txtDescrizione.setEditable(false);
        txtDescrizione.setBackground(new Color(245, 245, 245));
        txtDescrizione.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        txtDescrizione.setMaximumSize(new Dimension(350, 150));
        txtDescrizione.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Assembla pannello destro
        panelDestro.add(lblTitolo);
        panelDestro.add(Box.createRigidArea(new Dimension(0, 15)));
        panelDestro.add(separator);
        panelDestro.add(Box.createRigidArea(new Dimension(0, 20)));
        panelDestro.add(panelInfo);
        panelDestro.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JLabel lblDescrizioneTitle = new JLabel("📝 Descrizione");
        lblDescrizioneTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblDescrizioneTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDestro.add(lblDescrizioneTitle);
        panelDestro.add(Box.createRigidArea(new Dimension(0, 10)));
        panelDestro.add(txtDescrizione);
        panelDestro.add(Box.createVerticalGlue());
        
        // Pulsante chiudi
        JButton btnChiudi = new JButton("Chiudi");
        btnChiudi.setFont(new Font("Arial", Font.BOLD, 14));
        btnChiudi.setBackground(new Color(0, 146, 70));
        btnChiudi.setForeground(Color.WHITE);
        btnChiudi.setFocusPainted(false);
        btnChiudi.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChiudi.setMaximumSize(new Dimension(350, 40));
        btnChiudi.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnChiudi.addActionListener(e -> dialog.dispose());
        panelDestro.add(btnChiudi);
        
        // Aggiungi pannelli al dialog
        dialog.add(panelSinistro, BorderLayout.CENTER);
        dialog.add(panelDestro, BorderLayout.EAST);
        
        // Supporto tasti freccia
        dialog.getRootPane().registerKeyboardAction(
            e -> btnPrecedente.doClick(),
            KeyStroke.getKeyStroke("LEFT"),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        dialog.getRootPane().registerKeyboardAction(
            e -> btnSuccessiva.doClick(),
            KeyStroke.getKeyStroke("RIGHT"),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        dialog.setVisible(true);
    }
    
    private List<String> caricaImmaginiAuto(Auto auto) {
        List<String> immagini = new ArrayList<>();
        
        if (auto.getImmagine() != null && !auto.getImmagine().isEmpty()) {
            immagini.add(auto.getImmagine());
        }
        
        // Cerca altre immagini con pattern: nomefile_1.jpg, nomefile_2.jpg, ecc.
        if (auto.getImmagine() != null) {
            File dirImages = new File("resources/images");
            if (dirImages.exists() && dirImages.isDirectory()) {
                String nomeBase = auto.getImmagine().replaceAll("\\.[^.]+$", "");
                
                for (int i = 1; i <= 10; i++) {
                    String estensione = auto.getImmagine().substring(auto.getImmagine().lastIndexOf("."));
                    String nomeFile = nomeBase + "_" + i + estensione;
                    File file = new File(dirImages, nomeFile);
                    if (file.exists()) {
                        immagini.add(nomeFile);
                    }
                }
            }
        }
        
        return immagini;
    }
    
    private void mostraImmagineFullscreen(JDialog parent, String nomeImmagine) {
        JDialog fullscreenDialog = new JDialog(parent, "Immagine Ingrandita", true);
        fullscreenDialog.setSize(1000, 800);
        fullscreenDialog.setLocationRelativeTo(parent);
        fullscreenDialog.setLayout(new BorderLayout());
        
        File fileImmagine = new File("resources/images/" + nomeImmagine);
        
        if (fileImmagine.exists()) {
            JLabel lblFullscreen = new JLabel();
            lblFullscreen.setHorizontalAlignment(SwingConstants.CENTER);
            
            ImageIcon icon = new ImageIcon(fileImmagine.getAbsolutePath());
            Image img = icon.getImage().getScaledInstance(950, 750, Image.SCALE_SMOOTH);
            lblFullscreen.setIcon(new ImageIcon(img));
            
            fullscreenDialog.add(lblFullscreen, BorderLayout.CENTER);
        }
        
        JButton btnChiudi = new JButton("Chiudi");
        btnChiudi.addActionListener(e -> fullscreenDialog.dispose());
        fullscreenDialog.add(btnChiudi, BorderLayout.SOUTH);
        
        fullscreenDialog.setVisible(true);
    }
    
    private JLabel creaLabel(String testo, Font font) {
        JLabel label = new JLabel(testo);
        label.setFont(font);
        return label;
    }
    
    // ============================================
    // METODI ESISTENTI
    // ============================================
    
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
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
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
    
    private String selezionaImmagine(JDialog parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleziona un'immagine");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Immagini", "jpg", "jpeg", "png", "gif"));
        
        int result = fileChooser.showOpenDialog(parent);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File fileSelezionato = fileChooser.getSelectedFile();
            
            try {
                File dirImages = new File("resources/images");
                if (!dirImages.exists()) {
                    dirImages.mkdirs();
                }
                
                String nomeFile = fileSelezionato.getName();
                File destinazione = new File(dirImages, nomeFile);
                
                Files.copy(fileSelezionato.toPath(), destinazione.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                JOptionPane.showMessageDialog(parent, 
                    "✅ Immagine caricata con successo!\nFile: " + nomeFile, 
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