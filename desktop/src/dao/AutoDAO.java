package dao;

import model.Auto;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AutoDAO {
    
    // Inserisce una nuova auto CON IMMAGINE
    public boolean inserisci(Auto auto) {
        String sql = "INSERT INTO auto (marca, modello, targa, anno, prezzo, giacenza, scorta_minima, immagine) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, auto.getMarca());
            pstmt.setString(2, auto.getModello());
            pstmt.setString(3, auto.getTarga());
            pstmt.setInt(4, auto.getAnno());
            pstmt.setDouble(5, auto.getPrezzo());
            pstmt.setInt(6, auto.getGiacenza());
            pstmt.setInt(7, auto.getScortaMinima());
            pstmt.setString(8, auto.getImmagine());  // ← AGGIUNTO
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Errore inserimento: " + e.getMessage());
            return false;
        }
    }
    
    // Recupera tutte le auto CON IMMAGINE
    public List<Auto> getAll() {
        List<Auto> lista = new ArrayList<>();
        String sql = "SELECT * FROM auto ORDER BY marca, modello";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Auto auto = new Auto();
                auto.setId(rs.getInt("id"));
                auto.setMarca(rs.getString("marca"));
                auto.setModello(rs.getString("modello"));
                auto.setTarga(rs.getString("targa"));
                auto.setAnno(rs.getInt("anno"));
                auto.setPrezzo(rs.getDouble("prezzo"));
                auto.setGiacenza(rs.getInt("giacenza"));
                auto.setScortaMinima(rs.getInt("scorta_minima"));
                auto.setImmagine(rs.getString("immagine"));  // ← AGGIUNTO
                lista.add(auto);
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura: " + e.getMessage());
        }
        return lista;
    }
    
    // Recupera un'auto per ID CON IMMAGINE
    public Auto getById(int id) {
        String sql = "SELECT * FROM auto WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Auto auto = new Auto();
                auto.setId(rs.getInt("id"));
                auto.setMarca(rs.getString("marca"));
                auto.setModello(rs.getString("modello"));
                auto.setTarga(rs.getString("targa"));
                auto.setAnno(rs.getInt("anno"));
                auto.setPrezzo(rs.getDouble("prezzo"));
                auto.setGiacenza(rs.getInt("giacenza"));
                auto.setScortaMinima(rs.getInt("scorta_minima"));
                auto.setImmagine(rs.getString("immagine"));  // ← AGGIUNTO
                return auto;
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura auto per ID: " + e.getMessage());
        }
        return null;
    }
    
    // Aggiorna un'auto esistente CON IMMAGINE
    public boolean aggiorna(Auto auto) {
        String sql = "UPDATE auto SET marca=?, modello=?, targa=?, anno=?, prezzo=?, giacenza=?, scorta_minima=?, immagine=? WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, auto.getMarca());
            pstmt.setString(2, auto.getModello());
            pstmt.setString(3, auto.getTarga());
            pstmt.setInt(4, auto.getAnno());
            pstmt.setDouble(5, auto.getPrezzo());
            pstmt.setInt(6, auto.getGiacenza());
            pstmt.setInt(7, auto.getScortaMinima());
            pstmt.setString(8, auto.getImmagine());  // ← AGGIUNTO
            pstmt.setInt(9, auto.getId());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Errore aggiornamento: " + e.getMessage());
            return false;
        }
    }
    
    // Elimina un'auto
    public boolean elimina(int id) {
        String sql = "DELETE FROM auto WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Errore eliminazione: " + e.getMessage());
            return false;
        }
    }
    
    // Recupera le auto sotto scorta minima CON IMMAGINE
    public List<Auto> getAutoSottoScorta() {
        List<Auto> lista = new ArrayList<>();
        String sql = "SELECT * FROM auto WHERE giacenza <= scorta_minima ORDER BY giacenza ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Auto auto = new Auto();
                auto.setId(rs.getInt("id"));
                auto.setMarca(rs.getString("marca"));
                auto.setModello(rs.getString("modello"));
                auto.setTarga(rs.getString("targa"));
                auto.setAnno(rs.getInt("anno"));
                auto.setPrezzo(rs.getDouble("prezzo"));
                auto.setGiacenza(rs.getInt("giacenza"));
                auto.setScortaMinima(rs.getInt("scorta_minima"));
                auto.setImmagine(rs.getString("immagine"));  // ← AGGIUNTO
                lista.add(auto);
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura auto sotto scorta: " + e.getMessage());
        }
        return lista;
    }
}