package it.univaq.swa.webmarket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@AllArgsConstructor
public class User {

	@Getter
	private Long id;

	@Getter
	private String username;

	@Getter
	@JsonIgnore
	private String password;

	private String email;

	@Getter
	private UserType userType;

}
