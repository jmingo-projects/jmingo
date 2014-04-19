package com.mingo.parser.xml.dom;

import com.google.common.collect.Sets;
import com.mingo.exceptions.MingoParserException;
import com.mingo.parser.Parser;
import com.mingo.query.Query;
import com.mingo.query.QueryCase;
import com.mingo.query.QueryFragment;
import com.mingo.query.QuerySet;
import com.mingo.query.QueryType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.mingo.parser.xml.dom.DocumentBuilderFactoryCreator.createDocumentBuilderFactory;
import static com.mingo.parser.xml.dom.DomUtil.getAttributeBoolean;
import static com.mingo.parser.xml.dom.DomUtil.getAttributeInt;
import static com.mingo.parser.xml.dom.DomUtil.getAttributeString;
import static com.mingo.parser.xml.dom.DomUtil.getAttributes;
import static com.mingo.parser.xml.dom.DomUtil.getFirstNecessaryTagOccurrence;
import static com.mingo.parser.xml.dom.DomUtil.isNotEmpty;
import static com.mingo.query.util.QueryUtils.validate;
import static com.mingo.query.util.QueryUtils.wrap;

/**
 * Copyright 2012-2013 The Mingo Team
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
 * <p>
 * This class is implementation of {@link Parser} interface for parsing QuerySet xml.
 */
public class QuerySetParser implements Parser<QuerySet> {

    private DocumentBuilderFactory documentBuilderFactory;

    /* default handler */
    private ErrorHandler parseErrorHandler = new ParseErrorHandler();

    private static final Logger LOGGER = LoggerFactory.getLogger(QuerySetParser.class);

    private static final String ID = "id";
    private static final String CONDITION = "condition";
    private static final String QUERY_SET_TAG = "querySet";
    private static final String CONFIG_TAG = "config";
    private static final String QUERY_TAG = "query";
    // private static final String CASE_NAME = "name"; // optional
    private static final String CASE_TAG = "case";
    private static final String QUERY_FRAGMENT = "queryFragment";
    private static final String FRAGMENT = "fragment";
    private static final String FRAGMENT_REF_ATTR = "ref";
    private static final String CASE_PRIORITY_ATTR = "priority";
    private static final String COLLECTION_NAME_ATTR = "collectionName";
    private static final String CONVERTER_CLASS_ATTR = "converter-class";
    private static final String CONVERTER_METHOD_ATTR = "converter-method";
    private static final String CONVERTER_INHERIT_ATTR = "converter-inherit";
    private static final String ESCAPE_NULL_PARAMETERS = "escape-null-parameters";
    //private static final String DB_NAME = "dbName";
    private static final String TYPE_ATTR = "type";

    private static final String INVALID_QUERY_ERROR_MSG = "invalid query with id: {}. Query: {}";

