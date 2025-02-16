package it.univaq.swa.webmarket.business;

import it.univaq.swa.webmarket.model.User;

public interface UserService {

	User getUserByUsername(String username);
}
