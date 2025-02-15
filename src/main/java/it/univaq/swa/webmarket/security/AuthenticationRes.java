package it.univaq.swa.webmarket.security;

import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import it.univaq.swa.webmarket.exceptions.RESTWebApplicationException;
import it.univaq.swa.webmarket.exceptions.WebMarketException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@SecurityScheme(
	name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
@Path("auth")
public class AuthenticationRes {
    
	private static final String TOKEN = "token";
	
    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
	@Operation(description = "Performs the login by sending the username/password pair", 
		tags = {"Authentication"}, 
		responses = {
			@ApiResponse(responseCode = "200", description = "The authorization token", 
					content = @Content(schema = @Schema(type = "string")),
					headers = {
		                    @Header(
		                            name = "Authorization",
		                            description = "Bearer token for authentication",
		                            schema = @Schema(type = "string", pattern = "Bearer [a-z0-9A-Z-]+")
		                        )
		                    }),
			@ApiResponse(responseCode = "500", description = "Username or password not setted"),
			@ApiResponse(responseCode = "401", description = "Unauthorized, wrong username or password") })
    public Response login(@Context UriInfo uriinfo,
    		@Parameter(schema = @Schema(type = "string"))
            @FormParam("username") String username,
            @Parameter(schema = @Schema(type = "string"))
            @FormParam("password") String password) {
        try {
            
            if (JWTHelpers.getInstance().authenticateUser(username, password)) {
                String authToken = JWTHelpers.getInstance().issueToken(uriinfo, username);
                
                return Response.ok(authToken)
                        .cookie(new NewCookie.Builder(TOKEN).value(authToken).build())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken).build();
            }
        } catch (WebMarketException e) {
            throw new RESTWebApplicationException(500, e.getMessage()); 
        }
        return Response.status(UNAUTHORIZED).entity("Unauthorized, wrong username or password").type(MediaType.TEXT_PLAIN).build();
    }
    
    @DELETE
    @Path("logout")
    @Logged
    @Operation(description = "Invalidates the current user session and removes the authentication token.",
            tags = {"Authentication"},
            security = @SecurityRequirement(name = "bearerAuth"),
        	responses = {
        			@ApiResponse(responseCode = "204", description = "Successful logout"),
        			@ApiResponse(responseCode = "401", description = "Unauthorized")
        	})
    public Response logout(@Context ContainerRequestContext req) {
        String token = (String) req.getProperty(TOKEN);
        JWTHelpers.getInstance().revokeToken(token);
        return Response.noContent()
                .cookie(new NewCookie.Builder(TOKEN).value("").maxAge(0).build())
                .build();
    }

    @GET
    @Path("refresh")
    @Produces(MediaType.TEXT_PLAIN)
    @Logged
    @Operation(description = "Refreshes the current authorization token",
    tags = {"Authentication"},
    security = @SecurityRequirement(name = "bearerAuth"),
	responses = {
			@ApiResponse(responseCode = "200", description = "The refreshed authorization token", 
					content = @Content(schema = @Schema(format = "text/plain", type = "string")),
					headers = {
		                    @Header(
		                            name = "Authorization",
		                            description = "Bearer token for authentication",
		                            schema = @Schema(type = "string", pattern = "Bearer [a-z0-9A-Z-]+")
		                        )
		                    }),
			@ApiResponse(responseCode = "401", description = "Unauthorized")
	})
    public Response refresh(@Context ContainerRequestContext req, @Context UriInfo uriinfo) {
        String username = (String) req.getProperty("user");
        String newtoken = JWTHelpers.getInstance().issueToken(uriinfo, username);
        return Response.ok(newtoken)
                .cookie(new NewCookie.Builder(TOKEN).value(newtoken).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + newtoken).build();
        
    }
}
