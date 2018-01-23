package com.github.tomakehurst.wiremock.extension.helpers;

import com.github.jknack.handlebars.Helper;

public interface INamedHelper extends Helper<Object> {
	String getName();
}
