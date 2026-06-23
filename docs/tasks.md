# Tasks

Questo documento e la fonte di verita operativa per milestone, task e stato progetto.

Input:

* `docs/requirements.md`;
* `docs/architecture.md`;
* `docs/security.md`;
* `docs/design.md`, quando i task riguardano UI;
* `docs/decisions.md`, quando i task derivano da decisioni registrate.

Legenda:

* `P0`: immediato o bloccante;
* `P1`: importante per MVP;
* `P2`: utile ma posticipabile;
* `todo`: da fare;
* `doing`: in corso;
* `done`: completato.

---

# 1. Completed Milestones

## POC

Stato: `done`.

Obiettivo validato:

```text
visualizzazione stock -> creazione ordine -> decremento stock -> approvazione o cancellazione -> stock coerente
```

Completato:

* `done` POC 0.1 - setup Maven, profilo `poc`, H2, Flyway, Thymeleaf e Validation;
* `done` POC 0.2 - schema iniziale, entity JPA, enum e repository;
* `done` POC 0.3 - seed demo con 2 magazzini, articoli, giacenze e ordini demo;
* `done` POC 0.4 - service ordini con prenotazione stock, approvazione, cancellazione e reintegro;
* `done` POC 0.5 - UI minimale per stock, creazione ordine e lista ordini;
* `done` POC 0.6 - test minimi e README operativo;
* `done` audit ordine su `order_status_events`;
* `done` magazzino obbligatorio in creazione ordine;
* `done` disponibilita mostrata in UI per articolo e magazzino;
* `done` Dockerfile e `.dockerignore`;
* `done` deploy POC Render documentato in `docs/archive/poc.md`.

Nota verifica:

* la POC e stata verificata prima delle ultime modifiche locali;
* dopo problemi locali JDK/Maven, rilanciare la suite completa appena la toolchain e stabile.

---

# 2. MVP

Obiettivo: evolvere la POC in un'applicazione interna usabile con utenti, ruoli, anagrafiche e workflow ordini completo.

Scope MVP:

* struttura package definitiva;
* profili `local`, `test`, `prod`;
* sicurezza con Spring Security;
* utenti e ruoli persistenti;
* CRUD magazzini;
* CRUD articoli;
* gestione giacenze;
* ricerca articoli;
* ordine multi-riga;
* workflow `REQUIRED -> APPROVED/REJECTED/CANCELED`;
* audit eventi ordine;
* test service, repository e MVC principali.

Fuori MVP:

* AWS production deploy;
* PDF avanzato;
* dashboard statistiche;
* notifiche;
* esportazione Excel.

---

# 3. Milestones

## 1. Fondazione Progetto Completo

Output:

* profili Spring reali;
* struttura package consolidata;
* README aggiornato;
* configurazione locale chiara.

## 2. Modello Dati e Migrazioni

Output:

* schema persistente completo;
* utenti e ruoli;
* migrazioni Flyway;
* vincoli database;
* audit eventi ordine consolidato.

## 3. Sicurezza e Utenti

Output:

* login;
* ruoli;
* gestione utenti base;
* password BCrypt;
* autorizzazioni lato server.

## 4. Magazzini, Articoli e Giacenze

Output:

* CRUD magazzini;
* CRUD articoli;
* gestione giacenze;
* ricerca articoli.

## 5. Motore Ordini

Output:

* ordine multi-riga;
* magazzino obbligatorio per ogni riga;
* locking giacenze;
* transizioni complete;
* reintegro su reject/cancel;
* ownership USER.

## 6. Interfaccia Web

Output:

* layout condiviso;
* dashboard per ruolo;
* pagine CRUD;
* dettaglio ordine;
* messaggi errore coerenti.

## 7. PDF Ordine

Output:

* PDF scaricabile;
* dati coerenti con database;
* accesso protetto.

## 8. Test e Qualita

Output:

