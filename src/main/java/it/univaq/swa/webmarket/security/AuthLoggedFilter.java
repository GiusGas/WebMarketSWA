package it.univaq.swa.webmarket.security;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;

import it.univaq.swa.webmarket.model.UserType;
import it.univaq.swa.webmarket.utility.FakeDb;

@Provider
@Logged
@Priority(Priorities.AUTHENTICATION)
public class AuthLoggedFilter implements ContainerRequestFilter {

	private static final String NOT_AUTHENTICATED = "User not authenticated";
	
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String token = null;
        final String path = requestContext.getUriInfo().getAbsolutePath().toString();

        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring("Bearer".length()).trim();
        }
        
        if (token != null && !token.isEmpty()) {
            try {
                final String username = JWTHelpers.getInstance().validateToken(token);
                if (username != null) {
                    requestContext.setProperty("token", token);
                    requestContext.setProperty("user", username);
                    
                    requestContext.setSecurityContext(new SecurityContext() {
                        @Override
                        public Principal getUserPrincipal() {
                            return new Principal() {
                                @Override
                                public String getName() {
                                    return username;
                                }
                            };
                        }

                        @Override
                        public boolean isUserInRole(String role) {
                            return FakeDb.isUserOfType(username, UserType.valueOf(role));
                        }

                        @Override
                        public boolean isSecure() {
                            return path.startsWith("https");
                        }

                        @Override
                        public String getAuthenticationScheme() {
                            return "Token-Based-Auth-Scheme";
                        }
                    });

                } else {
                    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(NOT_AUTHENTICATED).type(MediaType.TEXT_PLAIN).build());
                }
            } catch (Exception e) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(NOT_AUTHENTICATED).type(MediaType.TEXT_PLAIN).build());
            }
        } else {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(NOT_AUTHENTICATED).type(MediaType.TEXT_PLAIN).build());
        }
    }
}
