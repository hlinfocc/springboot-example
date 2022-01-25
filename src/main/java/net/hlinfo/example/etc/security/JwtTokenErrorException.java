package net.hlinfo.example.etc.security;

import org.springframework.security.core.AuthenticationException;

public class JwtTokenErrorException extends AuthenticationException {

	private static final long serialVersionUID = 1L;

	public JwtTokenErrorException(String msg) {
		super(msg);
	}
	
	public JwtTokenErrorException(String msg, Throwable cause) {
		super(msg, cause);
		// TODO Auto-generated constructor stub
	}


}
