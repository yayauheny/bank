package by.yayauheny.service;

import by.yayauheny.dao.UserDao;
import by.yayauheny.entity.User;
import by.yayauheny.exception.InvalidIdException;
import by.yayauheny.util.Validator;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class UserService implements Service<Integer, User>{

    private final UserDao userDao;

    @Override
    public Optional<User> findById(Integer id) throws InvalidIdException {
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
}
