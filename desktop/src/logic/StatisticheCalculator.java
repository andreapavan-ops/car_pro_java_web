package logic;

import model.Auto;
import model.Movimento;
import model.Vendita;
//import model.Ordine;
import dao.AutoDAO;
import dao.MovimentoDAO;
import dao.VenditaDAO;
//import dao.OrdineDAO;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
//import java.util.stream.Collectors;

/**
 * Classe che calcola statistiche e metriche sul magazzino e le vendite.
 * Fornisce analisi su vendite, rotazione scorte, trend e performance.
 */
public class StatisticheCalculator {
    
    private AutoDAO autoDAO;
    private MovimentoDAO movimentoDAO;
    private VenditaDAO venditaDAO;
    
    // Costruttore
    public StatisticheCalculator() {
        this.autoDAO = new AutoDAO();
        this.movimentoDAO = new MovimentoDAO();
        this.venditaDAO = new VenditaDAO();
    }
    
    /**
     * Calcola il totale delle vendite in un periodo
     * @param dataInizio Data inizio periodo
     * @param dataFine Data fine periodo
     * @return importo totale vendite
     */
    public double getTotaleVendite(LocalDate dataInizio, LocalDate dataFine) {
        List<Vendita> tutteVendite = venditaDAO.getAll();
        double totale = 0.0;
        
        for (Vendita vendita : tutteVendite) {
            LocalDate dataVendita = vendita.getDataVendita();
            if (dataVendita != null && !dataVendita.isBefore(dataInizio) && !dataVendita.isAfter(dataFine)) {
                totale += vendita.getPrezzoVendita();
            }
        }
        
        return totale;
    }
    
