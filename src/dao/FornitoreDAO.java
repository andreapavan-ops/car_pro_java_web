package dao;

import model.Fornitore;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FornitoreDAO {
    
    // Inserisce un nuovo fornitore
    public boolean inserisci(Fornitore fornitore) {
        String sql = "INSERT INTO fornitore (nome, partita_iva, indirizzo, telefono, email, note) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, fornitore.getNome());
            pstmt.setString(2, fornitore.getPartitaIva());
            pstmt.setString(3, fornitore.getIndirizzo());
            pstmt.setString(4, fornitore.getTelefono());
            pstmt.setString(5, fornitore.getEmail());
            pstmt.setString(6, fornitore.getNote());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Errore inserimento fornitore: " + e.getMessage());
            return false;
        }
    }
    
    // Recupera tutti i fornitori
    public List<Fornitore> getAll() {
        List<Fornitore> lista = new ArrayList<>();
        String sql = "SELECT * FROM fornitore ORDER BY nome";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Fornitore fornitore = new Fornitore();
                fornitore.setId(rs.getInt("id"));
                fornitore.setNome(rs.getString("nome"));
                fornitore.setPartitaIva(rs.getString("partita_iva"));
                fornitore.setIndirizzo(rs.getString("indirizzo"));
                fornitore.setTelefono(rs.getString("telefono"));
                fornitore.setEmail(rs.getString("email"));
                fornitore.setNote(rs.getString("note"));
                lista.add(fornitore);
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura fornitori: " + e.getMessage());
        }
        return lista;
    }
    
    // Recupera un fornitore per ID
    public Fornitore getById(int id) {
        String sql = "SELECT * FROM fornitore WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Fornitore fornitore = new Fornitore();
                fornitore.setId(rs.getInt("id"));
                fornitore.setNome(rs.getString("nome"));
                fornitore.setPartitaIva(rs.getString("partita_iva"));
                fornitore.setIndirizzo(rs.getString("indirizzo"));
                fornitore.setTelefono(rs.getString("telefono"));
                fornitore.setEmail(rs.getString("email"));
                fornitore.setNote(rs.getString("note"));
                return fornitore;
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura fornitore: " + e.getMessage());
        }
        return null;
    }
    
    // Aggiorna un fornitore esistente
    public boolean aggiorna(Fornitore fornitore) {
        String sql = "UPDATE fornitore SET nome=?, partita_iva=?, indirizzo=?, telefono=?, email=?, note=? WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, fornitore.getNome());
            pstmt.setString(2, fornitore.getPartitaIva());
            pstmt.setString(3, fornitore.getIndirizzo());
            pstmt.setString(4, fornitore.getTelefono());
            pstmt.setString(5, fornitore.getEmail());
            pstmt.setString(6, fornitore.getNote());
            pstmt.setInt(7, fornitore.getId());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Errore aggiornamento fornitore: " + e.getMessage());
            return false;
        }
    }
    
    // Elimina un fornitore
    public boolean elimina(int id) {
        String sql = "DELETE FROM fornitore WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Errore eliminazione fornitore: " + e.getMessage());
            return false;
        }
    }
}