/**
 * Copyright 2013-2014 The JMingo Team
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
package org.jmingo.benchmark;


/**
 * This interface provides necessary methods that are allowed to
 * react as needed when some code was profiled, for instance save metrics in a file or display it as graphic.
 */
public interface BenchmarkService {

    /**
     * Third-party service invokes this method to prepare current benchmark service for use.
     */
    void init();

    /**
     * Third-party service invokes this method each time when some code was profiled,
     * for instance it can be measuring execution time and etc.
     * Try to avoid long operations in this method but if needed to perform long operations such as saving in database
     * or sending via network then perform this operations in separate thread.
     *
     * @param metrics the metrics
     */
    void submit(Metrics metrics);

    /**
     * Destroys current service. Can be used to close connections, release resources and etc.
     * Try to avoid long operations in this method.
     */
    void destroy();
}
