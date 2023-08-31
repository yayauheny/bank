package by.yayauheny.dao;

import by.yayauheny.entity.Account;
import by.yayauheny.util.ConnectionManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountDao implements Dao<Integer, Account> {

    private static final AccountDao accountDao = new AccountDao();
    private static final String FIND_BY_ID = """
            SELECT * FROM account
            WHERE id=?;
            """;
    private static final String FIND_ALL_BY_ID = """
            SELECT * FROM account;
            """;
    private static final String SAVE = """
            INSERT INTO account(bank_id, currency_id, balance, created_at)
            VALUES (?,?,?,?);
            """;
    private static final String UPDATE = """
            UPDATE account
            SET bank_id = ?,
                currency_id = ?,
                balance = ?,
                created_at = ?
            WHERE id = ?;
            """;
    private static final String DELETE_BY_ID = """
            DELETE FROM account
            WHERE id = ?;
            """;

    @Override
    @SneakyThrows
    public Optional<Account> findById(Integer id) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next()
                    ? Optional.of(buildAccount(resultSet))
                    : Optional.empty();
        }
    }

    @Override
    @SneakyThrows
    public List<Account> findAll() {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_ID)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Account> accounts = new ArrayList<>();

            while (resultSet.next()) {
                accounts.add(buildAccount(resultSet));
            }

            return accounts;
        }
    }

    @Override
    @SneakyThrows
    public Account save(Account account) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setObject(1, account.getBankId());
            preparedStatement.setObject(2, account.getCurrencyId());
            preparedStatement.setBigDecimal(3, account.getBalance());
            preparedStatement.setObject(4, account.getCreatedAt());

            preparedStatement.executeUpdate();
            ResultSet keys = preparedStatement.getGeneratedKeys();

            if (keys.next()) {
                account.setId(keys.getObject("id", Integer.class));
            }

            return account;
        }
    }

    @Override
    @SneakyThrows
    public void update(Account account) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)) {

            preparedStatement.setObject(1, account.getBankId());
            preparedStatement.setObject(2, account.getCurrencyId());
            preparedStatement.setBigDecimal(3, account.getBalance());
            preparedStatement.setObject(4, account.getCreatedAt());
            preparedStatement.setInt(5, account.getId());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    @SneakyThrows
    public boolean delete(Integer id) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_ID)) {
            preparedStatement.setInt(1, id);

            return preparedStatement.executeUpdate() > 0;
        }
    }

    private Account buildAccount(ResultSet resultSet) throws SQLException {
        return Account.builder()
                .id(resultSet.getObject("id", Integer.class))
                .bankId(resultSet.getObject("bank_id", Integer.class))
                .currencyId(resultSet.getObject("currency_id", Integer.class))
                .balance(resultSet.getObject("balance", BigDecimal.class))
                .createdAt(resultSet.getObject("created_at", LocalDate.class))
                .build();
    }

    public static AccountDao getInstance() {
        return accountDao;
    }
}