# Project Status

Documento di avanzamento del progetto Stockly.

Ultimo aggiornamento: 2026-06-11.

---

# 1. Stato Attuale

La POC e stata completata fino al punto `0.6`.

L'applicazione dimostra il flusso minimo:

```text
visualizzazione stock -> creazione ordine -> decremento stock -> approvazione o cancellazione -> stock coerente
```

Database e runtime POC:

* H2 in-memory;
* schema creato da Flyway;
* dati demo creati da `DemoDataSeeder`;
* profilo Spring `poc`.

---

# 2. Task Completati Oggi

Documentazione:

* pulizia e riorganizzazione delle specifiche iniziali;
* creazione roadmap tecnica;
* creazione documento POC;
* creazione linee guida di architettura;
* creazione linee guida di coding;
* creazione strategia test;
* creazione regole di dominio;
* creazione linee guida di documentazione;
* aggiunta preferenza per struttura futura a layer tecnici;
* annotazione su possibile uso prolungato di H2 prima di PostgreSQL;
* annotazione regole future per Lombok.

Implementazione POC:

* `0.1` setup tecnico Maven, profilo `poc`, H2, Flyway, Thymeleaf e Validation;
* `0.2` schema iniziale, entity JPA, enum e repository;
* `0.3` seed automatico con 2 magazzini, articoli, giacenze e ordini demo;
* `0.4` service ordini con prenotazione stock, approvazione, cancellazione e reintegro;
* `0.5` UI minimale per stock, creazione ordine e lista ordini;
* `0.6` test minimi e README operativo della POC.

---

# 3. Funzionalita Disponibili

Pagine:

* `/stock`: mostra le giacenze demo;
* `/orders/new`: crea un ordine con magazzino specifico;
* `/orders`: mostra ordini e azioni disponibili.

Regole implementate:

* un ordine nasce in stato `REQUIRED`;
* la creazione ordine decrementa lo stock;
* se lo stock non basta, l'ordine non viene creato;
* approvare un ordine non modifica lo stock;
* cancellare un ordine reintegra lo stock;
* ordini finali non possono essere modificati.

---

# 4. Verifiche

Verifiche eseguite il 2026-06-11:

* `mvn test`: passante, 7 test eseguiti;
* `/stock`: risposta HTTP 200;
* `/orders`: risposta HTTP 200;
* `/orders/new`: risposta HTTP 200;
* `POST /orders`: redirect HTTP 302 dopo creazione ordine;
* `POST /orders/{id}/approve`: redirect HTTP 302 dopo approvazione ordine.

Esito: POC chiusa e funzionante per lo scope definito.

---

# 5. Prossimi Task

Prima di iniziare la fase `1`:

* discutere e decidere l'alberatura definitiva del progetto;
* valutare struttura per layer tecnici, preferita attualmente;
* decidere se mantenere H2 anche nelle prime fasi post-POC;
* definire quando introdurre PostgreSQL;
* valutare se introdurre Lombok secondo le regole documentate.

Fase successiva prevista:

* `1. Fondazione Progetto Completo`, con profili reali, struttura package consolidata, prime configurazioni per evoluzione non-POC.

---

# 6. Decisioni Aperte

* Struttura package finale: layer tecnici o dominio con sottopackage.
* Momento di introduzione PostgreSQL.
* Uso di Lombok dopo la POC.
* Libreria PDF: OpenPDF o Apache PDFBox.
* Modalita di creazione admin iniziale.
