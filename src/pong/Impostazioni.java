// Pong — Programmazione Orientata agli Oggetti e Laboratori, Udine, a.a. 2026-27
// Consegna 2: le impostazioni. Scadenza: domenica 1 novembre 2026.
package pong;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Le impostazioni di una partita di Pong: quanti punti servono per vincere, con quale scarto
 * minimo, a che velocità va la palla. È un record (lezione 4): una volta create, non cambiano.
 *
 * IL PEZZO MANCANTE: il costruttore che rifiuta i valori sbagliati e il metodo leggi.
 * Ogni metodo ha il suo contratto, nel commento sopra. I test ufficiali (cartella test)
 * controllano i contratti.
 */
public record Impostazioni(int puntiPerVincere, int scartoMinimo, int velocitaPalla) {

    /** Le impostazioni standard: 11 punti per vincere, scarto minimo 2, velocità della palla 5. */
    public static final Impostazioni STANDARD = new Impostazioni(11, 2, 5);

    /**
     * Il costruttore (lezione 7: un record che nasce giusto).
     * Promette: se i tre valori sono nei loro intervalli, le impostazioni con quei valori.
     * Lancia IllegalArgumentException se puntiPerVincere non è fra 1 e 99, se scartoMinimo
     * non è fra 1 e 10, o se velocitaPalla non è fra 1 e 20 (estremi compresi). Il messaggio
     * dice quale valore è sbagliato e perché.
     */
    public Impostazioni {
        if (puntiPerVincere < 1 || puntiPerVincere > 99) {
            throw new IllegalArgumentException(
                    "puntiPerVincere deve essere fra 1 e 99, ma è " + puntiPerVincere);
        }
        if (scartoMinimo < 1 || scartoMinimo > 10) {
            throw new IllegalArgumentException(
                    "scartoMinimo deve essere fra 1 e 10, ma è " + scartoMinimo);
        }
        if (velocitaPalla < 1 || velocitaPalla > 20) {
            throw new IllegalArgumentException(
                    "velocitaPalla deve essere fra 1 e 20, ma è " + velocitaPalla);
        }
    }

    /**
     * Legge le impostazioni dalle righe di un file, una impostazione per riga.
     * Pretende: righe non è null e non contiene null.
     * Promette:
     *  - le righe vuote (o di soli spazi) e quelle che, tolti gli spazi iniziali, cominciano
     *    con # sono ignorate;
     *  - ogni altra riga è nella forma  chiave = valore , con spazi qualsiasi attorno alla chiave,
     *    al segno = e al valore; le chiavi sono puntiPerVincere, scartoMinimo, velocitaPalla;
     *  - un'impostazione che nelle righe non c'è prende il valore standard;
     *  - il risultato sono le impostazioni lette.
     * Lancia IllegalArgumentException, con un messaggio che comincia con «riga N: » (N è il numero
     * della riga, contando da 1 tutte le righe, anche quelle vuote e i commenti), se in una riga:
     *  1. manca il segno = ;
     *  2. la chiave non è una delle tre;
     *  3. il valore non è un numero intero;
     *  4. la stessa chiave c'era già in una riga precedente;
     *  5. il valore è fuori dal suo intervallo (gli intervalli del costruttore).
     * Se ci sono errori in più righe, il messaggio è quello della riga più in alto.
     */
    public static Impostazioni leggi(List<String> righe) {
        int punti = STANDARD.puntiPerVincere();
        int scarto = STANDARD.scartoMinimo();
        int velocita = STANDARD.velocitaPalla();
        boolean haPunti = false;
        boolean haScarto = false;
        boolean haVelocita = false;

        for (int i = 0; i < righe.size(); i++) {
            String riga = righe.get(i);
            String prefisso = "riga " + (i + 1) + ": ";

            if (riga.isBlank() || riga.stripLeading().startsWith("#")) {
                continue;
            }

            int uguale = riga.indexOf('=');
            if (uguale < 0) {
                throw new IllegalArgumentException(prefisso + "manca il segno =");
            }
            String chiave = riga.substring(0, uguale).strip();
            String testo = riga.substring(uguale + 1).strip();

            if (chiave.equals("puntiPerVincere")) {
                punti = leggiValore(prefisso, chiave, testo, haPunti, 1, 99);
                haPunti = true;
            } else if (chiave.equals("scartoMinimo")) {
                scarto = leggiValore(prefisso, chiave, testo, haScarto, 1, 10);
                haScarto = true;
            } else if (chiave.equals("velocitaPalla")) {
                velocita = leggiValore(prefisso, chiave, testo, haVelocita, 1, 20);
                haVelocita = true;
            } else {
                throw new IllegalArgumentException(prefisso + "chiave sconosciuta: '" + chiave + "'");
            }
        }

        return new Impostazioni(punti, scarto, velocita);
    }

    private static int leggiValore(String prefisso, String chiave, String testo,
                                   boolean giaLetta, int min, int max) {
        if (!eUnIntero(testo)) {
            throw new IllegalArgumentException(
                    prefisso + chiave + ": '" + testo + "' non è un numero intero");
        }
        if (giaLetta) {
            throw new IllegalArgumentException(prefisso + chiave + " è già stata indicata");
        }
        int valore;
        try {
            valore = Integer.parseInt(testo);
        } catch (NumberFormatException e) {
            // è un intero, ma troppo grande per un int: quindi fuori intervallo
            throw new IllegalArgumentException(
                    prefisso + chiave + " deve essere fra " + min + " e " + max + ", ma è " + testo);
        }
        if (valore < min || valore > max) {
            throw new IllegalArgumentException(
                    prefisso + chiave + " deve essere fra " + min + " e " + max + ", ma è " + valore);
        }
        return valore;
    }

    /** Vero se s è un segno opzionale (+ o -) seguito da almeno una cifra, e nient'altro. */
    private static boolean eUnIntero(String s) {
        int inizio = 0;
        if (!s.isEmpty() && (s.charAt(0) == '+' || s.charAt(0) == '-')) {
            inizio = 1;
        }
        if (inizio == s.length()) {
            return false;
        }
        for (int i = inizio; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * Legge le impostazioni da un file di testo.
     * Promette: come leggi, con le righe del file.
     * Lancia IOException se il file non si può leggere (per esempio non esiste): non la cattura,
     * la lascia a chi chiama. Lancia IllegalArgumentException come leggi.
     */
    public static Impostazioni daFile(Path file) throws IOException {
        return leggi(Files.readAllLines(file));
    }
}
