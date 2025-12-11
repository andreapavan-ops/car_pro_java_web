package model;

public class RigaOrdine {
    private int id;
    private int ordineId;
    private int autoId;
    private int quantita;
    private double prezzoUnitario;
    private double subtotale;
    
    // Costruttore vuoto
    public RigaOrdine() {}
    
    // Costruttore con parametri principali
    public RigaOrdine(int ordineId, int autoId, int quantita, double prezzoUnitario) {
        this.ordineId = ordineId;
        this.autoId = autoId;
        this.quantita = quantita;
        this.prezzoUnitario = prezzoUnitario;
        this.subtotale = quantita * prezzoUnitario;
    }
    
    // Costruttore completo
    public RigaOrdine(int id, int ordineId, int autoId, int quantita, 
                      double prezzoUnitario, double subtotale) {
        this.id = id;
        this.ordineId = ordineId;
        this.autoId = autoId;
        this.quantita = quantita;
        this.prezzoUnitario = prezzoUnitario;
        this.subtotale = subtotale;
    }
    
    // Getter e Setter
    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }
    
    public int getOrdineId() { 
        return ordineId; 
    }
    
    public void setOrdineId(int ordineId) { 
        this.ordineId = ordineId; 
    }
    
    public int getAutoId() { 
        return autoId; 
    }
    
    public void setAutoId(int autoId) { 
        this.autoId = autoId; 
    }
    
    public int getQuantita() { 
        return quantita; 
    }
    
    public void setQuantita(int quantita) { 
        this.quantita = quantita;
        this.subtotale = quantita * prezzoUnitario;
    }
    
    public double getPrezzoUnitario() { 
        return prezzoUnitario; 
    }
    
    public void setPrezzoUnitario(double prezzoUnitario) { 
        this.prezzoUnitario = prezzoUnitario;
        this.subtotale = quantita * prezzoUnitario;
    }
    
    public double getSubtotale() { 
        return subtotale; 
    }
    
    public void setSubtotale(double subtotale) { 
        this.subtotale = subtotale; 
    }
    
    // Metodo per ricalcolare il subtotale
    public void calcolaSubtotale() {
        this.subtotale = this.quantita * this.prezzoUnitario;
    }
    
    @Override
    public String toString() {
        return "Riga Ordine #" + id + " - Quantità: " + quantita + " - Subtotale: €" + 
               String.format("%.2f", subtotale);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RigaOrdine riga = (RigaOrdine) obj;
        return id == riga.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}