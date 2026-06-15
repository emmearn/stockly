# Development Guidelines

Questo documento raccoglie le regole operative per sviluppare Stockly: codice, logging, test e documentazione.

---

# 1. Principi di Codice

Il codice deve essere:

* chiaro prima che ingegnoso;
* piccolo e componibile;
* vicino al dominio;
* facile da testare;
* esplicito nelle regole importanti.

Non si introducono astrazioni finche non eliminano duplicazione reale o rendono piu chiara una regola di dominio.

Usare nomi orientati al dominio, ad esempio:

* `OrderService`;
* `StockReservationService`;
* `WarehouseItemRepository`;
* `CreateOrderForm`;
* `InsufficientStockException`.

Evitare nomi generici come:

* `Manager`;
* `Helper`;
* `Utils`;
* `Processor`;
* `DataObject`.

---

# 2. Struttura e Responsabilita

Il package principale e `com.tuna.stockly`.

Durante la POC i package seguono il dominio:

* `user`;
* `warehouse`;
* `item`;
* `stock`;
* `order`;
* `pdf`;
* `config`;
* `web`.

La struttura package definitiva verra rivalutata dopo la POC, come descritto in `docs/architecture.md`.

## Controller

I controller devono essere sottili.

Devono:

* ricevere form;
* validare input;
* chiamare service;
* gestire redirect e model;
* delegare la business logic.

Non devono:

* calcolare disponibilita;
* decidere transizioni ordine;
* modificare entity complesse direttamente;
* contenere query;
* aprire transazioni.

## Service

I service contengono le regole applicative.

Regole:

* ogni operazione di scrittura rilevante deve essere transazionale;
* i metodi pubblici devono rappresentare casi d'uso;
* le eccezioni di dominio devono essere leggibili;
* i controlli di permesso critici devono stare anche nei service.

## Entity JPA

Le entity devono rappresentare lo stato persistente.

Regole:

* usare `Long` come id;
* evitare setter pubblici quando una modifica deve passare da una regola;
* inizializzare collezioni;
* evitare logica che richiede repository dentro entity;
* evitare `EAGER` salvo necessita esplicita;
* non esporre entity direttamente nei form web.

Metodi di dominio piccoli sono accettati.

---

# 3. DTO, Form, Command e Validazione

Separare i modelli in base all'uso:

* Form: dati provenienti da pagine Thymeleaf;
* Command: input interno verso i service;
* DTO/ViewModel: dati preparati per UI, API future o PDF.

Non usare entity JPA come oggetti di form.

Usare Bean Validation per controlli semplici:

* campi obbligatori;
* lunghezze;
* numeri positivi;
* formato base.

Usare service per controlli di dominio:

* stock disponibile;
* transizione consentita;
* ownership ordine;
* permessi applicativi.

---

# 4. Errori e Logging

Creare eccezioni specifiche quando aiutano a capire il problema.

Esempi:

* `InsufficientStockException`;
* `InvalidOrderTransitionException`;
* `OrderNotAccessibleException`;
* `StockConcurrencyException`.

Evitare messaggi generici come `operation failed`.

## Logging

Usare logging per eventi utili alla diagnosi.

Regole:

* usare SLF4J tramite il logger standard del progetto;
* preferire messaggi brevi, concreti e orientati all'evento;
* usare placeholder parametrizzati, non concatenazione di stringhe;
* inserire identificativi utili, ad esempio `orderId`, `itemId`, `warehouseId` e username applicativo;
* non usare `System.out.println` o `printStackTrace`;
* non usare il log come sostituto di test, validazioni o gestione errori.

Livelli:

* `TRACE`: dettagli molto fini, normalmente disabilitati;
* `DEBUG`: informazioni utili durante sviluppo o diagnosi locale;
* `INFO`: eventi applicativi rilevanti ma attesi;
* `WARN`: situazioni anomale ma gestite;
* `ERROR`: errori inattesi o fallimenti che richiedono attenzione.

Loggare a `INFO`:

* creazione ordine;
* cambio stato ordine;
* cancellazione o rifiuto ordine;
* bootstrap o seed demo in ambiente POC.

Loggare a `WARN`:

* tentativi di operazioni non consentite;
* stock insufficiente su richiesta valida dal punto di vista formale;
* problemi di concorrenza gestiti;
* dati demo mancanti o incoerenti in ambiente locale.

Non loggare mai:

* password;
* token;
* cookie o session id;
* stringhe di connessione complete;
* dati personali non necessari;
* payload completi di form o request;
* stack trace duplicati senza contesto.

Le eccezioni di dominio attese non devono essere sempre `ERROR`. Loggare lo stack trace una sola volta, nel punto in cui l'errore viene gestito o trasformato in risposta.

---

# 5. Lombok

