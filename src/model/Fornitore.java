package model;

public class Fornitore {
    private int id;
    private String nome;
    private String partitaIva;
    private String indirizzo;
    private String telefono;
    private String email;
    private String note;
    
    // Costruttore vuoto
    public Fornitore() {}
    
    // Costruttore con parametri principali
    public Fornitore(String nome, String partitaIva, String telefono, String email) {
        this.nome = nome;
        this.partitaIva = partitaIva;
        this.telefono = telefono;
        this.email = email;
    }
    
    // Costruttore completo
    public Fornitore(int id, String nome, String partitaIva, String indirizzo, 
                     String telefono, String email, String note) {
        this.id = id;
        this.nome = nome;
        this.partitaIva = partitaIva;
        this.indirizzo = indirizzo;
        this.telefono = telefono;
        this.email = email;
        this.note = note;
    }
    
    // Getter e Setter
    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }
    
    public String getNome() { 
        return nome; 
    }
    
    public void setNome(String nome) { 
        this.nome = nome; 
    }
    
    public String getPartitaIva() { 
        return partitaIva; 
    }
    
    public void setPartitaIva(String partitaIva) { 
        this.partitaIva = partitaIva; 
    }
    
    public String getIndirizzo() { 
        return indirizzo; 
    }
    
    public void setIndirizzo(String indirizzo) { 
        this.indirizzo = indirizzo; 
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
    
    public String getNote() { 
        return note; 
    }
    
    public void setNote(String note) { 
        this.note = note; 
    }
    
    @Override
    public String toString() {
        return nome + (partitaIva != null && !partitaIva.isEmpty() ? " (P.IVA: " + partitaIva + ")" : "");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Fornitore fornitore = (Fornitore) obj;
        return id == fornitore.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
