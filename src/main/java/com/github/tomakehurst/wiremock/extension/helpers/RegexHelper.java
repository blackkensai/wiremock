package com.github.tomakehurst.wiremock.extension.helpers;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.jknack.handlebars.Options;

public class RegexHelper implements INamedHelper {

	@Override
	public Object apply(Object value, Options options) throws IOException {
		String str = value.toString();
		String regex = options.param(0);
		Object group = options.param(1, (Object) 0);
		String namedGroup = "";
		Integer indexedGroup = -1;
		if (group instanceof String) {
			namedGroup = group.toString();
		} else if (group instanceof Integer) {
			indexedGroup = (Integer) group;
		} else {
			return String.format("Unknown group type: %s", group.getClass());
		}
		/// * if (namedGroup.matches("\\d+")) {
		// indexedGroup = Integer.parseInt(namedGroup);
		// }*/
		Matcher matcher = Pattern.compile(regex).matcher(str);
		if (matcher.find()) {
			if (indexedGroup >= 0) {
				return matcher.group(indexedGroup);
			} else if (namedGroup.length() > 0) {
				return matcher.group(namedGroup);
			} else {
				return matcher.group(0);
			}
		}
		return String.format("Regex not matched: '%s' to '%s'", regex, str);
	}

	@Override
	public String getName() {
		return "regex";
	}

}
