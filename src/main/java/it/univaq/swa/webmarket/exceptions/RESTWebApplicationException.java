package it.univaq.swa.webmarket.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class RESTWebApplicationException extends WebApplicationException {

	public RESTWebApplicationException() {
		super(Response.serverError().build());
	}

	public RESTWebApplicationException(String message) {
		super(Response.serverError().entity(message).type(MediaType.TEXT_PLAIN).build());
	}

	public RESTWebApplicationException(int status, String message) {
		super(Response.status(status).entity(message).type(MediaType.TEXT_PLAIN).build());
	}
	
	public RESTWebApplicationException(NotFoundError error) {
		super(Response.status(error.getStatus()).entity(error).build());
	}
}
