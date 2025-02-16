package it.univaq.swa.webmarket.resources;

import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;

import java.util.Objects;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.univaq.swa.webmarket.business.PurchaseRequestsService;
import it.univaq.swa.webmarket.business.PurchaseRequestsServiceFactory;
import it.univaq.swa.webmarket.exceptions.NotFoundException;
import it.univaq.swa.webmarket.exceptions.WebMarketException;
import it.univaq.swa.webmarket.model.PurchaseProposal;
import it.univaq.swa.webmarket.model.PurchaseRequest;
import it.univaq.swa.webmarket.model.User;
import it.univaq.swa.webmarket.model.UserType;
import it.univaq.swa.webmarket.security.Logged;
import it.univaq.swa.webmarket.utility.FakeDb;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

public class PurchaseRequestRes {

	private static final String NOT_FOUND = "Purchase Request not found";
	private static final String NOT_PURCHASER = "User is not a purchaser";
	private static final String NOT_TECHNICIAN = "User is not a technician";
	
	private final PurchaseRequestsService business;
	private final PurchaseRequest request;

	public PurchaseRequestRes(PurchaseRequest purchaseRequest) {
		this.business = PurchaseRequestsServiceFactory.getPurchaseRequestsService();
		this.request = purchaseRequest;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(description = "Retrieve a purchase request by its ID",
	  tags = {"PurchaseRequest"},
	  responses = {
	          @ApiResponse(responseCode = "200", description = "The desired purchase request", content = @Content(
	                  schema = @Schema(implementation = PurchaseRequest.class)
	          )),
	          @ApiResponse(responseCode = "404", description = NOT_FOUND)
	  })
	public Response getPurchaseRequest() {
		return Response.ok(request).build();
	}

	@PUT
	@Logged
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(description = "Update a purchase request",
	  tags = {"PurchaseRequest"},
	  security = @SecurityRequirement(name = "bearerAuth"),
	  responses = {
	          @ApiResponse(responseCode = "200", description = "The updated purchase request", content = @Content(
	                  schema = @Schema(implementation = PurchaseRequest.class)
	          )),
	          @ApiResponse(responseCode = "401", description = "Unauthorized: \n- User not authenticated\n- "+NOT_PURCHASER+"\n- You can't update another user's request"),
	          @ApiResponse(responseCode = "404", description = NOT_FOUND)
	  })
	public Response updatePurchaseRequest(@Parameter(
            description = "The updated purchase request",
            schema = @Schema(implementation = PurchaseRequest.class),
            required = true)PurchaseRequest body, @Context SecurityContext securityContext) {
		
		String username = securityContext.getUserPrincipal().getName();
		User user = FakeDb.getUserByUsername(username);

		if (!securityContext.isUserInRole(UserType.PURCHASER.toString())) {
			return Response.status(UNAUTHORIZED).type(MediaType.TEXT_PLAIN).entity(NOT_PURCHASER).build();
		} else if(!request.getPurchaser().equals(user)) {
			return Response.status(UNAUTHORIZED).type(MediaType.TEXT_PLAIN).entity("You can't update another user's request").build();
		}
		
		body.setId(request.getId());
		PurchaseRequest updatedRequest = business.updatePurchaseRequest(body);
		return Response.ok(updatedRequest).build();
	}

	@PUT
	@Path("/technician")
	@Logged
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(description = "Set technician by ID for a purchase request",
	  tags = {"PurchaseRequest properties"},
	  security = @SecurityRequirement(name = "bearerAuth"),
	  responses = {
	          @ApiResponse(responseCode = "200", description = "The purchase request updated with the assigned technician", content = @Content(
	                  schema = @Schema(implementation = PurchaseRequest.class)
	          )),
	          @ApiResponse(responseCode = "400", description = "Technician ID is not valid"),
	          @ApiResponse(responseCode = "401", description = "Unauthorized: \n- User not authenticated\n- "+NOT_TECHNICIAN),
	          @ApiResponse(responseCode = "404", description = NOT_FOUND)
	  })
	public Response setTechnician(@Parameter(
            description = "ID of technician that needs to be assigned to the requesy",
            schema = @Schema(
                    type = "integer",
                    format = "int64"
            ),
            required = true)Long technicianId, @Context SecurityContext securityContext) {
		
		if (!securityContext.isUserInRole(UserType.TECHNICIAN.toString())) {
			return Response.status(UNAUTHORIZED).type(MediaType.TEXT_PLAIN).entity(NOT_TECHNICIAN).build();
		}
		
		try {
			PurchaseRequest updatedRequest = business.setTechnician(request.getId(), technicianId);
			return Response.ok(updatedRequest).build();
		} catch (NotFoundException ex) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Technician ID is not valid").build();
		}
	}