    /**
     * Constructor with parameters.
     *
     * @param parserConfiguration parser configuration
     * @param parseErrorHandler   parser error handler
     * @throws ParserConfigurationException {@link ParserConfigurationException}
     */
    public QuerySetParser(ParserConfiguration parserConfiguration, ErrorHandler parseErrorHandler)
            throws ParserConfigurationException {
        this.documentBuilderFactory = createDocumentBuilderFactory(parserConfiguration);
        this.parseErrorHandler = parseErrorHandler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuerySet parse(InputStream xml) throws MingoParserException {
        LOGGER.debug("QuerySetParser:: START PARSING");
        QuerySet querySet;
        try {
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            builder.setErrorHandler(parseErrorHandler);
            Document document = builder.parse(new InputSource(xml));
            Element root = document.getDocumentElement();
            querySet = new QuerySet();
            parseConfigTag(root, querySet);
            parseQueryFragments(root, querySet);
            parseQueries(root, querySet);
        } catch (Exception e) {
            throw new MingoParserException(e);
        }
        return querySet;
    }


    /**
     * parse <config/> tag and add parsed information to querySet.
     *
     * @param element  element of XML document
     * @param querySet {@link QuerySet}
     */
    private void parseConfigTag(Element element, QuerySet querySet) throws MingoParserException {
        Validate.notNull(element, "element cannot be null");
        Validate.notNull(querySet, "query set cannot be null");
        Node configTag = getFirstNecessaryTagOccurrence(element, CONFIG_TAG);
        String collectionName = getAttributeString(configTag, COLLECTION_NAME_ATTR);
        Validate.notEmpty(collectionName, "collectionName cannot be null or empty");
        querySet.setCollectionName(collectionName);
        //querySet.setDbName(getAttributeString(configTag, DB_NAME));
    }

    private void parseQueryFragments(Element root, QuerySet querySet) {
        Set<QueryFragment> queryFragments = Collections.emptySet();
        NodeList fragmentNodeList = root.getElementsByTagName(QUERY_FRAGMENT);
        if (isNotEmpty(fragmentNodeList)) {
            queryFragments = Sets.newHashSet();
            for (int i = 0; i < fragmentNodeList.getLength(); i++) {
                queryFragments.add(parseQueryFragment(fragmentNodeList.item(i)));
            }
        }
        querySet.setQueryFragments(queryFragments);
    }

    /**
     * Parse <queryFragment/> tag.
     *
     * @param fragmentNode node
     * @return {@link QueryCase}
     */
    private QueryFragment parseQueryFragment(Node fragmentNode) {
        String fragmentId = getAttributeString(fragmentNode, ID);
        String fragmentBody = processString(fragmentNode.getTextContent());
        return new QueryFragment(fragmentId, fragmentBody);
    }

    /**
     * Parse query sequence. XML document can have one or more query tags, also no query tags can be.
     *
     * @param root element XML document
     * @return set of {@link Query} elements
     */
    private void parseQueries(Element root, QuerySet querySet) throws MingoParserException {
        NodeList queryNodeList = root.getElementsByTagName(QUERY_TAG);
        if (isNotEmpty(queryNodeList)) {
            for (int i = 0; i < queryNodeList.getLength(); i++) {
                parseQueryTag(queryNodeList.item(i), querySet);
            }
        }
    }

    /**
     * Parse <query/> node.
     *
     * @param node node
     * @return {@link Query}
     */
    private void parseQueryTag(Node node, QuerySet querySet) throws MingoParserException {
        if (node == null || !QUERY_TAG.equals(node.getNodeName())) {
            return;
        }
        Map<String, String> attributes = getAttributes(node);
        StringBuilder queryBodyBuilder = new StringBuilder();
        Query query = new Query(attributes.get(ID));
        query.setQueryType(QueryType.getByName(attributes.get(TYPE_ATTR)));
        query.setConverter(attributes.get(CONVERTER_CLASS_ATTR));
        query.setConverterMethod(attributes.get(CONVERTER_METHOD_ATTR));
        query.setEscapeNullParameters(getAttributeBoolean(node, ESCAPE_NULL_PARAMETERS));
        if (node.hasChildNodes()) {
            NodeList childList = node.getChildNodes();
            for (int i = 0; i < childList.getLength(); i++) {
                Node child = childList.item(i);
                // parse query body
                parseBody(queryBodyBuilder, child, querySet);
                // parse <case> tag
                parseCaseTag(child, query, querySet);
            }
        }
        query.setBody(queryBodyBuilder.toString());

        if (!validate(wrap(query.getBody()))) {
            throw new MingoParserException(MessageFormatter.format(INVALID_QUERY_ERROR_MSG,
                    query.getId(), query).getMessage());
        }
        querySet.addQuery(query);
    }


    /**
     * Parse <case/> tag.
     *
     * @param node node
     * @return {@link QueryCase}
     */
    private void parseCaseTag(Node node, Query query, QuerySet querySet) throws MingoParserException {
        if (node == null || !CASE_TAG.equals(node.getNodeName())) {
            return;
        }
        QueryCase queryCase = new QueryCase();
        queryCase.setId(getAttributeString(node, ID));
        queryCase.setPriority(getAttributeInt(node, CASE_PRIORITY_ATTR));
        queryCase.setCondition(getAttributeString(node, CONDITION));
        QueryType type = QueryType.getByName(getAttributeString(node, TYPE_ATTR));
        queryCase.setQueryType(type != null ? type : query.getQueryType());
        boolean inheritConverter = getAttributeBoolean(node, CONVERTER_INHERIT_ATTR);
        if (!inheritConverter) {
            queryCase.setConverter(getAttributeString(node, CONVERTER_CLASS_ATTR));
            queryCase.setConverterMethod(getAttributeString(node, CONVERTER_METHOD_ATTR));
        } else {
            queryCase.setConverterMethod(query.getConverterMethod());
            queryCase.setConverter(query.getConverter());
        }

        // parse case body
        if (node.hasChildNodes()) {
            StringBuilder caseBodyBuilder = new StringBuilder();
            NodeList childList = node.getChildNodes();
            for (int i = 0; i < childList.getLength(); i++) {
                Node child = childList.item(i);
                parseBody(caseBodyBuilder, child, querySet);
            }

            queryCase.setBody(StringUtils.strip(caseBodyBuilder.toString()));
            if (!validate(wrap(queryCase.getBody()))) {
                throw new MingoParserException(MessageFormatter.format(INVALID_QUERY_ERROR_MSG,
                        queryCase.getId(), queryCase).getMessage());
            }
        }
        query.addQueryCase(queryCase);
    }

    private void parseBody(StringBuilder builder, Node node, QuerySet querySet) throws MingoParserException {
        if (node == null) {
            return;
        }
        if (node.getNodeType() == Node.TEXT_NODE) {
            String body = node.getNodeValue();
            if (StringUtils.isNotBlank(body)) {
                builder.append(processString(body));
            }
        } else if (FRAGMENT.equals(node.getNodeName())) {
            applyFragment(builder, getFragmentRef(node), querySet);
        }
    }

    private String getFragmentRef(Node child) {
        return getAttributeString(child, FRAGMENT_REF_ATTR);
    }

    private void applyFragment(StringBuilder body, String fRef, QuerySet querySet) throws MingoParserException {
        QueryFragment queryFragment = querySet.getQueryFragmentById(fRef);
        if (queryFragment != null) {
            body.append(processString(queryFragment.getBody()));
        } else {
            throw new MingoParserException("not found query fragment for ref: " + fRef);
        }
    }

    private String processString(String source) {
        if (StringUtils.isNotBlank(source)) {
            source = StringUtils.strip(source);
            source = source.replace("(?m)^[ \t]*\r?\n", "");
        }
        return source;
    }

}