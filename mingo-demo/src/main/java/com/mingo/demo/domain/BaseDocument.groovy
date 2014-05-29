package com.mingo.demo.domain

import com.mingo.document.annotation.Document
import com.mingo.document.annotation.GeneratedValue
import com.mingo.document.annotation.Id
import com.mingo.document.id.generator.IdGeneratorStrategy

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
