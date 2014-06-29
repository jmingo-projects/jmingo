package org.jmingo.demo.domain

import org.jmingo.document.annotation.Document
import org.jmingo.document.annotation.GeneratedValue
import org.jmingo.document.annotation.Id
import org.jmingo.document.id.generator.IdGeneratorStrategy

@Document
class BaseDocument {

    @Id
    @GeneratedValue(strategy = IdGeneratorStrategy.UUID)
    String id;


    @Override
    public String toString() {
        return "BaseDocument{id='$id'}";
    }
}
