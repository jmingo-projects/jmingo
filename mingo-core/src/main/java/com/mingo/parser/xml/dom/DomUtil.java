package com.mingo.parser.xml.dom;

import com.google.common.collect.ImmutableMap;
import com.mingo.exceptions.ParserException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Map;

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
 */
public class DomUtil {

    private static final int FIRST_ELEMENT = 0;

    /**
     * Transform node attributes to map.
     *
     * @param node {@link Node}
     * @return map : key - attribute name; value - attribute value
     */
    public static Map<String, String> getAttributes(Node node) {
        Map<String, String> attributes = ImmutableMap.of();
        if (node.hasAttributes()) {
            ImmutableMap.Builder builder = ImmutableMap.builder();
            // get attributes names and values
            NamedNodeMap nodeMap = node.getAttributes();
            for (int i = 0; i < nodeMap.getLength(); i++) {
                Node currentNode = nodeMap.item(i);
                builder.put(currentNode.getNodeName(), currentNode.getNodeValue());
            }
            attributes = builder.build();
        }
        return attributes;
    }

    /**
     * Gets first tag occurrence.
     *
     * @param element element interface represents an element in an HTML or XML document
     * @param tagName tag name
     * @return found node
     */
    public static Node getFirstTagOccurrence(Element element, String tagName) {
        Node firstNode = null;
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {
            firstNode = nodeList.item(FIRST_ELEMENT);
        }
        return firstNode;
    }

    /**
     * Gets first necessary tag occurrence.
     * If no tags with specified name in xml then throw exception.
     *
     * @param element interface represents an element in an HTML or XML document.
     * @param tagName tag name
     * @return found node
     * @throws ParserException {@link ParserException}
     */
    public static Node getFirstNecessaryTagOccurrence(Element element, String tagName) throws ParserException {
        Node node = getFirstTagOccurrence(element, tagName);
        if (node == null) {
            throw new ParserException("not found necessary <" + tagName + "/> tag");
        }
        return node;
    }

    /**
     * Gets attribute by name.
     *
     * @param node          {@link Node}
     * @param attributeName attribute name
     * @return attribute value with type 'string'
     */
    public static String getAttributeString(Node node, String attributeName) {
        Validate.notNull(node, "getAttributeString::node cannot be null");
        String value = StringUtils.EMPTY;
        if (node.hasAttributes()) {
            value = getAttributes(node).get(attributeName);
        }
        return value;
    }

    /**
     * Gets attribute by name.
     *
     * @param node          {@link Node}
     * @param attributeName attribute name
     * @return attribute value with type 'int'
     */
    public static int getAttributeInt(Node node, String attributeName) {
        Validate.notNull(node, "getAttributeInt::node cannot be null");
        String value = getAttributeString(node, attributeName);
        return Integer.parseInt(value);
    }

    /**
     * Gets attribute by name.
     *
     * @param node          {@link Node}
     * @param attributeName attribute name
     * @return attribute value with type 'boolean'
     */
    public static boolean getAttributeBoolean(Node node, String attributeName) {
        Validate.notNull(node, "getAttributeBoolean::node cannot be null");
        String value = getAttributeString(node, attributeName);
        return Boolean.valueOf(value);
    }

    /**
     * Checks what value is positive.
     *
     * @param val what
     * @param msg error message
     */
    public static void assertPositive(int val, String msg) {
        if (val < 0) {
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Null-safe check if the specified <code>nodeList</code> is not empty.
     *
     * @param nodeList {@link NodeList}
     * @return true if non-null and non-empty otherwise returns false
     */
    public static boolean isNotEmpty(NodeList nodeList) {
        return nodeList != null && nodeList.getLength() > 0;
    }

}

