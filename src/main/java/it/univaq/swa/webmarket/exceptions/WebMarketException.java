package it.univaq.swa.webmarket.exceptions;

public class WebMarketException extends Exception {

	public WebMarketException() {
	}

	public WebMarketException(String message) {
		super(message);
	}

	public WebMarketException(String message, Throwable cause) {
		super(message, cause);
	}

}
