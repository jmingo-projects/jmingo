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
package org.jmingo.context;

import com.mongodb.Mongo;
import org.apache.commons.lang3.StringUtils;
import org.jmingo.MingoTemplate;
import org.jmingo.config.ContextConfiguration;
import org.jmingo.document.id.generator.factory.DefaultIdGeneratorFactory;
import org.jmingo.document.id.generator.factory.IdGeneratorFactory;
import org.jmingo.el.ELEngineFactory;
import org.jmingo.el.api.ELEngine;
import org.jmingo.exceptions.ContextInitializationException;
import org.jmingo.exceptions.ShutdownException;
import org.jmingo.executor.MongoQueryExecutor;
import org.jmingo.executor.QueryExecutor;
import org.jmingo.mapping.convert.ConverterService;
import org.jmingo.mongo.MongoDBFactory;
import org.jmingo.parser.Parser;
import org.jmingo.parser.xml.dom.ParserFactory;
import org.jmingo.query.QueryManager;
import org.jmingo.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jmingo.parser.xml.dom.ParserFactory.ParseComponent.CONTEXT;

/**
 * Mingo context includes all components and also combines them together.
 * All necessary components can be taken from context.
 * Context is loaded from xml file that's managed by context.xsd schema.
 */
public class Context {

    private ELEngine elEngine;
    private ConverterService converterService;
    private QueryManager queryManager;

    private ContextConfiguration config;
    private IdGeneratorFactory idGeneratorFactory = new DefaultIdGeneratorFactory();
    private MongoDBFactory mongoDBFactory;
    private QueryExecutor queryExecutor;
    private MingoTemplate mingoTemplate;

    private static final String CONTEXT_PATH_ERROR = "path to context configuration cannot be empty or null";

    private static final Parser<ContextConfiguration> CONTEXT_CONFIGURATION_PARSER =
            ParserFactory.createParser(CONTEXT);

    private static ThreadLocal<Context> currentContext = new InheritableThreadLocal<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(Context.class);

    /**
     * Loads context from file. Path can be relative(file should be in application classpath) or absolute.
     *
     * @param contextPath the path to the xml file with context configuration
     * @return loaded and configured context
     */
    public static Context create(String contextPath) {
        return create(contextPath, null);
    }

    /**
     * Loads context from file. Path can be relative(file should be in application classpath) or absolute.
     *
     * @param contextPath
     * @param mongo             the {@link Mongo} instance. If mongo configuration was defined in mingo context then will be
     *                          ignored and the given mongo will be used instead. It can be useful if you want to configure
     *                          your own mongo instance and don't use mingo for it.
     * @return loaded and configured context
     */
    public static Context create(String contextPath, Mongo mongo) {
        Context context = new Context();
        context.initialize(contextPath, mongo);
        currentContext.set(context);
        return context;
    }

    /**
     * Return current context instance.
     *
     * @return current context instance or null if this method is being called from another/no-child thread.
     */
    public static Context getCurrent() {
        return currentContext.get();
    }

    /**
     * Gets id generator factory {@link IdGeneratorFactory}.
     * Factory to create generators {@link org.jmingo.document.id.generator.IdGenerator}.
     * The factory has the opportunity to register custom generators for specific strategies.
     * Context doesn't have ability to set own implementation of {@link IdGeneratorFactory}.
     *
     * @return id generator factory
     */
    public IdGeneratorFactory getIdGeneratorFactory() {
        return idGeneratorFactory;
    }

    /**
     * Gets current implementation of {@link ELEngine} interface. A EL engine allows evaluate EL expressions.
     * Basically this component is required by another components and isn't of interest as separate component for use.
     *
     * @return EL engine
     */
    public ELEngine getElEngine() {
        return elEngine;
    }

    /**
     * Gets converter service. Converter service is used to find an applicable converter by specific type.
     * Also responsible to create converters.
     *
     * @return converter service
     */
    public ConverterService getConverterService() {
        return converterService;
    }

    /**
     * Gets query manager. Contains all information about queries and provides methods to get them.
     *
     * @return query manager
     */
    public QueryManager getQueryManager() {
        return queryManager;
    }

    /**
     * Gets mongoDB factory.
     * Factory creates and configures {@link com.mongodb.Mongo} instances  based on mongo configuration.
     *
     * @return mongoDB factory
     */
    public MongoDBFactory getMongoDBFactory() {
        return mongoDBFactory;
    }

    /**
     * Gets query executor.
     * Executor is used to perform queries from parsed and loaded query sets.
     *
     * @return query executor
     */
    public QueryExecutor getQueryExecutor() {
        return queryExecutor;
    }

    /**
     * Gets mingo template. Template contains methods to perform CRUD
     * operations and also user queries that are defined in xml files.
     *
     * @return mingo template
     */
    public MingoTemplate getMingoTemplate() {
        return mingoTemplate;
    }

    /**
     * Closes context. Notifies all component what context is closing and will be destroyed soon.
     * How long this method will execute depends on time of actions that will be performed be other components:
     * for instance if one of the components will save data in storage then calling code will wait until
     * the operation is completed, thus try to avoid complicated logic in shutdown methods.
     *
     * @throws ShutdownException if any errors occur
     */
    public void shutdown() throws ShutdownException {
        try {
            queryManager.shutdown();
        } catch (RuntimeException e) {
            throw new ShutdownException(e);
        }
    }

    /**
     * Creates and initialize mingo context with all necessary components based on {@link ContextConfiguration}.
     *
     * @param contextPath the path to the xml file with context configuration
     * @param mongo       the mongo instance , can be null
     */
    private void initialize(String contextPath, Mongo mongo) {
        Context context;
        try {
            if (StringUtils.isBlank(contextPath)) {
                throw new IllegalArgumentException(CONTEXT_PATH_ERROR);
            }
            // load context configuration from xml file
            config = loadContextConfiguration(contextPath);
            converterService = new ConverterService(config.getConverterPackageScan(), config.getDefaultConverter());
            queryManager = new QueryManager(config.getQuerySetConfiguration().getQuerySets());
            mongoDBFactory = mongo != null ? new MongoDBFactory(config.getMongoConfig(), mongo)
                    : new MongoDBFactory(config.getMongoConfig());
            createElEngine();
            queryExecutor = new MongoQueryExecutor(mongoDBFactory, queryManager, elEngine, converterService);
            mingoTemplate = new MingoTemplate(queryExecutor, mongoDBFactory, converterService, idGeneratorFactory);
        } catch (Throwable e) {
            throw new ContextInitializationException(e);
        }

    }

    /**
     * Parses xml file which managing by schema: context.xsd and creates {@link ContextConfiguration} object.
     */
    private ContextConfiguration loadContextConfiguration(String contextPath) {
        return CONTEXT_CONFIGURATION_PARSER.parse(FileUtils.getAbsolutePath(contextPath));
    }

    private void createElEngine() {
        elEngine = ELEngineFactory.getElEngine();
        if (elEngine != null) {
            LOGGER.info("detected and loaded EL engine implementation: '{}'", elEngine);
        }
    }
}
