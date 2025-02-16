package it.univaq.swa.webmarket.resources;

import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;

import java.net.URI;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.univaq.swa.webmarket.business.PurchaseRequestsService;
import it.univaq.swa.webmarket.business.PurchaseRequestsServiceFactory;
import it.univaq.swa.webmarket.exceptions.NotFoundException;
import it.univaq.swa.webmarket.exceptions.RESTWebApplicationException;
import it.univaq.swa.webmarket.exceptions.WebMarketException;
import it.univaq.swa.webmarket.model.PurchaseRequest;
import it.univaq.swa.webmarket.model.User;
import it.univaq.swa.webmarket.model.UserType;
import it.univaq.swa.webmarket.security.Logged;
import it.univaq.swa.webmarket.utility.FakeDb;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;

@Path("/requests")
public class PurchaseRequestsRes {

	private static final String NOT_PURCHASER = "User is not a purchaser";
	private static final String NOT_TECHNICIAN = "User is not a technician";
	
	private final PurchaseRequestsService business;

	public PurchaseRequestsRes() {
		this.business = PurchaseRequestsServiceFactory.getPurchaseRequestsService();
	}

	@POST
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Logged
	@Operation(description = "Add a purchase request", 
		tags = { "PurchaseRequest" }, 
		security = @SecurityRequirement(name = "bearerAuth"),
		responses = {
			@ApiResponse(responseCode = "201", description = "Purchase request created", 
					content = @Content(schema = @Schema(implementation = URI.class))),
			@ApiResponse(responseCode = "400", description = "Can not add this purchase request"),
			@ApiResponse(responseCode = "401", description = "Unauthorized: \n- User not authenticated\n- "+NOT_PURCHASER) })
	public Response addPurchaseRequest(
			@Parameter(description = "The purchase request", schema = @Schema(implementation = PurchaseRequest.class), required = true) PurchaseRequest purchaseRequest,
			@Context SecurityContext securityContext, @Context ContainerRequestContext requestcontext,
			@Context UriInfo uriinfo) {

		String username = securityContext.getUserPrincipal().getName();
		User user = FakeDb.getUserByUsername(username);

		if (!securityContext.isUserInRole(UserType.PURCHASER.toString())) {
			return Response.status(UNAUTHORIZED).type(MediaType.TEXT_PLAIN).entity(NOT_PURCHASER).build();
		}

		purchaseRequest.setPurchaser(user);

		Long id;

		try {
			id = business.addPurchaseRequest(purchaseRequest);
		} catch (WebMarketException e) {
			return Response.status(Response.Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
		}

		URI uri = uriinfo.getBaseUriBuilder().path(getClass()).path(getClass(), "getPurchaseRequest").build(id);

		return Response.created(uri).entity(uri.toString()).build();
	}

	@Path("/{id}")
	public PurchaseRequestRes getPurchaseRequest(@PathParam("id") Long requestId) {
		try {
			PurchaseRequest request = business.getPurchaseRequest(requestId);
			return new PurchaseRequestRes(request);
		} catch (NotFoundException ex) {
			throw new RESTWebApplicationException(404, ex.getMessage());
		}
	}

	@GET
	@Path("/byUser")
	@Produces(MediaType.APPLICATION_JSON)
	@Logged
	@Operation(description = "Get the purchase requests collection of a purchaser", 
		tags = {"PurchaseRequest collection" },
		security = @SecurityRequirement(name = "bearerAuth"),
		responses = {
			@ApiResponse(responseCode = "200", description = "The purchase requests collection", 
					content = @Content(schema = @Schema(implementation = PurchaseRequest.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized: \n- User not authenticated\n- "+NOT_PURCHASER) })
	public Response getPurchaseRequestsByUser(@Context SecurityContext securityContext,
			@Context ContainerRequestContext requestcontext, @Context UriInfo uriinfo) {

		String username = securityContext.getUserPrincipal().getName();

		if (securityContext.isUserInRole(UserType.PURCHASER.toString())) {
			return Response.ok(mapPurchaseRequests(business.getRequestsByUser(username), uriinfo)).build();
		} else {
			return Response.status(UNAUTHORIZED).type(MediaType.TEXT_PLAIN).entity(NOT_PURCHASER).build();
		}
	}

	@GET
	@Path("/inProgress")
	@Produces(MediaType.APPLICATION_JSON)
	@Logged
	@Operation(description = "Get the in progress purchase requests collection of a purchaser", 
		tags = {"PurchaseRequest collection" }, 
		security = @SecurityRequirement(name = "bearerAuth"),
		responses = {
			@ApiResponse(responseCode = "200", description = "The in progress purchase requests collection", 
					content = @Content(schema = @Schema(implementation = PurchaseRequest.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized: \n- User not authenticated\n- "+NOT_PURCHASER) })
	public Response getInProgressPurchaseRequestsByUser(@Context SecurityContext securityContext,
			@Context ContainerRequestContext requestcontext, @Context UriInfo uriinfo) {

		String username = securityContext.getUserPrincipal().getName();

		if (securityContext.isUserInRole(UserType.PURCHASER.toString())) {
			return Response.ok(mapPurchaseRequests(business.getInProgressRequestsByUser(username), uriinfo)).build();
		} else {
			return Response.status(UNAUTHORIZED).type(MediaType.TEXT_PLAIN).entity(NOT_PURCHASER).build();
		}
	}

	@GET
	@Path("/unassigned")
	@Produces(MediaType.APPLICATION_JSON)
	@Logged
	@Operation(description = "Get the unassigned purchase requests collection", 
		tags = {"PurchaseRequest collection" }, 
		security = @SecurityRequirement(name = "bearerAuth"),
		responses = {
			@ApiResponse(responseCode = "200", description = "The unassigned purchase requests collection", content = @Content(schema = @Schema(implementation = PurchaseRequest.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized: \n- User not authenticated\n- "+NOT_TECHNICIAN)})
	public Response getUnassignedPurchaseRequests(@Context SecurityContext securityContext,
			@Context ContainerRequestContext requestcontext, @Context UriInfo uriinfo) {

		if (securityContext.isUserInRole(UserType.TECHNICIAN.toString())) {
			return Response.ok(mapPurchaseRequests(business.getNotAssignedRequests(), uriinfo)).build();
		} else {
			return Response.status(UNAUTHORIZED).type(MediaType.TEXT_PLAIN).entity(NOT_TECHNICIAN).build();
		}
	}

	@GET
	@Path("/byTechnician")
	@Produces(MediaType.APPLICATION_JSON)
	@Logged
	@Operation(description = "Get the purchase requests collection assigned to a technician", 
		tags = {"PurchaseRequest collection" }, 
		security = @SecurityRequirement(name = "bearerAuth"),
		responses = {
			@ApiResponse(responseCode = "200", description = "The purchase requests collection assigned to a technician", content = @Content(schema = @Schema(implementation = PurchaseRequest.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized: \n- User not authenticated\n- "+NOT_TECHNICIAN) })
	public Response getPurchaseRequestsByTechnician(@Context SecurityContext securityContext,
			@Context ContainerRequestContext requestcontext, @Context UriInfo uriinfo) {
		
		String username = securityContext.getUserPrincipal().getName();

		if (securityContext.isUserInRole(UserType.TECHNICIAN.toString())) {
			return Response.ok(mapPurchaseRequests(business.getRequestsByTechnician(username), uriinfo)).build();
		} else {
			return Response.status(UNAUTHORIZED).type(MediaType.TEXT_PLAIN).entity(NOT_TECHNICIAN).build();
		}
	}

	private List<Map<String, Object>> mapPurchaseRequests(List<PurchaseRequest> requests, UriInfo uriinfo) {
		return requests.stream().map(r -> {
			URI uri = uriinfo.getBaseUriBuilder().path(getClass()).path(getClass(), "getPurchaseRequest")
					.build(r.getId());

			return Map.of("request", r, "url", uri);

		}).toList();
	}
}
