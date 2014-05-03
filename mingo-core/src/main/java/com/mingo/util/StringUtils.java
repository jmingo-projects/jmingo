package com.mingo.util;


import static org.apache.commons.lang3.StringUtils.trim;

public class StringUtils {

    /**
     * Replace all line breaks from a string.
     *
     * @param source the source string
     * @return the processed string without newline characters.
     */
    public static String removeLineBreaks(String source) {
        if(org.apache.commons.lang3.StringUtils.isNotBlank(source)) {
            source = source.replaceAll("\\r|\\n", "");
        }
        return source;
    }

    /**
     * Append the suffix to the source string if source don't ends with the specified suffix.
     *
     * @param source the source string
     * @param suffix the string to add
     * @return if source string don't ends with suffix then the source string plus specified suffix otherwise just source string
     */
    public static String appendIfAbsent(String source, String suffix) {
        if(org.apache.commons.lang3.StringUtils.isNotBlank(source)) {
            String plain = removeLineBreaks(trim(source));
            return (!plain.endsWith(suffix)) ? source + suffix : source;
        }
        return source;
    }

    /**
     * Append the suffix to the source string if source don't ends with the specified suffix.
     *
     * @param source the source string
     * @param suffix the string to add
     * @return if source string don't ends with suffix then the source string plus specified suffix otherwise just source string
     */
    public static StringBuilder appendIfAbsent(StringBuilder source, String suffix) {
        if(org.apache.commons.lang3.StringUtils.isNotBlank(source)) {
            String plain = removeLineBreaks(trim(source.toString()));
            return (!plain.endsWith(suffix)) ? source.append(suffix) : source;
        }
        return source;
    }

    public static String replaceLastComma(String text) {
        return text.replaceAll(",$", "");
    }
}