Lombok non viene introdotto nella POC iniziale.

Potra essere introdotto in futuro se il boilerplate diventa significativo, ma solo con regole strette.

Regole consentite:

* `@Getter` su entity, DTO, form e command;
* `@NoArgsConstructor(access = AccessLevel.PROTECTED)` sulle entity JPA;
* `@RequiredArgsConstructor` su service, controller e componenti Spring;
* `@Builder` su DTO o command, se migliora la leggibilita dei test o dei casi d'uso.

Regole vietate o da evitare:

* evitare `@Data` sulle entity JPA;
* evitare `@Setter` globale sulle entity JPA;
* evitare `@AllArgsConstructor` pubblico sulle entity JPA;
* evitare `@EqualsAndHashCode` automatico sulle entity JPA senza una decisione esplicita;
* non usare Lombok per nascondere regole di dominio.

---

# 6. Test

La priorita dei test e proteggere le regole di dominio, in particolare stock, ordini, permessi e transazioni.

I test devono garantire che:

* lo stock non diventi mai negativo;
* gli ordini rispettino il workflow;
* i permessi siano applicati lato server;
* le query principali restituiscano dati corretti;
* il PDF venga generato dai dati persistiti;
* l'applicazione resti avviabile.

## Tipi di Test

Unit test:

* logica pura o componenti isolabili;
* validazione transizioni ordine;
* calcolo stati finali.

Integration test:

* repository;
* transazioni;
* comportamento JPA;
* vincoli database;
* migrazioni Flyway.

Service test:

* creazione ordine;
* stock insufficiente;
* decremento stock;
* approvazione senza modifica stock;
* rifiuto o cancellazione con reintegro;
* divieto di modifica per stati finali;
* permessi applicativi.

MVC test:

* rotte;
* form;
* sicurezza web;
* azioni visibili e invocabili secondo ruolo.

## Database nei Test

Decisione consigliata:

* H2 per test semplici e veloci;
* Testcontainers PostgreSQL per stock, locking, migrazioni e concorrenza realistica.

I dati di test devono essere espliciti e leggibili. Evitare dati globali condivisi che rendono i test dipendenti dall'ordine di esecuzione.

## Porte e Ambiente Locale

I test automatici non devono occupare porte fisse.

Regole:

* non usare la porta `8080` nei test;
* preferire test senza web server quando si testano service, repository o contesto applicativo;
* usare `server.port=0` per eventuali test che avviano un server reale;
* lasciare la porta `8080` libera per l'avvio manuale da IDE;
* eventuali verifiche manuali o automatiche fatte fuori dai test devono usare una porta diversa, ad esempio `18080`, se la `8080` serve allo sviluppatore.

Nel codice:

* i test di service e context usano `SpringBootTest.WebEnvironment.NONE`;
* la configurazione test imposta `server.port=0`.

---

# 7. Frontend Thymeleaf

Le pagine devono essere semplici e consistenti.

Regole:

* layout comune;
* messaggi di errore visibili;
* azioni disponibili solo quando permesse;
* controlli server-side sempre presenti;
* Bootstrap usato in modo sobrio.

La UI non deve essere l'unico livello di sicurezza.

---

# 8. Documentazione

La documentazione deve aiutare chi sviluppa, usa o deploya il progetto.

Ogni documento deve rispondere a una domanda concreta:

* cosa fa il sistema;
* come e fatto;
* perche e stata scelta una soluzione;
* come si avvia;
* come si testa;
* come si deploya.

Documenti principali:

* `README.md`: ingresso pratico al progetto;
* `docs/functional-specs.md`: specifiche funzionali;
* `docs/domain-rules.md`: regole operative e invarianti;
* `docs/architecture.md`: scelte tecniche;
* `docs/roadmap.md`: piano tecnico;
* `docs/poc.md`: scope e criteri della POC;
* `docs/project-status.md`: stato di avanzamento;
* `docs/development-guidelines.md`: regole di sviluppo, test e documentazione.

Regole:

* scrivere in italiano;
* usare frasi brevi;
* preferire liste quando aiutano la scansione;
* evitare dettagli implementativi instabili nei documenti funzionali;
* aggiornare la documentazione quando cambia un comportamento;
* non duplicare intere sezioni tra documenti diversi.

---

# 9. Qualita Prima di Chiudere una Feature

Prima di considerare completa una modifica:

* il codice compila;
* i test rilevanti passano;
* le regole di dominio sono coperte se modificate;
* la documentazione e aggiornata se cambia un comportamento;
* non sono presenti credenziali o segreti;
* la porta `8080` resta libera per l'avvio manuale da IDE durante i test automatici.

Comando standard:

```bash
./mvnw test
```

Su Windows:

```powershell
.\mvnw.cmd test
```