    /**
     * Calcola il numero di auto vendute in un periodo
     * @param dataInizio Data inizio periodo
     * @param dataFine Data fine periodo
     * @return numero di auto vendute
     */
    public int getNumeroVendite(LocalDate dataInizio, LocalDate dataFine) {
        List<Vendita> tutteVendite = venditaDAO.getAll();
        int count = 0;
        
        for (Vendita vendita : tutteVendite) {
            LocalDate dataVendita = vendita.getDataVendita();
            if (dataVendita != null && !dataVendita.isBefore(dataInizio) && !dataVendita.isAfter(dataFine)) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Calcola la media delle vendite giornaliere in un periodo
     * @param dataInizio Data inizio periodo
     * @param dataFine Data fine periodo
     * @return media vendite giornaliere
     */
    public double getMediaVenditeGiornaliere(LocalDate dataInizio, LocalDate dataFine) {
        double totaleVendite = getTotaleVendite(dataInizio, dataFine);
        long giorniPeriodo = ChronoUnit.DAYS.between(dataInizio, dataFine) + 1;
        
        if (giorniPeriodo <= 0) {
            return 0.0;
        }
        
        return totaleVendite / giorniPeriodo;
    }
    
    /**
     * Calcola il prezzo medio di vendita
     * @param dataInizio Data inizio periodo
     * @param dataFine Data fine periodo
     * @return prezzo medio vendita
     */
    public double getPrezzoMedioVendita(LocalDate dataInizio, LocalDate dataFine) {
        List<Vendita> tutteVendite = venditaDAO.getAll();
        double somma = 0.0;
        int count = 0;
        
        for (Vendita vendita : tutteVendite) {
            LocalDate dataVendita = vendita.getDataVendita();
            if (dataVendita != null && !dataVendita.isBefore(dataInizio) && !dataVendita.isAfter(dataFine)) {
                somma += vendita.getPrezzoVendita();
                count++;
            }
        }
        
        return count > 0 ? somma / count : 0.0;
    }
    
    /**
     * Trova l'auto più venduta in un periodo
     * @param dataInizio Data inizio periodo
     * @param dataFine Data fine periodo
     * @return Auto più venduta o null
     */
    public Auto getAutoPiuVenduta(LocalDate dataInizio, LocalDate dataFine) {
        List<Vendita> tutteVendite = venditaDAO.getAll();
        Map<Integer, Integer> conteggioVendite = new HashMap<>();
        
        for (Vendita vendita : tutteVendite) {
            LocalDate dataVendita = vendita.getDataVendita();
            if (dataVendita != null && !dataVendita.isBefore(dataInizio) && !dataVendita.isAfter(dataFine)) {
                int autoId = vendita.getAutoId();
                conteggioVendite.put(autoId, conteggioVendite.getOrDefault(autoId, 0) + 1);
            }
        }
        
        if (conteggioVendite.isEmpty()) {
            return null;
        }
        
        int autoIdPiuVenduta = Collections.max(conteggioVendite.entrySet(), 
            Map.Entry.comparingByValue()).getKey();
        
        return getAutoById(autoIdPiuVenduta);
    }
    
    /**
     * Calcola l'indice di rotazione delle scorte per un'auto
     * Rotazione = Vendite / Giacenza Media
     * @param autoId ID dell'auto
     * @param dataInizio Data inizio periodo
     * @param dataFine Data fine periodo
     * @return indice di rotazione
     */
    public double getIndiceRotazione(int autoId, LocalDate dataInizio, LocalDate dataFine) {
        // Conta vendite nel periodo
        List<Vendita> tutteVendite = venditaDAO.getAll();
        int vendite = 0;
        
        for (Vendita vendita : tutteVendite) {
            if (vendita.getAutoId() == autoId) {
                LocalDate dataVendita = vendita.getDataVendita();
                if (dataVendita != null && !dataVendita.isBefore(dataInizio) && !dataVendita.isAfter(dataFine)) {
                    vendite++;
                }
            }
        }
        
        // Calcola giacenza media
        double giacenzaMedia = calcolaGiacenzaMedia(autoId, dataInizio, dataFine);
        
        if (giacenzaMedia == 0) {
            return 0.0;
        }
        
        return vendite / giacenzaMedia;
    }
    
    /**
     * Calcola la giacenza media di un'auto in un periodo
     * @param autoId ID dell'auto
     * @param dataInizio Data inizio periodo
     * @param dataFine Data fine periodo
     * @return giacenza media
     */
    private double calcolaGiacenzaMedia(int autoId, LocalDate dataInizio, LocalDate dataFine) {
        List<Movimento> movimenti = movimentoDAO.getByAutoId(autoId);
        
        if (movimenti.isEmpty()) {
            Auto auto = getAutoById(autoId);
            return auto != null ? auto.getGiacenza() : 0.0;
        }
        
        // Ordina movimenti per data
        movimenti.sort(Comparator.comparing(Movimento::getDataMovimento));
        
        double sommaGiacenze = 0.0;
        int count = 0;
        
        for (Movimento movimento : movimenti) {
            LocalDateTime dataMovimento = movimento.getDataMovimento();
            if (dataMovimento != null) {
                LocalDate dataMovimentoDate = dataMovimento.toLocalDate();
                if (!dataMovimentoDate.isBefore(dataInizio) && !dataMovimentoDate.isAfter(dataFine)) {
                    sommaGiacenze += movimento.getGiacenzaSuccessiva();
                    count++;
                }
            }
        }
        
        if (count == 0) {
            Auto auto = getAutoById(autoId);
            return auto != null ? auto.getGiacenza() : 0.0;
        }
        
        return sommaGiacenze / count;
    }
    
    /**
     * Genera classifica auto per fatturato
     * @param dataInizio Data inizio periodo
     * @param dataFine Data fine periodo
     * @param limit Numero massimo di risultati
     * @return lista di auto ordinate per fatturato decrescente
     */
    public List<Map.Entry<Auto, Double>> getClassificaPerFatturato(
            LocalDate dataInizio, LocalDate dataFine, int limit) {
        
        List<Vendita> tutteVendite = venditaDAO.getAll();
        Map<Integer, Double> fatturatoPerAuto = new HashMap<>();
        
        for (Vendita vendita : tutteVendite) {
            LocalDate dataVendita = vendita.getDataVendita();
            if (dataVendita != null && !dataVendita.isBefore(dataInizio) && !dataVendita.isAfter(dataFine)) {
                int autoId = vendita.getAutoId();
                double prezzoVendita = vendita.getPrezzoVendita();
                fatturatoPerAuto.put(autoId, 
                    fatturatoPerAuto.getOrDefault(autoId, 0.0) + prezzoVendita);
            }
        }
        
        List<Map.Entry<Auto, Double>> classifica = new ArrayList<>();
        
        for (Map.Entry<Integer, Double> entry : fatturatoPerAuto.entrySet()) {
            Auto auto = getAutoById(entry.getKey());
            if (auto != null) {
                classifica.add(new AbstractMap.SimpleEntry<>(auto, entry.getValue()));
            }
        }
        
        classifica.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));
        
        return classifica.subList(0, Math.min(limit, classifica.size()));
    }
    
    /**
     * Genera classifica auto per numero di vendite
     * @param dataInizio Data inizio periodo
     * @param dataFine Data fine periodo
     * @param limit Numero massimo di risultati
     * @return lista di auto ordinate per numero vendite decrescente
     */
    public List<Map.Entry<Auto, Integer>> getClassificaPerNumeroVendite(
            LocalDate dataInizio, LocalDate dataFine, int limit) {
        
        List<Vendita> tutteVendite = venditaDAO.getAll();
        Map<Integer, Integer> venditePerAuto = new HashMap<>();
        
        for (Vendita vendita : tutteVendite) {
            LocalDate dataVendita = vendita.getDataVendita();
            if (dataVendita != null && !dataVendita.isBefore(dataInizio) && !dataVendita.isAfter(dataFine)) {
                int autoId = vendita.getAutoId();
                venditePerAuto.put(autoId, venditePerAuto.getOrDefault(autoId, 0) + 1);
            }
        }
        
        List<Map.Entry<Auto, Integer>> classifica = new ArrayList<>();
        
        for (Map.Entry<Integer, Integer> entry : venditePerAuto.entrySet()) {
            Auto auto = getAutoById(entry.getKey());
            if (auto != null) {
                classifica.add(new AbstractMap.SimpleEntry<>(auto, entry.getValue()));
            }
        }
        
        classifica.sort((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()));
        
        return classifica.subList(0, Math.min(limit, classifica.size()));
    }
    
