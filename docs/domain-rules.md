# Domain Rules

Questo documento raccoglie le regole di dominio che il codice deve rispettare. Se una regola cambia, aggiornare questo file prima o insieme all'implementazione.

---

# 1. Ruoli

## ADMIN

Puo:

* effettuare ricerche;
* creare utenti;
* creare STORE_MANAGER;
* registrare articoli;
* gestire giacenze;
* creare ordini;
* visualizzare tutti gli ordini;
* approvare ordini;
* rifiutare ordini;
* cancellare ordini;
* scaricare PDF;
* accedere a tutte le schermate.

## STORE_MANAGER

Puo:

* effettuare ricerche;
* registrare articoli;
* gestire giacenze;
* creare ordini;
* visualizzare tutti gli ordini;
* approvare ordini;
* rifiutare ordini;
* cancellare ordini;
* scaricare PDF;
* accedere a tutte le schermate tranne gestione utenti.

Non puo:

* creare utenti;
* creare altri STORE_MANAGER.

## USER

Puo:

* effettuare ricerche;
* creare ordini;
* visualizzare esclusivamente i propri ordini;
* scaricare PDF dei propri ordini;
* cancellare i propri ordini in stato REQUIRED.

Non puo:

* modificare lo stato degli ordini;
* visualizzare ordini altrui;
* gestire articoli;
* gestire giacenze;
* creare utenti.

---

# 2. Stati Ordine

Stati disponibili:

* `REQUIRED`
* `APPROVED`
* `REJECTED`
* `CANCELED`

Stati finali:

* `APPROVED`
* `REJECTED`
* `CANCELED`

Un ordine in stato finale non puo cambiare stato.

---

# 3. Transizioni Ordine

Transizioni consentite:

* `REQUIRED -> APPROVED`
* `REQUIRED -> REJECTED`
* `REQUIRED -> CANCELED`

Nessuna altra transizione e consentita.

---

# 4. Creazione Ordine

Alla creazione:

* l'ordine nasce sempre in stato `REQUIRED`;
* il sistema verifica la disponibilita;
* le quantita vengono decrementate immediatamente;
* lo stock decrementato rappresenta stock prenotato;
* se la disponibilita non basta, l'ordine non viene creato;
* l'operazione deve essere atomica.

---

# 5. Ordine con Magazzino Obbligatorio

Ogni riga ordine deve indicare un magazzino.

Regole:

* la quantita richiesta deve essere disponibile in quel magazzino;
* il prelievo avviene solo da quel magazzino;
* viene creata una riga `OrderItem` per quel magazzino.
* l'ordine non puo essere creato se il magazzino non e selezionato.

La UI deve aiutare l'utente mostrando la disponibilita quando sono selezionati articolo e magazzino.

---

# 6. Approvazione

Quando un ordine passa da `REQUIRED` ad `APPROVED`:

* lo stock non cambia;
* viene registrato un evento di cambio stato;
* l'evento contiene l'id utente che ha autorizzato l'approvazione;
* l'evento contiene il timestamp dell'approvazione;
* l'ordine diventa finale.

---

# 7. Rifiuto

Quando un ordine passa da `REQUIRED` a `REJECTED`:

* le quantita prenotate vengono reintegrate;
* puo essere salvata una motivazione;
* viene registrato un evento di cambio stato;
* l'evento contiene l'id utente che ha autorizzato il rifiuto;
* l'evento contiene il timestamp del rifiuto;
* l'ordine diventa finale.

---

# 8. Cancellazione

Quando un ordine passa da `REQUIRED` a `CANCELED`:

* le quantita prenotate vengono reintegrate;
* puo essere salvata una motivazione;
* viene registrato un evento di cambio stato;
* l'evento contiene l'id utente che ha autorizzato la cancellazione;
* l'evento contiene il timestamp della cancellazione;
* l'ordine diventa finale.

Regola ownership:

* USER puo cancellare solo i propri ordini;
* ADMIN e STORE_MANAGER possono cancellare qualsiasi ordine REQUIRED.

---

# 9. Invarianti Stock

Le seguenti regole devono essere sempre vere:

* la quantita stock non puo essere negativa;
* non si puo prenotare piu della disponibilita;
* ogni modifica stock collegata a un ordine deve essere transazionale;
* rifiuto e cancellazione devono reintegrare esattamente le righe ordine;
* approvazione non deve reintegrare ne decrementare stock.

---

# 10. Audit Ordine

L'audit dei cambi stato non vive direttamente sulla testata ordine.

Per ogni creazione o cambio stato deve essere memorizzato un evento separato con:

* riferimento all'ordine;
* stato precedente, nullable per la creazione;
* nuovo stato;
* id utente che ha autorizzato l'operazione;
* timestamp dell'operazione;
* motivazione di rifiuto, se presente;
* motivazione di cancellazione, se presente.

La creazione ordine e rappresentata da un evento con:

* stato precedente nullo;
* nuovo stato `REQUIRED`;
* id utente richiedente;
* timestamp richiesta.

La testata ordine mantiene lo stato corrente, ma non duplica richiedente, data richiesta, gestore o data gestione.

---

# 11. PDF Ordine

Il PDF deve riflettere i dati salvati a database.

Deve includere:

* numero ordine;
* data creazione;
* utente richiedente;
* stato;
* elenco articoli;
* quantita;
* magazzini coinvolti;
* motivazione di rifiuto, se presente;
* motivazione di cancellazione, se presente.

Le regole di accesso al PDF sono le stesse del dettaglio ordine.
