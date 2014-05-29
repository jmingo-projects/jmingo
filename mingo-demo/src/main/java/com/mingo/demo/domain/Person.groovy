package com.mingo.demo.domain

public class Person {
    String firstName;
    String secondName;
    Date birth;
    int age;
    String email;


    @Override
    public String toString() {
        return """\
                Person{
                    firstName='$firstName',
                    secondName='$secondName',
                    birth=$birth,
                    age=$age,
                    email='$email'
                }"""
    }
}
