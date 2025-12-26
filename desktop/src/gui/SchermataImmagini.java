package gui;

import dao.AutoDAO;
import model.Auto;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class SchermataImmagini extends JPanel {
    private AutoDAO autoDAO;
    private Auto autoSelezionata;
    private JLabel lblImmagineCorrente;
    private JLabel lblContatore;
    private JLabel lblAutoSelezionata;
    private List<String> immaginiAuto;
    private int indiceCorrente = 0;
    private JButton btnPrecedente;
    private JButton btnSuccessiva;
    private JButton btnElimina;
    private JButton btnModifica;
    private JPanel panelGriglia;
    private JScrollPane scrollGriglia;

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

        // Pannello centrale con split orizzontale
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(320);
        splitPane.setResizeWeight(0.3);

        // === PANNELLO SINISTRO: Griglia Miniature ===
        JPanel panelSinistro = new JPanel(new BorderLayout());
        panelSinistro.setBackground(Color.WHITE);
        panelSinistro.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            "Seleziona Modello",
            0, 0,
            new Font("Arial", Font.BOLD, 16)
        ));

        // Griglia con scroll
        panelGriglia = new JPanel(new GridLayout(0, 2, 10, 10));
        panelGriglia.setBackground(Color.WHITE);
        panelGriglia.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        scrollGriglia = new JScrollPane(panelGriglia);
        scrollGriglia.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollGriglia.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollGriglia.getVerticalScrollBar().setUnitIncrement(16);

        panelSinistro.add(scrollGriglia, BorderLayout.CENTER);
        splitPane.setLeftComponent(panelSinistro);

        // === PANNELLO DESTRO: Carosello ===
        JPanel panelDestro = new JPanel(new BorderLayout(10, 10));
        panelDestro.setBackground(Color.WHITE);
        panelDestro.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            "Carosello Immagini",
            0, 0,
            new Font("Arial", Font.BOLD, 16)
        ));

        // Label auto selezionata
        lblAutoSelezionata = new JLabel("Clicca su un modello per visualizzare le immagini", JLabel.CENTER);
        lblAutoSelezionata.setFont(new Font("Arial", Font.BOLD, 16));
        lblAutoSelezionata.setForeground(new Color(0, 146, 70));
        lblAutoSelezionata.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelDestro.add(lblAutoSelezionata, BorderLayout.NORTH);

        // Immagine centrale
        lblImmagineCorrente = new JLabel("", JLabel.CENTER);
        lblImmagineCorrente.setFont(new Font("Arial", Font.BOLD, 16));
        lblImmagineCorrente.setForeground(Color.GRAY);
        lblImmagineCorrente.setPreferredSize(new Dimension(500, 350));
        lblImmagineCorrente.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));

        // Mostra logo di default
        mostraLogoDefault();

        panelDestro.add(lblImmagineCorrente, BorderLayout.CENTER);

        // Pannello controlli navigazione
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
            }
        });

        btnSuccessiva.addActionListener(e -> {
            if (!immaginiAuto.isEmpty()) {
                indiceCorrente = (indiceCorrente + 1) % immaginiAuto.size();
                mostraImmagineCorrente();
            }
        });

        panelControlli.add(btnPrecedente);
        panelControlli.add(lblContatore);
        panelControlli.add(btnSuccessiva);

        panelDestro.add(panelControlli, BorderLayout.SOUTH);

        splitPane.setRightComponent(panelDestro);
        add(splitPane, BorderLayout.CENTER);

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

        btnElimina = new JButton("\uD83D\uDDD1 Elimina Immagine");
        btnElimina.setFont(new Font("Arial", Font.BOLD, 14));
        btnElimina.setBackground(new Color(206, 43, 55));
        btnElimina.setForeground(Color.BLACK);
        btnElimina.setFocusPainted(false);
        btnElimina.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnElimina.setPreferredSize(new Dimension(200, 40));
        btnElimina.setEnabled(false);
        btnElimina.addActionListener(e -> eliminaImmagine());

        btnModifica = new JButton("\u270F Modifica Immagine");
        btnModifica.setFont(new Font("Arial", Font.BOLD, 14));
        btnModifica.setBackground(new Color(206, 43, 55));
        btnModifica.setForeground(Color.BLACK);
        btnModifica.setFocusPainted(false);
        btnModifica.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnModifica.setPreferredSize(new Dimension(200, 40));
        btnModifica.setEnabled(false);
        btnModifica.addActionListener(e -> modificaImmagine());

        JButton btnAggiorna = new JButton("🔄 Ricarica");
        btnAggiorna.setFont(new Font("Arial", Font.BOLD, 14));
        btnAggiorna.setBackground(new Color(206, 43, 55));
        btnAggiorna.setForeground(Color.BLACK);
        btnAggiorna.setFocusPainted(false);
        btnAggiorna.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAggiorna.setPreferredSize(new Dimension(150, 40));
        btnAggiorna.addActionListener(e -> {
            caricaGriglia();
            if (autoSelezionata != null) {
                caricaImmaginiAuto();
            }
        });

        panelPulsanti.add(btnCaricaImmagine);
        panelPulsanti.add(btnModifica);
        panelPulsanti.add(btnElimina);
        panelPulsanti.add(btnAggiorna);

        add(panelPulsanti, BorderLayout.SOUTH);

        // Carica la griglia all'avvio
        caricaGriglia();
    }

    private void caricaGriglia() {
        panelGriglia.removeAll();
        List<Auto> listaAuto = autoDAO.getAll();

        for (Auto auto : listaAuto) {
            JPanel cardAuto = creaMiniatura(auto);
            panelGriglia.add(cardAuto);
        }

        panelGriglia.revalidate();
        panelGriglia.repaint();
    }

    private JPanel creaMiniatura(Auto auto) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Miniatura immagine (ridimensionata automaticamente)
        JLabel lblMiniatura = new JLabel("", JLabel.CENTER);
        lblMiniatura.setPreferredSize(new Dimension(120, 80));
        lblMiniatura.setBackground(new Color(240, 240, 240));
        lblMiniatura.setOpaque(true);

        // Carica immagine e ridimensiona automaticamente
        if (auto.getImmagine() != null && !auto.getImmagine().isEmpty()) {
            File fileImg = new File("resources/images/" + auto.getImmagine());
            if (fileImg.exists()) {
                ImageIcon icon = new ImageIcon(fileImg.getAbsolutePath());
                Image img = icon.getImage().getScaledInstance(120, 80, Image.SCALE_SMOOTH);
                lblMiniatura.setIcon(new ImageIcon(img));
            } else {
                lblMiniatura.setText("🚗");
                lblMiniatura.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
            }
        } else {
            lblMiniatura.setText("🚗");
            lblMiniatura.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        }

        card.add(lblMiniatura, BorderLayout.CENTER);

        // Nome modello sotto la miniatura
        JLabel lblNome = new JLabel("<html><center>" + auto.getMarca() + "<br>" + auto.getModello() + "</center></html>", JLabel.CENTER);
        lblNome.setFont(new Font("Arial", Font.BOLD, 11));
        lblNome.setForeground(new Color(45, 52, 54));
        card.add(lblNome, BorderLayout.SOUTH);

        // Click handler
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selezionaAuto(auto);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 146, 70), 2),
                    BorderFactory.createEmptyBorder(4, 4, 4, 4)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }
        });

        return card;
    }

    private void mostraLogoDefault() {
        File fileLogo = new File("resources/images/logo.jpeg");
        if (fileLogo.exists()) {
            ImageIcon icon = new ImageIcon(fileLogo.getAbsolutePath());
            // Ridimensiona mantenendo le proporzioni
            int maxW = 400;
            int maxH = 300;
            int imgW = icon.getIconWidth();
            int imgH = icon.getIconHeight();

            double ratio = Math.min((double) maxW / imgW, (double) maxH / imgH);
            int newW = (int) (imgW * ratio);
            int newH = (int) (imgH * ratio);

            Image img = icon.getImage().getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
            lblImmagineCorrente.setIcon(new ImageIcon(img));
            lblImmagineCorrente.setText("");
        } else {
            lblImmagineCorrente.setIcon(null);
            lblImmagineCorrente.setText("Seleziona un modello dalla griglia");
        }
    }

    private void selezionaAuto(Auto auto) {
        autoSelezionata = auto;
        lblAutoSelezionata.setText(auto.getMarca() + " " + auto.getModello());
        lblAutoSelezionata.setForeground(new Color(0, 146, 70));
        caricaImmaginiAuto();
    }

    private void caricaImmaginiAuto() {
        if (autoSelezionata == null) return;

        immaginiAuto.clear();
        indiceCorrente = 0;

        // Carica immagine principale
        if (autoSelezionata.getImmagine() != null && !autoSelezionata.getImmagine().isEmpty()) {
            File fileMain = new File("resources/images/" + autoSelezionata.getImmagine());
            if (fileMain.exists()) {
                immaginiAuto.add(autoSelezionata.getImmagine());
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
                    }
                }
            }
        }

        // Abilita/disabilita controlli
        boolean hasImages = !immaginiAuto.isEmpty();
        btnPrecedente.setEnabled(hasImages && immaginiAuto.size() > 1);
        btnSuccessiva.setEnabled(hasImages && immaginiAuto.size() > 1);
        btnElimina.setEnabled(hasImages);
        btnModifica.setEnabled(hasImages);

        if (hasImages) {
            mostraImmagineCorrente();
        } else {
            lblImmagineCorrente.setIcon(null);
            lblImmagineCorrente.setText("Nessuna immagine per questo modello");
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
            // Ridimensiona mantenendo le proporzioni
            int maxW = 500;
            int maxH = 350;
            int imgW = icon.getIconWidth();
            int imgH = icon.getIconHeight();

            double ratio = Math.min((double) maxW / imgW, (double) maxH / imgH);
            int newW = (int) (imgW * ratio);
            int newH = (int) (imgH * ratio);

            Image img = icon.getImage().getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
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
                "⚠️ Seleziona prima un modello dalla griglia!",
                "Attenzione",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleziona un'immagine");
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "Immagini (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif"
        ));
        fileChooser.setMultiSelectionEnabled(true);

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File[] filesSelezionati = fileChooser.getSelectedFiles();
            if (filesSelezionati == null || filesSelezionati.length == 0) {
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
                int prossimoNumero = immaginiAuto.size();

                for (File fileSelezionato : filesSelezionati) {
                    String nomeFile;

                    if (autoSelezionata.getImmagine() == null || autoSelezionata.getImmagine().isEmpty()) {
                        nomeFile = "auto_" + autoSelezionata.getId() + "_" + fileSelezionato.getName();
                        autoSelezionata.setImmagine(nomeFile);
                        autoDAO.aggiorna(autoSelezionata);
                    } else {
                        String nomeBase = autoSelezionata.getImmagine().replaceAll("\\.[^.]+$", "");
                        String nomeFileOriginale = fileSelezionato.getName();
                        String estensione = "";
                        if (nomeFileOriginale.contains(".")) {
                            estensione = nomeFileOriginale.substring(nomeFileOriginale.lastIndexOf("."));
                        }

                        nomeFile = nomeBase + "_" + prossimoNumero + estensione;
                        prossimoNumero++;
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

                caricaGriglia();
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

        boolean isPrincipale = (autoSelezionata.getImmagine() != null &&
                                autoSelezionata.getImmagine().equals(nomeImmagine));

        String messaggio = "Sei sicuro di voler eliminare l'immagine:\n" + nomeImmagine;
        if (isPrincipale) {
            messaggio += "\n\n⚠️ ATTENZIONE: Questa è l'immagine principale!";
        }

        int conferma = JOptionPane.showConfirmDialog(this,
            messaggio,
            "Conferma Eliminazione",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (conferma == JOptionPane.YES_OPTION) {
            File fileImmagine = new File("resources/images/" + nomeImmagine);

            if (fileImmagine.delete()) {
                if (isPrincipale) {
                    autoSelezionata.setImmagine(null);
                    autoDAO.aggiorna(autoSelezionata);
                }

                JOptionPane.showMessageDialog(this,
                    "✅ Immagine eliminata con successo!",
                    "Successo",
                    JOptionPane.INFORMATION_MESSAGE);

                caricaGriglia();
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

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Modifica Immagine", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panelContenuto = new JPanel();
        panelContenuto.setLayout(new BoxLayout(panelContenuto, BoxLayout.Y_AXIS));
        panelContenuto.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

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

        JPanel panelNome = new JPanel(new BorderLayout(10, 0));
        panelNome.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelNome.setMaximumSize(new Dimension(450, 30));
        JLabel lblNuovoNome = new JLabel("Nuovo nome:");
        lblNuovoNome.setPreferredSize(new Dimension(100, 25));

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

        JCheckBox chkPrincipale = new JCheckBox("Imposta come immagine principale");
        chkPrincipale.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkPrincipale.setSelected(isPrincipale);
        chkPrincipale.setEnabled(!isPrincipale);
        panelContenuto.add(chkPrincipale);

        dialog.add(panelContenuto, BorderLayout.CENTER);

        JPanel panelPulsantiDialog = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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

            nuovoNome = nuovoNome + estensioneFinale;

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
                    if (isPrincipale) {
                        autoSelezionata.setImmagine(nuovoNome);
                        autoDAO.aggiorna(autoSelezionata);
                    }

                    JOptionPane.showMessageDialog(dialog,
                        "✅ Immagine rinominata con successo!",
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
            caricaGriglia();
            caricaImmaginiAuto();
        });

        btnAnnulla.addActionListener(e -> dialog.dispose());

        panelPulsantiDialog.add(btnSalva);
        panelPulsantiDialog.add(btnAnnulla);
        dialog.add(panelPulsantiDialog, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    /**
     * Metodo pubblico per aggiornare i dati della schermata.
     */
    public void refresh() {
        caricaGriglia();
        if (autoSelezionata != null) {
            // Ricarica l'auto dal database per avere dati aggiornati
            autoSelezionata = autoDAO.getById(autoSelezionata.getId());
            if (autoSelezionata != null) {
                caricaImmaginiAuto();
            }
        }
    }
}
