# Architecture

Questo documento raccoglie le scelte tecniche principali di Stockly. Le decisioni qui presenti guidano l'implementazione e vanno aggiornate quando una scelta architetturale cambia.

---

# 1. Stile Architetturale

Stockly e un monolite web Spring Boot.

La scelta del monolite e intenzionale:

* il dominio e compatto;
* le funzionalita sono fortemente correlate;
* il deploy deve restare semplice;
* il team ha forte competenza backend e limitata esperienza frontend.

Non si introducono microservizi, code o componenti distribuiti finche non esiste un bisogno reale.

---

# 2. Stack Tecnologico

## Backend

* Java 21
* Spring Boot
* Spring MVC
* Spring Data JPA
* Hibernate
* Spring Security
* Maven

## Frontend

* Thymeleaf
* Bootstrap
* HTMX solo se aggiunge valore concreto a una schermata

## Database

* PostgreSQL
* Flyway per migrazioni versionate
* H2 in-memory solo per POC e test locali mirati

## Strategia Temporanea H2/PostgreSQL

Per accelerare lo sviluppo iniziale, H2 in-memory puo essere mantenuto anche oltre la POC, finche il prodotto non raggiunge una fase piu matura.

Motivazioni:

* avvio locale piu semplice;
* nessuna dipendenza da servizi esterni;
* iterazione rapida su UI, service e flussi base;
* seed demo sempre ripetibile.

Vincoli:

* PostgreSQL resta il database target per produzione;
* Flyway resta obbligatorio anche con H2;
* evitare SQL non portabile quando possibile;
* prima di lavorare seriamente su concorrenza, locking, performance o deploy, introdurre PostgreSQL locale;
* prima del deploy, testare le migrazioni e i flussi principali su PostgreSQL.

Trigger consigliati per passare a PostgreSQL locale:

* introduzione di locking pessimistico sulle giacenze;
* completamento del workflow ordini reale;
* introduzione di sicurezza e utenti persistenti;
* preparazione Docker/deploy;
* differenze H2/PostgreSQL che iniziano a influenzare il codice.

## PDF

* OpenPDF come prima scelta, salvo vincoli tecnici futuri

## Deploy

* Docker
* AWS App Runner
* Amazon RDS PostgreSQL

---

# 3. Struttura Package

La struttura dei package segue il dominio, non gli strati tecnici generici.

Package previsti:

* `com.tuna.stockly.config`
* `com.tuna.stockly.user`
* `com.tuna.stockly.warehouse`
* `com.tuna.stockly.item`
* `com.tuna.stockly.stock`
* `com.tuna.stockly.order`
* `com.tuna.stockly.pdf`
* `com.tuna.stockly.web`

Ogni package di dominio puo contenere controller, service, repository, entity, DTO e form relativi a quel dominio.

Esempio:

```text
order/
  Order.java
  OrderItem.java
  OrderStatus.java
  OrderRepository.java
  OrderService.java
  OrderController.java
  CreateOrderForm.java
```

## Rivalutazione Post-POC

Durante la POC e accettabile mantenere package di dominio piatti, ad esempio `order` con entity, enum, repository, service e command nello stesso livello.

Dopo la POC bisogna rivalutare l'alberatura del progetto, perche con la crescita del codice questa struttura puo diventare confusionaria.

Alternative da valutare:

### Alternativa A - Package per dominio con sottopackage tecnici

```text
order/
  domain/
    StockOrder.java
    OrderItem.java
    OrderStatus.java
  application/
    OrderService.java
    CreateOrderCommand.java
  persistence/
    StockOrderRepository.java
    OrderItemRepository.java
  web/
    OrderController.java
    CreateOrderForm.java
```

Vantaggi:

* mantiene il dominio come confine principale;
* separa entity, service, repository e web;
* scala meglio quando una feature cresce.

Svantaggi:

* piu cartelle;
* puo essere eccessivo per domini piccoli.

### Alternativa B - Package per layer tecnici

```text
domain/
  order/
  stock/
  item/
repository/
service/
web/
dto/
```

Vantaggi:

* familiare per molti progetti Spring;
* facile trovare tutti i repository o tutti i controller.

Svantaggi:

* disperde una feature su molti package;
* rende meno visibili i confini del dominio;
* tende a favorire dipendenze trasversali.

Nota di preferenza:

* il committente preferisce questa alternativa;
* e percepita come la struttura piu diffusa nei progetti Spring tradizionali;
* va valutata seriamente dopo la POC, bilanciando familiarita, ordine del codice e confini di dominio.

### Alternativa C - Package per feature con separazione minima

```text
order/
  StockOrder.java
  OrderItem.java
  OrderStatus.java
  OrderService.java
  StockOrderRepository.java
  web/
    OrderController.java
    CreateOrderForm.java
```

Vantaggi:

