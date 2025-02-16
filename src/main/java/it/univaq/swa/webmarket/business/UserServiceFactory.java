package it.univaq.swa.webmarket.business;

public class UserServiceFactory {

	private static final UserServiceImpl service = new UserServiceImpl();

	public static UserServiceImpl getUserService() {
		return service;
	}
}
