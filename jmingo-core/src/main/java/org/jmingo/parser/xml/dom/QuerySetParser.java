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
package org.jmingo.parser.xml.dom;

import com.google.common.collect.Sets;
import org.jmingo.exceptions.MingoParserException;
import org.jmingo.parser.Parser;
import org.jmingo.query.ConditionElement;
import org.jmingo.query.IfElseConditionalConstruct;
import org.jmingo.query.Query;
import org.jmingo.query.QueryElement;
import org.jmingo.query.QueryFragment;
import org.jmingo.query.QuerySet;
import org.jmingo.query.QueryType;
import org.jmingo.util.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jmingo.util.QueryUtils;
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
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

import static org.jmingo.parser.xml.dom.DocumentBuilderFactoryCreator.createDocumentBuilderFactory;
import static org.jmingo.parser.xml.dom.util.DomUtil.getAttributeBoolean;
import static org.jmingo.parser.xml.dom.util.DomUtil.getAttributeString;
import static org.jmingo.parser.xml.dom.util.DomUtil.getChildNodes;
import static org.jmingo.parser.xml.dom.util.DomUtil.getFirstNecessaryTagOccurrence;
import static org.jmingo.parser.xml.dom.util.DomUtil.isNotEmpty;
import static org.jmingo.util.StringUtils.removeLineBreaks;
import static org.apache.commons.lang3.StringUtils.trim;

/**
 * This class is implementation of {@link Parser} interface for parsing QuerySet xml.
 */
public class QuerySetParser implements Parser<QuerySet> {

    private DocumentBuilderFactory documentBuilderFactory;

    /* default handler */
    private ErrorHandler parseErrorHandler = new ParseErrorHandler();

    private static final Logger LOGGER = LoggerFactory.getLogger(QuerySetParser.class);

    private static final String ID = "id";
    private static final String CONDITION = "condition";
    private static final String CONFIG_TAG = "config";
    private static final String QUERY_TAG = "query";
    private static final String QUERY_FRAGMENT = "queryFragment";
    private static final String FRAGMENT_TAG = "fragment";
    private static final String FRAGMENT_REF_ATTR = "ref";
    private static final String COLLECTION_NAME_ATTR = "collectionName";
    private static final String CONVERTER_CLASS_ATTR = "converter-class";
    private static final String CONVERTER_METHOD_ATTR = "converter-method";
    private static final String ESCAPE_NULL_PARAMETERS = "escape-null-parameters";
    private static final String TYPE_ATTR = "type";
    private static final String IF_TAG = "if";
    private static final String ELSE_IF_TAG = "elseIf";
    private static final String ELSE_TAG = "else";

