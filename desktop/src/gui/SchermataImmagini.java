package gui;

import dao.AutoDAO;
import model.Auto;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class SchermataImmagini extends JPanel {
    private AutoDAO autoDAO;
    private Auto autoSelezionata;
    private JLabel lblAutoSelezionata;
    private JLabel lblImmagineCorrente;
    private JLabel lblContatore;
    private List<String> immaginiAuto;
    private int indiceCorrente = 0;
    private JButton btnPrecedente;
    private JButton btnSuccessiva;
    private JButton btnElimina;
    private JButton btnModifica;
    private JList<String> listaImmagini;
    private DefaultListModel<String> modelLista;
    
    public SchermataImmagini() {
        autoDAO = new AutoDAO();
        immaginiAuto = new ArrayList<>();
        
        setLayout(new BorderLayout(0, 0));
        setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 146, 70));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel titolo = new JLabel("Gestione Immagini");
        titolo.setFont(new Font("Arial", Font.BOLD, 28));
        titolo.setForeground(Color.WHITE);
        headerPanel.add(titolo, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Pannello centrale
        JPanel panelCentrale = new JPanel(new BorderLayout(20, 20));
        panelCentrale.setBackground(Color.WHITE);
        panelCentrale.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // === PANNELLO SINISTRO: Selezione Auto ===
        JPanel panelSinistro = new JPanel();
        panelSinistro.setLayout(new BoxLayout(panelSinistro, BoxLayout.Y_AXIS));
        panelSinistro.setBackground(Color.WHITE);
        panelSinistro.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            "1. Seleziona Auto",
            0, 0,
            new Font("Arial", Font.BOLD, 16)
        ));
        panelSinistro.setPreferredSize(new Dimension(300, 0));
        
        lblAutoSelezionata = new JLabel("Nessuna auto selezionata");
        lblAutoSelezionata.setFont(new Font("Arial", Font.BOLD, 14));
        lblAutoSelezionata.setForeground(new Color(206, 43, 55));
        lblAutoSelezionata.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAutoSelezionata.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        
        JButton btnSelezionaAuto = new JButton("🚗 Seleziona Auto");
        btnSelezionaAuto.setFont(new Font("Arial", Font.BOLD, 14));
        btnSelezionaAuto.setBackground(new Color(0, 146, 70));
        btnSelezionaAuto.setForeground(Color.WHITE);
        btnSelezionaAuto.setFocusPainted(false);
        btnSelezionaAuto.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSelezionaAuto.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSelezionaAuto.setMaximumSize(new Dimension(250, 40));
        btnSelezionaAuto.addActionListener(e -> selezionaAuto());
        
        panelSinistro.add(Box.createRigidArea(new Dimension(0, 20)));
        panelSinistro.add(lblAutoSelezionata);
        panelSinistro.add(btnSelezionaAuto);
        panelSinistro.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Lista immagini
        JLabel lblListaTitolo = new JLabel("Immagini caricate:");
        lblListaTitolo.setFont(new Font("Arial", Font.BOLD, 12));
        lblListaTitolo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        modelLista = new DefaultListModel<>();
        listaImmagini = new JList<>(modelLista);
        listaImmagini.setFont(new Font("Arial", Font.PLAIN, 12));
        listaImmagini.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaImmagini.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = listaImmagini.getSelectedIndex();
                if (idx >= 0 && idx < immaginiAuto.size()) {
                    indiceCorrente = idx;
                    mostraImmagineCorrente();
                }
            }
        });
        
        JScrollPane scrollLista = new JScrollPane(listaImmagini);
        scrollLista.setPreferredSize(new Dimension(250, 200));
        scrollLista.setMaximumSize(new Dimension(250, 200));
        scrollLista.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panelSinistro.add(lblListaTitolo);
        panelSinistro.add(Box.createRigidArea(new Dimension(0, 10)));
        panelSinistro.add(scrollLista);
        panelSinistro.add(Box.createVerticalGlue());
        
        panelCentrale.add(panelSinistro, BorderLayout.WEST);
        
        // === PANNELLO CENTRALE: Visualizzazione Immagine ===
        JPanel panelVisualizzazione = new JPanel(new BorderLayout(10, 10));
        panelVisualizzazione.setBackground(Color.WHITE);
        panelVisualizzazione.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            "2. Visualizza e Gestisci Immagini",
            0, 0,
            new Font("Arial", Font.BOLD, 16)
        ));
        
        // Immagine
        lblImmagineCorrente = new JLabel("Seleziona un'auto per visualizzare le immagini", JLabel.CENTER);
        lblImmagineCorrente.setFont(new Font("Arial", Font.BOLD, 16));
        lblImmagineCorrente.setForeground(Color.GRAY);
        lblImmagineCorrente.setPreferredSize(new Dimension(600, 400));
        lblImmagineCorrente.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        
        // Pannello controlli
        JPanel panelControlli = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelControlli.setBackground(Color.WHITE);
        
        btnPrecedente = new JButton("◀ Precedente");
        btnSuccessiva = new JButton("Successiva ▶");
        lblContatore = new JLabel("0 / 0");
        lblContatore.setFont(new Font("Arial", Font.BOLD, 14));
        
        btnPrecedente.setFont(new Font("Arial", Font.PLAIN, 13));
        btnSuccessiva.setFont(new Font("Arial", Font.PLAIN, 13));
        btnPrecedente.setFocusPainted(false);
        btnSuccessiva.setFocusPainted(false);
        btnPrecedente.setEnabled(false);
        btnSuccessiva.setEnabled(false);
        
        btnPrecedente.addActionListener(e -> {
            if (!immaginiAuto.isEmpty()) {
                indiceCorrente = (indiceCorrente - 1 + immaginiAuto.size()) % immaginiAuto.size();
                mostraImmagineCorrente();
                listaImmagini.setSelectedIndex(indiceCorrente);
            }
        });
        
        btnSuccessiva.addActionListener(e -> {
            if (!immaginiAuto.isEmpty()) {
                indiceCorrente = (indiceCorrente + 1) % immaginiAuto.size();
                mostraImmagineCorrente();
                listaImmagini.setSelectedIndex(indiceCorrente);
            }
        });
        
        panelControlli.add(btnPrecedente);
        panelControlli.add(lblContatore);
        panelControlli.add(btnSuccessiva);
        
        panelVisualizzazione.add(lblImmagineCorrente, BorderLayout.CENTER);
        panelVisualizzazione.add(panelControlli, BorderLayout.SOUTH);
        
        panelCentrale.add(panelVisualizzazione, BorderLayout.CENTER);
        
        add(panelCentrale, BorderLayout.CENTER);
        
        // === PANNELLO PULSANTI IN BASSO ===
        JPanel panelPulsanti = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelPulsanti.setBackground(new Color(206, 43, 55));
        panelPulsanti.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        JButton btnCaricaImmagine = new JButton("📁 Carica Nuova Immagine");
        btnCaricaImmagine.setFont(new Font("Arial", Font.BOLD, 14));
        btnCaricaImmagine.setBackground(new Color(206, 43, 55));
        btnCaricaImmagine.setForeground(Color.BLACK);
        btnCaricaImmagine.setFocusPainted(false);
        btnCaricaImmagine.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCaricaImmagine.setPreferredSize(new Dimension(220, 40));
        btnCaricaImmagine.addActionListener(e -> caricaImmagine());
        
        btnElimina = new JButton("🗑️ Elimina Immagine");
        btnElimina.setFont(new Font("Arial", Font.BOLD, 14));
        btnElimina.setBackground(new Color(206, 43, 55));
        btnElimina.setForeground(Color.BLACK);
        btnElimina.setFocusPainted(false);
        btnElimina.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnElimina.setPreferredSize(new Dimension(200, 40));
        btnElimina.setEnabled(false);
        btnElimina.addActionListener(e -> eliminaImmagine());

        btnModifica = new JButton("✏️ Modifica Immagine");
        btnModifica.setFont(new Font("Arial", Font.BOLD, 14));
        btnModifica.setBackground(new Color(206, 43, 55));
        btnModifica.setForeground(Color.BLACK);
        btnModifica.setFocusPainted(false);
        btnModifica.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnModifica.setPreferredSize(new Dimension(200, 40));
        btnModifica.setEnabled(false);
        btnModifica.addActionListener(e -> modificaImmagine());

        JButton btnAggiorna = new JButton("🔄 Ricarica Immagini");
        btnAggiorna.setFont(new Font("Arial", Font.BOLD, 14));
        btnAggiorna.setBackground(new Color(206, 43, 55));
        btnAggiorna.setForeground(Color.BLACK);
        btnAggiorna.setFocusPainted(false);
        btnAggiorna.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAggiorna.setPreferredSize(new Dimension(220, 40));
        btnAggiorna.addActionListener(e -> {
            if (autoSelezionata != null) {
                caricaImmaginiAuto();
            }
        });
        
        panelPulsanti.add(btnCaricaImmagine);
        panelPulsanti.add(btnModifica);
        panelPulsanti.add(btnElimina);
        panelPulsanti.add(btnAggiorna);
        
        add(panelPulsanti, BorderLayout.SOUTH);
    }
    
    private void selezionaAuto() {
        List<Auto> listaAuto = autoDAO.getAll();
        
        if (listaAuto.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "⚠️ Nessuna auto presente nel catalogo!", 
                "Attenzione", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Crea array di stringhe per la combo
        String[] opzioni = new String[listaAuto.size()];
        for (int i = 0; i < listaAuto.size(); i++) {
            Auto a = listaAuto.get(i);
            opzioni[i] = a.getId() + " - " + a.getMarca() + " " + a.getModello() + " (" + a.getTarga() + ")";
        }
        
        String selezione = (String) JOptionPane.showInputDialog(
            this,
            "Seleziona l'auto per gestire le immagini:",
            "Selezione Auto",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opzioni,
            opzioni[0]
        );
        
        if (selezione != null) {
            // Estrai l'ID dalla stringa selezionata
            int id = Integer.parseInt(selezione.split(" - ")[0]);
            autoSelezionata = autoDAO.getById(id);
            
            if (autoSelezionata != null) {
                lblAutoSelezionata.setText(
                    "<html><center>" + 
                    autoSelezionata.getMarca() + " " + autoSelezionata.getModello() + 
                    "<br>" + autoSelezionata.getTarga() + 
                    "</center></html>"
                );
                lblAutoSelezionata.setForeground(new Color(0, 146, 70));
                caricaImmaginiAuto();
            }
        }
    }
    
    private void caricaImmaginiAuto() {
        if (autoSelezionata == null) return;
        
        immaginiAuto.clear();
        modelLista.clear();
        indiceCorrente = 0;
        
        // Carica immagine principale
        if (autoSelezionata.getImmagine() != null && !autoSelezionata.getImmagine().isEmpty()) {
            File fileMain = new File("resources/images/" + autoSelezionata.getImmagine());
            if (fileMain.exists()) {
                immaginiAuto.add(autoSelezionata.getImmagine());
                modelLista.addElement("📷 " + autoSelezionata.getImmagine() + " (principale)");
            }
        }
        
        // Carica immagini aggiuntive (pattern: nomefile_1.jpg, nomefile_2.jpg, ...)
        if (autoSelezionata.getImmagine() != null && autoSelezionata.getImmagine().contains(".")) {
            File dirImages = new File("resources/images");
            if (dirImages.exists() && dirImages.isDirectory()) {
                String nomeBase = autoSelezionata.getImmagine().replaceAll("\\.[^.]+$", "");
                String estensione = autoSelezionata.getImmagine().substring(
                    autoSelezionata.getImmagine().lastIndexOf(".")
                );

                for (int i = 1; i <= 50; i++) {
                    String nomeFile = nomeBase + "_" + i + estensione;
                    File file = new File(dirImages, nomeFile);
                    if (file.exists()) {
                        immaginiAuto.add(nomeFile);
                        modelLista.addElement("📷 " + nomeFile);
                    }
                }
            }
        }
        
        // Abilita/disabilita controlli
        boolean hasImages = !immaginiAuto.isEmpty();
        btnPrecedente.setEnabled(hasImages);
        btnSuccessiva.setEnabled(hasImages);
        btnElimina.setEnabled(hasImages);
        btnModifica.setEnabled(hasImages);
        
        if (hasImages) {
            mostraImmagineCorrente();
            listaImmagini.setSelectedIndex(0);
        } else {
            lblImmagineCorrente.setIcon(null);
            lblImmagineCorrente.setText("Nessuna immagine caricata per questa auto");
            lblImmagineCorrente.setFont(new Font("Arial", Font.BOLD, 16));
            lblImmagineCorrente.setForeground(Color.GRAY);
            lblContatore.setText("0 / 0");
        }
    }
    
    private void mostraImmagineCorrente() {
        if (immaginiAuto.isEmpty()) {
            lblImmagineCorrente.setText("Nessuna immagine disponibile");
            lblImmagineCorrente.setIcon(null);
            lblContatore.setText("0 / 0");
            return;
        }
        
        String nomeImmagine = immaginiAuto.get(indiceCorrente);
        File fileImmagine = new File("resources/images/" + nomeImmagine);
        
        if (fileImmagine.exists()) {
            ImageIcon icon = new ImageIcon(fileImmagine.getAbsolutePath());
            Image img = icon.getImage().getScaledInstance(580, 380, Image.SCALE_SMOOTH);
            lblImmagineCorrente.setIcon(new ImageIcon(img));
            lblImmagineCorrente.setText("");
        } else {
            lblImmagineCorrente.setIcon(null);
            lblImmagineCorrente.setText("🖼️ Immagine non trovata");
            lblImmagineCorrente.setFont(new Font("Arial", Font.BOLD, 18));
            lblImmagineCorrente.setForeground(Color.RED);
        }
        
        lblContatore.setText((indiceCorrente + 1) + " / " + immaginiAuto.size());
    }
    
    private void caricaImmagine() {
        if (autoSelezionata == null) {
            JOptionPane.showMessageDialog(this,
                "⚠️ Seleziona prima un'auto!",
                "Attenzione",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleziona un'immagine");
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "Immagini (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif"
        ));
        fileChooser.setMultiSelectionEnabled(true); // Permette selezione multipla
        
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            // Gestisce sia selezione singola che multipla
            File[] filesSelezionati = fileChooser.getSelectedFiles();
            if (filesSelezionati == null || filesSelezionati.length == 0) {
                // Fallback per selezione singola
                File fileSingolo = fileChooser.getSelectedFile();
                if (fileSingolo != null) {
                    filesSelezionati = new File[] { fileSingolo };
                } else {
                    return;
                }
            }

            try {
                File dirImages = new File("resources/images");
                if (!dirImages.exists()) {
                    dirImages.mkdirs();
                }
                
                int numeroCaricate = 0;
                int prossimoNumero = immaginiAuto.size(); // Conta le immagini già esistenti

                for (File fileSelezionato : filesSelezionati) {
                    // Determina il nome del file
                    String nomeFile;

                    if (autoSelezionata.getImmagine() == null || autoSelezionata.getImmagine().isEmpty()) {
                        // Prima immagine - diventa l'immagine principale
                        nomeFile = "auto_" + autoSelezionata.getId() + "_" + fileSelezionato.getName();
                        autoSelezionata.setImmagine(nomeFile);
                        autoDAO.aggiorna(autoSelezionata);
                    } else {
                        // Immagini successive - usa pattern _1, _2, _3...
                        String nomeBase = autoSelezionata.getImmagine().replaceAll("\\.[^.]+$", "");
                        String nomeFileOriginale = fileSelezionato.getName();
                        String estensione = "";
                        if (nomeFileOriginale.contains(".")) {
                            estensione = nomeFileOriginale.substring(nomeFileOriginale.lastIndexOf("."));
                        }

                        nomeFile = nomeBase + "_" + prossimoNumero + estensione;
                        prossimoNumero++; // Incrementa per la prossima immagine
                    }

                    File destinazione = new File(dirImages, nomeFile);
                    Files.copy(fileSelezionato.toPath(), destinazione.toPath(),
                              StandardCopyOption.REPLACE_EXISTING);

                    numeroCaricate++;
                }
                
                JOptionPane.showMessageDialog(this,
                    "✅ " + numeroCaricate + " immagine(i) caricata(e) con successo!",
                    "Successo",
                    JOptionPane.INFORMATION_MESSAGE);
                
                caricaImmaginiAuto();
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "❌ Errore durante il caricamento:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void eliminaImmagine() {
        if (immaginiAuto.isEmpty() || indiceCorrente >= immaginiAuto.size()) {
            return;
        }
        
        String nomeImmagine = immaginiAuto.get(indiceCorrente);
        
        // Verifica se è l'immagine principale
        boolean isPrincipale = (autoSelezionata.getImmagine() != null && 
                                autoSelezionata.getImmagine().equals(nomeImmagine));
        
        String messaggio = "Sei sicuro di voler eliminare l'immagine:\n" + nomeImmagine;
        if (isPrincipale) {
            messaggio += "\n\n⚠️ ATTENZIONE: Questa è l'immagine principale dell'auto!";
        }
        
        int conferma = JOptionPane.showConfirmDialog(this,
            messaggio,
            "Conferma Eliminazione",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (conferma == JOptionPane.YES_OPTION) {
            File fileImmagine = new File("resources/images/" + nomeImmagine);
            
            if (fileImmagine.delete()) {
                // Se era l'immagine principale, aggiorna il database
                if (isPrincipale) {
                    autoSelezionata.setImmagine(null);
                    autoDAO.aggiorna(autoSelezionata);
                }
                
                JOptionPane.showMessageDialog(this,
                    "✅ Immagine eliminata con successo!",
                    "Successo",
                    JOptionPane.INFORMATION_MESSAGE);
                
                caricaImmaginiAuto();
            } else {
                JOptionPane.showMessageDialog(this,
                    "❌ Errore durante l'eliminazione del file!",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modificaImmagine() {
        if (immaginiAuto.isEmpty() || indiceCorrente >= immaginiAuto.size()) {
            return;
        }

        String nomeImmagineAttuale = immaginiAuto.get(indiceCorrente);
        boolean isPrincipale = (autoSelezionata.getImmagine() != null &&
                                autoSelezionata.getImmagine().equals(nomeImmagineAttuale));

        // Dialog per modifica
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Modifica Immagine", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panelContenuto = new JPanel();
        panelContenuto.setLayout(new BoxLayout(panelContenuto, BoxLayout.Y_AXIS));
        panelContenuto.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Info immagine attuale
        JLabel lblInfo = new JLabel("Immagine attuale: " + nomeImmagineAttuale);
        lblInfo.setFont(new Font("Arial", Font.BOLD, 14));
        lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (isPrincipale) {
            JLabel lblPrincipale = new JLabel("⭐ Questa è l'immagine principale");
            lblPrincipale.setFont(new Font("Arial", Font.ITALIC, 12));
            lblPrincipale.setForeground(new Color(0, 146, 70));
            lblPrincipale.setAlignmentX(Component.LEFT_ALIGNMENT);
            panelContenuto.add(lblPrincipale);
            panelContenuto.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        panelContenuto.add(lblInfo);
        panelContenuto.add(Box.createRigidArea(new Dimension(0, 20)));

        // Campo nuovo nome
        JPanel panelNome = new JPanel(new BorderLayout(10, 0));
        panelNome.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelNome.setMaximumSize(new Dimension(450, 30));
        JLabel lblNuovoNome = new JLabel("Nuovo nome:");
        lblNuovoNome.setPreferredSize(new Dimension(100, 25));

        // Estrai nome senza estensione
        String estensione = "";
        String nomeBase = nomeImmagineAttuale;
        if (nomeImmagineAttuale.contains(".")) {
            estensione = nomeImmagineAttuale.substring(nomeImmagineAttuale.lastIndexOf("."));
            nomeBase = nomeImmagineAttuale.substring(0, nomeImmagineAttuale.lastIndexOf("."));
        }

        JTextField txtNuovoNome = new JTextField(nomeBase);
        JLabel lblEstensione = new JLabel(estensione);
        lblEstensione.setFont(new Font("Arial", Font.BOLD, 12));

        panelNome.add(lblNuovoNome, BorderLayout.WEST);
        panelNome.add(txtNuovoNome, BorderLayout.CENTER);
        panelNome.add(lblEstensione, BorderLayout.EAST);

        panelContenuto.add(panelNome);
        panelContenuto.add(Box.createRigidArea(new Dimension(0, 15)));

        // Checkbox per impostare come principale
        JCheckBox chkPrincipale = new JCheckBox("Imposta come immagine principale");
        chkPrincipale.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkPrincipale.setSelected(isPrincipale);
        chkPrincipale.setEnabled(!isPrincipale); // Disabilita se già principale
        panelContenuto.add(chkPrincipale);

        dialog.add(panelContenuto, BorderLayout.CENTER);

        // Pulsanti
        JPanel panelPulsanti = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalva = new JButton("💾 Salva");
        JButton btnAnnulla = new JButton("Annulla");

        String estensioneFinale = estensione;
        btnSalva.addActionListener(e -> {
            String nuovoNome = txtNuovoNome.getText().trim();
            if (nuovoNome.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "⚠️ Il nome non può essere vuoto!",
                    "Attenzione",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Aggiungi estensione
            nuovoNome = nuovoNome + estensioneFinale;

            // Se il nome è cambiato, rinomina il file
            if (!nuovoNome.equals(nomeImmagineAttuale)) {
                File fileOriginale = new File("resources/images/" + nomeImmagineAttuale);
                File fileNuovo = new File("resources/images/" + nuovoNome);

                if (fileNuovo.exists()) {
                    JOptionPane.showMessageDialog(dialog,
                        "⚠️ Esiste già un file con questo nome!",
                        "Attenzione",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (fileOriginale.renameTo(fileNuovo)) {
                    // Se era l'immagine principale, aggiorna il database
                    if (isPrincipale) {
                        autoSelezionata.setImmagine(nuovoNome);
                        autoDAO.aggiorna(autoSelezionata);
                    }

                    JOptionPane.showMessageDialog(dialog,
                        "✅ Immagine rinominata con successo!\n" +
                        "Da: " + nomeImmagineAttuale + "\n" +
                        "A: " + nuovoNome,
                        "Successo",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "❌ Errore durante la rinomina del file!",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Se richiesto, imposta come principale
            if (chkPrincipale.isSelected() && !isPrincipale) {
                String nomeFinale = nuovoNome.equals(nomeImmagineAttuale) ? nomeImmagineAttuale : nuovoNome;
                autoSelezionata.setImmagine(nomeFinale);
                autoDAO.aggiorna(autoSelezionata);

                JOptionPane.showMessageDialog(dialog,
                    "✅ Immagine impostata come principale!",
                    "Successo",
                    JOptionPane.INFORMATION_MESSAGE);
            }

            dialog.dispose();
            caricaImmaginiAuto();
        });

        btnAnnulla.addActionListener(e -> dialog.dispose());

        panelPulsanti.add(btnSalva);
        panelPulsanti.add(btnAnnulla);
        dialog.add(panelPulsanti, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}