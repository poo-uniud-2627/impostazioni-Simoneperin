# Consegna 2 — Le impostazioni di Pong

**Scadenza: domenica 1 novembre 2026, alle 23:59.** Conta com'è il repository a quell'ora: se i test automatici sono verdi, prendete un gettone (e, se è la vostra prima consegna verde, 10 crediti). Questa consegna non va all'asta.

## Cosa c'è qui

Un pezzo nuovo di Pong: le impostazioni della partita lette da un file di testo, una per riga, come questo:

```
# le mie impostazioni
puntiPerVincere = 21
scartoMinimo = 2
velocitaPalla = 7
```

La classe è `src/pong/Impostazioni.java`: un **record** (lezione 4), con il contratto scritto sopra ogni metodo. La cartella `test` contiene i test ufficiali (`test/pong/ImpostazioniTest.java`); `lib` contiene JUnit; `.vscode` e `.github` le impostazioni di VS Code e dei test su GitHub.

## Cosa dovete fare

Scrivere due cose, seguendo i contratti (lezione 7):

- **il costruttore** del record, che rifiuta con un'`IllegalArgumentException` i valori fuori dagli intervalli;
- **`leggi(List<String> righe)`**, che legge le righe e, a ogni errore, lancia un'`IllegalArgumentException` con un messaggio che comincia con `riga N: ` (il numero della riga sbagliata). Le impostazioni che mancano prendono il valore standard.

`daFile` è già scritto: legge il file con `Files.readAllLines` e passa le righe a `leggi`. Se il file non c'è, `readAllLines` lancia `IOException`, e `daFile` la lascia passare a chi lo chiama (lezione 7: non la inghiotte).

Potete aggiungere metodi `private` di aiuto. Non cambiate le intestazioni dei metodi `public`, non toccate i test: i test ufficiali vengono comunque rimessi a ogni prova su GitHub. Non toccate nemmeno la cartella `.github`: se il file che fa partire i test viene cambiato, i test della consegna non contano.

Quello che vi serve: `righe.size()` (quante righe), `righe.get(i)` (la riga in posizione `i`, contando da 0); sulle stringhe `trim()` (toglie gli spazi all'inizio e alla fine), `isEmpty()`, `startsWith("#")`, `indexOf('=')` (la posizione del carattere `=`, −1 se non c'è; lezione 3), `substring(da, a)`; `Integer.parseInt(testo)`, che lancia `NumberFormatException` se il testo non è un numero intero; `throw new IllegalArgumentException("riga " + n + ": ...")`; `try … catch` (lezione 7).

Un modo di cominciare: un ciclo sulle righe con l'indice (`for (int i = 0; i < righe.size(); i++)`: la riga numero `i + 1`); le righe da ignorare; poi i cinque errori del contratto, uno alla volta, nell'ordine; poi i valori standard per quelle che mancano.

Tre cose a cui pensare:

- **il fuori intervallo va scoperto nella riga in cui c'è**, non solo nel costruttore: quando il costruttore viene chiamato, alla fine, il numero della riga non lo sapete più. Per non scrivere gli intervalli due volte, un metodo `private static` che dice se un valore è nel suo intervallo, usato da `leggi` e dal costruttore;
- **la chiave ripetuta**: dovete ricordarvi quali chiavi avete già visto, per esempio con tre variabili `boolean`, una per chiave;
- **la costante `STANDARD`** usa il vostro costruttore, appena la classe viene caricata. Se il costruttore rifiuta 11, 2, 5, falliscono tutti i test con `ExceptionInInitializerError` o `NoClassDefFoundError`: guardate il costruttore.

## Come si prova

**In VS Code**: barra laterale **Testing**, il triangolo. I test confrontano anche due record con `assertEquals`: due record con gli stessi valori sono uguali (Java scrive per voi anche il metodo che li confronta, `equals`). Nei test ci sono cose che vedremo più avanti (`List.of`, `@TempDir`): per la consegna non serve capirle. Un test rosso dice cosa si aspettava e cosa ha avuto (lezione 5). Per un test che si aspetta un'eccezione (`assertThrows`, lezione 7), il messaggio dice se l'eccezione non è arrivata o se il suo messaggio non comincia con `riga N: `.

**Su GitHub**: a ogni `git push` partono i test; il risultato accanto all'ultimo commit e nella scheda **Actions**.

## Come si consegna

Prima, una volta: il clone di questo repository (in VS Code: Clone Repository, con l'indirizzo del pulsante verde Code; oppure `git clone`), poi aprite la cartella. Poi con git, come la consegna 1: Source Control in VS Code (commit, poi Sync Changes), oppure `git add`, `git commit`, `git push`. Se git non funziona: dalla pagina web del repository, aprite `src/pong/Impostazioni.java`, la matita («Edit this file»), incollate il vostro codice, «Commit changes».

## L'invito

Il repository vi arriva con un invito di GitHub, per email (o dalla campanella delle notifiche su GitHub). Va accettato entro 7 giorni. Senza invito, scrivete al docente su Teams.
