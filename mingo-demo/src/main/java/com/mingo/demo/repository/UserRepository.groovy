package com.mingo.demo.repository

import com.mingo.demo.domain.User
import com.mingo.demo.repository.api.IBaseRepository
import com.mingo.mongo.index.Index
import com.mingo.query.Criteria
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

    List<User> findByBirthday(Date startDate, Date endDate) {
        Criteria criteria = Criteria
                .where("{ 'person.birthday' : { '\$gt' : '#startDate', '\$lt' : '#endDate'} }")
                .with("startDate", startDate)
                .with("endDate", endDate);
        getMingoTemplate().find(criteria, User.class);
    }

    User findByLogin(String login) {
        Criteria criteria = Criteria.where("{'login' : '#login'}")
                .with("login", login);
        getMingoTemplate().findOne(criteria, User.class);
    }

    List<User> findByAccounts(def accounts) {
        Criteria criteria = Criteria.where("{'accounts' : {'\$in' : '#accounts'}}")
                .with("accounts", accounts);
        getMingoTemplate().find(criteria, User.class);
    }

    List<User> findByGroupName(String name) {
        Criteria criteria = Criteria.where("{'groups': {'\$elemMatch': {'name' : '#name'}}}")
                .with("name", name)
        getMingoTemplate().find(criteria, User.class);
    }

    @Override
    protected Class<User> getDocumentType() {
        return User.class;
    }
}
