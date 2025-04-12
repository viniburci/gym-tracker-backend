package com.burci.security.user;

public interface UserProjection {
	
	String getEmail();

	String getFirstname();

	String getLastname();

	Role getRole();
}
