package it.univaq.swa.webmarket.business;

public class PurchaseRequestsServiceFactory {

	private static final PurchaseRequestsServiceImpl service = new PurchaseRequestsServiceImpl();

	public static PurchaseRequestsServiceImpl getPurchaseRequestsService() {
		return service;
	}
}
