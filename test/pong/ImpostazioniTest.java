// Pong — Programmazione Orientata agli Oggetti e Laboratori, Udine, a.a. 2026-27
// Consegna 2: i test ufficiali delle impostazioni. Ogni test è un caso del contratto.
package pong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ImpostazioniTest {

    // ---- attrezzi

    /** Controlla che leggi lanci IllegalArgumentException con un messaggio che comincia con «riga n: ». */
    private static void erroreAllaRiga(int n, String... righe) {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> Impostazioni.leggi(List.of(righe)));
        String messaggio = e.getMessage();
        assertTrue(messaggio != null && messaggio.startsWith("riga " + n + ": "),
                "il messaggio deve cominciare con «riga " + n + ": », invece è: " + messaggio);
    }

    private static void costruttoreRifiuta(int punti, int scarto, int velocita) {
        assertThrows(IllegalArgumentException.class, () -> new Impostazioni(punti, scarto, velocita));
    }

    // ---- il costruttore

    @Test
    void valoriNegliIntervalliVanno() {
        Impostazioni i = new Impostazioni(21, 3, 9);
        assertEquals(21, i.puntiPerVincere());
        assertEquals(3, i.scartoMinimo());
        assertEquals(9, i.velocitaPalla());
    }

    @Test
    void iConfiniDegliIntervalliVanno() {
        new Impostazioni(1, 1, 1);
        new Impostazioni(99, 10, 20);
    }

    @Test
    void ilCostruttoreRifiutaPuntiFuoriIntervallo() {
        costruttoreRifiuta(0, 2, 5);
        costruttoreRifiuta(100, 2, 5);
        costruttoreRifiuta(-11, 2, 5);
    }

    @Test
    void ilCostruttoreRifiutaScartoFuoriIntervallo() {
        costruttoreRifiuta(11, 0, 5);
        costruttoreRifiuta(11, 11, 5);
    }

    @Test
    void ilCostruttoreRifiutaVelocitaFuoriIntervallo() {
        costruttoreRifiuta(11, 2, 0);
        costruttoreRifiuta(11, 2, 21);
    }

    @Test
    void leImpostazioniStandard() {
        assertEquals(new Impostazioni(11, 2, 5), Impostazioni.STANDARD);
    }

    // ---- leggi: le righe giuste

    @Test
    void treImpostazioni() {
        Impostazioni i = Impostazioni.leggi(List.of("puntiPerVincere = 21", "scartoMinimo = 3", "velocitaPalla = 7"));
        assertEquals(new Impostazioni(21, 3, 7), i);
    }

    @Test
    void lOrdineNonConta() {
        Impostazioni i = Impostazioni.leggi(List.of("velocitaPalla = 7", "puntiPerVincere = 21", "scartoMinimo = 3"));
        assertEquals(new Impostazioni(21, 3, 7), i);
    }

    @Test
    void gliSpaziNonContano() {
        Impostazioni i = Impostazioni.leggi(List.of("puntiPerVincere=21", "   scartoMinimo   =   3   ", "\tvelocitaPalla =7"));
        assertEquals(new Impostazioni(21, 3, 7), i);
    }

    @Test
    void righeVuoteECommentiSonoIgnorati() {
        Impostazioni i = Impostazioni.leggi(List.of("# le impostazioni di Pong", "", "puntiPerVincere = 21",
                "   ", "  # un commento con spazi davanti", "scartoMinimo = 3", "velocitaPalla = 7"));
        assertEquals(new Impostazioni(21, 3, 7), i);
    }

    @Test
    void quelleCheMancanoPrendonoIlValoreStandard() {
        assertEquals(new Impostazioni(21, 2, 5), Impostazioni.leggi(List.of("puntiPerVincere = 21")));
        assertEquals(new Impostazioni(11, 2, 9), Impostazioni.leggi(List.of("velocitaPalla = 9")));
    }

    @Test
    void nessunaRigaVuolDireTutteStandard() {
        assertEquals(Impostazioni.STANDARD, Impostazioni.leggi(List.of()));
        assertEquals(Impostazioni.STANDARD, Impostazioni.leggi(List.of("", "# niente")));
    }

    @Test
    void iConfiniDegliIntervalliSiLeggono() {
        assertEquals(new Impostazioni(1, 1, 1), Impostazioni.leggi(List.of("puntiPerVincere = 1", "scartoMinimo = 1", "velocitaPalla = 1")));
        assertEquals(new Impostazioni(99, 10, 20), Impostazioni.leggi(List.of("puntiPerVincere = 99", "scartoMinimo = 10", "velocitaPalla = 20")));
    }

    // ---- leggi: gli errori, con il numero della riga

    @Test
    void rigaSenzaUguale() {
        erroreAllaRiga(2, "puntiPerVincere = 21", "velocitaPalla 7");
    }

    @Test
    void chiaveScrittaMale() {
        erroreAllaRiga(1, "puntiPerVinciere = 21", "scartoMinimo = 3");
        erroreAllaRiga(1, "PuntiPerVincere = 5");                  // le maiuscole contano
    }

    @Test
    void chiaveVuota() {
        erroreAllaRiga(1, "= 5");
    }

    @Test
    void valoreInLettere() {
        erroreAllaRiga(3, "puntiPerVincere = 21", "scartoMinimo = 3", "velocitaPalla = sette");
        erroreAllaRiga(1, "velocitaPalla = 7 # veloce");           // # vale solo a inizio riga
    }

    @Test
    void valoreConLaVirgola() {
        erroreAllaRiga(1, "velocitaPalla = 7.5");
    }

    @Test
    void valoreVuoto() {
        erroreAllaRiga(1, "puntiPerVincere =");
    }

    @Test
    void chiaveRipetuta() {
        erroreAllaRiga(4, "puntiPerVincere = 21", "scartoMinimo = 3", "velocitaPalla = 7", "puntiPerVincere = 5");
    }

    @Test
    void chiaveRipetutaConLoStessoValore() {
        erroreAllaRiga(2, "scartoMinimo = 3", "scartoMinimo = 3");
    }

    @Test
    void valoreFuoriIntervallo() {
        erroreAllaRiga(1, "puntiPerVincere = 0");
        erroreAllaRiga(2, "puntiPerVincere = 21", "velocitaPalla = 21");
        erroreAllaRiga(1, "scartoMinimo = -2");
        erroreAllaRiga(1, "puntiPerVincere = 100");
        erroreAllaRiga(1, "scartoMinimo = 11");
    }

    @Test
    void leRigheSiContanoTutteAncheQuelleVuoteEICommenti() {
        erroreAllaRiga(5, "# impostazioni", "", "puntiPerVincere = 21", "   ", "velocitaPalla = veloce");
    }

    @Test
    void conPiuErroriVinceLaRigaPiuInAlto() {
        erroreAllaRiga(2, "puntiPerVincere = 21", "scartoMinimo = tre", "velocitaPalla 7");
    }

    // ---- daFile

    @Test
    void daFileLeggeLeRigheDelFile(@TempDir Path cartella) throws IOException {
        Path file = cartella.resolve("pong.txt");
        Files.write(file, List.of("# Pong", "puntiPerVincere = 15", "velocitaPalla = 6"));
        assertEquals(new Impostazioni(15, 2, 6), Impostazioni.daFile(file));
    }

    @Test
    void daFileConUnFileCheNonCeLanciaIOException(@TempDir Path cartella) {
        Path file = cartella.resolve("non-c-e.txt");
        assertThrows(IOException.class, () -> Impostazioni.daFile(file));
    }

    @Test
    void daFileConUnErroreDiceLaRiga(@TempDir Path cartella) throws IOException {
        Path file = cartella.resolve("pong.txt");
        Files.write(file, List.of("puntiPerVincere = 15", "scarto = 3"));
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> Impostazioni.daFile(file));
        assertTrue(e.getMessage() != null && e.getMessage().startsWith("riga 2: "), "il messaggio: " + e.getMessage());
    }
}
