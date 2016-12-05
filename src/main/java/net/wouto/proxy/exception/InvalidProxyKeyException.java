package net.wouto.proxy.exception;

public class InvalidProxyKeyException extends Exception {

	public InvalidProxyKeyException() {
		super("Invalid proxy key");
		super.setStackTrace(new StackTraceElement[0]);
	}

}
