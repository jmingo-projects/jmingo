package com.mingo.demo.repository

import com.mingo.demo.domain.User
import com.mingo.demo.repository.api.IBaseRepository
import com.mingo.mongo.index.Index
import org.springframework.stereotype.Repository

import javax.annotation.PostConstruct


@Repository
class UserRepository extends AbstractRepository<User> implements IBaseRepository<String, User> {

    private indexes;

    @PostConstruct
    public void init() {
        indexes = [
                Index.builder().name("user_login_index").key("login").unique(true).build()
        ]
        createIndexes()
    }

    private def createIndexes() {
        indexes.each { getMingoTemplate().ensureIndex(User.class, it) }
    }

    @Override
    protected Class<User> getDocumentType() {
        return User.class;
    }
}
