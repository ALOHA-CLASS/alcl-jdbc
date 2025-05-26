package com.alohaclass.jdbc.utils;

public class StringUtil {
	
	// camel --> underscore
	// * sampleObject --> sample_object
	public static String convertCamelCaseToUnderscore(String camelCase) {
        StringBuilder result = new StringBuilder();
        for (char c : camelCase.toCharArray()) {
            if (Character.isUpperCase(c)) {
                result.append('_').append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
	
	
	// underscore --> camel  
	// * sample_object --> sampleObject
	public static String convertUnderscoreToCamelCase(String underscore) {
		StringBuilder result = new StringBuilder();
		boolean nextUpperCase = false;
		for (char c : underscore.toCharArray()) {
			if (c == '_') {
				nextUpperCase = true;
			} else {
				if (nextUpperCase) {
					result.append(Character.toUpperCase(c));
					nextUpperCase = false;
				} else {
					result.append(c);
				}
			}
		}
		return result.toString();
	}
}
