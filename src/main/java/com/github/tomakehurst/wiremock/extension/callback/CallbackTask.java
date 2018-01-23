package com.github.tomakehurst.wiremock.extension.callback;

import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;

public class CallbackTask {
	private Request request;
	private ResponseDefinition responseDefinition;

	public CallbackTask(Request request, ResponseDefinition responseDefinition) {
		super();
		this.request = request;
		this.responseDefinition = responseDefinition;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public ResponseDefinition getResponseDefinition() {
		return responseDefinition;
	}

	public void setResponseDefinition(ResponseDefinition responseDefinition) {
		this.responseDefinition = responseDefinition;
	}

}
