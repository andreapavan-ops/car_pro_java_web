package dao;

import model.Auto;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AutoDAO {
    
    // Inserisce una nuova auto
    public boolean inserisci(Auto auto) {
        String sql = "INSERT INTO auto (marca, modello, targa, anno, prezzo, giacenza, scorta_minima) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, auto.getMarca());
            pstmt.setString(2, auto.getModello());
            pstmt.setString(3, auto.getTarga());
            pstmt.setInt(4, auto.getAnno());
            pstmt.setDouble(5, auto.getPrezzo());
            pstmt.setInt(6, auto.getGiacenza());
            pstmt.setInt(7, auto.getScortaMinima());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Errore inserimento: " + e.getMessage());
            return false;
        }
    }
    
    // Recupera tutte le auto
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
                lista.add(auto);
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura: " + e.getMessage());
        }
        return lista;
    }
    
    // Aggiorna un'auto esistente
    public boolean aggiorna(Auto auto) {
        String sql = "UPDATE auto SET marca=?, modello=?, targa=?, anno=?, prezzo=?, giacenza=?, scorta_minima=? WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, auto.getMarca());
            pstmt.setString(2, auto.getModello());
            pstmt.setString(3, auto.getTarga());
            pstmt.setInt(4, auto.getAnno());
            pstmt.setDouble(5, auto.getPrezzo());
            pstmt.setInt(6, auto.getGiacenza());
            pstmt.setInt(7, auto.getScortaMinima());
            pstmt.setInt(8, auto.getId());
            
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
}