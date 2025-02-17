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

Segue la lista di tutte le funzionalità implementate, con una breve descrizione e i requisiti per ogni servizio REST associato.
La base URL alla quale fare riferimento per accedere ai servizi nel caso in cui l'applicazione web venga deployata su un server Tomcat in locale sulla porta 8080 è la seguente:
`http://localhost:8080/WebMarket`

#### 1. Login/logout con username e password.

API URL: 
- `POST /rest/auth/login`
- `DELETE /rest/auth/logout`

Funzionalità realizzata sia lato server (tramite apposito servizio REST) che lato client.
Una volta effettuato il login verrà generato un token JWT che potrà essere successivamente utilizzato per i servizi REST che richiedono l'autenticazione.

#### 2. Inserimento di una *richiesta di acquisto* (comprensiva di categoria di prodotto, di tutte le caratteristiche richieste per quel tipo di prodotto e delle eventuali note)

API URL: `POST /rest/request/add`

Funzionalità accessibile tramite chiamata REST che richiede l'invio della richiesta in formato JSON.
Inoltre, il servizio richiede che l'utente sia autenticato come "ordinante".

#### 3. Associazione di una richiesta di acquisto a un tecnico incaricato  

API URL: `PUT /rest/requests/{id}/technician`

Funzionalità accessibile tramite chiamata REST che richiede l'invio di due parametri nell'URL dell'API:
- l'id della richiesta di acquisto
- l'id del tecnico a cui viene assegnato l'incarico
Tale servizio richiede che l'utente sia autenticato come "tecnico".

#### 4. Inserimento e modifica (da parte del tecnico incaricato) di una *proposta di acquisto* associata a una richiesta 

API URL: 
- `PUT /rest/requests/{id}/proposal`

Funzionalità accessibile tramite chiamata REST che richiede l'invio della proposta di acquisto in formato JSON e l'id della richiesta alla quale è associata come parametro nell'URL dell'API.
Il servizio richiede, quindi, che l'utente sia autenticato come "tecnico" e che la richiesta di acquisto associata alla proposta sia stata assegnata al medesimo utente.

#### 5. Approvazione (da parte dell'ordinante) di una *proposta di acquisto*

API URL: `PUT /rest/requests/{id}/proposal/approve`

Funzionalità accessibile tramite chiamata REST che richiede l'invio delll'id della richiesta di acquisto associata alla proposta nell'URL dell'API.
Questa funzionalità è stata implementata anche lato client.
Il servizio richiede che l'utente sia autenticato come "ordinante" e che la proposta di acquisto sia associata ad una richiesta di proprietà del medesimo utente.

#### 6. Eliminazione di una *richiesta di acquisto* dal sistema

API URL: `DELETE /rest/requests/{id}`

Funzionalità realizzata sia lato server (tramite apposito servizio REST) che lato client.
Per eliminare con successo una richiesta tramite servizio REST basta inserire l'id della richiesta come parametro nell'URL dell'API.
Il servizio richiede che l'utente sia autenticato come "ordinante" e che la richiesta di acquisto sia di proprietà del medesimo utente.

#### 7. Estrazione lista delle richieste di acquisto *in corso* (non chiuse) di un determinato ordinante

API URL: `GET /rest/requests/inProgress`

Funzionalità realizzata sia lato server (tramite apposito servizio REST) che lato client.
Tale servizio richiede che l'utente sia autenticato come "ordinante" e potrà vedere solo la lista delle proprie richieste *in corso*.

#### 8. Estrazione lista delle richieste di acquisto non ancora assegnate ad alcun tecnico

API URL: `GET /rest/requests/unassigned`

Funzionalità accessibile tramite chiamata REST e che non richiede alcuna risorsa o parametro.
Tale servizio richiede però che l'utente sia autenticato come "tecnico".

#### 9. Estrazione di tutti i dettagli di una richiesta di acquisto (richiesta iniziale, eventuale prodotto candidato, approvazione/rifiuto dell'ordinante con relativa motivazione)

API URL: `GET /rest/requests/{id}`

Funzionalità realizzata sia lato server (tramite apposito servizio REST) che lato client.
Per quanto riguarda il servizio REST, richiede solo l'id della richiesta di acquisto come parametro nell'URL dell'API.

#### 10. Estrazione lista richieste di acquisto gestite da un determinato tecnico

API URL: `GET /rest/requests/byTechnician`

Funzionalità accessibile tramite chiamata REST, non richiede alcuna risorsa o parametro in quanto è necessario che l'utente sia autenticato e che sia un tecnico.
A quel punto il servizio restituirà la lista di richieste gestite dal tecnico autenticato.

## Funzionalità aggiuntive

#### 1. Estrazione lista richieste di acquisto di un determinato ordinante

API URL: `GET /rest/requests/byUser`

Funzionalità realizzata sia lato server (tramite apposito servizio REST) che lato client.
Tale servizio richiede che l'utente sia autenticato come "ordinante" e potrà vedere solo la lista delle proprie richieste, qualsiasi sia il loro stato.

#### 2. Modifica di una *richiesta di acquisto*

API URL: `PUT /rest/requests/{id}`

Funzionalità accessibile tramite chiamata REST che richiede l'invio della richiesta in formato JSON e l'id della richiesta come parametro nell'URL dell'API.
Il servizio richiede che l'utente sia autenticato come "ordinante" e che la richiesta di acquisto sia di proprietà del medesimo utente.

## Specifica OpenAPI

Per maggiori dettagli sulle REST API rese disponibili è consigliato consultare l'apposita [documentazione OpenAPI](/openapi.yaml) (versione 3.0), da aprire con l'apposito [Swagger Editor](https://editor.swagger.io)
