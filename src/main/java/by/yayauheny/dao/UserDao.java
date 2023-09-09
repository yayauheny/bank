package by.yayauheny.dao;

import by.yayauheny.entity.User;
import by.yayauheny.util.ConnectionManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDao implements Dao<Integer, User> {

    private static final UserDao userDao = new UserDao();


    @Override
    @SneakyThrows
    public Optional<User> findById(Integer id) {
        String sqlFindById = """
                SELECT * FROM users
                WHERE id=?;
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlFindById)) {

            preparedStatement.setObject(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next()
                    ? Optional.of(buildUser(resultSet))
                    : Optional.empty();
        }
    }

    @Override
    @SneakyThrows
    public List<User> findAll() {
        String sqlFindAll = """
                SELECT * FROM users;
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlFindAll)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            List<User> users = new ArrayList<>();

            while (resultSet.next()) {
                users.add(buildUser(resultSet));
            }

            return users;
        }
    }

    @Override
    @SneakyThrows
    public User save(User user) {
        String sqlSave = """
                INSERT INTO users(full_name, date_of_birth, address)
                VALUES (?,?,?);
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlSave, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, user.getFullName());
            preparedStatement.setDate(2, Date.valueOf(user.getBirthDate()));
            preparedStatement.setString(3, user.getAddress());

            preparedStatement.executeUpdate();
            ResultSet keys = preparedStatement.getGeneratedKeys();

            if (keys.next()) {
                user.setId(keys.getObject("id", Integer.class));
            }

            return user;
        }
    }

    @Override
    @SneakyThrows
    public void update(User user) {
        String sqlUpdate = """
                UPDATE users
                SET full_name = ?,
                    date_of_birth = ?,
                    address = ?
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate)) {

            preparedStatement.setString(1, user.getFullName());
            preparedStatement.setDate(2, Date.valueOf(user.getBirthDate()));
            preparedStatement.setString(3, user.getAddress());
            preparedStatement.setObject(4, user.getId());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    @SneakyThrows
    public boolean delete(Integer id) {
        String sqlDeleteById = """
                DELETE FROM users
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlDeleteById)) {
            preparedStatement.setObject(1, id);

            return preparedStatement.executeUpdate() > 0;
        }
    }

    private User buildUser(ResultSet resultSet) throws SQLException {
        return User.builder()
                .id(resultSet.getObject("id", Integer.class))
                .fullName(resultSet.getObject("full_name", String.class))
                .birthDate(resultSet.getObject("date_of_birth", LocalDate.class))
                .address(resultSet.getObject("address", String.class))
                .build();
    }

    public static UserDao getInstance() {
        return userDao;
    }
}
