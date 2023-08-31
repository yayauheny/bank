package by.yayauheny.dao;

import by.yayauheny.entity.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static by.yayauheny.util.TestObjectsUtil.IVAN_USER;
import static by.yayauheny.util.TestObjectsUtil.TEST_USER;
import static by.yayauheny.util.TestObjectsUtil.USERS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDaoTest {
    private final UserDao userDao = UserDao.getInstance();

    @Test
    void shouldReturnExistingUser() {
        Optional<User> actualResult = userDao.findById(IVAN_USER.getId());

        assertThat(actualResult).isPresent();
        assertEquals(IVAN_USER, actualResult.get());
    }

    @Test
    void shouldReturnAllExistingUsers() {
        List<User> actualResult = userDao.findAll();

        assertThat(actualResult).containsExactlyInAnyOrderElementsOf(USERS);
    }

    @Test
    void shouldNotExistInDatabase() {
        userDao.save(TEST_USER);
        boolean deleted = userDao.delete(TEST_USER.getId());

        assertTrue(deleted);
    }

    @Test
    void shouldSaveUserCorrectly() {
        User savedUser = userDao.save(TEST_USER);

        assertEquals(TEST_USER, savedUser);
        userDao.delete(savedUser.getId());
    }

    @Test
    void shouldUpdateUserCorrectly() {
        userDao.save(TEST_USER);
        TEST_USER.setAddress("Moscow, Russia");

        userDao.update(TEST_USER);
        Optional<User> actualResult = userDao.findById(TEST_USER.getId());

        assertThat(actualResult).isPresent();
        assertEquals(TEST_USER, actualResult.get());
        userDao.delete(TEST_USER.getId());
    }
}