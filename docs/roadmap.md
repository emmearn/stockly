# Stockly Roadmap Tecnica

Questa roadmap traduce le specifiche funzionali in passi di implementazione progressivi. L'obiettivo immediato e costruire una POC piccola che dimostri il cuore del dominio; successivamente si estende il progetto fino alla versione completa.

---

# 0. POC

Stato: completata.

## Obiettivi

Realizzare una prima demo locale, piccola e funzionante, basata su H2 in-memory.

La POC deve dimostrare:

* disponibilita stock;
* creazione ordine;
* decremento immediato dello stock;
* approvazione ordine senza modifica stock;
* cancellazione ordine con reintegro stock.

## Attivita

* POC 0.1 - Setup tecnico:
  * dipendenze Maven H2, Flyway, Thymeleaf, Validation;
  * profilo `poc`;
  * H2 in-memory;
  * console H2;
  * Flyway abilitato;
  * JPA con `ddl-auto=validate`.
* POC 0.2 - Schema e modello dati:
  * `V1__create_initial_schema.sql`;
  * entity minime;
  * enum `OrderStatus`;
  * repository.
* POC 0.3 - Seed demo:
  * `DemoDataSeeder` attivo solo con profilo `poc`;
  * 2 magazzini demo;
  * articoli demo;
  * giacenze demo;
  * ordini demo.
* POC 0.4 - Service ordini:
  * creazione ordine;
  * decremento stock;
  * approvazione;
  * cancellazione;
  * reintegro stock;
  * errori dominio minimi.
* POC 0.5 - UI minimale:
  * `/stock`;
  * `/orders/new`;
  * `/orders`;
  * azioni approva/cancella.
* POC 0.6 - Test e README:
  * test minimi su `OrderService`;
  * istruzioni avvio POC;
  * aggiornamento documentazione se necessario.

## Esclusioni

* sicurezza e login;
* ruoli reali;
* gestione utenti;
* CRUD anagrafiche;
* PDF;
* Docker;
* AWS.

## Output

* applicazione avviabile localmente;
* demo ripetibile a ogni riavvio;
* dati demo ricreati automaticamente;
* test minimi passanti.

## Appunti Post-POC

Prima di passare alla fondazione del progetto completo, rivalutare l'alberatura dei package.

Tema da discutere:

* la POC usa package di dominio piatti, ad esempio `order` con enum, entity, repository, service e command insieme;
* questa scelta e accettabile per velocita iniziale;
* dopo la POC valutare se introdurre sottopackage come `domain`, `application`, `persistence` e `web`.
* il committente preferisce una struttura per layer tecnici, perche ritenuta piu diffusa nei progetti Spring tradizionali.

Documento di riferimento: `docs/architecture.md`, sezione "Rivalutazione Post-POC".

Documento di riferimento: `docs/poc.md`.

---

# 1. Fondazione Progetto Completo

## Obiettivi

Preparare il progetto Spring Boot per evolvere dalla POC alla versione reale.

Nota: H2 in-memory puo restare il database di sviluppo anche nelle prime fasi dopo la POC, se questo mantiene alta la velocita di iterazione. PostgreSQL va introdotto quando il progetto entra in una fase piu matura o quando diventano centrali locking, concorrenza, sicurezza persistente, Docker o deploy.

## Attivita

* completare le dipendenze Maven:
  * Spring Security;
  * PostgreSQL;
  * Flyway;
  * OpenPDF o PDFBox;
* configurare profili Spring:
  * `local`;
  * `test`;
  * `prod`;
* aggiungere configurazione database tramite variabili ambiente;
* creare una struttura package coerente:
  * `config`;
  * `user`;
  * `warehouse`;
  * `item`;
  * `stock`;
  * `order`;
  * `pdf`;
  * `web`;
* aggiornare `README.md` con requisiti e avvio locale.

## Output

* progetto Maven pronto;
* applicazione avviabile;
* configurazione locale documentata.

---

# 2. Modello Dati e Migrazioni

## Obiettivi

Definire lo schema persistente e le entita principali.

## Attivita

* introdurre Flyway;
* creare migrazione iniziale per:
  * users;
  * warehouses;
  * items;
  * warehouse_items;
  * orders;
  * order_items;
* definire enum:
  * `Role`;
  * `OrderStatus`;
* creare entita JPA;
* configurare vincoli:
  * username univoco;
  * barcode univoco;
  * combinazione warehouse-item univoca;
  * quantity non negativa;
* aggiungere auditing base su ordini.

## Output

* schema database versionato;
* entita JPA;
* repository principali.

---

# 3. Sicurezza e Utenti

## Obiettivi

Abilitare autenticazione, ruoli e gestione utenti.

## Attivita

* configurare Spring Security con form login;
* implementare `UserDetailsService`;
* salvare password con BCrypt;
* creare seed o bootstrap admin iniziale;
* implementare gestione utenti riservata ad ADMIN:
  * creazione USER;
  * creazione STORE_MANAGER;
  * attivazione/disattivazione;
* proteggere rotte e metodi con autorizzazioni coerenti.

## Output

* login funzionante;
* ruoli applicati;
* gestione utenti base.

---

# 4. Magazzini, Articoli e Giacenze

## Obiettivi

