# Proof of Concept

Questo documento definisce la POC iniziale di Stockly. La POC deve dimostrare il cuore del sistema con il minimo numero di funzionalita.

Stato: completata con il punto `0.6`.

---

# 1. Obiettivo

Dimostrare il flusso essenziale:

```text
disponibilita stock -> creazione ordine -> decremento stock -> approvazione o cancellazione -> stock coerente
```

La POC non deve coprire tutto il prodotto finale. Deve essere piccola, avviabile velocemente e utile per validare il dominio principale.

---

# 2. Database

La POC usa H2 in-memory con schema gestito da Flyway.

Conseguenze:

* a ogni riavvio dell'applicazione il database riparte da zero;
* lo schema viene ricreato tramite migrazioni Flyway;
* i dati demo vengono ricreati automaticamente da un seeder applicativo;
* non serve installare PostgreSQL;
* lo stato della demo e sempre prevedibile.

Nota futura: H2 puo restare utile anche per alcune fasi successive alla POC, finche il progetto non richiede validazioni realistiche su PostgreSQL, locking, concorrenza o deploy.

Configurazione attesa:

```properties
spring.datasource.url=jdbc:h2:mem:stockly
spring.h2.console.enabled=true
spring.flyway.enabled=true
spring.jpa.hibernate.ddl-auto=validate
```

## Migrazioni

Anche per la POC lo schema deve essere creato con Flyway.

File atteso:

```text
src/main/resources/db/migration/V1__create_initial_schema.sql
```

Regole:

* Flyway crea tabelle, vincoli e indici;
* Hibernate valida lo schema, non lo genera;
* evitare `spring.jpa.hibernate.ddl-auto=update`;
* le migrazioni devono restare compatibili con H2 per la POC e facili da portare a PostgreSQL.

---

# 3. Micro-Step di Implementazione

La POC va implementata in micro-step, non in un unico blocco.

## POC 0.1 - Setup Tecnico

Obiettivo: preparare il progetto per avviare la POC.

Attivita:

* aggiungere dipendenze Maven:
  * H2;
  * Flyway;
  * Thymeleaf;
  * Validation;
* creare profilo `poc`;
* configurare H2 in-memory;
* abilitare console H2;
* abilitare Flyway;
* configurare JPA con `ddl-auto=validate`.

Output:

* applicazione avviabile con profilo `poc`;
* configurazione POC documentata.

## POC 0.2 - Schema e Modello Dati

Obiettivo: creare lo schema minimo e il modello persistente.

Attivita:

* creare `src/main/resources/db/migration/V1__create_initial_schema.sql`;
* creare tabelle:
  * `warehouses`;
  * `items`;
  * `warehouse_items`;
  * `orders`;
  * `order_items`;
* creare entity JPA minime;
* creare enum `OrderStatus`;
* creare repository.

Output:

* schema creato da Flyway;
* entity validate da Hibernate;
* repository disponibili.

## POC 0.3 - Seed Demo

Obiettivo: popolare automaticamente dati demo coerenti.

Attivita:

* creare `DemoDataSeeder`;
* attivarlo solo con profilo `poc`;
* creare 2 magazzini demo;
* creare articoli demo;
* creare giacenze demo;
* creare ordini demo:
  * uno `REQUIRED`;
  * uno `APPROVED`;
  * uno `CANCELED`.

Output:

* dati demo ricreati a ogni avvio;
* stato iniziale prevedibile.

## POC 0.4 - Service Ordini

Obiettivo: implementare le regole centrali di stock e ordini.

Attivita:

* creare ordine con magazzino specifico;
* verificare disponibilita;
* decrementare stock alla creazione;
* approvare ordine `REQUIRED`;
* cancellare ordine `REQUIRED`;
* reintegrare stock alla cancellazione;
* impedire modifiche su ordini finali;
* introdurre eccezioni di dominio minime.

Output:

* workflow ordine minimo funzionante;
* stock coerente dopo ogni operazione.

## POC 0.5 - UI Minimale

Obiettivo: rendere la POC dimostrabile via browser.

Attivita:

* creare layout Thymeleaf semplice;
* creare pagina `/stock`;
* creare pagina `/orders/new`;
* creare pagina `/orders`;
* aggiungere azioni:
  * approva;
  * cancella;
* mostrare messaggi di successo o errore.

Output:

* flusso demo eseguibile da browser.

## POC 0.6 - Test e README

Obiettivo: chiudere la POC con verifica minima e istruzioni.

Attivita:

* aggiungere test minimi su `OrderService`;
* verificare avvio applicazione;
* aggiornare `README.md` con istruzioni POC;
* aggiornare roadmap se necessario.

Output:

* test minimi passanti;
* istruzioni per avviare e provare la POC.

---

# 4. Seed Automatico

All'avvio della POC devono essere creati dati demo tramite codice applicativo, non tramite migrazione Flyway.

Classe indicativa:

```text
src/main/java/com/tuna/stockly/config/DemoDataSeeder.java
```

Regole:

* il seeder deve essere attivo solo con profilo `poc`;
* i dati demo sono temporanei e servono alla dimostrazione;
* il seeder deve creare dati coerenti con le regole stock;
* gli ordini demo devono lasciare le giacenze in uno stato comprensibile.

## Magazzini

* Milano
* Roma

## Articoli

Almeno 3 articoli demo, ad esempio:

* Bullone M8
* Martello
* Tubo PVC

## Giacenze

Ogni articolo deve avere disponibilita in almeno un magazzino.

Esempio:

| Articolo   | Magazzino | Quantita |
| ---------- | --------- | -------: |
| Bullone M8 | Milano    |       50 |
| Bullone M8 | Roma      |       20 |
| Martello   | Milano    |       10 |
| Tubo PVC   | Roma      |       30 |

