package com.github.tomakehurst.wiremock.extension.helpers;

import java.io.IOException;
import java.util.UUID;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public class UUIDHelper implements Helper<Object>, INamedHelper {

	@Override
	public Object apply(Object context, Options options) throws IOException {
		return UUID.randomUUID();
	}

	@Override
	public String getName() {
		return "uuid";
	}

}