	@PUT
	@Path("/proposal")
	@Logged
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(description = "Set or update a purchase proposal for a request",
	  tags = {"PurchaseRequest properties"},
	  security = @SecurityRequirement(name = "bearerAuth"),
	  responses = {
	          @ApiResponse(responseCode = "200", description = "The purchase request updated with the purchase proposal", content = @Content(
	                  schema = @Schema(implementation = PurchaseRequest.class)
	          )),
	          @ApiResponse(responseCode = "401", description = "Unauthorized: \n- User not authenticated\n- "+NOT_TECHNICIAN+"\n- You are not assigned to this request"),
	          @ApiResponse(responseCode = "404", description = NOT_FOUND)
	  })
	public Response setPurchaseProposal(@Parameter(
            description = "The purchase proposal for a request",
            schema = @Schema(implementation = PurchaseProposal.class),
            required = true)PurchaseProposal purchaseProposal, @Context SecurityContext securityContext) {
		
		String username = securityContext.getUserPrincipal().getName();
		User user = FakeDb.getUserByUsername(username);
		
		if (!securityContext.isUserInRole(UserType.TECHNICIAN.toString())) {
			return Response.status(UNAUTHORIZED).type(MediaType.TEXT_PLAIN).entity(NOT_TECHNICIAN).build();
		} else if (Objects.isNull(request.getTechnician()) || !request.getTechnician().equals(user)) {
			return Response.status(UNAUTHORIZED).type(MediaType.TEXT_PLAIN).entity("You are not assigned to this request").build();
		}
		
		try {
			PurchaseRequest updatedRequest = business.setPurchaseProposal(purchaseProposal, request.getId());
			return Response.ok(updatedRequest).build();
		} catch (NotFoundException ex) {
			return Response.status(Response.Status.NOT_FOUND).entity(NOT_FOUND).build();
		}
	}

	@PUT
	@Path("/proposal/approve")
	@Logged
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(description = "Approve a purchase proposal for a request",
	  tags = {"PurchaseRequest properties"},
	  security = @SecurityRequirement(name = "bearerAuth"),
	  responses = {
	          @ApiResponse(responseCode = "200", description = "The purchase request with the approved proposal", content = @Content(
	                  schema = @Schema(implementation = PurchaseRequest.class)
	          )),
	          @ApiResponse(responseCode = "401", description = "Unauthorized: \n- User not authenticated\n- "+NOT_PURCHASER+"\n- You can't approve the proposal of another user's request"),
	          @ApiResponse(responseCode = "404", description = NOT_FOUND),
	          @ApiResponse(responseCode = "500", description = "Purchase proposal can not be approved")
	  })
	public Response approvePurchaseProposal(@Context SecurityContext securityContext) {
		
		String username = securityContext.getUserPrincipal().getName();
		User user = FakeDb.getUserByUsername(username);

		if (!securityContext.isUserInRole(UserType.PURCHASER.toString())) {
			return Response.status(UNAUTHORIZED).type(MediaType.TEXT_PLAIN).entity(NOT_PURCHASER).build();
		} else if(!request.getPurchaser().equals(user)) {
			return Response.status(UNAUTHORIZED).type(MediaType.TEXT_PLAIN).entity("You can't approve the proposal of another user's request").build();
		}
		
		try {
			PurchaseRequest updatedRequest = business.approvePurchaseProposal(request.getId());
			return Response.ok(updatedRequest).build();
		} catch (NotFoundException ex) {
			return Response.status(Response.Status.NOT_FOUND).entity("PurchaseRequest not found").build();
		} catch (WebMarketException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Purchase proposal can not be approved, caused by: ".concat(e.getMessage())).build();
		}
	}

	@DELETE
	@Logged
	@Operation(description = "Delete a purchase request",
	  tags = {"PurchaseRequest"},
	  security = @SecurityRequirement(name = "bearerAuth"),
	  responses = {
	          @ApiResponse(responseCode = "204", description = "PurchaseRequest succesfully deleted"),
	          @ApiResponse(responseCode = "401", description = "Unauthorized: \n- User not authenticated\n- "+NOT_PURCHASER+"\n- You can't delete another user's request"),
	          @ApiResponse(responseCode = "404", description = NOT_FOUND)
	  })
	public Response deletePurchaseRequest(@Context SecurityContext securityContext) {
		
		String username = securityContext.getUserPrincipal().getName();
		User user = FakeDb.getUserByUsername(username);

		if (!securityContext.isUserInRole(UserType.PURCHASER.toString())) {
			return Response.status(UNAUTHORIZED).type(MediaType.TEXT_PLAIN).entity(NOT_PURCHASER).build();
		} else if(!request.getPurchaser().equals(user)) {
			return Response.status(UNAUTHORIZED).type(MediaType.TEXT_PLAIN).entity("You can't delete another user's request").build();
		}
		
		try {
			business.deletePurchaseRequest(request.getId());
			return Response.noContent().build();
		} catch (NotFoundException ex) {
			return Response.status(Response.Status.NOT_FOUND).entity("PurchaseRequest not found").build();
		}
	}
}
