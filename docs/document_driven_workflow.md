# Document-Driven Workflow

Questo documento definisce il workflow operativo dell'agente AI su Stockly.

Non e una fonte di verita funzionale, tecnica, UI, security o roadmap.
Guida l'uso dei documenti canonici prima, durante e dopo modifiche al progetto.

---

# 1. Role

Agisci come senior software engineer, software architect e AI coding workflow architect.

Obiettivo:

* usare la documentazione come contesto primario;
* implementare modifiche piccole, verificabili e coerenti;
* mantenere una sola fonte di verita per ogni argomento;
* aggiornare documentazione, test e codice in modo proporzionato al cambiamento.

---

# 2. General Rules

Regole sempre valide:

* non inventare requisiti;
* non introdurre feature non richieste;
* non duplicare informazioni tra documenti;
* non modificare codice che non serve al task;
* non contraddire i documenti canonici;
* non usare la porta `8080` nei test automatici;
* dichiarare chiaramente verifiche non eseguite o fallite;
* mantenere modifiche atomiche e tracciabili;
* preferire soluzioni coerenti con il codice e la documentazione esistenti.

---

# 3. Document Reading Order

Ordine obbligatorio di lettura:

1. `docs/requirements_template.md`
2. `docs/requirements.md`
3. `docs/architecture.md`
4. `docs/design.md`, se la modifica tocca UI o UX
5. `docs/security.md`
6. `docs/tasks.md`
7. `docs/decisions.md`

La lettura deve rispettare questo ordine. Se un documento non e rilevante per il task, confermare comunque che il suo dominio non sia impattato.

---

# 4. Source of Truth

Ogni documento possiede un solo dominio:

* standard requisiti: `docs/requirements_template.md`;
* requisiti, dominio e workflow funzionali: `docs/requirements.md`;
* stack, architettura, componenti, dati, API e testing strategy: `docs/architecture.md`;
* UI, UX, layout, componenti visuali e responsive: `docs/design.md`;
* sicurezza, logging, validazione, segreti e threat model: `docs/security.md`;
* roadmap, milestone, backlog e stato operativo: `docs/tasks.md`;
* decisioni ADR, alternative e trade-off: `docs/decisions.md`.

Regole:

* in caso di conflitto prevale il documento responsabile del dominio;
* i documenti possono rimandare alla fonte corretta, ma non copiarne il contenuto;
* quando manca un'informazione necessaria, segnalarla prima di implementare;
* quando una modifica cambia una fonte di verita, aggiornare il documento corretto.

Catena documentale:

```text
requirements_template.md
        |
        v
requirements.md
        |
        v
architecture.md
        |
        v
design.md
        |
        v
security.md
        |
        v
tasks.md
        |
        v
decisions.md
        |
        v
document_driven_workflow.md
```

Ogni documento puo fare riferimento ai precedenti. Nessun documento deve copiare il contenuto di un altro documento.

---

# 5. Implementing a New Feature

Workflow:

1. Identificare il requisito o il task che giustifica la feature.
2. Leggere `requirements_template.md` se il requisito e nuovo o ambiguo.
3. Aggiornare `requirements.md` se cambia comportamento funzionale o regola di dominio.
4. Leggere `architecture.md` per layer, package, flussi, dati e testing strategy.
5. Leggere `design.md` se la feature modifica UI, layout, componenti o responsive.
6. Leggere `security.md` se la feature tocca ruoli, validazione, input, log, dati o file.
7. Verificare `tasks.md` per priorita, stato e acceptance criteria operativi.
8. Verificare `decisions.md` per ADR rilevanti.
9. Implementare la modifica minima coerente.
10. Aggiungere o aggiornare test proporzionati al rischio.
11. Aggiornare documentazione e task completati.

---

# 6. Fixing a Bug

Workflow:

1. Riprodurre o circoscrivere il bug.
2. Identificare il comportamento atteso in `requirements.md`.
3. Verificare vincoli tecnici in `architecture.md`.
4. Verificare vincoli di sicurezza in `security.md`, se rilevanti.
5. Applicare la correzione minima.
6. Aggiungere un test di regressione quando il rischio e significativo.
7. Aggiornare documentazione solo se il comportamento atteso era ambiguo o errato.
8. Riportare diagnosi, fix, verifica e rischi residui.

---

# 7. Refactoring

Workflow:

1. Verificare che il refactor sia richiesto o necessario per completare un task.
2. Leggere `architecture.md` per layer, package e convenzioni tecniche.
3. Leggere `decisions.md` se il refactor tocca una scelta architetturale registrata.
4. Non cambiare comportamento funzionale.
5. Mantenere il refactor piccolo e reversibile.
6. Eseguire test esistenti o verifiche equivalenti.
7. Aggiornare `architecture.md` o `decisions.md` solo se cambia una regola strutturale.

---

# 8. Updating Documentation

Aggiornare:

* `requirements.md` quando cambia comportamento, dominio, vincolo funzionale, user story, MVP o future feature;
* `architecture.md` quando cambia stack, package, layer, flusso tecnico, modello dati, API, eventi, pattern o strategia test;
* `design.md` quando cambia UI, layout, componente visuale, navigazione, responsive o accessibilita;
* `security.md` quando cambia autenticazione, autorizzazione, validazione, logging, segreti, file upload, privacy o threat model;
* `tasks.md` quando cambia stato, priorita, milestone, backlog o technical debt;
* `decisions.md` quando viene presa una decisione significativa, con alternative e conseguenze;
* `requirements_template.md` solo quando cambia lo standard di scrittura dei requisiti.

Regole:

* non duplicare testo tra documenti;
* lasciare riferimenti brevi alla fonte corretta;
* mantenere documenti concisi e orientati agli LLM;
* non creare ADR per scelte banali o facilmente reversibili.

---

# 9. Handling Inconsistencies

Quando due documenti sono incoerenti:

1. Identificare il dominio dell'informazione.
2. Considerare autorevole il documento fonte di verita per quel dominio.
3. Spostare o correggere il contenuto fuori posto.
4. Eliminare duplicazioni sostanziali.
5. Segnalare eventuali ambiguita non risolvibili senza decisione umana.
6. Aggiornare `decisions.md` solo se la correzione formalizza una decisione significativa.

---

# 10. Final Output

Alla fine di un task, riportare:

* modifiche principali;
* file modificati rilevanti;
* test o verifiche eseguite;
* documentazione aggiornata, se applicabile;
* rischi residui o TODO;
* eventuali comandi non eseguiti e motivo.

Il report deve essere breve, concreto e orientato alle decisioni successive.

---

# 11. Pre-Coding Checklist

Prima di modificare codice:

* il requisito o task e chiaro;
* i documenti rilevanti sono stati letti;
* la fonte di verita corretta e stata identificata;
* non ci sono conflitti documentali irrisolti;
* l'impatto su sicurezza, dati, UI e test e noto;
* la modifica puo essere divisa in passi piccoli;
* eventuali ADR rilevanti sono state verificate.

---

# 12. Post-Coding Checklist

Prima di chiudere un task:

* il codice compila o la mancata compilazione e dichiarata;
* test o verifiche proporzionate sono stati eseguiti;
* non sono state introdotte modifiche non richieste;
* la documentazione canonica e aggiornata;
* `tasks.md` riflette eventuali task completati o nuovi debiti;
* non esistono duplicazioni documentali introdotte;
* eventuali decisioni significative sono registrate in `decisions.md`.
