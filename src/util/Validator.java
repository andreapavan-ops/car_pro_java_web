package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class Validator {
    
    // Pattern per validazioni
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern TELEFONO_PATTERN = Pattern.compile(
        "^[+]?[0-9]{9,15}$"
    );
    
    private static final Pattern PARTITA_IVA_PATTERN = Pattern.compile(
        "^[0-9]{11}$"
    );
    
    private static final Pattern CODICE_FISCALE_PATTERN = Pattern.compile(
        "^[A-Z]{6}[0-9]{2}[A-Z][0-9]{2}[A-Z][0-9]{3}[A-Z]$"
    );
    
    private static final Pattern TARGA_PATTERN = Pattern.compile(
        "^[A-Z]{2}[0-9]{3}[A-Z]{2}$"
    );
    
    // ==================== VALIDAZIONI STRINGHE ====================
    
    /**
     * Verifica che una stringa non sia null o vuota
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * Verifica che una stringa abbia una lunghezza minima
     */
    public static boolean hasMinLength(String str, int minLength) {
        return isNotEmpty(str) && str.trim().length() >= minLength;
    }
    
    /**
     * Verifica che una stringa abbia una lunghezza massima
     */
    public static boolean hasMaxLength(String str, int maxLength) {
        return str != null && str.trim().length() <= maxLength;
    }
    
    /**
     * Verifica che una stringa sia compresa tra una lunghezza min e max
     */
    public static boolean hasLengthBetween(String str, int minLength, int maxLength) {
        return hasMinLength(str, minLength) && hasMaxLength(str, maxLength);
    }
    
    // ==================== VALIDAZIONI EMAIL ====================
    
    /**
     * Valida un indirizzo email
     */
    public static boolean isValidEmail(String email) {
        if (!isNotEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    // ==================== VALIDAZIONI TELEFONO ====================
    
    /**
     * Valida un numero di telefono (9-15 cifre, può iniziare con +)
     */
    public static boolean isValidTelefono(String telefono) {
        if (!isNotEmpty(telefono)) {
            return false;
        }
        // Rimuove spazi e trattini
        String cleanTelefono = telefono.replaceAll("[\\s-]", "");
        return TELEFONO_PATTERN.matcher(cleanTelefono).matches();
    }
    
    // ==================== VALIDAZIONI PARTITA IVA ====================
    
    /**
     * Valida una partita IVA italiana (11 cifre)
     */
    public static boolean isValidPartitaIva(String partitaIva) {
        if (!isNotEmpty(partitaIva)) {
            return false;
        }
        return PARTITA_IVA_PATTERN.matcher(partitaIva.trim()).matches();
    }
    
    // ==================== VALIDAZIONI CODICE FISCALE ====================
    
    /**
     * Valida un codice fiscale italiano (formato base, no controllo checksum)
     */
    public static boolean isValidCodiceFiscale(String codiceFiscale) {
        if (!isNotEmpty(codiceFiscale)) {
            return false;
        }
        return CODICE_FISCALE_PATTERN.matcher(codiceFiscale.trim().toUpperCase()).matches();
    }
    
    // ==================== VALIDAZIONI TARGA ====================
    
    /**
     * Valida una targa italiana (formato: AA123BB)
     */
    public static boolean isValidTarga(String targa) {
        if (!isNotEmpty(targa)) {
            return false;
        }
        return TARGA_PATTERN.matcher(targa.trim().toUpperCase()).matches();
    }
    
    // ==================== VALIDAZIONI NUMERICHE ====================
    
    /**
     * Verifica che un numero sia positivo (> 0)
     */
    public static boolean isPositive(int number) {
        return number > 0;
    }
    
    /**
     * Verifica che un numero sia positivo (> 0)
     */
    public static boolean isPositive(double number) {
        return number > 0;
    }
    
    /**
     * Verifica che un numero sia non negativo (>= 0)
     */
    public static boolean isNonNegative(int number) {
        return number >= 0;
    }
    
    /**
     * Verifica che un numero sia non negativo (>= 0)
     */
    public static boolean isNonNegative(double number) {
        return number >= 0;
    }
    
    /**
     * Verifica che un numero sia compreso in un range (min <= number <= max)
     */
    public static boolean isInRange(int number, int min, int max) {
        return number >= min && number <= max;
    }
    
    /**
     * Verifica che un numero sia compreso in un range (min <= number <= max)
     */
    public static boolean isInRange(double number, double min, double max) {
        return number >= min && number <= max;
    }
    
    // ==================== VALIDAZIONI DATE ====================
    
    /**
     * Verifica che una data sia valida nel formato specificato
     */
    public static boolean isValidDate(String dateStr, String pattern) {
        if (!isNotEmpty(dateStr)) {
            return false;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDate.parse(dateStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    /**
     * Verifica che una data sia valida nel formato dd/MM/yyyy
     */
    public static boolean isValidDate(String dateStr) {
        return isValidDate(dateStr, "dd/MM/yyyy");
    }
    
    /**
     * Verifica che una data non sia futura
     */
    public static boolean isNotFutureDate(LocalDate date) {
        return date != null && !date.isAfter(LocalDate.now());
    }
    
    /**
     * Verifica che una data non sia passata
     */
    public static boolean isNotPastDate(LocalDate date) {
        return date != null && !date.isBefore(LocalDate.now());
    }
    
    /**
     * Verifica che una data sia compresa in un range
     */
    public static boolean isDateInRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return date != null && 
               !date.isBefore(startDate) && 
               !date.isAfter(endDate);
    }
    
    // ==================== VALIDAZIONI AUTO ====================
    
    /**
     * Valida i dati di un'auto
     */
    public static String validateAuto(String marca, String modello, String targa, 
                                      int anno, double prezzo, int giacenza, int scortaMinima) {
        StringBuilder errors = new StringBuilder();
        
        if (!isNotEmpty(marca)) {
            errors.append("- Marca obbligatoria\n");
        }
        
        if (!isNotEmpty(modello)) {
            errors.append("- Modello obbligatorio\n");
        }
        
        if (isNotEmpty(targa) && !isValidTarga(targa)) {
            errors.append("- Targa non valida (formato: AA123BB)\n");
        }
        
        int annoCorrente = LocalDate.now().getYear();
        if (!isInRange(anno, 1900, annoCorrente + 1)) {
            errors.append("- Anno deve essere tra 1900 e " + (annoCorrente + 1) + "\n");
        }
        
        if (!isPositive(prezzo)) {
            errors.append("- Prezzo deve essere maggiore di 0\n");
        }
        
        if (!isNonNegative(giacenza)) {
            errors.append("- Giacenza non può essere negativa\n");
        }
        
        if (!isPositive(scortaMinima)) {
            errors.append("- Scorta minima deve essere maggiore di 0\n");
        }
        
        return errors.length() > 0 ? errors.toString() : null;
    }
    
    // ==================== VALIDAZIONI FORNITORE ====================
    
    /**
     * Valida i dati di un fornitore
     */
    public static String validateFornitore(String nome, String partitaIva, String telefono, String email) {
        StringBuilder errors = new StringBuilder();
        
        if (!isNotEmpty(nome)) {
            errors.append("- Nome fornitore obbligatorio\n");
        }
        
        if (isNotEmpty(partitaIva) && !isValidPartitaIva(partitaIva)) {
            errors.append("- Partita IVA non valida (11 cifre)\n");
        }
        
        if (isNotEmpty(telefono) && !isValidTelefono(telefono)) {
            errors.append("- Numero di telefono non valido\n");
        }
        
        if (isNotEmpty(email) && !isValidEmail(email)) {
            errors.append("- Email non valida\n");
        }
        
        return errors.length() > 0 ? errors.toString() : null;
    }
    
    // ==================== VALIDAZIONI VENDITA ====================
    
    /**
     * Valida i dati di una vendita
     */
    public static String validateVendita(String nomeCliente, String cognomeCliente, 
                                         String codiceFiscale, String telefono, 
                                         String email, double prezzoVendita) {
        StringBuilder errors = new StringBuilder();
        
        if (!isNotEmpty(nomeCliente)) {
            errors.append("- Nome cliente obbligatorio\n");
        }
        
        if (!isNotEmpty(cognomeCliente)) {
            errors.append("- Cognome cliente obbligatorio\n");
        }
        
        if (isNotEmpty(codiceFiscale) && !isValidCodiceFiscale(codiceFiscale)) {
            errors.append("- Codice fiscale non valido\n");
        }
        
        if (isNotEmpty(telefono) && !isValidTelefono(telefono)) {
            errors.append("- Numero di telefono non valido\n");
        }
        
        if (isNotEmpty(email) && !isValidEmail(email)) {
            errors.append("- Email non valida\n");
        }
        
        if (!isPositive(prezzoVendita)) {
            errors.append("- Prezzo vendita deve essere maggiore di 0\n");
        }
        
        return errors.length() > 0 ? errors.toString() : null;
    }
    
    // ==================== METODO DI TEST ====================
    
    public static void main(String[] args) {
        System.out.println("=== TEST VALIDATOR ===\n");
        
        // Test Email
        System.out.println("Test Email:");
        System.out.println("mario.rossi@gmail.com -> " + isValidEmail("mario.rossi@gmail.com"));
        System.out.println("invalid-email -> " + isValidEmail("invalid-email"));
        
        // Test Telefono
        System.out.println("\nTest Telefono:");
        System.out.println("3331234567 -> " + isValidTelefono("3331234567"));
        System.out.println("+393331234567 -> " + isValidTelefono("+393331234567"));
        System.out.println("123 -> " + isValidTelefono("123"));
        
        // Test Partita IVA
        System.out.println("\nTest Partita IVA:");
        System.out.println("12345678901 -> " + isValidPartitaIva("12345678901"));
        System.out.println("123 -> " + isValidPartitaIva("123"));
        
        // Test Codice Fiscale
        System.out.println("\nTest Codice Fiscale:");
        System.out.println("RSSMRA80A01H501U -> " + isValidCodiceFiscale("RSSMRA80A01H501U"));
        System.out.println("INVALID -> " + isValidCodiceFiscale("INVALID"));
        
        // Test Targa
        System.out.println("\nTest Targa:");
        System.out.println("AB123CD -> " + isValidTarga("AB123CD"));
        System.out.println("123ABC -> " + isValidTarga("123ABC"));
        
        // Test Data
        System.out.println("\nTest Data:");
        System.out.println("25/12/2024 -> " + isValidDate("25/12/2024"));
        System.out.println("32/13/2024 -> " + isValidDate("32/13/2024"));
        
        System.out.println("\n=== TEST COMPLETATO ===");
    }
}