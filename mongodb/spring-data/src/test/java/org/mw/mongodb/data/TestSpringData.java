package org.mw.mongodb.data;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mw.mongodb.data.model.User;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringMongoConfig.class})
public class TestSpringData {

    @Resource
    private MongoOperations mongoTemplate;

    @Test
    public void test() {
        mongoTemplate.dropCollection("users");

        final User user = new User("amw061", "password");
        assertNull(user.getId());

        mongoTemplate.save(user);
        assertEquals("User(username=amw061, password=password)", user.toString());
        assertNotNull(user.getId());

        final Query searchUserQuery = new Query(Criteria.where("username").is("amw061"));
        final User savedUser = mongoTemplate.findOne(searchUserQuery, User.class);
        assertEquals("User(username=amw061, password=password)", savedUser.toString());
        assertNotNull(savedUser.getId());
        assertEquals(user.getId(), savedUser.getId());

        mongoTemplate.updateFirst(searchUserQuery, Update.update("password", "new password"), User.class);
        final User updatedUser = mongoTemplate.findOne(searchUserQuery, User.class);
        assertEquals("User(username=amw061, password=new password)", updatedUser.toString());
        assertNotNull(updatedUser.getId());
        assertEquals(user.getId(), updatedUser.getId());

        final List<User> listUser1 = mongoTemplate.findAll(User.class);
        assertEquals(1, listUser1.size());

        mongoTemplate.remove(searchUserQuery, User.class);
        final List<User> listUser2 = mongoTemplate.findAll(User.class);
        assertTrue(listUser2.isEmpty());
    }
}
