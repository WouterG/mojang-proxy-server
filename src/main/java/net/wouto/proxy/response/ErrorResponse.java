package net.wouto.proxy.response;

import com.fasterxml.jackson.annotation.JsonInclude;

public abstract class ErrorResponse implements BaseResponse {

	@JsonInclude(JsonInclude.Include.NON_EMPTY) private String error;
	@JsonInclude(JsonInclude.Include.NON_EMPTY) private String errorMessage;

	public ErrorResponse() {
	}

	public ErrorResponse(String error) {
		this.error = error;
	}

	public ErrorResponse(String error, String errorMessage) {
		this.error = error;
		this.errorMessage = errorMessage;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
