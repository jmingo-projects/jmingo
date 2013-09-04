package com.mingo.parser.xml.dom;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

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
public class ParseErrorHandler implements ErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParseErrorHandler.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void warning(SAXParseException ex) throws SAXException {
        LOGGER.warn(ExceptionUtils.getMessage(ex));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(SAXParseException ex) throws SAXException {
        LOGGER.error(ExceptionUtils.getMessage(ex));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fatalError(SAXParseException ex) throws SAXException {
        throw ex;
    }
}
