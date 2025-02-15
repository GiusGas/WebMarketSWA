package it.univaq.swa.webmarket.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import it.univaq.swa.webmarket.business.PurchaseRequestsService;
import it.univaq.swa.webmarket.business.PurchaseRequestsServiceFactory;
import it.univaq.swa.webmarket.exceptions.NotFoundException;
import it.univaq.swa.webmarket.exceptions.WebMarketException;
import it.univaq.swa.webmarket.model.PurchaseProposal;
import it.univaq.swa.webmarket.model.PurchaseRequest;
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

	private static final String NOT_FOUND = "Purchase request not found";
	
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
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(description = "Update a purchase request",
	  tags = {"PurchaseRequest"},
	  responses = {
	          @ApiResponse(responseCode = "200", description = "The updated purchase request", content = @Content(
	                  schema = @Schema(implementation = PurchaseRequest.class)
	          )),
	          @ApiResponse(responseCode = "404", description = NOT_FOUND)
	  })
	public Response updatePurchaseRequest(@Parameter(
            description = "The updated purchase request",
            schema = @Schema(implementation = PurchaseRequest.class),
            required = true)PurchaseRequest body, @Context SecurityContext securityContext) {
		body.setId(request.getId());
		PurchaseRequest updatedRequest = business.updatePurchaseRequest(body);
		return Response.ok(updatedRequest).build();
	}

	@PUT
	@Path("/technician")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(description = "Set technician by ID for a purchase request",
	  tags = {"PurchaseRequest properties"},
	  responses = {
	          @ApiResponse(responseCode = "200", description = "The purchase request updated with the assigned technician", content = @Content(
	                  schema = @Schema(implementation = PurchaseRequest.class)
	          )),
	          @ApiResponse(responseCode = "400", description = "Technician ID is not valid"),
	          @ApiResponse(responseCode = "404", description = NOT_FOUND)
	  })
	public Response setTechnician(@Parameter(
            description = "ID of technician that needs to be assigned to the requesy",
            schema = @Schema(
                    type = "integer",
                    format = "int64"
            ),
            required = true)Long technicianId, @Context SecurityContext securityContext) {
		try {
			PurchaseRequest updatedRequest = business.setTechnician(request.getId(), technicianId);
			return Response.ok(updatedRequest).build();
		} catch (NotFoundException ex) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Technician ID is not valid").build();
		}
	}

	@PUT
	@Path("/proposal")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(description = "Set a purchase proposal for a request",
	  tags = {"PurchaseRequest properties"},
	  responses = {
	          @ApiResponse(responseCode = "200", description = "The purchase request updated with the purchase proposal", content = @Content(
	                  schema = @Schema(implementation = PurchaseRequest.class)
	          )),
	          @ApiResponse(responseCode = "404", description = NOT_FOUND)
	  })
	public Response setPurchaseProposal(@Parameter(
            description = "The purchase proposal for a request",
            schema = @Schema(implementation = PurchaseProposal.class),
            required = true)PurchaseProposal purchaseProposal, @Context SecurityContext securityContext) {
		try {
			PurchaseRequest updatedRequest = business.setPurchaseProposal(purchaseProposal, request.getId());
			return Response.ok(updatedRequest).build();
		} catch (NotFoundException ex) {
			return Response.status(Response.Status.NOT_FOUND).entity(NOT_FOUND).build();
		}
	}

	@PUT
	@Path("/proposal/modify")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(description = "Update the purchase proposal for a request",
	  tags = {"PurchaseRequest properties"},
	  responses = {
	          @ApiResponse(responseCode = "200", description = "The purchase request with the updated purchase proposal", content = @Content(
	                  schema = @Schema(implementation = PurchaseRequest.class)
	          )),
	          @ApiResponse(responseCode = "404", description = NOT_FOUND)
	  })
	public Response updatePurchaseProposal(@Parameter(
            description = "The purchase proposal to update for a request",
            schema = @Schema(implementation = PurchaseProposal.class),
            required = true)PurchaseProposal purchaseProposal,
			@Context SecurityContext securityContext) {
		try {
			PurchaseRequest updatedRequest = business.setPurchaseProposal(purchaseProposal, request.getId());
			return Response.ok(updatedRequest).build();
		} catch (NotFoundException ex) {
			return Response.status(Response.Status.NOT_FOUND).entity(NOT_FOUND).build();
		}
	}

	@PUT
	@Path("/proposal/approve")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(description = "Approve a purchase proposal for a request",
	  tags = {"PurchaseRequest properties"},
	  responses = {
	          @ApiResponse(responseCode = "200", description = "The purchase request with the approved proposal", content = @Content(
	                  schema = @Schema(implementation = PurchaseRequest.class)
	          )),
	          @ApiResponse(responseCode = "404", description = NOT_FOUND),
	          @ApiResponse(responseCode = "500", description = "Purchase proposal can not be approved")
	  })
	public Response approvePurchaseProposal(@Context SecurityContext securityContext) {
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
	@Operation(description = "Delete a purchase request",
	  tags = {"PurchaseRequest"},
	  responses = {
	          @ApiResponse(responseCode = "204", description = "PurchaseRequest succesfully deleted"),
	          @ApiResponse(responseCode = "404", description = NOT_FOUND)
	  })
	public Response deletePurchaseRequest(@Context SecurityContext securityContext) {
		try {
			business.deletePurchaseRequest(request.getId());
			return Response.noContent().build();
		} catch (NotFoundException ex) {
			return Response.status(Response.Status.NOT_FOUND).entity("PurchaseRequest not found").build();
		}
	}
}
