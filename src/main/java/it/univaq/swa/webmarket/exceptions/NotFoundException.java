package it.univaq.swa.webmarket.exceptions;

public class NotFoundException extends Exception {

	public NotFoundException() {
	}

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(Throwable cause) {
		super(cause);
	}

}
