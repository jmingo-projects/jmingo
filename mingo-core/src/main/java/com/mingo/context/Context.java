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
 */
package com.mingo.context;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mingo.MingoTemplate;
import com.mingo.benchmark.BenchmarkService;
import com.mingo.config.ContextConfiguration;
import com.mingo.exceptions.ContextInitializationException;
import com.mingo.executor.MongoQueryExecutor;
import com.mingo.mapping.convert.ConverterService;
import com.mingo.mongo.MongoDBFactory;
import com.mingo.parser.Parser;
import com.mingo.parser.xml.dom.ParserFactory;
import com.mingo.query.ELEngineType;
import com.mingo.query.QueryExecutorType;
import com.mingo.query.QueryManager;
import com.mingo.query.el.ELEngine;
import com.mingo.query.el.ELEngineFactory;
import com.mingo.util.FileUtils;
import com.mongodb.Mongo;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.mingo.parser.xml.dom.ParserFactory.ParseComponent.CONTEXT;


public class Context {

    private ELEngine queryAnalyzer;
    private ConverterService converterService;
    private QueryManager queryManager;

    private ContextConfiguration config;
    private MongoDBFactory mongoDBFactory;
    private MongoQueryExecutor mongoQueryExecutor;
    private MingoTemplate mingoTemplate;
    private List<BenchmarkService> benchmarkServices = Lists.newCopyOnWriteArrayList();

    private static final String CONTEXT_PATH_ERROR = "path to context configuration cannot be empty or null";

    private static final Parser<ContextConfiguration> CONTEXT_CONFIGURATION_PARSER =
            ParserFactory.createParser(CONTEXT);

    private static ThreadLocal<Context> currentContext = new InheritableThreadLocal<>();

    @Deprecated
    public Context(String contextPath) {
        initialize(contextPath, null);
        currentContext.set(this);
    }

    @Deprecated
    public Context(String contextPath, Mongo mongo) {
        initialize(contextPath, mongo);
        currentContext.set(this);
    }

    public static Context create(String contextPath) {
        Context context = new Context(contextPath);
        currentContext.set(context);
        return context;
    }

    public static Context create(String contextPath, Mongo mongo) {
        Context context = new Context(contextPath, mongo);
        currentContext.set(context);
        return context;
    }

    public void addBenchmarkService(BenchmarkService service) {
        benchmarkServices.add(service);
    }

    public static Context getCurrent() {
        return currentContext.get();
    }

    public ELEngine getQueryAnalyzer() {
        return queryAnalyzer;
    }

    public ConverterService getConverterService() {
        return converterService;
    }

    public QueryManager getQueryManager() {
        return queryManager;
    }

    public MongoDBFactory getMongoDBFactory() {
        return mongoDBFactory;
    }

    public MongoQueryExecutor getMongoQueryExecutor() {
        return mongoQueryExecutor;
    }

    public MingoTemplate getMingoTemplate() {
        return mingoTemplate;
    }

    public ContextConfiguration getConfig() {
        return config;
    }

    public ELEngineType getQueryAnalyzerType() {
        return config.getQueryAnalyzerType();
    }

    public QueryExecutorType getQueryExecutorType() {
        return config.getQueryExecutorType();
    }

    public List<BenchmarkService> getBenchmarkServices() {
        return ImmutableList.copyOf(benchmarkServices);
    }

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
            queryAnalyzer = ELEngineFactory.create(config.getQueryAnalyzerType());
            mongoQueryExecutor = new MongoQueryExecutor(mongoDBFactory, queryManager, queryAnalyzer, converterService);
            mingoTemplate = new MingoTemplate(mongoQueryExecutor, mongoDBFactory, converterService);
        } catch (Throwable e) {
            throw new ContextInitializationException(e);
        }

    }

    public void shutdown() {
        queryManager.shutdown();
    }

    /**
     * Parses xml file which managing by schema: context.xsd and creates {@link ContextConfiguration} object.
     */
    private ContextConfiguration loadContextConfiguration(String contextPath) {
        return CONTEXT_CONFIGURATION_PARSER.parse(FileUtils.getAbsolutePath(contextPath));
    }
}
