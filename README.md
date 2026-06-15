# Stockly

Stockly e un'applicazione web monolitica per la gestione di magazzini, giacenze e ordini interni.

## POC

La proof of concept usa H2 in-memory, Flyway e dati demo ricreati a ogni avvio. Serve a dimostrare il flusso minimo:

```text
stock disponibile -> creazione ordine -> decremento stock -> approvazione o cancellazione
```

Pagine disponibili:

* `http://localhost:8080/stock`
* `http://localhost:8080/orders/new`
* `http://localhost:8080/orders`

Avvio locale su Windows:

```powershell
$env:MAVEN_USER_HOME="$PWD\.m2"
.\mvnw.cmd "-Dmaven.repo.local=.m2/repository" spring-boot:run -Dspring-boot.run.profiles=poc
```

Avvio locale su macOS/Linux:

```bash
export MAVEN_USER_HOME="$PWD/.m2"
./mvnw -Dmaven.repo.local=.m2/repository spring-boot:run -Dspring-boot.run.profiles=poc
```

Test:

```powershell
$env:MAVEN_USER_HOME="$PWD\.m2"
.\mvnw.cmd "-Dmaven.repo.local=.m2/repository" test
```

Console H2:

```text
http://localhost:8080/h2-console
```

Parametri H2:

```text
JDBC URL: jdbc:h2:mem:stockly
User: sa
Password:
```

## Flusso Demo

1. Aprire `/stock` e verificare le giacenze demo.
2. Aprire `/orders/new` e creare un ordine scegliendo magazzino, articolo e quantita.
3. Tornare su `/stock` e verificare il decremento della giacenza.
4. Aprire `/orders` e approvare oppure cancellare l'ordine.
5. In caso di cancellazione, tornare su `/stock` e verificare il reintegro della giacenza.

Gli ordini demo includono gia stati `REQUIRED`, `APPROVED` e `CANCELED`.

## Documentazione

* `docs/poc.md`
* `docs/roadmap.md`
* `docs/project-status.md`
* `docs/functional-specs.md`
* `docs/domain-rules.md`
* `docs/architecture.md`
* `docs/development-guidelines.md`