    // messages
    private static final String INVALID_QUERY_ERROR_MSG = "invalid query with id: {}. Query: {}";
    private static final String CONVERTER_METHOD_DEFINITION_ERROR = "if converter class was defined then " +
            "converter method must be defined too. see query set : '{}', query: '{}'";

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
    public QuerySet parse(Path path) throws MingoParserException {
        LOGGER.debug("parse query set: {}", path);
        QuerySet querySet;
        try (InputStream is = new FileInputStream(path.toFile())) {
            String checksum = FileUtils.checksum(path.toFile());
            querySet = new QuerySet();
            querySet.setChecksum(checksum);
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            builder.setErrorHandler(parseErrorHandler);
            Document document = builder.parse(new InputSource(is));
            Element root = document.getDocumentElement();
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
     */
    private QueryFragment parseQueryFragment(Node fragmentNode) {
        String fragmentId = getAttributeString(fragmentNode, ID);
        String fragmentBody = removeLineBreaks(trim(fragmentNode.getTextContent()));
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
     * Parse <query/> node and add query in querySet.
     *
     * @param node the query node
     */
    private void parseQueryTag(Node node, QuerySet querySet) throws MingoParserException {
        if (node == null || !QUERY_TAG.equals(node.getNodeName())) {
            return;
        }
        Query.Builder builder = Query.builder();
        String queryId = getAttributeString(node, ID);
        builder.id(queryId);
        builder.collectionName(querySet.getCollectionName());
        builder.queryType(QueryType.getByName(getAttributeString(node, TYPE_ATTR, QueryType.PLAIN.getName())));
        String converterClass = getAttributeString(node, CONVERTER_CLASS_ATTR);
        String converterMethod = getAttributeString(node, CONVERTER_METHOD_ATTR);
        validateConverter(converterClass, converterMethod, queryId, querySet);
        builder.converterClass(converterClass);
        builder.converterMethod(converterMethod);
        builder.escapeNullParameters(getAttributeBoolean(node, ESCAPE_NULL_PARAMETERS));

        getChildNodes(node).forEach(child -> {
            if (child.getNodeType() == Node.TEXT_NODE) {
                String text = parseTextNode(child);
                if (StringUtils.isNotBlank(text)) {
                    builder.addTextElement(text);
                }
            }
            // parse <fragment> tag
            if (FRAGMENT_TAG.equals(child.getNodeName())) {
                QueryFragment queryFragment = querySet.getQueryFragmentById(getFragmentRef(child));
                if (queryFragment != null) {
                    builder.addTextElement(queryFragment.getBody());
                }
            }
            // parse <if> tag
            if (IF_TAG.equals(child.getNodeName())) {
                QueryElement ifStatement = parseIfElseTag(child);
                builder.add(ifStatement);
            }
        });
        Query query = builder.build();
        if (!QueryUtils.isValidJSON(query.getText())) {
            throw new MingoParserException(MessageFormatter.format(INVALID_QUERY_ERROR_MSG,
                    query.getId(), query).getMessage());
        }
        querySet.addQuery(query);
    }

    /**
     * Parse <case/> tag.
     *
     * @param node node
     */
    private IfElseConditionalConstruct parseIfElseTag(Node node) throws MingoParserException {
        IfElseConditionalConstruct.Builder builder = IfElseConditionalConstruct.builder();
        String ifCondition = getAttributeString(node, CONDITION);
        StringBuilder clauseBuilder = new StringBuilder();
        getChildNodes(node).forEach(child -> {
            if (child.getNodeType() == Node.TEXT_NODE) {
                clauseBuilder.append(parseTextNode(child));
            }
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (ELSE_IF_TAG.equals(child.getNodeName())) {
                    builder.elseIf(parseElseIfTag(child));
                }
                if (ELSE_TAG.equals(child.getNodeName())) {
                    builder.withElse(parseElseTag(child));
                }
            }
        });

        builder.withIf(ifCondition, clauseBuilder.toString());
        return builder.build();
    }

    private ConditionElement parseElseIfTag(Node elseIfNode) {
        String condition = getAttributeString(elseIfNode, CONDITION);
        final StringBuilder clause = new StringBuilder();
        getChildNodes(elseIfNode).forEach(child -> {
            if (child.getNodeType() == Node.TEXT_NODE) {
                clause.append(parseTextNode(child));
            }
        });
        return new ConditionElement(clause.toString(), condition);
    }

    private String parseElseTag(Node elseIfNode) {
        final StringBuilder clause = new StringBuilder();
        getChildNodes(elseIfNode).forEach(child -> {
            if (child.getNodeType() == Node.TEXT_NODE) {
                clause.append(parseTextNode(child));
            }
        });
        return clause.toString();
    }

    private String parseTextNode(Node node) throws MingoParserException {
        String body = node.getNodeValue();
        return removeLineBreaks(trim(body));
    }

    private String getFragmentRef(Node child) {
        return getAttributeString(child, FRAGMENT_REF_ATTR);
    }

    /**
     * Checks that the next assumption is correct: if custom converter is specified for the query
     * then convert method should be specified as well.
     *
     * @param converterClass  the converter class
     * @param converterMethod the converter method
     * @param queryId         the qeruy id
     * @param querySet        the query set to build detail information for exception message
     * @throws IllegalArgumentException if current converter configuration isn't correct
     */
    public static void validateConverter(String converterClass, String converterMethod, String queryId, QuerySet querySet) {
        if (StringUtils.isNotBlank(converterClass) && StringUtils.isBlank(converterMethod)) {
            throw new IllegalArgumentException(MessageFormatter.arrayFormat(CONVERTER_METHOD_DEFINITION_ERROR,
                    new Object[]{querySet.getPath(), queryId}).getMessage());
        }
    }
}