package dao;

import model.Categoria;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {
    
    // Inserisce una nuova categoria
    public boolean inserisci(Categoria categoria) {
        String sql = "INSERT INTO categoria (nome, descrizione) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, categoria.getNome());
            pstmt.setString(2, categoria.getDescrizione());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Errore inserimento categoria: " + e.getMessage());
            return false;
        }
    }
    
    // Recupera tutte le categorie
    public List<Categoria> getAll() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM categoria ORDER BY nome";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setId(rs.getInt("id"));
                categoria.setNome(rs.getString("nome"));
                categoria.setDescrizione(rs.getString("descrizione"));
                lista.add(categoria);
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura categorie: " + e.getMessage());
        }
        return lista;
    }
    
    // Recupera una categoria per ID
    public Categoria getById(int id) {
        String sql = "SELECT * FROM categoria WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setId(rs.getInt("id"));
                categoria.setNome(rs.getString("nome"));
                categoria.setDescrizione(rs.getString("descrizione"));
                return categoria;
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura categoria: " + e.getMessage());
        }
        return null;
    }
    
    // Aggiorna una categoria esistente
    public boolean aggiorna(Categoria categoria) {
        String sql = "UPDATE categoria SET nome=?, descrizione=? WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, categoria.getNome());
            pstmt.setString(2, categoria.getDescrizione());
            pstmt.setInt(3, categoria.getId());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Errore aggiornamento categoria: " + e.getMessage());
            return false;
        }
    }
    
    // Elimina una categoria
    public boolean elimina(int id) {
        String sql = "DELETE FROM categoria WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Errore eliminazione categoria: " + e.getMessage());
            return false;
        }
    }
}