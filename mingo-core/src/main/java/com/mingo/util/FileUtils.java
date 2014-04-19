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
package com.mingo.util;

import com.google.common.base.Throwables;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by dmgcodevil on 19.04.2014.
 */
public class FileUtils {

    /**
     * Try to get stream of file by specified path.
     * If file doesn't exist in classpath then try to load from other place.
     *
     * @param filePath path to file
     * @return InputStream {@link java.io.InputStream}
     * @throws java.lang.RuntimeException
     */
    public static InputStream getAsInputStream(String filePath) {
        InputStream is = FileUtils.class.getResourceAsStream(filePath);
        if (is != null) {
            return is;
        }
        try {
            return new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            throw Throwables.propagate(e);
        }
    }
}
