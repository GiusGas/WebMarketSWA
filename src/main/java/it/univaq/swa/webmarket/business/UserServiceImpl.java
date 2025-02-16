package it.univaq.swa.webmarket.business;

import it.univaq.swa.webmarket.model.User;
import it.univaq.swa.webmarket.utility.FakeDb;

public class UserServiceImpl implements UserService{

	@Override
	public User getUserByUsername(String username) {
		return FakeDb.getUserByUsername(username);
	}

}
