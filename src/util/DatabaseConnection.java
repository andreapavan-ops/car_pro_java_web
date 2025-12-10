package util;

import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:database/carpro.db";
    private static Connection connection = null;
    
    // Carica il driver esplicitamente
    static {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("✅ Driver SQLite caricato!");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver SQLite non trovato: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL);
                System.out.println("✅ Connessione al database riuscita!");
                creaTabelle();
            }
        } catch (SQLException e) {
            System.err.println("❌ Errore connessione database: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }
    
    private static void creaTabelle() {
        String sqlAuto = """
            CREATE TABLE IF NOT EXISTS auto (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                marca TEXT NOT NULL,
                modello TEXT NOT NULL,
                targa TEXT UNIQUE,
                anno INTEGER,
                prezzo REAL,
                giacenza INTEGER DEFAULT 0,
                scorta_minima INTEGER DEFAULT 5
            );
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlAuto);
            System.out.println("✅ Tabella 'auto' pronta!");
        } catch (SQLException e) {
            System.err.println("❌ Errore creazione tabelle: " + e.getMessage());
        }
    }
}