package com.mingo.demo.domain

import com.mingo.document.annotation.Document;

@Document(collectionName = "userCollection")
class User extends BaseDocument {
    String login;
    String password;
    Person person;


    @Override
    public String toString() {
        return """\
                User{
                    id = '$id'
                    login='$login',
                    password='$password',
                    person=$person
                }"""
    }
}
