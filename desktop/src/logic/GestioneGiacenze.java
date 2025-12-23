package logic;

import model.Auto;
import model.Movimento;
import dao.AutoDAO;
import dao.MovimentoDAO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe che gestisce la logica delle giacenze delle auto.
 * Si occupa di carico, scarico e controllo scorte minime.
 */
public class GestioneGiacenze {
    
    private AutoDAO autoDAO;
    private MovimentoDAO movimentoDAO;
    
    // Costruttore
    public GestioneGiacenze() {
        this.autoDAO = new AutoDAO();
        this.movimentoDAO = new MovimentoDAO();
    }
    
    /**
     * Carica una quantità di auto in magazzino (aumenta giacenza)
     * @param autoId ID dell'auto
     * @param quantita Quantità da caricare
     * @param causale Causale del movimento
     * @return true se l'operazione è riuscita
     */
    public boolean carico(int autoId, int quantita, String causale) {
        if (quantita <= 0) {
            System.err.println("Errore: la quantità deve essere positiva");
            return false;
        }
        
        // Recupera l'auto dal database
        List<Auto> tutteAuto = autoDAO.getAll();
        Auto auto = null;
        for (Auto a : tutteAuto) {
            if (a.getId() == autoId) {
                auto = a;
                break;
            }
        }
        
        if (auto == null) {
            System.err.println("Errore: auto non trovata");
            return false;
        }
        
        // Salva giacenza precedente
        int giacenzaPrecedente = auto.getGiacenza();
        
        // Aggiorna la giacenza
        int nuovaGiacenza = giacenzaPrecedente + quantita;
        auto.setGiacenza(nuovaGiacenza);
        
        // Salva l'aggiornamento nel database
        if (!autoDAO.aggiorna(auto)) {
            return false;
        }
        
        // Registra il movimento
        Movimento movimento = new Movimento();
        movimento.setAutoId(autoId);
        movimento.setTipoMovimento("CARICO");
        movimento.setQuantita(quantita);
        movimento.setDataMovimento(LocalDateTime.now());
        movimento.setCausale(causale != null ? causale : "Carico magazzino");
        movimento.setGiacenzaPrecedente(giacenzaPrecedente);
        movimento.setGiacenzaSuccessiva(nuovaGiacenza);
        
        return movimentoDAO.inserisci(movimento);
    }
    
    /**
     * Scarica una quantità di auto dal magazzino (diminuisce giacenza)
     * @param autoId ID dell'auto
     * @param quantita Quantità da scaricare
     * @param causale Causale del movimento
     * @return true se l'operazione è riuscita
     */
    public boolean scarico(int autoId, int quantita, String causale) {
        if (quantita <= 0) {
            System.err.println("Errore: la quantità deve essere positiva");
            return false;
        }
        
        // Recupera l'auto dal database
        List<Auto> tutteAuto = autoDAO.getAll();
        Auto auto = null;
        for (Auto a : tutteAuto) {
            if (a.getId() == autoId) {
                auto = a;
                break;
            }
        }
        
        if (auto == null) {
            System.err.println("Errore: auto non trovata");
            return false;
        }
        
        // Verifica disponibilità
        if (auto.getGiacenza() < quantita) {
            System.err.println("Errore: giacenza insufficiente. Disponibili: " + 
                             auto.getGiacenza() + ", richiesti: " + quantita);
            return false;
        }
        
        // Salva giacenza precedente
        int giacenzaPrecedente = auto.getGiacenza();
        
        // Aggiorna la giacenza
        int nuovaGiacenza = giacenzaPrecedente - quantita;
        auto.setGiacenza(nuovaGiacenza);
        
        // Salva l'aggiornamento nel database
        if (!autoDAO.aggiorna(auto)) {
            return false;
        }
        
        // Registra il movimento
        Movimento movimento = new Movimento();
        movimento.setAutoId(autoId);
        movimento.setTipoMovimento("SCARICO");
        movimento.setQuantita(quantita);
        movimento.setDataMovimento(LocalDateTime.now());
        movimento.setCausale(causale != null ? causale : "Scarico magazzino");
        movimento.setGiacenzaPrecedente(giacenzaPrecedente);
        movimento.setGiacenzaSuccessiva(nuovaGiacenza);
        
        return movimentoDAO.inserisci(movimento);
    }
    
    /**
     * Verifica se un'auto ha giacenza sufficiente
     * @param autoId ID dell'auto
     * @param quantitaRichiesta Quantità da verificare
     * @return true se la giacenza è sufficiente
     */
    public boolean verificaDisponibilita(int autoId, int quantitaRichiesta) {
        List<Auto> tutteAuto = autoDAO.getAll();
        for (Auto auto : tutteAuto) {
            if (auto.getId() == autoId) {
                return auto.getGiacenza() >= quantitaRichiesta;
            }
        }
        return false;
    }
    
    /**
     * Restituisce la giacenza attuale di un'auto
     * @param autoId ID dell'auto
     * @return giacenza attuale, -1 se auto non trovata
     */
    public int getGiacenzaAttuale(int autoId) {
        List<Auto> tutteAuto = autoDAO.getAll();
        for (Auto auto : tutteAuto) {
            if (auto.getId() == autoId) {
                return auto.getGiacenza();
            }
        }
        return -1;
    }
    
    /**
     * Verifica se un'auto è sotto scorta minima
     * @param autoId ID dell'auto
     * @return true se è sotto scorta minima
     */
    public boolean isSottoScortaMinima(int autoId) {
        List<Auto> tutteAuto = autoDAO.getAll();
        for (Auto auto : tutteAuto) {
            if (auto.getId() == autoId) {
                return auto.getGiacenza() < auto.getScortaMinima();
            }
        }
        return false;
    }
    
