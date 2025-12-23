package model;

public class Categoria {
    private int id;
    private String nome;
    private String descrizione;
    
    // Costruttore vuoto
    public Categoria() {}
    
    // Costruttore con parametri principali
    public Categoria(String nome, String descrizione) {
        this.nome = nome;
        this.descrizione = descrizione;
    }
    
    // Costruttore completo
    public Categoria(int id, String nome, String descrizione) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
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
    
    public String getDescrizione() { 
        return descrizione; 
    }
    
    public void setDescrizione(String descrizione) { 
        this.descrizione = descrizione; 
    }
    
    @Override
    public String toString() {
        return nome;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Categoria categoria = (Categoria) obj;
        return id == categoria.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
//Questa classe ti permetterà di organizzare le auto per categoria nel tuo gestionale. Dovrai poi collegare le categorie alle auto, probabilmente aggiungendo un campo categoriaId nella classe Auto o creando una relazione nel database.
//Ti serve aiuto con gli altri file del model o vuoi procedere con i DAO?