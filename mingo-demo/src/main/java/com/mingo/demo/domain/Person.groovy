package com.mingo.demo.domain

public class Person {
    String firstName;
    String secondName;
    Date birthday;
    int age;
    String email;


    @Override
    public String toString() {
        return """\
                Person{
                    firstName='$firstName',
                    secondName='$secondName',
                    birthday=$birthday,
                    age=$age,
                    email='$email'
                }"""
    }
}
