package dao;

import model.Ordine;
import model.RigaOrdine;
import util.DatabaseConnection;
import java.sql.*;
//import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrdineDAO {
    
    // Inserisce un nuovo ordine
    public int inserisci(Ordine ordine) {
        String sql = "INSERT INTO ordine (fornitore_id, data_ordine, data_consegna, stato, totale, note) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, ordine.getFornitoreId());
            pstmt.setDate(2, Date.valueOf(ordine.getDataOrdine()));
            pstmt.setDate(3, ordine.getDataConsegna() != null ? Date.valueOf(ordine.getDataConsegna()) : null);
            pstmt.setString(4, ordine.getStato());
            pstmt.setDouble(5, ordine.getTotale());
            pstmt.setString(6, ordine.getNote());
            
            pstmt.executeUpdate();
            
            // Recupera l'ID generato
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Errore inserimento ordine: " + e.getMessage());
        }
        return -1;
    }
    
    // Recupera tutti gli ordini
    public List<Ordine> getAll() {
        List<Ordine> lista = new ArrayList<>();
        String sql = "SELECT * FROM ordine ORDER BY data_ordine DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Ordine ordine = new Ordine();
                ordine.setId(rs.getInt("id"));
                ordine.setFornitoreId(rs.getInt("fornitore_id"));
                ordine.setDataOrdine(rs.getDate("data_ordine").toLocalDate());
                
                Date dataConsegna = rs.getDate("data_consegna");
                if (dataConsegna != null) {
                    ordine.setDataConsegna(dataConsegna.toLocalDate());
                }
                
                ordine.setStato(rs.getString("stato"));
                ordine.setTotale(rs.getDouble("totale"));
                ordine.setNote(rs.getString("note"));
                lista.add(ordine);
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura ordini: " + e.getMessage());
        }
        return lista;
    }
    
    // Recupera un ordine per ID
    public Ordine getById(int id) {
        String sql = "SELECT * FROM ordine WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Ordine ordine = new Ordine();
                ordine.setId(rs.getInt("id"));
                ordine.setFornitoreId(rs.getInt("fornitore_id"));
                ordine.setDataOrdine(rs.getDate("data_ordine").toLocalDate());
                
                Date dataConsegna = rs.getDate("data_consegna");
                if (dataConsegna != null) {
                    ordine.setDataConsegna(dataConsegna.toLocalDate());
                }
                
                ordine.setStato(rs.getString("stato"));
                ordine.setTotale(rs.getDouble("totale"));
                ordine.setNote(rs.getString("note"));
                return ordine;
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura ordine: " + e.getMessage());
        }
        return null;
    }
    
    // Aggiorna un ordine esistente
    public boolean aggiorna(Ordine ordine) {
        String sql = "UPDATE ordine SET fornitore_id=?, data_ordine=?, data_consegna=?, stato=?, totale=?, note=? WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, ordine.getFornitoreId());
            pstmt.setDate(2, Date.valueOf(ordine.getDataOrdine()));
            pstmt.setDate(3, ordine.getDataConsegna() != null ? Date.valueOf(ordine.getDataConsegna()) : null);
            pstmt.setString(4, ordine.getStato());
            pstmt.setDouble(5, ordine.getTotale());
            pstmt.setString(6, ordine.getNote());
            pstmt.setInt(7, ordine.getId());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Errore aggiornamento ordine: " + e.getMessage());
            return false;
        }
    }
    
    // Elimina un ordine
    public boolean elimina(int id) {
        String sql = "DELETE FROM ordine WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Errore eliminazione ordine: " + e.getMessage());
            return false;
        }
    }
    
    // Recupera le righe di un ordine specifico
    public List<RigaOrdine> getRigheOrdine(int ordineId) {
        List<RigaOrdine> lista = new ArrayList<>();
        String sql = "SELECT * FROM riga_ordine WHERE ordine_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, ordineId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                RigaOrdine riga = new RigaOrdine();
                riga.setId(rs.getInt("id"));
                riga.setOrdineId(rs.getInt("ordine_id"));
                riga.setAutoId(rs.getInt("auto_id"));
                riga.setQuantita(rs.getInt("quantita"));
                riga.setPrezzoUnitario(rs.getDouble("prezzo_unitario"));
                riga.setSubtotale(rs.getDouble("subtotale"));
                lista.add(riga);
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura righe ordine: " + e.getMessage());
        }
        return lista;
    }
    
    // Inserisce una riga ordine
    public boolean inserisciRiga(RigaOrdine riga) {
        String sql = "INSERT INTO riga_ordine (ordine_id, auto_id, quantita, prezzo_unitario, subtotale) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, riga.getOrdineId());
            pstmt.setInt(2, riga.getAutoId());
            pstmt.setInt(3, riga.getQuantita());
            pstmt.setDouble(4, riga.getPrezzoUnitario());
            pstmt.setDouble(5, riga.getSubtotale());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Errore inserimento riga ordine: " + e.getMessage());
            return false;
        }
    }
    
    // Elimina tutte le righe di un ordine
    public boolean eliminaRighe(int ordineId) {
        String sql = "DELETE FROM riga_ordine WHERE ordine_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, ordineId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Errore eliminazione righe ordine: " + e.getMessage());
            return false;
        }
    }
}