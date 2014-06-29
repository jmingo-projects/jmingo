/**
 * Copyright 2013-2014 The JMingo Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jmingo.util;


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
