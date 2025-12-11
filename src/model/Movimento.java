package model;

import java.time.LocalDateTime;

public class Movimento {
    private int id;
    private int autoId;
    private String tipoMovimento;
    private int quantita;
    private LocalDateTime dataMovimento;
    private String causale;
    private String riferimento;
    private int giacenzaPrecedente;
    private int giacenzaSuccessiva;
    
    // Costruttore vuoto
    public Movimento() {}
    
    // Costruttore con parametri principali
    public Movimento(int autoId, String tipoMovimento, int quantita, String causale) {
        this.autoId = autoId;
        this.tipoMovimento = tipoMovimento;
        this.quantita = quantita;
        this.causale = causale;
        this.dataMovimento = LocalDateTime.now();
    }
    
    // Costruttore completo
    public Movimento(int id, int autoId, String tipoMovimento, int quantita,
                     LocalDateTime dataMovimento, String causale, String riferimento,
                     int giacenzaPrecedente, int giacenzaSuccessiva) {
        this.id = id;
        this.autoId = autoId;
        this.tipoMovimento = tipoMovimento;
        this.quantita = quantita;
        this.dataMovimento = dataMovimento;
        this.causale = causale;
        this.riferimento = riferimento;
        this.giacenzaPrecedente = giacenzaPrecedente;
        this.giacenzaSuccessiva = giacenzaSuccessiva;
    }
    
    // Getter e Setter
    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }
    
    public int getAutoId() { 
        return autoId; 
    }
    
    public void setAutoId(int autoId) { 
        this.autoId = autoId; 
    }
    
    public String getTipoMovimento() { 
        return tipoMovimento; 
    }
    
    public void setTipoMovimento(String tipoMovimento) { 
        this.tipoMovimento = tipoMovimento; 
    }
    
    public int getQuantita() { 
        return quantita; 
    }
    
    public void setQuantita(int quantita) { 
        this.quantita = quantita; 
    }
    
    public LocalDateTime getDataMovimento() { 
        return dataMovimento; 
    }
    
    public void setDataMovimento(LocalDateTime dataMovimento) { 
        this.dataMovimento = dataMovimento; 
    }
    
    public String getCausale() { 
        return causale; 
    }
    
    public void setCausale(String causale) { 
        this.causale = causale; 
    }
    
    public String getRiferimento() { 
        return riferimento; 
    }
    
    public void setRiferimento(String riferimento) { 
        this.riferimento = riferimento; 
    }
    
    public int getGiacenzaPrecedente() { 
        return giacenzaPrecedente; 
    }
    
    public void setGiacenzaPrecedente(int giacenzaPrecedente) { 
        this.giacenzaPrecedente = giacenzaPrecedente; 
    }
    
    public int getGiacenzaSuccessiva() { 
        return giacenzaSuccessiva; 
    }
    
    public void setGiacenzaSuccessiva(int giacenzaSuccessiva) { 
        this.giacenzaSuccessiva = giacenzaSuccessiva; 
    }
    
    @Override
    public String toString() {
        return tipoMovimento + " - " + quantita + " unità - " + 
               dataMovimento.toLocalDate() + " (" + causale + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Movimento movimento = (Movimento) obj;
        return id == movimento.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}