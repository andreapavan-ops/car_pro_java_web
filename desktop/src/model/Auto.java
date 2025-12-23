package model;

public class Auto {
    private int id;
    private String marca;
    private String modello;
    private String targa;
    private int anno;
    private double prezzo;
    private int giacenza;
    private int scorta_minima;
    private String immagine;  // ← NUOVO CAMPO
    
    // Costruttore vuoto
    public Auto() {}
    
    // Costruttore completo (senza immagine - per retrocompatibilità)
    public Auto(String marca, String modello, String targa, int anno, 
                double prezzo, int giacenza, int scorta_minima) {
        this.marca = marca;
        this.modello = modello;
        this.targa = targa;
        this.anno = anno;
        this.prezzo = prezzo;
        this.giacenza = giacenza;
        this.scorta_minima = scorta_minima;
    }
    
    // Costruttore completo CON immagine
    public Auto(String marca, String modello, String targa, int anno, 
                double prezzo, int giacenza, int scorta_minima, String immagine) {
        this.marca = marca;
        this.modello = modello;
        this.targa = targa;
        this.anno = anno;
        this.prezzo = prezzo;
        this.giacenza = giacenza;
        this.scorta_minima = scorta_minima;
        this.immagine = immagine;
    }
    
    // Getter e Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    
    public String getModello() { return modello; }
    public void setModello(String modello) { this.modello = modello; }
    
    public String getTarga() { return targa; }
    public void setTarga(String targa) { this.targa = targa; }
    
    public int getAnno() { return anno; }
    public void setAnno(int anno) { this.anno = anno; }
    
    public double getPrezzo() { return prezzo; }
    public void setPrezzo(double prezzo) { this.prezzo = prezzo; }
    
    public int getGiacenza() { return giacenza; }
    public void setGiacenza(int giacenza) { this.giacenza = giacenza; }
    
    public int getScortaMinima() { return scorta_minima; }
    public void setScortaMinima(int scorta_minima) { this.scorta_minima = scorta_minima; }
    
    // ← NUOVO GETTER/SETTER per immagine
    public String getImmagine() { return immagine; }
    public void setImmagine(String immagine) { this.immagine = immagine; }
    
    @Override
    public String toString() {
        return marca + " " + modello + " (" + anno + ")";
    }
}