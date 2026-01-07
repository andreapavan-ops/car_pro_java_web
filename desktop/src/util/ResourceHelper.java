package util;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Utility per trovare le risorse (immagini) indipendentemente dalla working directory.
 */
public class ResourceHelper {

    private static String basePath = null;

    /**
     * Trova il percorso base della cartella resources/images.
     * Cerca in vari percorsi possibili per funzionare sia da IDE che da JAR.
     */
    public static String getImagesPath() {
        if (basePath != null) {
            return basePath;
        }

        // Lista di possibili percorsi da provare
        String[] possibiliPercorsi = {
            "resources/images",                          // Relativo alla working directory (desktop)
            "desktop/resources/images",                  // Relativo alla root del progetto
            "../resources/images",                       // Se la working dir è src o out
            getPathRelativoAllaClasse()                  // Relativo alla posizione della classe
        };

        for (String percorso : possibiliPercorsi) {
            if (percorso != null) {
                File dir = new File(percorso);
                if (dir.exists() && dir.isDirectory()) {
                    basePath = percorso;
                    System.out.println("[ResourceHelper] Cartella immagini trovata: " + dir.getAbsolutePath());
                    return basePath;
                }
            }
        }

        // Se non trovato, usa il default e stampa un warning
        System.err.println("[ResourceHelper] ATTENZIONE: Cartella immagini non trovata!");
        System.err.println("[ResourceHelper] Working directory: " + new File(".").getAbsolutePath());
        basePath = "resources/images"; // Fallback
        return basePath;
    }

    /**
     * Ottiene il percorso completo di un'immagine.
     */
    public static String getImagePath(String nomeImmagine) {
        return getImagesPath() + "/" + nomeImmagine;
    }

    /**
     * Ottiene il File di un'immagine.
     */
    public static File getImageFile(String nomeImmagine) {
        return new File(getImagePath(nomeImmagine));
    }

    /**
     * Verifica se un'immagine esiste.
     */
    public static boolean imageExists(String nomeImmagine) {
        return getImageFile(nomeImmagine).exists();
    }

    /**
     * Cerca il percorso resources/images relativo alla posizione della classe compilata.
     */
    private static String getPathRelativoAllaClasse() {
        try {
            // Ottiene la posizione del file .class
            File classLocation = new File(ResourceHelper.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());

            // Se è una directory (out/bin), risali
            File base = classLocation;
            if (base.isFile()) {
                base = base.getParentFile(); // Se è un JAR, prendi la cartella contenitore
            }

            // Prova vari percorsi relativi alla posizione della classe
            String[] relativiAllaClasse = {
                "../resources/images",        // out -> resources/images
                "../../resources/images",     // out/classes -> resources/images
                "../../../resources/images",  // out/production/desktop -> resources/images
                "resources/images"            // stesso livello
            };

            for (String relativo : relativiAllaClasse) {
                File prova = new File(base, relativo);
                if (prova.exists() && prova.isDirectory()) {
                    return prova.getPath();
                }
            }
        } catch (URISyntaxException e) {
            // Ignora errori
        }
        return null;
    }

    /**
     * Resetta il path cached (utile per testing).
     */
    public static void reset() {
        basePath = null;
    }
}
