package model;

import java.time.LocalDate;

public class Ordine {
    private int id;
    private int fornitoreId;
    private LocalDate dataOrdine;
    private LocalDate dataConsegna;
    private String stato;
    private double totale;
    private String note;
    
    // Costruttore vuoto
    public Ordine() {}
    
    // Costruttore con parametri principali
    public Ordine(int fornitoreId, LocalDate dataOrdine, String stato) {
        this.fornitoreId = fornitoreId;
        this.dataOrdine = dataOrdine;
        this.stato = stato;
        this.totale = 0.0;
    }
    
    // Costruttore completo
    public Ordine(int id, int fornitoreId, LocalDate dataOrdine, LocalDate dataConsegna,
                  String stato, double totale, String note) {
        this.id = id;
        this.fornitoreId = fornitoreId;
        this.dataOrdine = dataOrdine;
        this.dataConsegna = dataConsegna;
        this.stato = stato;
        this.totale = totale;
        this.note = note;
    }
    
    // Getter e Setter
    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }
    
    public int getFornitoreId() { 
        return fornitoreId; 
    }
    
    public void setFornitoreId(int fornitoreId) { 
        this.fornitoreId = fornitoreId; 
    }
    
    public LocalDate getDataOrdine() { 
        return dataOrdine; 
    }
    
    public void setDataOrdine(LocalDate dataOrdine) { 
        this.dataOrdine = dataOrdine; 
    }
    
    public LocalDate getDataConsegna() { 
        return dataConsegna; 
    }
    
    public void setDataConsegna(LocalDate dataConsegna) { 
        this.dataConsegna = dataConsegna; 
    }
    
    public String getStato() { 
        return stato; 
    }
    
    public void setStato(String stato) { 
        this.stato = stato; 
    }
    
    public double getTotale() { 
        return totale; 
    }
    
    public void setTotale(double totale) { 
        this.totale = totale; 
    }
    
    public String getNote() { 
        return note; 
    }
    
    public void setNote(String note) { 
        this.note = note; 
    }
    
    @Override
    public String toString() {
        return "Ordine #" + id + " - " + dataOrdine + " (" + stato + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ordine ordine = (Ordine) obj;
        return id == ordine.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}