package net.hlinfo.example.etc.security;

import org.springframework.security.core.AuthenticationException;

public class ParamterErrorException extends AuthenticationException {

	private static final long serialVersionUID = 1L;

	public ParamterErrorException(String msg) {
		super(msg);
	}
	
	public ParamterErrorException(String msg, Throwable cause) {
		super(msg, cause);
		// TODO Auto-generated constructor stub
	}


}
