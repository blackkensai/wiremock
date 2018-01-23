package com.github.tomakehurst.wiremock.extension.callback;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;

public class AsyncCallbackTransformer extends ResponseDefinitionTransformer {
	private AsyncCallbackTask asyncCallbackTask = new AsyncCallbackTask();

	@Override
	public String getName() {
		return "async-callback";
	}

	@Override
	public ResponseDefinition transform(Request request, ResponseDefinition responseDefinition, FileSource files,
			Parameters parameters) {
		if (parameters != null && parameters.containsKey("callback")) {
			asyncCallbackTask.execute(new CallbackTask(request, responseDefinition));
		}
		return responseDefinition;
	}

}