* test repository;
* test service;
* test MVC;
* test PDF smoke;
* eventuale Testcontainers PostgreSQL.

## 9. Docker e Deploy

Output:

* Dockerfile production-ready;
* `.dockerignore`;
* `docker-compose.yml` locale;
* documentazione variabili ambiente;
* note App Runner.

---

# 4. Tasks

## P0 - Consolidamento

* `todo` Rilanciare `mvn test` con JDK locale funzionante dall'ambiente utente.
* `done` Decidere struttura package definitiva.
* `done` Documentare la decisione package in `docs/decisions.md`.
* `done` Aggiornare README con nuova struttura documentale finale.

## P1 - Fondazione

* `done` Definire profilo `local`.
* `done` Definire profilo `test`.
* `done` Definire profilo `prod`.
* `done` Spostare configurazione POC fuori dai default generali.
* `done` Verificare che H2 console sia attiva solo in `local` e `poc`.

## P1 - Sicurezza

* `todo` Aggiungere dipendenza Spring Security.
* `todo` Creare enum `Role`.
* `todo` Creare entity `User`.
* `todo` Creare repository utenti.
* `todo` Configurare BCrypt.
* `todo` Implementare login form.
* `todo` Creare bootstrap admin iniziale.
* `todo` Proteggere `/orders` per ruolo.
* `todo` Applicare ownership ordini lato service.

## P1 - Stock

* `todo` Creare pagina lista magazzini.
* `todo` Creare form nuovo magazzino.
* `todo` Creare pagina lista articoli.
* `todo` Creare form nuovo articolo.
* `todo` Creare pagina lista giacenze.
* `todo` Creare form modifica giacenza.
* `todo` Aggiungere ricerca articoli per nome.
* `todo` Aggiungere ricerca per barcode.
* `todo` Aggiungere ricerca per marca.
* `todo` Aggiungere ricerca per tipologia.
* `todo` Aggiungere filtro per magazzino.

## P1 - Ordini

* `todo` Definire form ordine multi-riga.
* `todo` Implementare command multi-riga.
* `todo` Validare magazzino obbligatorio per ogni riga.
* `todo` Mostrare disponibilita per ogni riga ordine.
* `todo` Implementare rifiuto ordine.
* `todo` Salvare motivazione rifiuto.
* `todo` Salvare motivazione cancellazione, se richiesta.
* `todo` Applicare locking sulle righe stock.
* `todo` Testare transizioni non valide.
* `todo` Testare eventi audit.

## P2 - PDF

* `todo` Scegliere definitivamente OpenPDF o PDFBox.
* `todo` Creare servizio generazione PDF.
* `todo` Creare template dati PDF.
* `todo` Creare endpoint download PDF.
* `todo` Proteggere download PDF.
* `todo` Aggiungere smoke test PDF.

## P2 - Deploy

* `todo` Validare Dockerfile localmente.
* `todo` Decidere data introduzione PostgreSQL locale.
* `todo` Creare `docker-compose.yml`.
* `todo` Preparare profilo produzione.
* `todo` Documentare deploy App Runner.

---

# 5. Technical Debt

* `P0 todo` Verificare definitivamente JDK/Maven dall'ambiente utente.
* `P0 todo` Rilanciare test dopo refactor package.
* `P1 done` Rivalutare package structure.
* `P1 todo` Sostituire `demo.user` con utente autenticato.
* `P1 done` Disabilitare H2 console fuori da `local` e `poc`.
* `P1 todo` Migliorare gestione errori controller.
* `P2 todo` Valutare introduzione controllata di Lombok.
* `P2 todo` Evitare dipendenza prolungata da H2 per scenari di locking.

---

# 6. Future Features

* PDF ordine.
* Dashboard statistiche.
* Esportazione Excel.
* Notifiche email.
* Integrazioni con sistemi esterni.
* Deploy AWS App Runner.
* PostgreSQL RDS.
* Testcontainers.
* HTMX mirato dove utile.
