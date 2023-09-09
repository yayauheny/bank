package by.yayauheny.dao;

import by.yayauheny.entity.Bank;
import by.yayauheny.util.ConnectionManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BankDao implements Dao<Integer, Bank> {
    private static final BankDao bankDao = new BankDao();

    @Override
    @SneakyThrows
    public Optional<Bank> findById(Integer id) {
        String sqlFindById = """
                SELECT * FROM bank
                WHERE id=?;
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlFindById)) {

            preparedStatement.setObject(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next()
                    ? Optional.of(buildBank(resultSet))
                    : Optional.empty();
        }
    }

    @Override
    @SneakyThrows
    public List<Bank> findAll() {
        String sqlFindAll = """
                SELECT * FROM bank;
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlFindAll)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Bank> banks = new ArrayList<>();

            while (resultSet.next()) {
                banks.add(buildBank(resultSet));
            }

            return banks;
        }
    }

    @Override
    @SneakyThrows
    public Bank save(Bank bank) {
        String sqlSave = """
                INSERT INTO bank(name, address, department)
                VALUES (?,?,?);
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlSave, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, bank.getName());
            preparedStatement.setString(2, bank.getAddress());
            preparedStatement.setString(3, bank.getDepartment());

            preparedStatement.executeUpdate();
            ResultSet keys = preparedStatement.getGeneratedKeys();

            if (keys.next()) {
                bank.setId(keys.getObject("id", Integer.class));
            }

            return bank;
        }
    }

    @Override
    @SneakyThrows
    public void update(Bank bank) {
        String sqlUpdate = """
                UPDATE bank
                SET name = ?,
                    address = ?,
                    department = ?
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate)) {

            preparedStatement.setString(1, bank.getName());
            preparedStatement.setString(2, bank.getAddress());
            preparedStatement.setString(3, bank.getDepartment());
            preparedStatement.setInt(4, bank.getId());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    @SneakyThrows
    public boolean delete(Integer id) {
        String sqlDeleteById = """
                DELETE FROM bank
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlDeleteById)) {
            preparedStatement.setObject(1, id);

            return preparedStatement.executeUpdate() > 0;
        }
    }

    private Bank buildBank(ResultSet resultSet) throws SQLException {
        return Bank.builder()
                .id(resultSet.getObject("id", Integer.class))
                .name(resultSet.getObject("name", String.class))
                .address(resultSet.getObject("address", String.class))
                .department(resultSet.getObject("department", String.class))
                .build();
    }

    public static BankDao getInstance() {
        return bankDao;
    }
}
