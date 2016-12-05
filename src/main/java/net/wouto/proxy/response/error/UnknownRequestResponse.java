package net.wouto.proxy.response.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.wouto.proxy.MojangProxyServer;
import net.wouto.proxy.response.ErrorResponse;

public class UnknownRequestResponse extends ErrorResponse {

	private String url;
	private String requestMethod;
	@JsonInclude(JsonInclude.Include.NON_NULL) private String body;
	@JsonInclude(JsonInclude.Include.NON_NULL) private ObjectNode jsonBody;

	public UnknownRequestResponse(String url, String requestMethod) {
		this(url, requestMethod, null);
	}

	public UnknownRequestResponse(String url, String requestMethod, String requestBody) {
		super("Unknown request");
		this.url = url;
		this.requestMethod = requestMethod;
		if (requestBody != null) {
			try {
				JsonNode node = MojangProxyServer.get().getMapper(false).readTree(requestBody);
				this.jsonBody = (ObjectNode) node;
			} catch (Exception e) {
				this.body = requestBody;
			}
		}
	}

	public ObjectNode getJsonBody() {
		return jsonBody;
	}

	public String getUrl() {
		return url;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public String getBody() {
		return body;
	}
}
