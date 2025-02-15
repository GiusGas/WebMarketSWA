package it.univaq.swa.webmarket.business;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import it.univaq.swa.webmarket.exceptions.NotFoundException;
import it.univaq.swa.webmarket.exceptions.WebMarketException;
import it.univaq.swa.webmarket.model.PurchaseProposal;
import it.univaq.swa.webmarket.model.PurchaseRequest;
import it.univaq.swa.webmarket.model.Status;
import it.univaq.swa.webmarket.model.User;
import it.univaq.swa.webmarket.utility.FakeDb;

public class PurchaseRequestsServiceImpl implements PurchaseRequestsService {

	@Override
	public Long addPurchaseRequest(PurchaseRequest purchaseRequest) throws WebMarketException {

		purchaseRequest.setStatus(Status.UNASSIGNED);
		purchaseRequest.setCreatedAt(LocalDateTime.now());
		purchaseRequest.setUpdatedAt(LocalDateTime.now());

		try {
			return FakeDb.addPurchaseRequest(purchaseRequest);
		} catch (WebMarketException e) {
			throw new WebMarketException("Can not add this purchase request, caused by: ".concat(e.getMessage()));
		}
	}

	@Override
	public void deletePurchaseRequest(Long requestId) throws NotFoundException {
		try {
			FakeDb.deletePurchaseRequest(requestId);
		} catch (Exception e) {
			throw new NotFoundException("The Purchase Request you are trying to delete doesn't exists");
		}
	}

	@Override
	public PurchaseRequest updatePurchaseRequest(PurchaseRequest purchaseRequest) {
		purchaseRequest.setUpdatedAt(LocalDateTime.now());
		return FakeDb.updatePurchaseRequest(purchaseRequest);
	}

	@Override
	public PurchaseRequest getPurchaseRequest(Long requestId) throws NotFoundException {
		return FakeDb.getPurchaseRequest(requestId);
	}

	@Override
	public PurchaseRequest setTechnician(Long requestId, Long technicianId) throws NotFoundException {

		PurchaseRequest request = FakeDb.getPurchaseRequest(requestId);
		User technician = FakeDb.getTechnician(technicianId);

		request.setTechnician(technician);
		request.setUpdatedAt(LocalDateTime.now());
		request.setStatus(Status.IN_PROGRESS);

		return FakeDb.updatePurchaseRequest(request);
	}

	@Override
	public PurchaseRequest setPurchaseProposal(PurchaseProposal purchaseProposal, Long requestId)
			throws NotFoundException {

		PurchaseRequest request = FakeDb.getPurchaseRequest(requestId);

		PurchaseProposal oldProposal = request.getPurchaseProposal();
		if (Objects.isNull(oldProposal)) {
			purchaseProposal.setCreatedAt(LocalDateTime.now());
		} else {
			purchaseProposal.setCreatedAt(oldProposal.getCreatedAt());
		}

		purchaseProposal.setUpdatedAt(LocalDateTime.now());
		request.setPurchaseProposal(purchaseProposal);
		request.setUpdatedAt(LocalDateTime.now());

		return FakeDb.updatePurchaseRequest(request);
	}

	@Override
	public PurchaseRequest approvePurchaseProposal(Long requestId) throws NotFoundException, WebMarketException {

		PurchaseRequest request = FakeDb.getPurchaseRequest(requestId);
		if (Objects.isNull(request.getPurchaseProposal()) || request.getStatus().compareTo(Status.IN_PROGRESS) != 0) {
			throw new WebMarketException("There is no Proposal to approve or it is already approved or rejected");
		}
		request.setStatus(Status.APPROVED);
		request.setUpdatedAt(LocalDateTime.now());

		return FakeDb.updatePurchaseRequest(request);
	}

	@Override
	public List<PurchaseRequest> getInProgressRequests() throws NotFoundException {
		return FakeDb.getRequestsByStatus(Status.IN_PROGRESS);
	}

	@Override
	public List<PurchaseRequest> getRequestsByUser(String username) {
		return FakeDb.getRequestsByPurchaser(username);
	}

	@Override
	public List<PurchaseRequest> getInProgressRequestsByUser(String username) {
		return FakeDb.getInProgressRequestsByPurchaser(username);
	}

	@Override
	public List<PurchaseRequest> getRequestsByTechnician(String username) {
		return FakeDb.getRequestsByTechnician(username);
	}

	@Override
	public List<PurchaseRequest> getNotAssignedRequests() {
		return FakeDb.getRequestsByStatus(Status.UNASSIGNED);
	}

}
