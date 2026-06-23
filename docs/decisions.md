# Decisions

Decisioni architetturali registrate in formato ADR leggero.

Le decisioni devono restare coerenti con:

* `docs/architecture.md`;
* `docs/design.md`, quando toccano design applicativo;
* `docs/security.md`, quando toccano sicurezza;
* `docs/tasks.md`, quando generano lavoro operativo.

---

# ADR-001 - Monolite Spring Boot

## Decisione

Stockly e un monolite web Spring Boot.

## Motivazione

Il dominio e compatto, le funzionalita sono correlate e il deploy deve restare semplice.

## Alternative Considerate

* Microservizi.
* Architettura event-driven.

## Conseguenze

* Deploy piu semplice.
* Meno overhead operativo.
* Confini interni da mantenere con package, service e documentazione.

---

# ADR-002 - H2 In-Memory per POC

## Decisione

La POC usa H2 in-memory con Flyway e seed demo applicativo.

## Motivazione

Serve velocita di iterazione senza dipendenze esterne.

## Alternative Considerate

* PostgreSQL locale subito.
* Docker Compose con PostgreSQL dalla POC.

## Conseguenze

* Database reset a ogni riavvio.
* Demo sempre prevedibile.
* Non adatto a validare locking e concorrenza reale.
* PostgreSQL resta target per fasi mature.

---

# ADR-003 - Flyway Anche in POC

## Decisione

Lo schema e gestito da Flyway anche con H2.

## Motivazione

Le migrazioni devono essere versionate fin dall'inizio.

## Alternative Considerate

* Hibernate `ddl-auto=create`.
* Hibernate `ddl-auto=update`.

## Conseguenze

* Schema piu esplicito.
* Hibernate valida, non genera.
* Le migrazioni dovranno restare portabili verso PostgreSQL.

---

# ADR-004 - Audit Stato Ordine su Tabella Dedicata

## Decisione

I cambi stato ordine sono tracciati in `order_status_events`.

## Motivazione

La testata ordine deve mantenere lo stato corrente senza duplicare richiedente, gestore e timestamp derivabili dagli eventi.

## Alternative Considerate

* Campi audit direttamente su `orders`.
* Storico parziale solo con `updated_at`.

## Conseguenze

* Timeline consultabile.
* Meno duplicazione sulla testata.
* PDF e audit futuri potranno leggere dagli eventi.
* Finche non esiste `users`, `authorized_by_user_id` resta stringa applicativa.

---

# ADR-005 - Magazzino Obbligatorio in Creazione Ordine

## Decisione

Ogni riga ordine deve indicare un magazzino.

## Motivazione

La distribuzione automatica tra magazzini complica il dominio e non serve al flusso attuale.

## Alternative Considerate

* Ordine senza magazzino con scelta automatica del magazzino.
* Distribuzione automatica su piu magazzini.

## Conseguenze

* Regola piu semplice.
* UI deve mostrare disponibilita per articolo e magazzino.
* Il service rifiuta ordini senza magazzino.

---

# ADR-006 - Render Free per Deploy POC

## Decisione

La POC puo essere deployata su Render Free via Docker.

## Motivazione

Render permette un deploy rapido da GitHub senza gestire VM.

## Alternative Considerate

* Koyeb.
* Google Cloud Run.
* Oracle Cloud Always Free.
* Railway.

## Conseguenze

* Possibile sleep dopo inattivita.
* Primo accesso lento dopo sleep.
* H2 riparte da zero a ogni restart.
* Non e una configurazione production-ready.

---

# ADR-007 - Dockerfile Multi-Stage per POC

## Decisione

Il container usa build multi-stage con Java 21.

## Motivazione

Separare build e runtime riduce il peso dell'immagine finale.

## Alternative Considerate

* Deploy jar diretto.
* Dockerfile single-stage.

## Conseguenze

* Render puo buildare direttamente dal repository.
* Il runtime usa JRE.
* La porta e configurata con variabile `PORT`.

---

# ADR-008 - Lombok Non nella POC

## Decisione

Lombok non e introdotto nella POC.

## Motivazione

La POC privilegia esplicitezza e controllo delle entity JPA.

## Alternative Considerate

* Usare Lombok subito per ridurre boilerplate.

## Conseguenze

* Piu codice boilerplate.
* Regole entity piu visibili.
* Lombok potra essere introdotto dopo con regole strette.
