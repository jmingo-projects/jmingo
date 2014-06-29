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


import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;

import java.util.Set;

class ParserConfiguration {

    private boolean validate;

    private boolean namespaceAware;

    private Set<String> xsdSchemaPaths;

    private ParserConfiguration(Builder builder) {
        this.validate = builder.validate;
        this.namespaceAware = builder.namespaceAware;
        this.xsdSchemaPaths = builder.xsdSchemaPaths;
    }

    /**
     * Is validate.
     *
     * @return validate
     */
    public boolean isValidate() {
        return validate;
    }

    /**
     * Is namespace aware.
     *
     * @return namespace aware
     */
    public boolean isNamespaceAware() {
        return namespaceAware;
    }

    /**
     * Gets xsd-schemas paths.
     *
     * @return xsd-schemas paths
     */
    public Set<String> getXsdSchemaPaths() {
        return xsdSchemaPaths;
    }

    /**
     * ParserConfiguration builder.
     */
    public static class Builder {

        private boolean validate = false;

        private boolean namespaceAware = false;

        private Set<String> xsdSchemaPaths = ImmutableSet.of();

        /**
         * Sets validate.
         *
         * @param pValidate validate
         * @return {@link Builder}
         */
        public Builder validate(boolean pValidate) {
            this.validate = pValidate;
            return this;
        }

        /**
         * Sets namespace aware.
         *
         * @param pNamespaceAware namespace aware
         * @return {@link Builder}
         */
        public Builder namespaceAware(boolean pNamespaceAware) {
            this.namespaceAware = pNamespaceAware;
            return this;
        }

        /**
         * Sets xsd-schemas paths.
         *
         * @param pXsdSchemaPaths xsd-schemas paths
         * @return {@link Builder}
         */
        public Builder schemaSources(Set<String> pXsdSchemaPaths) {
            if (CollectionUtils.isNotEmpty(pXsdSchemaPaths)) {
                this.xsdSchemaPaths = pXsdSchemaPaths;
            }
            return this;
        }

        /**
         * Sets xsd-schema path.
         *
         * @param xsdSchemaPath xsd-schema path
         * @return {@link Builder}
         */
        public Builder schemaSources(String xsdSchemaPath) {
            if (CollectionUtils.isEmpty(xsdSchemaPaths)) {
                xsdSchemaPaths = Sets.newHashSet();
            }
            xsdSchemaPaths.add(xsdSchemaPath);
            return this;
        }

        /**
         * Builds {@link ParserConfiguration}.
         *
         * @return parser configuration {@link ParserConfiguration
         */
        public ParserConfiguration build() {
            return new ParserConfiguration(this);
        }
    }

}
