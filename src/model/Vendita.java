package model;

import java.time.LocalDate;

public class Vendita {
    private int id;
    private int autoId;
    private LocalDate dataVendita;
    private String nomeCliente;
    private String cognomeCliente;
    private String codiceFiscale;
    private String telefono;
    private String email;
    private double prezzoVendita;
    private String metodoPagamento;
    private String note;
    
    // Costruttore vuoto
    public Vendita() {}
    
    // Costruttore con parametri principali
    public Vendita(int autoId, LocalDate dataVendita, String nomeCliente, 
                   String cognomeCliente, double prezzoVendita) {
        this.autoId = autoId;
        this.dataVendita = dataVendita;
        this.nomeCliente = nomeCliente;
        this.cognomeCliente = cognomeCliente;
        this.prezzoVendita = prezzoVendita;
    }
    
    // Costruttore completo
    public Vendita(int id, int autoId, LocalDate dataVendita, String nomeCliente,
                   String cognomeCliente, String codiceFiscale, String telefono,
                   String email, double prezzoVendita, String metodoPagamento, String note) {
        this.id = id;
        this.autoId = autoId;
        this.dataVendita = dataVendita;
        this.nomeCliente = nomeCliente;
        this.cognomeCliente = cognomeCliente;
        this.codiceFiscale = codiceFiscale;
        this.telefono = telefono;
        this.email = email;
        this.prezzoVendita = prezzoVendita;
        this.metodoPagamento = metodoPagamento;
        this.note = note;
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
    
    public LocalDate getDataVendita() { 
        return dataVendita; 
    }
    
    public void setDataVendita(LocalDate dataVendita) { 
        this.dataVendita = dataVendita; 
    }
    
    public String getNomeCliente() { 
        return nomeCliente; 
    }
    
    public void setNomeCliente(String nomeCliente) { 
        this.nomeCliente = nomeCliente; 
    }
    
    public String getCognomeCliente() { 
        return cognomeCliente; 
    }
    
    public void setCognomeCliente(String cognomeCliente) { 
        this.cognomeCliente = cognomeCliente; 
    }
    
    public String getCodiceFiscale() { 
        return codiceFiscale; 
    }
    
    public void setCodiceFiscale(String codiceFiscale) { 
        this.codiceFiscale = codiceFiscale; 
    }
    
    public String getTelefono() { 
        return telefono; 
    }
    
    public void setTelefono(String telefono) { 
        this.telefono = telefono; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }
    
    public double getPrezzoVendita() { 
        return prezzoVendita; 
    }
    
    public void setPrezzoVendita(double prezzoVendita) { 
        this.prezzoVendita = prezzoVendita; 
    }
    
    public String getMetodoPagamento() { 
        return metodoPagamento; 
    }
    
    public void setMetodoPagamento(String metodoPagamento) { 
        this.metodoPagamento = metodoPagamento; 
    }
    
    public String getNote() { 
        return note; 
    }
    
    public void setNote(String note) { 
        this.note = note; 
    }
    
    // Metodo di utilità per ottenere il nome completo del cliente
    public String getNomeCompletoCliente() {
        return nomeCliente + " " + cognomeCliente;
    }
    
    @Override
    public String toString() {
        return "Vendita #" + id + " - " + getNomeCompletoCliente() + " - " + 
               dataVendita + " - €" + String.format("%.2f", prezzoVendita);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vendita vendita = (Vendita) obj;
        return id == vendita.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}