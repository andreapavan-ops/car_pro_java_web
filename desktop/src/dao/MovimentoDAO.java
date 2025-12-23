package dao;

import model.Movimento;
import util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MovimentoDAO {
    
    // Inserisce un nuovo movimento
    public boolean inserisci(Movimento movimento) {
        String sql = "INSERT INTO movimento (auto_id, tipo_movimento, quantita, data_movimento, causale, riferimento, giacenza_precedente, giacenza_successiva) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, movimento.getAutoId());
            pstmt.setString(2, movimento.getTipoMovimento());
            pstmt.setInt(3, movimento.getQuantita());
            pstmt.setTimestamp(4, Timestamp.valueOf(movimento.getDataMovimento()));
            pstmt.setString(5, movimento.getCausale());
            pstmt.setString(6, movimento.getRiferimento());
            pstmt.setInt(7, movimento.getGiacenzaPrecedente());
            pstmt.setInt(8, movimento.getGiacenzaSuccessiva());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Errore inserimento movimento: " + e.getMessage());
            return false;
        }
    }
    
    // Recupera tutti i movimenti
    public List<Movimento> getAll() {
        List<Movimento> lista = new ArrayList<>();
        String sql = "SELECT * FROM movimento ORDER BY data_movimento DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Movimento movimento = new Movimento();
                movimento.setId(rs.getInt("id"));
                movimento.setAutoId(rs.getInt("auto_id"));
                movimento.setTipoMovimento(rs.getString("tipo_movimento"));
                movimento.setQuantita(rs.getInt("quantita"));
                movimento.setDataMovimento(rs.getTimestamp("data_movimento").toLocalDateTime());
                movimento.setCausale(rs.getString("causale"));
                movimento.setRiferimento(rs.getString("riferimento"));
                movimento.setGiacenzaPrecedente(rs.getInt("giacenza_precedente"));
                movimento.setGiacenzaSuccessiva(rs.getInt("giacenza_successiva"));
                lista.add(movimento);
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura movimenti: " + e.getMessage());
        }
        return lista;
    }
    
    // Recupera i movimenti per una specifica auto
    public List<Movimento> getByAutoId(int autoId) {
        List<Movimento> lista = new ArrayList<>();
        String sql = "SELECT * FROM movimento WHERE auto_id=? ORDER BY data_movimento DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, autoId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Movimento movimento = new Movimento();
                movimento.setId(rs.getInt("id"));
                movimento.setAutoId(rs.getInt("auto_id"));
                movimento.setTipoMovimento(rs.getString("tipo_movimento"));
                movimento.setQuantita(rs.getInt("quantita"));
                movimento.setDataMovimento(rs.getTimestamp("data_movimento").toLocalDateTime());
                movimento.setCausale(rs.getString("causale"));
                movimento.setRiferimento(rs.getString("riferimento"));
                movimento.setGiacenzaPrecedente(rs.getInt("giacenza_precedente"));
                movimento.setGiacenzaSuccessiva(rs.getInt("giacenza_successiva"));
                lista.add(movimento);
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura movimenti per auto: " + e.getMessage());
        }
        return lista;
    }
    
    // Recupera i movimenti per tipo
    public List<Movimento> getByTipo(String tipoMovimento) {
        List<Movimento> lista = new ArrayList<>();
        String sql = "SELECT * FROM movimento WHERE tipo_movimento=? ORDER BY data_movimento DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tipoMovimento);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Movimento movimento = new Movimento();
                movimento.setId(rs.getInt("id"));
                movimento.setAutoId(rs.getInt("auto_id"));
                movimento.setTipoMovimento(rs.getString("tipo_movimento"));
                movimento.setQuantita(rs.getInt("quantita"));
                movimento.setDataMovimento(rs.getTimestamp("data_movimento").toLocalDateTime());
                movimento.setCausale(rs.getString("causale"));
                movimento.setRiferimento(rs.getString("riferimento"));
                movimento.setGiacenzaPrecedente(rs.getInt("giacenza_precedente"));
                movimento.setGiacenzaSuccessiva(rs.getInt("giacenza_successiva"));
                lista.add(movimento);
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura movimenti per tipo: " + e.getMessage());
        }
        return lista;
    }
    
    // Recupera i movimenti in un intervallo di date
    public List<Movimento> getByPeriodo(LocalDateTime dataInizio, LocalDateTime dataFine) {
        List<Movimento> lista = new ArrayList<>();
        String sql = "SELECT * FROM movimento WHERE data_movimento BETWEEN ? AND ? ORDER BY data_movimento DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(dataInizio));
            pstmt.setTimestamp(2, Timestamp.valueOf(dataFine));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Movimento movimento = new Movimento();
                movimento.setId(rs.getInt("id"));
                movimento.setAutoId(rs.getInt("auto_id"));
                movimento.setTipoMovimento(rs.getString("tipo_movimento"));
                movimento.setQuantita(rs.getInt("quantita"));
                movimento.setDataMovimento(rs.getTimestamp("data_movimento").toLocalDateTime());
                movimento.setCausale(rs.getString("causale"));
                movimento.setRiferimento(rs.getString("riferimento"));
                movimento.setGiacenzaPrecedente(rs.getInt("giacenza_precedente"));
                movimento.setGiacenzaSuccessiva(rs.getInt("giacenza_successiva"));
                lista.add(movimento);
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura movimenti per periodo: " + e.getMessage());
        }
        return lista;
    }
    
    // Recupera un movimento per ID
    public Movimento getById(int id) {
        String sql = "SELECT * FROM movimento WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Movimento movimento = new Movimento();
                movimento.setId(rs.getInt("id"));
                movimento.setAutoId(rs.getInt("auto_id"));
                movimento.setTipoMovimento(rs.getString("tipo_movimento"));
                movimento.setQuantita(rs.getInt("quantita"));
                movimento.setDataMovimento(rs.getTimestamp("data_movimento").toLocalDateTime());
                movimento.setCausale(rs.getString("causale"));
                movimento.setRiferimento(rs.getString("riferimento"));
                movimento.setGiacenzaPrecedente(rs.getInt("giacenza_precedente"));
                movimento.setGiacenzaSuccessiva(rs.getInt("giacenza_successiva"));
                return movimento;
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura movimento: " + e.getMessage());
        }
        return null;
    }
    
    // Elimina un movimento (usare con cautela!)
    public boolean elimina(int id) {
        String sql = "DELETE FROM movimento WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Errore eliminazione movimento: " + e.getMessage());
            return false;
        }
    }
}