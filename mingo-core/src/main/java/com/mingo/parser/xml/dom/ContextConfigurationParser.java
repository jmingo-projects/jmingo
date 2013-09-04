package com.mingo.parser.xml.dom;

import static com.mingo.parser.xml.dom.DocumentBuilderFactoryCreator.createDocumentBuilderFactory;
import static com.mingo.parser.xml.dom.DomUtil.assertPositive;
import static com.mingo.parser.xml.dom.DomUtil.getAttributeInt;
import static com.mingo.parser.xml.dom.DomUtil.getAttributeString;
import static com.mingo.parser.xml.dom.DomUtil.getFirstNecessaryTagOccurrence;
import static com.mingo.parser.xml.dom.DomUtil.getFirstTagOccurrence;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mingo.context.conf.ContextConfiguration;
import com.mingo.exceptions.ParserException;
import com.mingo.parser.Parser;
import com.mingo.query.QueryAnalyzerType;
import com.mingo.query.QueryExecutorType;
import com.mingo.query.conf.QuerySetConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Copyright 2012-2013 The Mingo Team
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p/>
 * This class is implementation of {@link Parser} interface.
 * XML parser for context configuration. See  context.xsd schema for details.
 */
public class ContextConfigurationParser implements Parser<ContextConfiguration> {

    private DocumentBuilderFactory documentBuilderFactory;