    /**
     * Restituisce tutte le auto sotto scorta minima
     * @return lista di auto sotto scorta
     */
    public List<Auto> getAutoSottoScorta() {
        List<Auto> tutteAuto = autoDAO.getAll();
        List<Auto> sottoScorta = new ArrayList<>();
        
        for (Auto auto : tutteAuto) {
            if (auto.getGiacenza() < auto.getScortaMinima()) {
                sottoScorta.add(auto);
            }
        }
        
        return sottoScorta;
    }
    
    /**
     * Restituisce tutte le auto con giacenza zero
     * @return lista di auto esaurite
     */
    public List<Auto> getAutoEsaurite() {
        List<Auto> tutteAuto = autoDAO.getAll();
        List<Auto> esaurite = new ArrayList<>();
        
        for (Auto auto : tutteAuto) {
            if (auto.getGiacenza() == 0) {
                esaurite.add(auto);
            }
        }
        
        return esaurite;
    }
    
    /**
     * Calcola il valore totale del magazzino
     * @return valore totale (prezzo * giacenza di tutte le auto)
     */
    public double calcolaValoreMagazzino() {
        List<Auto> tutteAuto = autoDAO.getAll();
        double valoreTotale = 0.0;
        
        for (Auto auto : tutteAuto) {
            valoreTotale += auto.getPrezzo() * auto.getGiacenza();
        }
        
        return valoreTotale;
    }
    
    /**
     * Conta il numero totale di auto in magazzino
     * @return totale auto in giacenza
     */
    public int contaTotaleAutoInMagazzino() {
        List<Auto> tutteAuto = autoDAO.getAll();
        int totale = 0;
        
        for (Auto auto : tutteAuto) {
            totale += auto.getGiacenza();
        }
        
        return totale;
    }
    
    /**
     * Aggiusta la giacenza manualmente (per correzioni inventario)
     * @param autoId ID dell'auto
     * @param nuovaGiacenza Nuova giacenza da impostare
     * @param motivazione Motivazione della correzione
     * @return true se l'operazione è riuscita
     */
    public boolean correzioneInventario(int autoId, int nuovaGiacenza, String motivazione) {
        if (nuovaGiacenza < 0) {
            System.err.println("Errore: la giacenza non può essere negativa");
            return false;
        }
        
        // Recupera l'auto dal database
        List<Auto> tutteAuto = autoDAO.getAll();
        Auto auto = null;
        for (Auto a : tutteAuto) {
            if (a.getId() == autoId) {
                auto = a;
                break;
            }
        }
        
        if (auto == null) {
            System.err.println("Errore: auto non trovata");
            return false;
        }
        
        int giacenzaPrecedente = auto.getGiacenza();
        int differenza = nuovaGiacenza - giacenzaPrecedente;
        
        // Aggiorna la giacenza
        auto.setGiacenza(nuovaGiacenza);
        
        if (!autoDAO.aggiorna(auto)) {
            return false;
        }
        
        // Registra il movimento di correzione
        Movimento movimento = new Movimento();
        movimento.setAutoId(autoId);
        movimento.setTipoMovimento("CORREZIONE");
        movimento.setQuantita(Math.abs(differenza));
        movimento.setDataMovimento(LocalDateTime.now());
        movimento.setCausale("Correzione inventario: " + motivazione);
        movimento.setRiferimento("Da " + giacenzaPrecedente + " a " + nuovaGiacenza);
        movimento.setGiacenzaPrecedente(giacenzaPrecedente);
        movimento.setGiacenzaSuccessiva(nuovaGiacenza);
        
        return movimentoDAO.inserisci(movimento);
    }
    
    /**
     * Ottiene lo storico movimenti di un'auto
     * @param autoId ID dell'auto
     * @return lista dei movimenti
     */
    public List<Movimento> getStoricoMovimenti(int autoId) {
        return movimentoDAO.getByAutoId(autoId);
    }
    
    /**
     * Genera un report di allerta per auto sotto scorta
     * @return stringa formattata con le allerte
     */
    public String generaReportAllerta() {
        List<Auto> sottoScorta = getAutoSottoScorta();
        List<Auto> esaurite = getAutoEsaurite();
        
        StringBuilder report = new StringBuilder();
        report.append("=== REPORT ALLERTA GIACENZE ===\n\n");
        
        if (!esaurite.isEmpty()) {
            report.append("⚠️ AUTO ESAURITE (" + esaurite.size() + "):\n");
            for (Auto auto : esaurite) {
                report.append("  - " + auto.getMarca() + " " + auto.getModello() + 
                            " (ID: " + auto.getId() + ")\n");
            }
            report.append("\n");
        }
        
        if (!sottoScorta.isEmpty()) {
            report.append("⚡ AUTO SOTTO SCORTA MINIMA (" + sottoScorta.size() + "):\n");
            for (Auto auto : sottoScorta) {
                report.append("  - " + auto.getMarca() + " " + auto.getModello() + 
                            " - Giacenza: " + auto.getGiacenza() + 
                            " / Min: " + auto.getScortaMinima() + "\n");
            }
            report.append("\n");
        }
        
        if (esaurite.isEmpty() && sottoScorta.isEmpty()) {
            report.append("✅ Tutte le auto hanno giacenze adeguate\n");
        }
        
        report.append("\n📊 Riepilogo magazzino:\n");
        report.append("  - Totale auto in magazzino: " + contaTotaleAutoInMagazzino() + "\n");
        report.append("  - Valore totale magazzino: €" + 
                     String.format("%.2f", calcolaValoreMagazzino()) + "\n");
        
        return report.toString();
    }
}