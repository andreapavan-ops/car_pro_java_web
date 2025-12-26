package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    
    // Percorso del database SQLite
    private static final String DB_URL = "jdbc:sqlite:database/CarPro.db";
    
    // Metodo per ottenere la connessione al database
    public static Connection getConnection() throws SQLException {
        try {
            // Carica il driver JDBC per SQLite
            Class.forName("org.sqlite.JDBC");
            
            // Crea la connessione
            Connection conn = DriverManager.getConnection(DB_URL);
            
            // Abilita le foreign keys (importante per l'integrità referenziale)
            Statement stmt = conn.createStatement();
            stmt.execute("PRAGMA foreign_keys = ON");
            stmt.close();
            
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("Driver SQLite non trovato! Aggiungi sqlite-jdbc-X.X.X.jar alla cartella lib/");
            throw new SQLException("Driver SQLite non disponibile", e);
        }
    }
    
    // Metodo per testare la connessione
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Connessione al database riuscita!");
                System.out.println("📍 Database: " + DB_URL);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Errore di connessione al database: " + e.getMessage());
            return false;
        }
        return false;
    }
    
    // Metodo per inizializzare il database (crea le tabelle se non esistono)
    public static void inizializzaDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Crea tabella AUTO (targa opzionale, senza vincolo UNIQUE)
            String sqlAuto = """
                CREATE TABLE IF NOT EXISTS auto (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    marca TEXT NOT NULL,
                    modello TEXT NOT NULL,
                    targa TEXT,
                    anno INTEGER,
                    prezzo REAL NOT NULL,
                    giacenza INTEGER DEFAULT 0,
                    scorta_minima INTEGER DEFAULT 5,
                    immagine TEXT
                )
            """;
            stmt.execute(sqlAuto);
            
            // Crea tabella FORNITORE
            String sqlFornitore = """
                CREATE TABLE IF NOT EXISTS fornitore (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    partita_iva TEXT,
                    indirizzo TEXT,
                    telefono TEXT,
                    email TEXT,
                    note TEXT
                )
            """;
            stmt.execute(sqlFornitore);
            
            // Crea tabella ORDINE
            String sqlOrdine = """
                CREATE TABLE IF NOT EXISTS ordine (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    fornitore_id INTEGER NOT NULL,
                    data_ordine DATE NOT NULL,
                    data_consegna DATE,
                    stato TEXT DEFAULT 'PENDENTE',
                    totale REAL DEFAULT 0.0,
                    note TEXT,
                    FOREIGN KEY (fornitore_id) REFERENCES fornitore(id) ON DELETE CASCADE
                )
            """;
            stmt.execute(sqlOrdine);
            
            // Crea tabella RIGA_ORDINE
            String sqlRigaOrdine = """
                CREATE TABLE IF NOT EXISTS riga_ordine (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    ordine_id INTEGER NOT NULL,
                    auto_id INTEGER NOT NULL,
                    quantita INTEGER NOT NULL,
                    prezzo_unitario REAL NOT NULL,
                    subtotale REAL NOT NULL,
                    FOREIGN KEY (ordine_id) REFERENCES ordine(id) ON DELETE CASCADE,
                    FOREIGN KEY (auto_id) REFERENCES auto(id) ON DELETE CASCADE
                )
            """;
            stmt.execute(sqlRigaOrdine);
            
            // Crea tabella MOVIMENTO
            String sqlMovimento = """
                CREATE TABLE IF NOT EXISTS movimento (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    auto_id INTEGER NOT NULL,
                    tipo_movimento TEXT NOT NULL,
                    quantita INTEGER NOT NULL,
                    data_movimento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    causale TEXT,
                    riferimento TEXT,
                    giacenza_precedente INTEGER,
                    giacenza_successiva INTEGER,
                    FOREIGN KEY (auto_id) REFERENCES auto(id) ON DELETE CASCADE
                )
            """;
            stmt.execute(sqlMovimento);
            
            // Crea tabella VENDITA
            String sqlVendita = """
                CREATE TABLE IF NOT EXISTS vendita (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    auto_id INTEGER NOT NULL,
                    data_vendita DATE NOT NULL,
                    nome_cliente TEXT,
                    cognome_cliente TEXT,
                    codice_fiscale TEXT,
                    telefono TEXT,
                    email TEXT,
                    prezzo_vendita REAL NOT NULL,
                    metodo_pagamento TEXT,
                    note TEXT,
                    FOREIGN KEY (auto_id) REFERENCES auto(id) ON DELETE CASCADE
                )
            """;
            stmt.execute(sqlVendita);
            
            System.out.println("✅ Database inizializzato con successo!");
            System.out.println("📊 Tabelle create: auto, fornitore, ordine, riga_ordine, movimento, vendita");
            
        } catch (SQLException e) {
            System.err.println("❌ Errore durante l'inizializzazione del database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Metodo main per testare la connessione e inizializzare il database
    public static void main(String[] args) {
        System.out.println("=== TEST DATABASE CONNECTION ===\n");
        
        // Test connessione
        if (testConnection()) {
            System.out.println("\n=== INIZIALIZZAZIONE DATABASE ===\n");
            // Inizializza database
            inizializzaDatabase();
        }
    }
}