* poco boilerplate;
* adatta a feature piccole o medie;
* mantiene vicine le classi correlate.

Svantaggi:

* puo diventare disordinata se il numero di classi cresce;
* non separa chiaramente application, domain e persistence.

Preferenza iniziale da discutere dopo la POC:

* preferenza del committente: Alternativa B, per layer tecnici;
* preferenza tecnica iniziale proposta: Alternativa A per domini centrali come `order` e `stock`, Alternativa C per domini piu semplici come `item` e `warehouse`;
* la scelta finale va presa dopo la POC, guardando il codice reale prodotto e non solo il modello teorico.

---

# 4. Separazione Responsabilita

## Controller

I controller:

* ricevono request web;
* validano form tramite Bean Validation;
* invocano service;
* scelgono view o redirect;
* non contengono logica di business.

## Service

I service:

* contengono le regole di dominio;
* gestiscono transazioni;
* applicano workflow e invarianti;
* coordinano repository multipli;
* sono il punto principale da testare.

## Repository

I repository:

* incapsulano accesso dati;
* espongono query esplicite quando necessario;
* non contengono logica di business.

## Entity

Le entity:

* rappresentano stato persistente;
* possono contenere piccoli metodi di dominio locali;
* non devono dipendere da controller, form o view.

## Audit Stato Ordine

L'audit delle transizioni ordine e separato dalla testata ordine.

Decisione:

* `StockOrder` mantiene lo stato corrente e le righe ordine;
* `StockOrder` non contiene richiedente, data richiesta, gestore o data gestione;
* ogni creazione o cambio stato crea una riga in `order_status_events`;
* `order_status_events` contiene stato precedente, nuovo stato, id utente autorizzante e timestamp;
* la creazione ordine viene tracciata come evento verso `REQUIRED` con stato precedente nullo.

Conseguenze:

* la storia delle transizioni e consultabile senza sovraccaricare la testata;
* l'ordine non duplica campi derivabili dagli eventi;
* in futuro sara piu semplice mostrare timeline, audit e PDF completi;
* finche non esiste una tabella utenti reale, l'id utente resta una stringa applicativa.

---

# 5. Transazioni e Concorrenza

Le operazioni che modificano stock e ordini devono essere transazionali.

Regole:

* la creazione ordine decrementa lo stock nella stessa transazione;
* rifiuto e cancellazione reintegrano lo stock nella stessa transazione;
* approvazione non modifica lo stock;
* le giacenze devono essere bloccate durante la prenotazione;
* la quantita disponibile non deve mai diventare negativa.

Per le giacenze si preferisce locking pessimistico sulle righe `warehouse_items` coinvolte.

---

# 6. Database

Lo schema database e governato da Flyway.

Regole:

* non usare `spring.jpa.hibernate.ddl-auto=update` negli ambienti reali;
* nella POC usare H2 in-memory con Flyway e `spring.jpa.hibernate.ddl-auto=validate`;
* ogni modifica schema passa da una migrazione;
* le migrazioni non si riscrivono dopo essere state condivise;
* vincoli importanti devono stare nel database oltre che nel codice.

Vincoli attesi:

* username univoco;
* barcode univoco;
* coppia warehouse-item univoca;
* quantita stock non negativa;
* stato ordine limitato agli enum previsti;
* eventi stato ordine collegati a un ordine esistente.

## Seed Demo

I dati demo della POC non devono stare nelle migrazioni Flyway.

Regole:

* Flyway gestisce lo schema;
* un seeder applicativo gestisce i dati demo;
* il seeder deve essere attivo solo con profilo `poc`;
* i dati demo possono cambiare liberamente durante la POC.

---

# 7. Sicurezza

Stockly usa Spring Security con sessione HTTP e form login.

Autorizzazione:

* rotte web protette per ruolo;
* controlli di ownership per gli ordini USER;
* controlli nei service per operazioni sensibili.

Non basta nascondere pulsanti nella UI: le regole devono essere applicate anche lato server.

---

# 8. Error Handling

Gli errori di dominio devono essere espliciti.

Esempi:

* stock insufficiente;
* ordine non modificabile;
* transizione non consentita;
* accesso a ordine non autorizzato;
* articolo o magazzino inesistente.

La UI deve mostrare messaggi comprensibili, senza esporre stack trace.

---

# 9. Configurazione

La configurazione varia per profilo.

Profili previsti:

* `poc`
* `local`
* `test`
* `prod`

In produzione le credenziali non devono stare nel repository. Si usano variabili ambiente.

---

# 10. Decisioni da Rivalutare

Le seguenti scelte possono cambiare se emergono vincoli concreti:

* uso di HTMX;
* OpenPDF rispetto ad Apache PDFBox;
* Testcontainers nella prima fase;
* livello di auditing oltre ai campi richiesti.
