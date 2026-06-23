# Requirements Template

Questo documento definisce lo standard per scrivere e mantenere i requisiti in `docs/requirements.md`.

---

# 1. Obiettivo

Ogni requisito definitivo deve essere:

* chiaro;
* atomico;
* non ambiguo;
* verificabile;
* testabile;
* tracciabile;
* coerente con il dominio;
* privo di dettagli implementativi prematuri, salvo vincoli espliciti.

Un requisito descrive cosa deve fare il sistema o quale vincolo funzionale deve rispettare. Non deve descrivere come implementarlo, salvo quando la scelta tecnica e parte del vincolo.

---

# 2. Struttura Consigliata

Usare questa struttura per requisiti definitivi o ad alto impatto:

```markdown
## REQ-ORD-001 - Titolo breve

**Descrizione**
Descrizione chiara del comportamento richiesto.

**Motivazione**
Perche il requisito esiste e quale obiettivo supporta.

**Attori**
Ruoli o sistemi coinvolti.

**Precondizioni**
Condizioni che devono essere vere prima del flusso.

**Flusso principale**
1. Primo passo.
2. Secondo passo.
3. Esito atteso.

**Flussi alternativi / errori**
* Caso alternativo.
* Errore previsto.

**Regole di dominio collegate**
* Regola A.
* Regola B.

**Criteri di accettazione**
* Criterio verificabile.
* Criterio verificabile.

**Impatti**
* Sicurezza:
* Dati:
* Integrazioni:

**Priorita**
P0 / P1 / P2.

**Stato**
Draft / Approved / Implemented / Deprecated.

**Tracciabilita**
* Task:
* Decisioni:
* Test:
```

---

# 3. Campi Minimi

Ogni requisito dovrebbe includere almeno:

* ID univoco;
* titolo;
* descrizione;
* motivazione o obiettivo;
* attori coinvolti;
* precondizioni;
* flusso principale;
* flussi alternativi o errori;
* regole di dominio collegate;
* criteri di accettazione;
* impatti su sicurezza, dati o integrazioni;
* priorita;
* stato;
* tracciabilita verso task, decisioni o test futuri.

Per requisiti piccoli o ancora in fase esplorativa si puo usare una forma piu breve, ma prima dell'implementazione il requisito deve diventare verificabile.

---

# 4. Regole di Scrittura

Regole:

* usare frasi brevi;
* usare termini del dominio;
* evitare ambiguita come "veloce", "semplice", "adeguato" senza criterio misurabile;
* evitare dettagli tecnici non necessari;
* indicare sempre chi compie l'azione;
* indicare sempre l'esito osservabile;
* separare comportamento atteso da soluzione tecnica;
* collegare ogni requisito alle regole di dominio rilevanti;
* evitare requisiti multipli nello stesso blocco.

Formulazioni consigliate:

* "Il sistema deve..."
* "L'utente puo..."
* "Quando..., allora..."
* "Se..., il sistema..."

Formulazioni da evitare:

* "Gestire bene..."
* "Rendere facile..."
* "Supportare varie cose..."
* "Migliorare la pagina..."

---

# 5. Criteri di Qualita

Un requisito e di buona qualita quando:

* descrive un solo comportamento;
* ha un ID stabile;
* puo essere verificato da test o controllo manuale;
* non dipende da conoscenza implicita;
* non contraddice altri requisiti;
* indica cosa succede nei casi di errore;
* chiarisce gli attori coinvolti;
* ha priorita e stato;
* rimanda a task o decisioni quando necessario.

---

# 6. Criteri di Testabilita

Un requisito e testabile quando e possibile rispondere in modo oggettivo a queste domande:

* Quale input o stato iniziale serve?
* Quale azione viene eseguita?
* Quale output o cambiamento deve avvenire?
* Quale errore deve comparire nei casi negativi?
* Quale dato deve essere persistito o non persistito?
* Quale ruolo puo o non puo eseguire l'azione?

Se non e possibile definire almeno un criterio di accettazione verificabile, il requisito e ancora troppo ambiguo.

---

# 7. Esempio di Requisito Ben Scritto

## REQ-ORD-001 - Creazione ordine con magazzino obbligatorio

**Descrizione**
Il sistema deve permettere a un utente autorizzato di creare un ordine selezionando articolo, magazzino e quantita.

**Motivazione**
Lo stock e gestito per combinazione articolo-magazzino; il magazzino e necessario per prenotare la giacenza corretta.

**Attori**
USER, ADMIN, STORE_MANAGER.

**Precondizioni**
* L'articolo esiste.
* Il magazzino esiste.
* La giacenza articolo-magazzino esiste.

**Flusso principale**
1. L'utente seleziona articolo.
2. L'utente seleziona magazzino.
3. Il sistema mostra la disponibilita.
4. L'utente inserisce quantita.
5. Il sistema crea l'ordine in stato `REQUIRED`.
6. Il sistema decrementa lo stock.
7. Il sistema registra un evento audit.

**Flussi alternativi / errori**
* Se il magazzino non e selezionato, l'ordine non viene creato.
* Se la quantita supera la disponibilita, l'ordine non viene creato.

**Regole di dominio collegate**
* Magazzino obbligatorio.
* Stock non negativo.
* Creazione ordine in stato `REQUIRED`.

**Criteri di accettazione**
* Non e possibile creare un ordine senza magazzino.
* Con stock sufficiente, l'ordine viene creato.
* Lo stock viene decrementato della quantita richiesta.
* Viene creato un evento `null -> REQUIRED`.

**Impatti**
* Sicurezza: l'utente deve essere autorizzato.
* Dati: crea ordine, righe ordine ed evento audit.
* Integrazioni: nessuna.

**Priorita**
P0.

**Stato**
Implemented nella POC.

**Tracciabilita**
* Task: Motore Ordini.
* Decisioni: ADR-005.

---

# 8. Esempi Deboli

Esempio ambiguo:

```text
L'utente deve poter fare ordini facilmente.
```

Problemi:

* non definisce attore preciso;
* non dice quali dati servono;
* non dice cosa succede allo stock;
* non e testabile.

Esempio con dettagli prematuri:

```text
La pagina deve usare JavaScript con una mappa globale per calcolare lo stock.
```

Problemi:

* descrive una soluzione tecnica;
* non chiarisce il comportamento funzionale;
* limita l'implementazione senza motivo esplicito.

Versione migliore:

```text
Quando l'utente seleziona articolo e magazzino, il sistema deve mostrare la disponibilita corrente per quella combinazione.
```

---

# 9. Checklist Finale

Prima di approvare un requisito:

* ha un ID univoco;
* descrive un solo comportamento;
* indica attori coinvolti;
* indica precondizioni;
* descrive flusso principale;
* descrive errori o alternative;
* contiene criteri di accettazione verificabili;
* non contiene dettagli implementativi non necessari;
* e coerente con le regole di dominio;
* indica impatti su sicurezza, dati o integrazioni;
* ha priorita;
* ha stato;
* e collegabile a task, decisioni o test.
