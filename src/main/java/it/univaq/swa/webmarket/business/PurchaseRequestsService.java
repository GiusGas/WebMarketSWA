package it.univaq.swa.webmarket.business;

import java.util.List;

import it.univaq.swa.webmarket.exceptions.NotFoundException;
import it.univaq.swa.webmarket.exceptions.WebMarketException;
import it.univaq.swa.webmarket.model.PurchaseProposal;
import it.univaq.swa.webmarket.model.PurchaseRequest;

public interface PurchaseRequestsService {

	Long addPurchaseRequest(PurchaseRequest purchaseRequest) throws WebMarketException;

	void deletePurchaseRequest(Long requestId) throws NotFoundException;

	PurchaseRequest updatePurchaseRequest(PurchaseRequest purchaseRequest);

	PurchaseRequest getPurchaseRequest(Long requestId) throws NotFoundException;

	PurchaseRequest setTechnician(Long requestId, Long technicianId) throws NotFoundException;

	PurchaseRequest setPurchaseProposal(PurchaseProposal purchaseProposal, Long requestId) throws NotFoundException;

	PurchaseRequest approvePurchaseProposal(Long requestId) throws NotFoundException, WebMarketException;

	List<PurchaseRequest> getInProgressRequests() throws NotFoundException;

	List<PurchaseRequest> getNotAssignedRequests();

	List<PurchaseRequest> getRequestsByUser(String username);

	List<PurchaseRequest> getInProgressRequestsByUser(String username);

	List<PurchaseRequest> getRequestsByTechnician(String username);
}
