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
package com.mingo.benchmark;


import com.mingo.context.Context;

/**
 * This interface provides necessary methods that are allowed to
 * react as needed when some code was profiled, for instance save metrics in a file or display it as graphic.
 */
public interface BenchmarkService {

    /**
     * This method invokes immediately after {@link com.mingo.context.Context#addBenchmarkService(BenchmarkService)}
     * methods invocation.
     *
     * @param context the mingo context
     */
    void init(Context context);

    /**
     * Mingo invokes this method each time when some code was profiled,
     * for instance it can be metering method execution time and etc.
     *
     * @param metric
     */
    void submit(Metrics metric);

    /**
     * This method is called when {@link Context#shutdown()} is invoked.
     */
    void destroy();
}
