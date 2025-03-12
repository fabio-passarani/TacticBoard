# TacticBoard

## Descrizione del Progetto

TacticBoard è un'applicazione web completa per allenatori di calcio che permette di creare, gestire e condividere schemi tattici, esercitazioni e piani di allenamento. L'applicazione è progettata per allenatori dilettantistici e semi-professionisti che necessitano di uno strumento accessibile ma potente per professionalizzare la loro attività.

Il sistema integra un editor tattico interattivo, una libreria di esercitazioni, un pianificatore di allenamenti, funzionalità di analisi video e gestione squadre, tutto in un'unica piattaforma unificata.

### Tecnologie

- **Backend**: Spring Boot 3.2.0, Java 17, PostgreSQL
- **Frontend**: Angular 17, Angular Material
- **Infrastruttura**: Maven (multi-modulo), Docker, AWS/Digital Ocean

## Funzionalità da Implementare - Checklist

### Core e Autenticazione

#### Backend

- [ ] Modello dati utenti e ruoli (User, Role)
- [ ] Repository e servizi autenticazione
- [ ] JWT authentication e security configuration
- [ ] API registrazione e login
- [ ] API gestione profilo utente
- [ ] Validazione e gestione eccezioni

#### Frontend

- [ ] Modulo Auth (login, registrazione, recupero password)
- [ ] Servizi AuthService e TokenInterceptor
- [ ] Guards per route protette
- [ ] Componente profilo utente
- [ ] Storage token e gestione sessione
- [ ] UI notifiche e messaggi di errore

### Gestione Squadre e Giocatori

#### Backend

- [ ] Modello dati squadre e giocatori (Team, Player)
- [ ] Repository e servizi relativi
- [ ] API CRUD per squadre
- [ ] API CRUD per giocatori
- [ ] API per statistiche squadra
- [ ] Filtri e ordinamento giocatori

#### Frontend

- [ ] Modulo Teams con componenti correlati
- [ ] Form creazione/modifica squadra
- [ ] Form creazione/modifica giocatori
- [ ] Visualizzazione roster con filtri e ordinamento
- [ ] Dashboard statistiche squadra
- [ ] UI assegnazione ruoli e posizioni

### Editor Tattico

#### Backend

- [ ] Modello dati tattiche (Tactic, Animation)
- [ ] Repository e servizi per schemi tattici
- [ ] API CRUD schemi tattici
- [ ] API per animazioni e movimenti
- [ ] Servizio esportazione/clonazione schemi
- [ ] Tagging e categorizzazione schemi

#### Frontend

- [ ] Modulo Editor Tattico
- [ ] Canvas interattivo con Fabric.js
- [ ] Controlli posizionamento giocatori
- [ ] Timeline animazioni tattiche
- [ ] Toolbar con strumenti disegno
- [ ] Sistema salvaggio/caricamento schemi
- [ ] Esportazione immagini/animazioni

### Libreria Esercitazioni

#### Backend

- [ ] Modello dati esercizi (Exercise, Category)
- [ ] Repository e servizi per esercitazioni
- [ ] API CRUD esercitazioni
- [ ] API ricerca e filtri avanzati
- [ ] Sistema categorizzazione
- [ ] Storage immagini esercizi

#### Frontend

- [ ] Modulo Library con componenti
- [ ] UI browser esercizi con filtri
- [ ] Form creazione/modifica esercizi
- [ ] Visualizzazione dettaglio esercizio
- [ ] Componente uploadlimmagini/diagrammi
- [ ] UI rating e feedback esercizi
- [ ] Sistema ricerca avanzata

### Pianificatore Allenamenti

#### Backend

- [ ] Modello dati allenamenti (TrainingPlan, TrainingSession)
- [ ] Repository e servizi relativi
- [ ] API CRUD piani allenamento
- [ ] API sessioni e timeline
- [ ] Servizio generazione PDF
- [ ] API presenze giocatori (Attendance)

