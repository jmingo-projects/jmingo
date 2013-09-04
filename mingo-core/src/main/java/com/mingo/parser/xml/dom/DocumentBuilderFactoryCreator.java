package com.mingo.parser.xml.dom;

import static com.mingo.parser.xml.ParserConstants.DYNAMIC_VALIDATION;
import static com.mingo.parser.xml.ParserConstants.SCHEMA_LANGUAGE;
import com.google.common.collect.ImmutableList;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.xml.sax.SAXException;

import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

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
public class DocumentBuilderFactoryCreator {

    /* schema factory*/
    private static final SchemaFactory SCHEMA_FACTORY = SchemaFactory.newInstance(SCHEMA_LANGUAGE);

    /**
     * Creates DocumentBuilderFactory.
     *
     * @param parserConfiguration {@link ParserConfiguration}
     * @return DocumentBuilderFactory a factory API that enables applications to obtain a
     *         parser that produces DOM object trees from XML documents
     * @throws ParserConfigurationException {@link ParserConfigurationException}
     */
    public static DocumentBuilderFactory createDocumentBuilderFactory(ParserConfiguration parserConfiguration)
        throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setValidating(parserConfiguration.isValidate());
        documentBuilderFactory.setNamespaceAware(parserConfiguration.isNamespaceAware());
        documentBuilderFactory.setFeature(DYNAMIC_VALIDATION, true);
        List<Source> sourceList = createSchemaSources(parserConfiguration.getXsdSchemaPaths());
        if (CollectionUtils.isNotEmpty(sourceList)) {
            documentBuilderFactory.setSchema(createSchema(sourceList));
        }
        return documentBuilderFactory;
    }

    /**
     * Create list of {@link Source} objects.
     *
     * @param xsdSchemaPaths paths to some schemas
     * @return list of {@link Source}
     */
    private static List<Source> createSchemaSources(Set<String> xsdSchemaPaths) {
        if (CollectionUtils.isEmpty(xsdSchemaPaths)) {
            return ImmutableList.of();
        }

        ImmutableList.Builder sourceBuilder = ImmutableList.<Source>builder();
        for (String xsdSchemaPath : xsdSchemaPaths) {
            sourceBuilder.add(createSchemaSource(xsdSchemaPath));
        }
        return sourceBuilder.build();
    }

    /**
     * Creates schema source.
     *
     * @param xsdSchemaPath path to schema location
     * @return {@link Source}
     */
    private static Source createSchemaSource(String xsdSchemaPath) {
        return new StreamSource(ParserFactory.class.getResourceAsStream(xsdSchemaPath));
    }

    /**
     * Creates schema from list of {@link Source} objects.
     *
     * @param sources list of {@link Source}
     * @return immutable in-memory representation of grammar
     * @throws ParserConfigurationException {@link ParserConfigurationException}
     */
    private static Schema createSchema(List<Source> sources) throws ParserConfigurationException {
        Schema schema;
        try {
            schema = SCHEMA_FACTORY.newSchema(sources.toArray(new Source[0]));
        } catch (SAXException e) {
            throw new ParserConfigurationException(ExceptionUtils.getMessage(e));
        }
        return schema;
    }
}
