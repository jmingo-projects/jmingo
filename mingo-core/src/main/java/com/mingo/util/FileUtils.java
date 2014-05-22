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

import com.google.common.hash.Hashing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    /**
     * Gets absolute path to file for specified original path. Handles relative and absolute.
     *
     * @param original the original path to file
     * @return the absolute path
     */
    public static Path getAbsolutePath(String original) {
        Path path;
        try {
            path = Paths.get(original);
            if (!path.isAbsolute()) {
                URL url = FileUtils.class.getResource(original);
                if (url == null) {
                    throw new FileNotFoundException("the path: " + original + " is relative but the file isn't in classpath");
                }
                URI uri = url.toURI();
                path = Paths.get(uri);
            }
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
        return path;
    }

    public static String checksum(File file) {
        String checksum;
        try {
            checksum = com.google.common.io.Files.hash(file, Hashing.md5()).toString();
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
        return checksum;
    }

}