#### Frontend

- [ ] Modulo Planner con componenti
- [ ] UI calendario allenamenti
- [ ] Drag & drop esercizi nel piano
- [ ] Timeline sessione con durate
- [ ] Generazione e download PDF
- [ ] Dashboard allenamenti settimanali/mensili
- [ ] Gestione presenze e carichi di lavoro

### Analisi Video

#### Backend

- [ ] Modello dati video (Video, Annotation)
- [ ] Repository e servizi video
- [ ] API upload/streaming video
- [ ] API annotazioni e commenti
- [ ] Storage video ottimizzato
- [ ] Gestione clipng e tagging

#### Frontend

- [ ] Modulo Video Analysis
- [ ] Player video con controlli custom
- [ ] Tool disegno e annotazione su video
- [ ] Timeline annotazioni sincronizzata
- [ ] Uploader video ottimizzato
- [ ] Condivisione clip con il team
- [ ] Integrazione con schemi tattici

### Abbonamenti e Pagamenti

#### Backend

- [ ] Modello dati abbonamenti (Subscription, Payment)
- [ ] Integrazione Stripe
- [ ] API gestione abbonamenti
- [ ] Webhook pagamenti
- [ ] Email transazionali (conferme, rinnovi)
- [ ] Gestione livelli di accesso basati su subscription

#### Frontend

- [ ] Modulo Subscription
- [ ] Pagina piani e confronto
- [ ] Integrazione Stripe Elements
- [ ] Dashboard abbonamento utente
- [ ] Storico pagamenti e fatture
- [ ] UI upgrade/downgrade piano

### Dashboard e Analytics

#### Backend

- [ ] API statistiche e analytics
- [ ] Aggregazione dati performance
- [ ] API reporting personalizzati
- [ ] Ottimizzazione e caching query complesse
- [ ] Export dati in formati standard

#### Frontend

- [ ] Modulo Dashboard principale
- [ ] Widget personalizzabili
- [ ] Grafici statistiche allenamenti/esercizi
- [ ] Metriche progresso squadra
- [ ] Reports esportabili (PDF, Excel)
- [ ] UI personalizzazione dashboard

### Testing e Qualità

#### Backend

- [ ] Unit testing (JUnit 5)
- [ ] Integration testing
- [ ] API testing
- [ ] Security testing
- [ ] Performance testing

#### Frontend

- [ ] Unit testing (Jasmine/Jest)
- [ ] Component testing
- [ ] E2E testing (Cypress)
- [ ] Accessibility testing
- [ ] Cross-browser testing

### Deployment e DevOps

#### Backend

- [ ] Configurazioni production-ready
- [ ] Containerizzazione Docker
- [ ] CI/CD pipeline
- [ ] Monitoring e logging
- [ ] Backup e disaster recovery

#### Frontend

- [ ] Build ottimizzato produzione
- [ ] Setup CDN per asset statici
- [ ] Configurazione CI/CD frontend
- [ ] Error tracking (Sentry)
- [ ] Analytics implementati

## Configurazione del Progetto

### Prerequisiti

- Java 17
- Maven 3.9+
- Node.js 18+
- Angular CLI 17
- PostgreSQL 15+
- Docker (opzionale per sviluppo)

### Setup Sviluppo Backend

# Clona il repository

git clone https://github.com/tuorepository/tacticboard.git

# Naviga nella directory del progetto

cd tacticboard

# Compila tutti i moduli

mvn clean install

# Avvia l'applicazione in modalità sviluppo

cd tacticboard-webapp
mvn spring-boot:run -Dspring-boot.run.profiles=dev

### Setup Sviluppo Frontend

# Naviga nella directory frontend

cd tacticboard-frontend

# Installa le dipendenze

npm install

# Avvia il server di sviluppo

ng serve

## Licenza

Proprietaria - Tutti i diritti riservati
