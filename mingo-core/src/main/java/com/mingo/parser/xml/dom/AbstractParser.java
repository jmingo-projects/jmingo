package com.mingo.parser.xml.dom;


import com.google.common.base.Throwables;
import com.mingo.exceptions.MingoParserException;
import com.mingo.parser.Parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public abstract class AbstractParser<T> implements Parser<T> {

    @Override
    public T parse(Path path) throws MingoParserException {
        try (InputStream is = new FileInputStream(path.toFile())) {
            return parse(is);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }
}
