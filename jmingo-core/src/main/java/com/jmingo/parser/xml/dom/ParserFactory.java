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
package com.jmingo.parser.xml.dom;

import static com.jmingo.parser.xml.ParserConstants.CONTEXT_XSD;
import static com.jmingo.parser.xml.ParserConstants.QUERY_XSD;
import com.google.common.collect.Sets;
import com.jmingo.parser.Parser;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Creates instance of specified {@link Parser} implementation.
 */
public final class ParserFactory {

    /* default handler */
    private static final ErrorHandler DEFAULT_PARSE_ERROR_HANDLER = new ParseErrorHandler();

    private static final Logger LOGGER = LoggerFactory.getLogger(ParserFactory.class);

    /**
     * Parse component represent a root xml element.
     */
    public enum ParseComponent {
        CONTEXT("context"),
        QUERY("query");

        private ParseComponent(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        private String name;
    }

    /**
     * Creates parser.
     *
     * @param parseComponent parse component {@link ParseComponent}
     * @param <T>            class
     * @return implementation of {@link Parser}
     */
    public static <T> Parser<T> createParser(ParseComponent parseComponent) {
        Parser xmlParser = null;
        try {
            switch (parseComponent) {
                case CONTEXT:
                    xmlParser = new ContextConfigurationParser(createValidatedConfiguration(CONTEXT_XSD),
                        DEFAULT_PARSE_ERROR_HANDLER);
                    break;
                case QUERY:
                    xmlParser = new QuerySetParser(createValidatedConfiguration(QUERY_XSD),
                        DEFAULT_PARSE_ERROR_HANDLER);
                    break;
                default:
                    xmlParser = null;
            }
        } catch (ParserConfigurationException ex) {
            LOGGER.error(ExceptionUtils.getMessage(ex));
        }
        return xmlParser;
    }

    /**
     * Creates predefined configuration of parser which support schema validation.
     *
     * @param schemaPaths array of schema path
     * @return configured {@link ParserConfiguration}
     */
    private static ParserConfiguration createValidatedConfiguration(String... schemaPaths) {
        return new ParserConfiguration.Builder()
            .validate(true).namespaceAware(true)
            .schemaSources(Sets.newHashSet(schemaPaths)).build();
    }
}
