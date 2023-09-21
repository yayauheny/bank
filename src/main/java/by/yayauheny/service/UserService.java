package by.yayauheny.service;

import by.yayauheny.dao.UserDao;
import by.yayauheny.entity.User;
import by.yayauheny.util.Validator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserService implements Service<Integer, User> {

    private static UserService userService = new UserService();
    private final UserDao userDao = UserDao.getInstance();

    @Override
    public Optional<User> findById(Integer id) {
        Validator.validateId(id);

        return userDao.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public User save(User user) {
        return userDao.save(user);
    }

    @Override
    public void update(User user) {
        userDao.update(user);
    }

    @Override
    public boolean delete(Integer id) {
        return userDao.delete(id);
    }

    public static UserService getInstance(){
        return userService;
    }
}