    private ErrorHandler parseErrorHandler;

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextConfigurationParser.class);

    private static final String CONTEXT_TAG = "context";
    private static final String QUERY_SET_CONFIG_TAG = "querySetConfig";
    private static final String QUERY_SET_TAG = "querySet";
    private static final String QUERY_EXECUTOR_TAG = "queryExecutor";
    private static final String QUERY_ANALYZER_TAG = "queryAnalyzer";
    private static final String QUERY_SET_PATH_ATTR = "path";
    private static final String QUERY_ANALYZER_TYPE_ATTR = "type";
    private static final String QUERY_EXECUTOR_TYPE_ATTR = "type";

    private static final String MONGO_TAG = "mongo";
    private static final String MONGO_HOST_ATTR = "host";
    private static final String MONGO_PORT_ATTR = "port";
    private static final String MONGO_DBNAME_ATTR = "dbName";


    private static final String DEFAULT_CONVERTER_TAG = "defaultConverter";
    private static final String CONVERTERS_TAG = "converters";
    private static final String CONVERTERS_PACKAGE_ATTR = "package";
    private static final String CONVERTER_CLASS_ATTR = "class";

    /**
     * Constructor with parameters.
     *
     * @param parserConfiguration parser configuration {@link ParserConfiguration}
     * @param parseErrorHandler   parser error handler {@link ErrorHandler}
     * @throws ParserConfigurationException {@link ParserConfigurationException}
     */
    public ContextConfigurationParser(ParserConfiguration parserConfiguration, ErrorHandler parseErrorHandler)
        throws ParserConfigurationException {
        this.documentBuilderFactory = createDocumentBuilderFactory(parserConfiguration);
        this.parseErrorHandler = parseErrorHandler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContextConfiguration parse(InputStream xml) throws ParserException {
        LOGGER.debug("ContextConfiguration:: START PARSING");
        ContextConfiguration contextConfiguration;
        try {
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            builder.setErrorHandler(parseErrorHandler);
            Document document = builder.parse(new InputSource(xml));
            contextConfiguration = new ContextConfiguration();
            Element element = document.getDocumentElement();
            contextConfiguration.setQuerySetConfiguration(parseQuerySetConfigTag(element));
            contextConfiguration.setQueryExecutorType(parseQueryExecutorTag(element));
            contextConfiguration.setQueryAnalyzerType(parseQueryAnalyzerTag(element));
            parseMongoTag(contextConfiguration, element);
            parseConvertersTag(contextConfiguration, element);
            contextConfiguration.setDefaultConverter(parseDefaultConverterTag(element));
        } catch (Exception e) {
            throw new ParserException(e);
        }
        return contextConfiguration;
    }

    /**
     * Parse <querySetConfig/> tag.
     *
     * @param element element of XML document
     * @return set of paths queries definitions
     */
    private QuerySetConfiguration parseQuerySetConfigTag(Element element) throws ParserException {
        Set<String> querySets = ImmutableSet.of();
        // expected what xml contains single <querySetConfig> tag
        // therefore take first element from node list is normal
        Node querySetConfigNode = getFirstNecessaryTagOccurrence(element, QUERY_SET_CONFIG_TAG);
        if (querySetConfigNode.hasChildNodes()) {
            querySets = parseQuerySets(querySetConfigNode.getChildNodes());
        }
        QuerySetConfiguration querySetConfiguration = new QuerySetConfiguration();
        querySetConfiguration.setDatabaseName(getAttributeString(querySetConfigNode, MONGO_DBNAME_ATTR));
        querySetConfiguration.addQuerySet(querySets);
        return querySetConfiguration;

    }

    /**
     * Parse <querySet/> tag.
     *
     * @param querySetNodeList list of 'querySet' tag nodes. root element must be <querySetConfig/> tag
     * @return set of paths queries definitions
     */
    private Set<String> parseQuerySets(NodeList querySetNodeList) {
        Set<String> querySets = Sets.newHashSet();
        for (int count = 0; count < querySetNodeList.getLength(); count++) {
            Node querySetNode = querySetNodeList.item(count);
            if (QUERY_SET_TAG.equals(querySetNode.getNodeName())) {
                querySets.add(getAttributeString(querySetNode, QUERY_SET_PATH_ATTR));
            }
        }
        return querySets;
    }

    /**
     * Gets 'type' attr from <queryExecutor/> tag.
     * Throws exception if 'queryExecutor' tag was not found.
     *
     * @param element element of XML document
     * @return {@link QueryExecutorType}
     * @throws ParserException {@link ParserException}
     */
    private QueryExecutorType parseQueryExecutorTag(Element element) throws ParserException {
        QueryExecutorType type = null;
        Node queryExecutorNode = getFirstTagOccurrence(element, QUERY_EXECUTOR_TAG);
        if (queryExecutorNode != null) {
            type = QueryExecutorType.getByName(getAttributeString(queryExecutorNode,
                QUERY_EXECUTOR_TYPE_ATTR));
            if (type == null) {
                throw new ParserException("unsupported query executor type.");
            }
        }
        return type;
    }

    /**
     * Gets 'type' attr from <queryAnalyzer/> tag.
     *
     * @param element element of XML document
     * @return {@link QueryAnalyzerType}
     * @throws ParserException {@link ParserException}
     */
    private QueryAnalyzerType parseQueryAnalyzerTag(Element element) throws ParserException {
        QueryAnalyzerType type = null;
        Node queryAnalyzerNode = getFirstTagOccurrence(element, QUERY_ANALYZER_TAG);
        if (queryAnalyzerNode != null) {
            type = QueryAnalyzerType.getByName(getAttributeString(queryAnalyzerNode,
                QUERY_ANALYZER_TYPE_ATTR));
            if (type == null) {
                throw new ParserException("unsupported query analyzer type.");
            }
        }

        return type;
    }

    /**
     * Parse <mongo/> tag. All information from this tag will pass to context configuration.
     * Throws exception if 'mongo' tag was not found.
     *
     * @param contextConfiguration {@link ContextConfiguration}
     * @param element              element of XML document
     * @throws ParserException {@link ParserException}
     */
    private void parseMongoTag(ContextConfiguration contextConfiguration, Element element)
        throws ParserException {
        Node mongoNode = getFirstTagOccurrence(element, MONGO_TAG);
        if (mongoNode != null) {
            String databaseHost = getAttributeString(mongoNode, MONGO_HOST_ATTR);
            int databasePort = getAttributeInt(mongoNode, MONGO_PORT_ATTR);
            assertPositive(databasePort, "wrong value for database port. database port must be gt 0");
            Validate.notBlank(databaseHost, "database host cannot be null or empty");
            contextConfiguration.setDatabaseHost(databaseHost);
            contextConfiguration.setDatabasePort(databasePort);
        }
    }

    private void parseConvertersTag(ContextConfiguration contextConfiguration, Element element) {
        Node convertersNode = getFirstTagOccurrence(element, CONVERTERS_TAG);
        if (convertersNode != null) {
            String convertersPackage = getAttributeString(convertersNode, CONVERTERS_PACKAGE_ATTR);
            contextConfiguration.setConverterPackageScan(convertersPackage);
        }
    }

    private String parseDefaultConverterTag(Element element) {
        String defaultConverter = StringUtils.EMPTY;
        Node defaultConverterNode = getFirstTagOccurrence(element, DEFAULT_CONVERTER_TAG);
        if (defaultConverterNode != null) {
            defaultConverter = getAttributeString(defaultConverterNode, CONVERTER_CLASS_ATTR);
        }
        return defaultConverter;
    }
}