Implementare anagrafiche e stock.

## Attivita

* CRUD magazzini;
* CRUD articoli;
* gestione giacenze per combinazione articolo-magazzino;
* validazioni form;
* ricerca articoli con filtri:
  * nome;
  * barcode;
  * marca;
  * tipologia;
  * magazzino;
* risultati aggregati per magazzino.

## Output

* gestione articoli e stock per ADMIN/STORE_MANAGER;
* ricerca disponibile a tutti i ruoli.

---

# 5. Motore Ordini e Stock Reservation

## Obiettivi

Implementare la parte piu critica: creazione ordini, prenotazione stock e transizioni.

## Attivita

* creare DTO/form per ordine multi-riga;
* implementare creazione ordine in stato REQUIRED;
* richiedere sempre il magazzino per ogni riga ordine;
* verificare disponibilita nel magazzino selezionato;
* mostrare in UI la disponibilita per articolo e magazzino selezionati;
* decrementare stock nella stessa transazione;
* applicare locking sulle giacenze;
* implementare transizioni:
  * REQUIRED -> APPROVED;
  * REQUIRED -> REJECTED;
  * REQUIRED -> CANCELED;
* reintegrare stock su REJECTED e CANCELED;
* impedire transizioni da stati finali;
* applicare autorizzazioni:
  * USER cancella solo propri ordini REQUIRED;
  * ADMIN/STORE_MANAGER gestiscono tutti gli ordini.

## Output

* workflow ordini completo;
* stock coerente;
* test di dominio per casi critici.

---

# 6. Interfaccia Web

## Obiettivi

Costruire le pagine applicative con Thymeleaf e Bootstrap.

## Attivita

* layout base condiviso;
* login;
* dashboard per ruolo;
* ricerca articoli;
* creazione ordine;
* lista ordini:
  * tutti per ADMIN/STORE_MANAGER;
  * propri per USER;
* dettaglio ordine con azioni consentite;
* gestione articoli e giacenze;
* gestione utenti.

## Output

* applicazione usabile via browser;
* navigazione coerente per ruolo.

---

# 7. PDF Ordine

## Obiettivi

Generare PDF riepilogativi dai dati persistiti.

## Attivita

* scegliere libreria PDF;
* creare servizio generazione PDF;
* includere:
  * numero ordine;
  * data creazione;
  * utente richiedente;
  * stato;
  * righe ordine;
  * quantita;
  * magazzini coinvolti;
  * motivazioni eventuali;
* aggiungere download dal dettaglio ordine;
* proteggere accesso PDF secondo le stesse regole del dettaglio ordine.

## Output

* PDF scaricabile per ogni ordine visibile all'utente.

---

# 8. Test e Qualita

## Obiettivi

Proteggere le regole centrali del sistema.

## Attivita

* test repository per query principali;
* test servizi per:
  * prenotazione stock;
  * reintegro;
  * transizioni non valide;
  * permessi principali;
* test MVC per rotte critiche;
* test PDF smoke;
* eventuale Testcontainers PostgreSQL per scenari realistici.

## Output

* suite test eseguibile con Maven;
* copertura sulle regole ad alto rischio.

---

# 9. Docker e Deploy

## Obiettivi

Preparare il rilascio su AWS App Runner con PostgreSQL RDS.

## Attivita

* creare `Dockerfile`;
* creare `.dockerignore`;
* documentare variabili ambiente:
  * `DATABASE_URL`;
  * `DATABASE_USERNAME`;
  * `DATABASE_PASSWORD`;
  * `SPRING_PROFILES_ACTIVE`;
  * eventuali credenziali admin iniziali;
* creare `docker-compose.yml` per sviluppo locale con PostgreSQL;
* documentare build e run;
* predisporre note per App Runner.

## Output

* container avviabile;
* ambiente locale replicabile;
* istruzioni deploy.

---

# 10. Rifinitura e Documentazione

## Obiettivi

Rendere il progetto mantenibile e consegnabile.

## Attivita

* aggiornare README;
* aggiungere documentazione architetturale breve;
* aggiungere guida utente essenziale;
* verificare messaggi di errore e validazioni;
* pulire codice e package;
* revisione finale sicurezza e permessi.

## Output

* documentazione minima completa;
* applicazione pronta per uso interno.

---

# Sequenza Consigliata

0. POC.
1. Fondazione progetto completo.
2. Modello dati e migrazioni.
3. Sicurezza e utenti.
4. Magazzini, articoli e giacenze.
5. Motore ordini e stock reservation.
6. Interfaccia web.
7. PDF.
8. Test e qualita.
9. Docker e deploy.
10. Rifinitura documentazione.

---

# Decisioni Aperte

* Decidere quando passare da H2 in-memory a PostgreSQL locale. Preferenza attuale: mantenere H2 anche per alcune fasi post-POC e introdurre PostgreSQL in una fase piu matura.
* Scegliere definitivamente tra OpenPDF e Apache PDFBox.
* Decidere se creare l'admin iniziale via seed locale, variabili ambiente o comando manuale.
* Decidere se includere HTMX nella prima versione o tenerlo come evoluzione.
* Decidere se usare Testcontainers gia dalla prima fase o introdurlo quando il dominio ordini sara completo.
