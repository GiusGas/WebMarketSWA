package it.univaq.swa.webmarket.exceptions;

import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class JacksonExceptionMapper implements ExceptionMapper<JsonMappingException> {

	@Override
	public Response toResponse(JsonMappingException exception) {
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Invalid JSON").build();
	}
}