## Ordini Demo

Creare almeno:

* un ordine `REQUIRED`;
* un ordine `APPROVED`;
* un ordine `CANCELED`.

Gli ordini demo devono essere coerenti con le giacenze seedate.

---

# 5. Funzionalita Incluse

## Pagina Stock

Rotta indicativa:

```text
/stock
```

Mostra:

* articolo;
* marca o tipologia, se disponibile;
* magazzino;
* quantita disponibile.

Filtro opzionale:

* nome articolo.

## Creazione Ordine

Rotta indicativa:

```text
/orders/new
```

Permette di inserire:

* articolo;
* magazzino;
* quantita.

Regole:

* l'ordine nasce in stato `REQUIRED`;
* la quantita richiesta non puo superare la disponibilita del magazzino;
* lo stock viene decrementato immediatamente;
* se lo stock non basta, l'ordine non viene creato.

## Lista Ordini

Rotta indicativa:

```text
/orders
```

Mostra:

* id ordine;
* stato;
* data creazione;
* righe ordine;
* azioni disponibili.

## Azioni Ordine

Azioni minime:

* approva ordine `REQUIRED`;
* cancella ordine `REQUIRED`.

Regole:

* approvazione: cambia stato in `APPROVED`, stock invariato;
* cancellazione: cambia stato in `CANCELED`, stock reintegrato;
* ordini finali non sono modificabili.

---

# 6. Funzionalita Escluse

Escluse dalla POC:

* Spring Security;
* login e ruoli reali;
* gestione utenti;
* CRUD magazzini;
* CRUD articoli;
* gestione manuale giacenze;
* ordine senza magazzino, perche il magazzino e sempre obbligatorio;
* rifiuto con motivazione;
* PDF;
* Docker;
* AWS;
* UI rifinita;
* Testcontainers.

Queste funzionalita restano previste per il prodotto finale o per fasi successive.

---

# 7. Test Minimi

La POC deve avere almeno test sul service ordini per:

* creazione ordine con stock sufficiente;
* errore con stock insufficiente;
* approvazione senza modifica stock;
* cancellazione con reintegro stock;
* blocco modifiche su ordini finali.

---

# 8. Criteri di Successo

La POC e riuscita quando:

* l'applicazione parte localmente senza dipendenze esterne;
* i dati demo vengono creati automaticamente;
* la pagina stock mostra le giacenze;
* un ordine puo essere creato;
* la creazione ordine decrementa lo stock;
* l'approvazione lascia lo stock invariato;
* la cancellazione reintegra lo stock;
* i test minimi passano.

Stato verifica: criteri coperti dalla POC, da rivalidare a ogni modifica rilevante.

---

# 9. Deploy POC su Render Free

La POC puo essere pubblicata su Render Free usando Docker.

## Premesse

La POC usa:

* Spring Boot;
* profilo `poc`;
* H2 in-memory;
* Flyway;
* seed demo automatico.

Conseguenze:

* non serve configurare un database esterno;
* i dati vengono ricreati a ogni riavvio;
* Render Free puo mettere il servizio in sleep dopo inattivita;
* quando il servizio si riavvia, H2 riparte da zero.

Questo comportamento e accettabile per la POC.

## File Necessari

Il deploy usa:

* `Dockerfile`;
* `.dockerignore`;
* repository GitHub collegato a Render.

Il container espone internamente la porta `8080`, ma su Render usa automaticamente la variabile `PORT` fornita dalla piattaforma.

## Variabili Ambiente

Configurare su Render:

```text
SPRING_PROFILES_ACTIVE=poc
JAVA_OPTS=-XX:MaxRAMPercentage=75.0
```

`SPRING_PROFILES_ACTIVE=poc` e anche gia impostata nel `Dockerfile`, ma tenerla esplicita su Render rende il deploy piu leggibile.

## Deploy da Dashboard Render

Passi:

1. Creare o accedere a un account Render.
2. Collegare il repository GitHub che contiene Stockly.
3. Cliccare `New`.
4. Scegliere `Web Service`.
5. Selezionare il repository Stockly.
6. Impostare:
   * `Environment`: `Docker`;
   * `Branch`: branch desiderato;
   * `Instance Type`: `Free`.
7. Aggiungere le variabili ambiente indicate sopra.
8. Impostare, se richiesto, health check path:

```text
/stock
```

9. Avviare il deploy.

## Verifica

Quando Render completa il deploy, aprire l'URL pubblico generato.

Pagine da verificare:

```text
/stock
/orders/new
/orders
/h2-console
```

Parametri H2:

```text
JDBC URL: jdbc:h2:mem:stockly
User: sa
Password:
```

Nota: la console H2 e utile solo per la POC. In ambienti successivi andra disabilitata.

## Limiti Render Free

Render Free e adatto alla POC, non alla produzione.

Limiti principali:

* il servizio puo andare in sleep dopo inattivita;
* il primo accesso dopo sleep puo essere lento;
* il filesystem e temporaneo;
* l'app puo essere riavviata dalla piattaforma;
* con H2 in-memory i dati demo vengono ricreati a ogni riavvio.

## Troubleshooting

### Il servizio non parte

Controllare i log Render e verificare:

* build Docker completata;
* Java 21 usato correttamente;
* profilo `poc` attivo;
* porta letta dalla variabile `PORT`.

### La pagina e lenta al primo accesso

Su Render Free e normale dopo sleep. Attendere circa un minuto e ricaricare.

### I dati spariscono

Normale per la POC: H2 e in-memory. I dati demo vengono ricreati dal seeder a ogni avvio.
