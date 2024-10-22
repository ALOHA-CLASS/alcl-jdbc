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
}
