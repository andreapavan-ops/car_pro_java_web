package dao;

import model.Vendita;
import util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VenditaDAO {
    
    // Inserisce una nuova vendita
    public boolean inserisci(Vendita vendita) {
        String sql = "INSERT INTO vendita (auto_id, data_vendita, nome_cliente, cognome_cliente, " +
                     "codice_fiscale, telefono, email, prezzo_vendita, metodo_pagamento, note) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, vendita.getAutoId());
            pstmt.setDate(2, Date.valueOf(vendita.getDataVendita()));
            pstmt.setString(3, vendita.getNomeCliente());
            pstmt.setString(4, vendita.getCognomeCliente());
            pstmt.setString(5, vendita.getCodiceFiscale());
            pstmt.setString(6, vendita.getTelefono());
            pstmt.setString(7, vendita.getEmail());
            pstmt.setDouble(8, vendita.getPrezzoVendita());
            pstmt.setString(9, vendita.getMetodoPagamento());
            pstmt.setString(10, vendita.getNote());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Errore inserimento vendita: " + e.getMessage());
            return false;
        }
    }
    
    // Recupera tutte le vendite
    public List<Vendita> getAll() {
        List<Vendita> lista = new ArrayList<>();
        String sql = "SELECT * FROM vendita ORDER BY data_vendita DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Vendita vendita = new Vendita();
                vendita.setId(rs.getInt("id"));
                vendita.setAutoId(rs.getInt("auto_id"));
                vendita.setDataVendita(rs.getDate("data_vendita").toLocalDate());
                vendita.setNomeCliente(rs.getString("nome_cliente"));
                vendita.setCognomeCliente(rs.getString("cognome_cliente"));
                vendita.setCodiceFiscale(rs.getString("codice_fiscale"));
                vendita.setTelefono(rs.getString("telefono"));
                vendita.setEmail(rs.getString("email"));
                vendita.setPrezzoVendita(rs.getDouble("prezzo_vendita"));
                vendita.setMetodoPagamento(rs.getString("metodo_pagamento"));
                vendita.setNote(rs.getString("note"));
                lista.add(vendita);
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura vendite: " + e.getMessage());
        }
        return lista;
    }
    
    // Recupera una vendita per ID
    public Vendita getById(int id) {
        String sql = "SELECT * FROM vendita WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Vendita vendita = new Vendita();
                vendita.setId(rs.getInt("id"));
                vendita.setAutoId(rs.getInt("auto_id"));
                vendita.setDataVendita(rs.getDate("data_vendita").toLocalDate());
                vendita.setNomeCliente(rs.getString("nome_cliente"));
                vendita.setCognomeCliente(rs.getString("cognome_cliente"));
                vendita.setCodiceFiscale(rs.getString("codice_fiscale"));
                vendita.setTelefono(rs.getString("telefono"));
                vendita.setEmail(rs.getString("email"));
                vendita.setPrezzoVendita(rs.getDouble("prezzo_vendita"));
                vendita.setMetodoPagamento(rs.getString("metodo_pagamento"));
                vendita.setNote(rs.getString("note"));
                return vendita;
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura vendita per ID: " + e.getMessage());
        }
        return null;
    }
    
    // Recupera vendite per una specifica auto
    public List<Vendita> getByAuto(int autoId) {
        List<Vendita> lista = new ArrayList<>();
        String sql = "SELECT * FROM vendita WHERE auto_id=? ORDER BY data_vendita DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, autoId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Vendita vendita = new Vendita();
                vendita.setId(rs.getInt("id"));
                vendita.setAutoId(rs.getInt("auto_id"));
                vendita.setDataVendita(rs.getDate("data_vendita").toLocalDate());
                vendita.setNomeCliente(rs.getString("nome_cliente"));
                vendita.setCognomeCliente(rs.getString("cognome_cliente"));
                vendita.setCodiceFiscale(rs.getString("codice_fiscale"));
                vendita.setTelefono(rs.getString("telefono"));
                vendita.setEmail(rs.getString("email"));
                vendita.setPrezzoVendita(rs.getDouble("prezzo_vendita"));
                vendita.setMetodoPagamento(rs.getString("metodo_pagamento"));
                vendita.setNote(rs.getString("note"));
                lista.add(vendita);
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura vendite per auto: " + e.getMessage());
        }
        return lista;
    }
    
    // Recupera vendite per cliente (nome o cognome)
    public List<Vendita> getByCliente(String ricerca) {
        List<Vendita> lista = new ArrayList<>();
        String sql = "SELECT * FROM vendita WHERE nome_cliente LIKE ? OR cognome_cliente LIKE ? " +
                     "ORDER BY data_vendita DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + ricerca + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Vendita vendita = new Vendita();
                vendita.setId(rs.getInt("id"));
                vendita.setAutoId(rs.getInt("auto_id"));
                vendita.setDataVendita(rs.getDate("data_vendita").toLocalDate());
                vendita.setNomeCliente(rs.getString("nome_cliente"));
                vendita.setCognomeCliente(rs.getString("cognome_cliente"));
                vendita.setCodiceFiscale(rs.getString("codice_fiscale"));
                vendita.setTelefono(rs.getString("telefono"));
                vendita.setEmail(rs.getString("email"));
                vendita.setPrezzoVendita(rs.getDouble("prezzo_vendita"));
                vendita.setMetodoPagamento(rs.getString("metodo_pagamento"));
                vendita.setNote(rs.getString("note"));
                lista.add(vendita);
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura vendite per cliente: " + e.getMessage());
        }
        return lista;
    }
    
    // Recupera vendite in un periodo specifico
    public List<Vendita> getByPeriodo(LocalDate dataInizio, LocalDate dataFine) {
        List<Vendita> lista = new ArrayList<>();
        String sql = "SELECT * FROM vendita WHERE data_vendita BETWEEN ? AND ? ORDER BY data_vendita DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(dataInizio));
            pstmt.setDate(2, Date.valueOf(dataFine));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Vendita vendita = new Vendita();
                vendita.setId(rs.getInt("id"));
                vendita.setAutoId(rs.getInt("auto_id"));
                vendita.setDataVendita(rs.getDate("data_vendita").toLocalDate());
                vendita.setNomeCliente(rs.getString("nome_cliente"));
                vendita.setCognomeCliente(rs.getString("cognome_cliente"));
                vendita.setCodiceFiscale(rs.getString("codice_fiscale"));
                vendita.setTelefono(rs.getString("telefono"));
                vendita.setEmail(rs.getString("email"));
                vendita.setPrezzoVendita(rs.getDouble("prezzo_vendita"));
                vendita.setMetodoPagamento(rs.getString("metodo_pagamento"));
                vendita.setNote(rs.getString("note"));
                lista.add(vendita);
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura vendite per periodo: " + e.getMessage());
        }
        return lista;
    }
    
    // Calcola il totale vendite
    public double getTotaleVendite() {
        String sql = "SELECT SUM(prezzo_vendita) as totale FROM vendita";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble("totale");
            }
        } catch (SQLException e) {
            System.err.println("Errore calcolo totale vendite: " + e.getMessage());
        }
        return 0.0;
    }
    
    // Calcola il totale vendite in un periodo
    public double getTotaleVenditePeriodo(LocalDate dataInizio, LocalDate dataFine) {
        String sql = "SELECT SUM(prezzo_vendita) as totale FROM vendita " +
                     "WHERE data_vendita BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(dataInizio));
            pstmt.setDate(2, Date.valueOf(dataFine));
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("totale");
            }
        } catch (SQLException e) {
            System.err.println("Errore calcolo totale vendite periodo: " + e.getMessage());
        }
        return 0.0;
    }
    
    // Conta il numero di vendite
    public int contaVendite() {
        String sql = "SELECT COUNT(*) as totale FROM vendita";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("totale");
            }
        } catch (SQLException e) {
            System.err.println("Errore conteggio vendite: " + e.getMessage());
        }
        return 0;
    }
    
    // Aggiorna una vendita esistente
    public boolean aggiorna(Vendita vendita) {
        String sql = "UPDATE vendita SET auto_id=?, data_vendita=?, nome_cliente=?, " +
                     "cognome_cliente=?, codice_fiscale=?, telefono=?, email=?, " +
                     "prezzo_vendita=?, metodo_pagamento=?, note=? WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, vendita.getAutoId());
            pstmt.setDate(2, Date.valueOf(vendita.getDataVendita()));
            pstmt.setString(3, vendita.getNomeCliente());
            pstmt.setString(4, vendita.getCognomeCliente());
            pstmt.setString(5, vendita.getCodiceFiscale());
            pstmt.setString(6, vendita.getTelefono());
            pstmt.setString(7, vendita.getEmail());
            pstmt.setDouble(8, vendita.getPrezzoVendita());
            pstmt.setString(9, vendita.getMetodoPagamento());
            pstmt.setString(10, vendita.getNote());
            pstmt.setInt(11, vendita.getId());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Errore aggiornamento vendita: " + e.getMessage());
            return false;
        }
    }
    
    // Elimina una vendita
    public boolean elimina(int id) {
        String sql = "DELETE FROM vendita WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Errore eliminazione vendita: " + e.getMessage());
            return false;
        }
    }
}