package org.jmingo.demo.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty


class Author {
    String name;
    String email;


    @JsonCreator
    Author(@JsonProperty("name") String name, @JsonProperty("email") String email) {
        this.name = name
        this.email = email
    }
}
