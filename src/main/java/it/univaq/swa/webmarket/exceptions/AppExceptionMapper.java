package it.univaq.swa.webmarket.exceptions;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AppExceptionMapper implements ExceptionMapper<WebMarketException> {
    @Override
    public Response toResponse(WebMarketException exception) {
        //possiamo trasformare queste eccezioni in una risposta formattata, se non le catturiamo all'origine...
        return Response.serverError().entity(exception.getMessage()).type(MediaType.APPLICATION_JSON).build();
    }
}
