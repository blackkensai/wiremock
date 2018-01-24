package com.github.tomakehurst.wiremock.extension.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SupplementHelpers {
	private static List<INamedHelper> helpers = new ArrayList<>();
	
	static {
		helpers.add(new UUIDHelper());
		helpers.add(new RegexHelper());
	}
	
	public static List<INamedHelper> values() {
		return Collections.unmodifiableList(helpers);
	}
}
