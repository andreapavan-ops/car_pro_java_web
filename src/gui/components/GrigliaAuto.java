package gui.components;

import model.Auto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import java.util.Locale;

/**
 * Componente per visualizzare le auto in una griglia di card con immagini
 */
public class GrigliaAuto extends JPanel {
    
    private static final int CARD_WIDTH = 280;
    private static final int CARD_HEIGHT = 320;
    private static final int GAP = 15;
    
    private List<Auto> listaAuto;
    private Consumer<Auto> onAutoClick;
    private JPanel containerGriglia;
    
    public GrigliaAuto() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Container scrollabile
        containerGriglia = new JPanel();
        containerGriglia.setBackground(new Color(245, 245, 245));
        containerGriglia.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JScrollPane scrollPane = new JScrollPane(containerGriglia);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Carica e visualizza la lista di auto
     */
    public void caricaAuto(List<Auto> auto) {
        this.listaAuto = auto;
        aggiornaGriglia();
    }
    
    /**
     * Aggiorna la visualizzazione della griglia
     */
    private void aggiornaGriglia() {
        containerGriglia.removeAll();
        
        if (listaAuto == null || listaAuto.isEmpty()) {
            mostraMessaggioVuoto();
            return;
        }
        
        // Layout a griglia responsive
        int cols = Math.max(1, (getWidth() - 40) / (CARD_WIDTH + GAP));
        containerGriglia.setLayout(new GridLayout(0, cols, GAP, GAP));
        
        // Crea card per ogni auto
        for (Auto auto : listaAuto) {
            JPanel card = creaCardAuto(auto);
            containerGriglia.add(card);
        }
        
        containerGriglia.revalidate();
        containerGriglia.repaint();
    }
    
