# Security

Questo documento definisce le regole di sicurezza del progetto.

Input:

* `docs/requirements.md`;
* `docs/architecture.md`;
* `docs/design.md`, quando una scelta UI impatta sicurezza o validazione.

---

# 1. Gestione Segreti

Regole:

* non committare password, token, API key o stringhe di connessione sensibili;
* usare variabili ambiente per configurazione sensibile;
* non salvare credenziali in `application.properties`;
* non loggare segreti;
* non includere `.env` nel repository;
* documentare ogni nuova variabile ambiente richiesta.

Variabili future previste:

* `DATABASE_URL`;
* `DATABASE_USERNAME`;
* `DATABASE_PASSWORD`;
* `SPRING_PROFILES_ACTIVE`;
* eventuali credenziali admin iniziali.

---

# 2. Logging

Usare SLF4J tramite il logger standard del progetto.

Regole:

* preferire messaggi brevi, concreti e orientati all'evento;
* usare placeholder parametrizzati;
* non usare concatenazione di stringhe per messaggi parametrizzati;
* non usare `System.out.println`;
* non usare `printStackTrace`;
* loggare lo stack trace una sola volta;
* non usare il log come sostituto di test o validazioni.

Livelli:

* `TRACE`: dettagli molto fini, normalmente disabilitati;
* `DEBUG`: diagnosi locale;
* `INFO`: eventi applicativi attesi;
* `WARN`: situazioni anomale ma gestite;
* `ERROR`: errori inattesi o fallimenti che richiedono attenzione.

Loggare a `INFO`:

* creazione ordine;
* cambio stato ordine;
* cancellazione o rifiuto ordine;
* bootstrap o seed demo in ambiente POC.

Loggare a `WARN`:

* tentativi di operazioni non consentite;
* stock insufficiente su richiesta formalmente valida;
* problemi di concorrenza gestiti;
* dati demo mancanti o incoerenti in ambiente locale.

Non loggare mai:

* password;
* token;
* cookie;
* session id;
* stringhe di connessione complete;
* dati personali non necessari;
* payload completi di form o request.

---

# 3. Input Validation

Regole:

* validare i form con Bean Validation;
* validare le regole di dominio nei service;
* non fidarsi della UI per applicare vincoli business;
* controllare quantita positive;
* controllare campi obbligatori;
* controllare transizioni ordine;
* controllare ownership e ruoli lato server.

Esempi:

* il magazzino e obbligatorio per ogni riga ordine;
* la quantita richiesta non puo superare la disponibilita;
* un ordine finale non puo cambiare stato;
* USER puo cancellare solo propri ordini `REQUIRED`.

---

# 4. API Security

La POC non espone API pubbliche e non ha Spring Security attivo.

Per il prodotto completo:

* usare Spring Security con form login;
* salvare password con BCrypt;
* proteggere rotte web per ruolo;
* applicare controlli nei service oltre che nella UI;
* applicare ownership sugli ordini USER;
* proteggere download PDF con le stesse regole del dettaglio ordine;
* evitare endpoint amministrativi non protetti;
* disabilitare H2 console fuori dal profilo POC.

---

# 5. Threat Model

Minacce da considerare nelle fasi successive:

* accesso non autorizzato a ordini altrui;
* escalation di privilegi tra USER, STORE_MANAGER e ADMIN;
* manipolazione lato client di quantita o magazzino;
* download PDF non autorizzato;
* esposizione di stack trace;
* segreti committati o loggati;
* H2 console esposta fuori dalla POC.

Mitigazioni:

* autorizzazioni lato server;
* controlli ownership nei service;
* Bean Validation piu validazioni di dominio;
* logging senza dati sensibili;
* profili separati;
* disabilitazione strumenti POC fuori dagli ambienti demo.

---

# 6. Pratiche Vietate

Vietato:

* committare segreti;
* usare `ddl-auto=update` in ambienti reali;
* esporre stack trace all'utente;
* affidarsi solo ai pulsanti nascosti nella UI;
* loggare password, token, cookie o session id;
* usare entity JPA come form web;
* introdurre setter globali sulle entity senza necessita;
* usare `@Data` sulle entity JPA;
* lasciare console H2 abilitata in produzione;
* usare la porta `8080` nei test automatici.
