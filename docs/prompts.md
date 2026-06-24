# Prompts

Questo documento orchestra l'uso dell'AI coding workflow su Stockly.

Non e una fonte di verita funzionale, tecnica o security. Rimanda ai documenti canonici.

---

# 1. Document Precedence

Quando i documenti sono in conflitto, applicare questa precedenza:

1. `docs/requirements.md` per comportamento funzionale e regole dominio.
2. `docs/requirements_template.md` per struttura e qualita dei requisiti.
3. `docs/security.md` per sicurezza, logging, validation e pratiche vietate.
4. `docs/architecture.md` per struttura tecnica, componenti, modello dati e test strategy.
5. `docs/design.md` per UI/UX e dettagli di design applicativo.
6. `docs/tasks.md` per priorita operative, milestone e technical debt.
7. `docs/decisions.md` per decisioni ADR e contesto storico.

Regole:

* non implementare codice che contraddice i documenti canonici;
* se cambia un comportamento, aggiornare il documento canonico corrispondente;
* non duplicare regole tra documenti.

---

# 2. Development Rules

Prima di implementare:

* leggere `docs/requirements.md`;
* leggere `docs/requirements_template.md` se si creano o modificano requisiti;
* leggere `docs/architecture.md`;
* leggere `docs/security.md`;
* leggere `docs/tasks.md` se la modifica riguarda priorita o milestone;
* leggere `docs/design.md` se la modifica tocca UI.

Regole operative:

* mantenere modifiche piccole e verificabili;
* non introdurre refactor non richiesti;
* rispettare layer e responsabilita;
* aggiornare documentazione canonica insieme al codice;
* non usare la porta `8080` nei test automatici;
* non introdurre dipendenze senza motivazione.

---

# 3. Testing Rules

Applicare la strategia definita in `docs/architecture.md`.

Regole operative:

* aggiungere test quando si modifica stock, ordini, transizioni, audit o permessi;
* usare dati espliciti e leggibili;
* evitare dipendenze dall'ordine di esecuzione;
* rilanciare test rilevanti prima di chiudere una modifica;
* se la toolchain locale fallisce, dichiarare chiaramente la verifica non eseguita.

---

# 4. Documentation Rules

Applicare questa mappa:

* requisiti e dominio: `docs/requirements.md`;
* standard requisiti: `docs/requirements_template.md`;
* architettura e testing strategy: `docs/architecture.md`;
* sicurezza e logging: `docs/security.md`;
* UI/UX: `docs/design.md`;
* task e milestone: `docs/tasks.md`;
* decisioni ADR e contesto storico: `docs/decisions.md`.

Regole operative:

* scrivere in italiano;
* usare frasi brevi;
* evitare duplicazioni;
* mantenere una sola fonte di verita per argomento;
* aggiornare o creare ADR quando viene presa una decisione significativa.

---

# 5. Bugfix Prompt

```text
Agisci come senior engineer sul progetto Stockly.

Obiettivo: correggere il bug descritto sotto senza introdurre refactor non richiesti.

Prima di modificare:
- leggi docs/requirements.md;
- leggi docs/requirements_template.md se il bug richiede chiarire o correggere requisiti;
- leggi docs/architecture.md;
- leggi docs/security.md;
- leggi docs/tasks.md se il bug riguarda priorita o stato lavoro.

Bug:
[descrizione bug]

Regole:
- modifica minima;
- nessun codice non correlato;
- documentazione aggiornata se cambia comportamento;
- test o verifica proporzionati al rischio.

Output:
- diagnosi sintetica;
- modifica fatta;
- test o verifica eseguita;
- rischi residui.
```

---

# 6. Review Prompt

```text
Agisci come senior software architect e reviewer.

Esegui una review focalizzata su:
- bug funzionali;
- regressioni dominio;
- requisiti ambigui rispetto a docs/requirements_template.md;
- violazioni di docs/requirements.md;
- violazioni di docs/security.md;
- violazioni di docs/architecture.md;
- problemi di transazioni, stock, audit o permessi;
- test mancanti o fragili;
- duplicazioni documentali.

Non proporre refactor estetici se non riducono rischio reale.

Ordina i finding per severita.
Per ogni finding indica:
- file;
- area;
- problema;
- impatto;
- fix consigliato.
```

---

# 7. Feature Prompt

```text
Agisci come senior engineer sul progetto Stockly.

Feature:
[descrizione feature]

Prima di implementare:
- leggi docs/requirements.md;
- leggi docs/requirements_template.md se crei o modifichi requisiti;
- leggi docs/architecture.md;
- leggi docs/security.md;
- leggi docs/design.md se tocchi UI;
- leggi docs/tasks.md.

Regole:
- mantieni modifiche piccole;
- aggiorna documentazione canonica se cambia comportamento;
- rispetta layer e package;
- aggiungi test proporzionati;
- non usare la porta 8080 nei test automatici.

Output:
- modifiche fatte;
- test eseguiti;
- note operative.
```