    /**
     * Crea una card per visualizzare un'auto
     */
    private JPanel creaCardAuto(Auto auto) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        card.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effetto hover
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(248, 249, 250));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 123, 255), 2),
                    new EmptyBorder(14, 14, 14, 14)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                    new EmptyBorder(15, 15, 15, 15)
                ));
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onAutoClick != null) {
                    onAutoClick.accept(auto);
                }
            }
        });
        
        // Sezione immagine/placeholder
        JPanel panelImmagine = creaPanelImmagine(auto);
        card.add(panelImmagine, BorderLayout.NORTH);
        
        // Sezione info
        JPanel panelInfo = creaPanelInfo(auto);
        card.add(panelInfo, BorderLayout.CENTER);
        
        // Sezione footer con prezzo e giacenza
        JPanel panelFooter = creaPanelFooter(auto);
        card.add(panelFooter, BorderLayout.SOUTH);
        
        return card;
    }
    
    /**
     * Crea il panel con l'immagine dell'auto
     */
    private JPanel creaPanelImmagine(Auto auto) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(CARD_WIDTH - 30, 140));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        
        // Prova a caricare l'immagine
        if (auto.getImmagine() != null && !auto.getImmagine().isEmpty()) {
            String percorsoImmagine = auto.getImmagine();
            
            // Se il percorso non è assoluto, cerca in resources/images/
            if (!new File(percorsoImmagine).isAbsolute()) {
                percorsoImmagine = "resources/images/" + auto.getImmagine();
            }
            
            File fileImmagine = new File(percorsoImmagine);
            
            if (fileImmagine.exists()) {
                try {
                    ImageIcon icon = new ImageIcon(fileImmagine.getAbsolutePath());
                    if (icon.getIconWidth() > 0) {
                        // Scala l'immagine mantenendo le proporzioni
                        Image img = icon.getImage();
                        Image scaledImg = scalaImmagine(img, CARD_WIDTH - 30, 140);
                        
                        JLabel lblImmagine = new JLabel(new ImageIcon(scaledImg));
                        lblImmagine.setHorizontalAlignment(JLabel.CENTER);
                        panel.add(lblImmagine, BorderLayout.CENTER);
                        return panel;
                    }
                } catch (Exception e) {
                    System.err.println("Errore caricamento immagine: " + e.getMessage());
                }
            }
        }
        
        // Placeholder con icona auto
        JLabel lblPlaceholder = new JLabel("🚗", JLabel.CENTER);
        lblPlaceholder.setFont(new Font("Arial", Font.PLAIN, 60));
        lblPlaceholder.setForeground(new Color(180, 180, 180));
        panel.add(lblPlaceholder, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Scala un'immagine mantenendo le proporzioni
     */
    private Image scalaImmagine(Image img, int maxWidth, int maxHeight) {
        int originalWidth = img.getWidth(null);
        int originalHeight = img.getHeight(null);
        
        double ratio = Math.min(
            (double) maxWidth / originalWidth,
            (double) maxHeight / originalHeight
        );
        
        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);
        
        return img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
    }
    
    /**
     * Crea il panel con le informazioni dell'auto
     */
    private JPanel creaPanelInfo(Auto auto) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        // Marca e Modello
        JLabel lblMarca = new JLabel(auto.getMarca() + " " + auto.getModello());
        lblMarca.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblMarca.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Anno e Targa
        JLabel lblDettagli = new JLabel("Anno: " + auto.getAnno() + " - " + auto.getTarga());
        lblDettagli.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDettagli.setForeground(new Color(108, 117, 125));
        lblDettagli.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // ID
        JLabel lblId = new JLabel("ID: " + auto.getId());
        lblId.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblId.setForeground(new Color(150, 150, 150));
        lblId.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(lblMarca);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblDettagli);
        panel.add(Box.createVerticalStrut(3));
        panel.add(lblId);
        
        return panel;
    }
    
    /**
     * Crea il panel footer con prezzo e giacenza
     */
    private JPanel creaPanelFooter(Auto auto) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));
        panel.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        
        // Prezzo
        JLabel lblPrezzo = new JLabel(String.format(Locale.ITALIAN, "€ %,.2f", auto.getPrezzo()));
        lblPrezzo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPrezzo.setForeground(new Color(40, 167, 69));
        
        // Giacenza con badge colorato
        JPanel panelGiacenza = creaBadgeGiacenza(auto.getGiacenza(), auto.getScortaMinima());
        
        panel.add(lblPrezzo, BorderLayout.WEST);
        panel.add(panelGiacenza, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Crea un badge colorato per la giacenza
     */
    private JPanel creaBadgeGiacenza(int giacenza, int scortaMinima) {
        JPanel badge = new JPanel();
        badge.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        badge.setOpaque(true);
        
        // Colore basato sulla giacenza
        Color coloreBg;
        Color coloreTxt;
        if (giacenza == 0) {
            coloreBg = new Color(220, 53, 69);    // Rosso
            coloreTxt = Color.WHITE;
        } else if (giacenza <= scortaMinima) {
            coloreBg = new Color(255, 193, 7);    // Giallo
            coloreTxt = new Color(33, 37, 41);
        } else {
            coloreBg = new Color(40, 167, 69);    // Verde
            coloreTxt = Color.WHITE;
        }
        
        badge.setBackground(coloreBg);
        badge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(coloreBg, 1),
            new EmptyBorder(3, 8, 3, 8)
        ));
        
        JLabel lblGiacenza = new JLabel("Giacenza: " + giacenza);
        lblGiacenza.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblGiacenza.setForeground(coloreTxt);
        
        badge.add(lblGiacenza);
        
        return badge;
    }
    
    /**
     * Mostra un messaggio quando non ci sono auto
     */
    private void mostraMessaggioVuoto() {
        containerGriglia.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));
        
        JLabel lblIcona = new JLabel("📦", JLabel.CENTER);
        lblIcona.setFont(new Font("Arial", Font.PLAIN, 80));
        lblIcona.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblMessaggio = new JLabel("Nessuna auto trovata");
        lblMessaggio.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblMessaggio.setForeground(new Color(108, 117, 125));
        lblMessaggio.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(Box.createVerticalGlue());
        panel.add(lblIcona);
        panel.add(Box.createVerticalStrut(20));
        panel.add(lblMessaggio);
        panel.add(Box.createVerticalGlue());
        
        containerGriglia.add(panel, BorderLayout.CENTER);
        containerGriglia.revalidate();
        containerGriglia.repaint();
    }
    
    /**
     * Imposta il listener per il click su un'auto
     */
    public void setOnAutoClickListener(Consumer<Auto> listener) {
        this.onAutoClick = listener;
    }
    
    /**
     * Aggiorna la griglia quando il componente viene ridimensionato
     */
    @Override
    public void doLayout() {
        super.doLayout();
        if (listaAuto != null && !listaAuto.isEmpty()) {
            aggiornaGriglia();
        }
    }
}