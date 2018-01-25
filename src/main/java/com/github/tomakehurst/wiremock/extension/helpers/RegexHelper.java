package com.github.tomakehurst.wiremock.extension.helpers;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.jknack.handlebars.Handlebars.SafeString;
import com.github.jknack.handlebars.Options;

public class RegexHelper implements INamedHelper {

	@Override
	public Object apply(Object value, Options options) throws IOException {
		String str = value.toString();
		String regex = options.param(0);
		Object group = options.param(1, (Object) 0);
		boolean escape = options.hash("escape", false);
		String namedGroup = "";
		Integer indexedGroup = -1;
		if (escape) {
			str = URLDecoder.decode(str, "utf-8");
		}
		if (group instanceof String) {
			namedGroup = group.toString();
		} else if (group instanceof Integer) {
			indexedGroup = (Integer) group;
		} else {
			return new SafeString(String.format("Unknown group type: %s", group.getClass()));
		}
		Matcher matcher = Pattern.compile(regex).matcher(str);
		if (matcher.find()) {
			if (indexedGroup >= 0) {
				return new SafeString(matcher.group(indexedGroup));
			} else if (namedGroup.length() > 0) {
				return new SafeString(matcher.group(namedGroup));
			} else {
				return new SafeString(matcher.group(0));
			}
		}
		return new SafeString(String.format("Regex not matched: '%s' to '%s'", regex, str));
	}

	@Override
	public String getName() {
		return "regex";
	}

}
