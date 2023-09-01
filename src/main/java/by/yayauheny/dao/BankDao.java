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
    private static final String FIND_BY_ID = """
            SELECT * FROM bank
            WHERE id=?;
            """;
    private static final String FIND_ALL_BY_ID = """
            SELECT * FROM bank;
            """;
    private static final String SAVE = """
            INSERT INTO bank(name, address, department)
            VALUES (?,?,?);
            """;
    private static final String UPDATE = """
            UPDATE bank
            SET name = ?,
                address = ?,
                department = ?
            WHERE id = ?;
            """;
    private static final String DELETE_BY_ID = """
            DELETE FROM bank
            WHERE id = ?;
            """;

    @Override
    @SneakyThrows
    public Optional<Bank> findById(Integer id) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID)) {

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
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_ID)) {

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
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {

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
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)) {

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
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_ID)) {
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
