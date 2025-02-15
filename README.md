# WebMarketSWA

## Dipendenze software
Di seguito la lista di dipendenze lato server:
- Java JDK 20.0.2
- Tomcat 10.1.12
- Maven 4.0
- Jakarta EE 10
- Jersey (JAXRS) 3.1.8
- Jackson JSON Provider (for Jakarta EE) 2.18.2
- Lombok 1.18.34
- Swagger Core (Jakarta version) 2.2.28

Di seguito, invece, la lista di dipendenze lato client:
- JQuery 3.7.1
- Bootstrap 5.3.3
- DataTables 2.2.1

## Funzionalità realizzate

#### 1. Login/logout con username e password.

Funzionalità realizzata sia lato server (tramite apposito servizio REST) che lato client.
Una volta effettuato il login verrà generato un token JWT che potrà essere successivamente utilizzato per i servizi REST che richiedono
l'autenticazione.

#### 2. Inserimento di una *richiesta di acquisto* (comprensiva di categoria di prodotto, di tutte le caratteristiche richieste per quel tipo di prodotto e delle eventuali note)
#### 3. Associazione di una richiesta di acquisto a un tecnico incaricato  
#### 4. Inserimento e modifica (da parte del tecnico incaricato) di una *proposta di acquisto* associata a una richiesta 
#### 5. Approvazione (da parte dell'ordinante) di una *proposta di acquisto*
#### 6. Eliminazione di una *richiesta di acquisto* dal sistema
#### 7. Estrazione lista delle richieste di acquisto *in corso* (non chiuse) di un determinato ordinante
#### 8. Estrazione lista delle richieste di acquisto non ancora assegnate ad alcun tecnico
#### 9. Estrazione di tutti i dettagli di una richiesta di acquisto (richiesta iniziale, eventuale prodotto candidato, approvazione/rifiuto dell'ordinante con relativa motivazione)
#### 10. Estrazione lista richieste di acquisto gestite da un determinato tecnico
