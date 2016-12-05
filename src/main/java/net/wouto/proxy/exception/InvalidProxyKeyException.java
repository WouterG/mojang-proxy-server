package net.wouto.proxy.exception;

public class InvalidProxyKeyException extends Exception {

	private static final long serialVersionUID = -8480240968810798356L;

	public InvalidProxyKeyException() {
		super("Invalid proxy key");
		super.setStackTrace(new StackTraceElement[0]);
	}

}