    /**
     * Calcola il margine medio di guadagno
     * @param dataInizio Data inizio periodo
     * @param dataFine Data fine periodo
     * @return margine percentuale medio
     */
    public double getMargineMedio(LocalDate dataInizio, LocalDate dataFine) {
        List<Vendita> tutteVendite = venditaDAO.getAll();
        double sommaMargini = 0.0;
        int count = 0;
        
        for (Vendita vendita : tutteVendite) {
            LocalDate dataVendita = vendita.getDataVendita();
            if (dataVendita != null && !dataVendita.isBefore(dataInizio) && !dataVendita.isAfter(dataFine)) {
                Auto auto = getAutoById(vendita.getAutoId());
                if (auto != null && auto.getPrezzo() > 0) {
                    double margine = ((vendita.getPrezzoVendita() - auto.getPrezzo()) 
                                    / auto.getPrezzo()) * 100;
                    sommaMargini += margine;
                    count++;
                }
            }
        }
        
        return count > 0 ? sommaMargini / count : 0.0;
    }
    
    /**
     * Genera un report statistico completo
     * @param dataInizio Data inizio periodo
     * @param dataFine Data fine periodo
     * @return stringa formattata con le statistiche
     */
    public String generaReportCompleto(LocalDate dataInizio, LocalDate dataFine) {
        StringBuilder report = new StringBuilder();
        
        report.append("=== REPORT STATISTICHE ===\n");
        report.append("Periodo: ").append(dataInizio)
              .append(" - ").append(dataFine).append("\n\n");
        
        // Statistiche vendite
        report.append("📊 VENDITE:\n");
        report.append("  - Numero vendite: ").append(getNumeroVendite(dataInizio, dataFine)).append("\n");
        report.append("  - Totale fatturato: €").append(String.format("%.2f", getTotaleVendite(dataInizio, dataFine))).append("\n");
        report.append("  - Prezzo medio vendita: €").append(String.format("%.2f", getPrezzoMedioVendita(dataInizio, dataFine))).append("\n");
        report.append("  - Media vendite giornaliere: €").append(String.format("%.2f", getMediaVenditeGiornaliere(dataInizio, dataFine))).append("\n");
        report.append("  - Margine medio: ").append(String.format("%.2f", getMargineMedio(dataInizio, dataFine))).append("%\n\n");
        
        // Auto più venduta
        Auto autoPiuVenduta = getAutoPiuVenduta(dataInizio, dataFine);
        if (autoPiuVenduta != null) {
            report.append("🏆 AUTO PIÙ VENDUTA:\n");
            report.append("  - ").append(autoPiuVenduta.getMarca()).append(" ")
                  .append(autoPiuVenduta.getModello()).append("\n\n");
        }
        
        // Top 5 per fatturato
        report.append("💰 TOP 5 PER FATTURATO:\n");
        List<Map.Entry<Auto, Double>> topFatturato = getClassificaPerFatturato(dataInizio, dataFine, 5);
        int pos = 1;
        for (Map.Entry<Auto, Double> entry : topFatturato) {
            report.append("  ").append(pos++).append(". ")
                  .append(entry.getKey().getMarca()).append(" ")
                  .append(entry.getKey().getModello())
                  .append(" - €").append(String.format("%.2f", entry.getValue())).append("\n");
        }
        
        return report.toString();
    }
    
    /**
     * Calcola il trend delle vendite (crescita/decrescita rispetto al periodo precedente)
     * @param dataInizio Data inizio periodo corrente
     * @param dataFine Data fine periodo corrente
     * @return percentuale di variazione
     */
    public double getTrendVendite(LocalDate dataInizio, LocalDate dataFine) {
        long giorniPeriodo = ChronoUnit.DAYS.between(dataInizio, dataFine);
        
        LocalDate periodoPrec_inizio = dataInizio.minusDays(giorniPeriodo + 1);
        LocalDate periodoPrec_fine = dataInizio.minusDays(1);
        
        double venditePeriodoCorrente = getTotaleVendite(dataInizio, dataFine);
        double venditePeriodoPrecedente = getTotaleVendite(periodoPrec_inizio, periodoPrec_fine);
        
        if (venditePeriodoPrecedente == 0) {
            return venditePeriodoCorrente > 0 ? 100.0 : 0.0;
        }
        
        return ((venditePeriodoCorrente - venditePeriodoPrecedente) / venditePeriodoPrecedente) * 100;
    }
    
    /**
     * Metodo helper per recuperare un'auto per ID
     */
    private Auto getAutoById(int autoId) {
        List<Auto> tutteAuto = autoDAO.getAll();
        for (Auto auto : tutteAuto) {
            if (auto.getId() == autoId) {
                return auto;
            }
        }
        return null;
    }
}