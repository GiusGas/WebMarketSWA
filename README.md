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
Una volta effettuato il login verrà generato un token JWT che potrà essere successivamente utilizzato per i servizi REST che richiedono l'autenticazione.

#### 2. Inserimento di una *richiesta di acquisto* (comprensiva di categoria di prodotto, di tutte le caratteristiche richieste per quel tipo di prodotto e delle eventuali note)

Funzionalità accessibile tramite chiamata REST che richiede l'invio della richiesta in formato JSON.

#### 3. Associazione di una richiesta di acquisto a un tecnico incaricato  

Funzionalità accessibile tramite chiamata REST che richiede l'invio di due parametri nell'URL dell'API:
- l'id della richiesta di acquisto
- l'id del tecnico a cui viene assegnato l'incarico

#### 4. Inserimento e modifica (da parte del tecnico incaricato) di una *proposta di acquisto* associata a una richiesta 

Funzionalità accessibile tramite chiamata REST che richiede l'invio della proposta di acquisto in formato JSON e l'id della richiesta alla quale è associata come parametro nell'URL dell'API.

#### 5. Approvazione (da parte dell'ordinante) di una *proposta di acquisto*

Funzionalità accessibile tramite chiamata REST che richiede l'invio delll'id della richiesta di acquisto associata alla proposta nell'URL dell'API.
Questa funzionalità è stata implementata anche lato client.

#### 6. Eliminazione di una *richiesta di acquisto* dal sistema

Funzionalità realizzata sia lato server (tramite apposito servizio REST) che lato client.
Per eliminare con successo una richiesta tramite servizio REST basta inserire l'id della richiesta come parametro nell'URL dell'API.

#### 7. Estrazione lista delle richieste di acquisto *in corso* (non chiuse) di un determinato ordinante

Funzionalità realizzata sia lato server (tramite apposito servizio REST) che lato client.
Tale servizio richiede che l'utente sia autenticato come "ordinante" e potrà vedere solo la lista delle proprie richieste.

#### 8. Estrazione lista delle richieste di acquisto non ancora assegnate ad alcun tecnico

Funzionalità accessibile tramite chiamata REST e che non richiede alcuna risorsa o parametro.

#### 9. Estrazione di tutti i dettagli di una richiesta di acquisto (richiesta iniziale, eventuale prodotto candidato, approvazione/rifiuto dell'ordinante con relativa motivazione)

Funzionalità realizzata sia lato server (tramite apposito servizio REST) che lato client.
Per quanto riguarda il servizio REST, richiede solo l'id della richiesta di acquisto come parametro nell'URL dell'API.

#### 10. Estrazione lista richieste di acquisto gestite da un determinato tecnico

Funzionalità accessibile tramite chiamata REST, non richiede alcuna risorsa o parametro in quanto è necessario che l'utente sia autenticato e che sia un tecnico.
