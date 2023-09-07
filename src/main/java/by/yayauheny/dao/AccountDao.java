package by.yayauheny.dao;

import by.yayauheny.entity.Account;
import by.yayauheny.entity.Bank;
import by.yayauheny.entity.Currency;
import by.yayauheny.entity.Transaction;
import by.yayauheny.exception.TransactionException;
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

    private static final String FIND_ALL = """
            SELECT * FROM account;
            """;
    private static final String FIND_ALL_BY_USER_ID = """
            SELECT a.id AS id,
                   a.user_id AS user_id,
                   a.balance AS balance,
                   a.created_at AS created_at,
                   b.id AS bank_id,
                   b.name AS bank_name,
                   b.address AS bank_address,
                   b.department AS bank_department,
                   c.id AS currency_id,
                   c.currency_code AS currency_currency_code,
                   c.currency_rate AS currency_currency_rate
            FROM account a
            INNER JOIN currency c ON c.id = a.currency_id
            INNER JOIN bank b ON b.id = a.bank_id
            WHERE user_id=?;
            """;

    private static final String UPDATE_BALANCE = """
            UPDATE account
            SET balance = ?;
            """;
    private static final String SAVE = """
            INSERT INTO account(bank_id, user_id, currency_id, balance, created_at)
            VALUES (?,?,?,?,?);
            """;
    private static final String UPDATE = """
            UPDATE account
            SET bank_id = ?,
                user_id = ?,
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

            preparedStatement.setObject(1, id);
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
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Account> accounts = new ArrayList<>();

            while (resultSet.next()) {
                accounts.add(buildAccount(resultSet));
            }

            return accounts;
        }
    }

    @SneakyThrows
    public List<Account> findAllByUserId(Integer userId) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_USER_ID)) {

            preparedStatement.setObject(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Account> accounts = new ArrayList<>();

            while (resultSet.next()) {
                accounts.add(buildAccountWithBankAndCurrency(resultSet));
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
            preparedStatement.setObject(2, account.getUserId());
            preparedStatement.setObject(3, account.getCurrencyId());
            preparedStatement.setBigDecimal(4, account.getBalance());
            preparedStatement.setObject(5, account.getCreatedAt());

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
            preparedStatement.setObject(1, id);

            return preparedStatement.executeUpdate() > 0;
        }
    }

    public void updateBalance(TransactionDao transactionDao, Transaction transaction) throws TransactionException {
        Connection connection = null;

        try {
            connection = ConnectionManager.getConnection();
            connection.setAutoCommit(false);

            try {
                Integer accountOwnerId = transaction.getReceiverAccountId();
                BigDecimal amount = transaction.getAmount();

                transactionDao.save(transaction, connection);
                updateAccountBalance(connection, accountOwnerId, amount);

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new TransactionException(e);
            }
        } catch (SQLException e) {
            throw new TransactionException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void transferMoney(TransactionDao transactionDao, Transaction transaction, BigDecimal convertedAmount) throws TransactionException {
        Connection connection = null;

        try {
            connection = ConnectionManager.getConnection();
            connection.setAutoCommit(false);

            try {
                Integer senderAccountId = transaction.getSenderAccountId();
                Integer receiverAccountId = transaction.getReceiverAccountId();
                BigDecimal amount = transaction.getAmount();
                BigDecimal updatedSenderBalance = transaction.getSender().getBalance().subtract(amount);
                BigDecimal updatedReceiverBalance = transaction.getReceiver().getBalance().add(convertedAmount);

                transactionDao.save(transaction, connection);
                updateAccountBalance(connection, senderAccountId, updatedSenderBalance);
                updateAccountBalance(connection, receiverAccountId, updatedReceiverBalance);

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new TransactionException(e);
            }
        } catch (SQLException e) {
            throw new TransactionException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void updateAccountBalance(Connection connection, Integer userId, BigDecimal amount) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BALANCE)) {
            preparedStatement.setObject(1, amount);
            preparedStatement.setObject(2, userId);
            preparedStatement.executeUpdate();
        }
    }

    private Account buildAccount(ResultSet resultSet) throws SQLException {
        return Account.builder()
                .id(resultSet.getObject("id", Integer.class))
                .bankId(resultSet.getObject("bank_id", Integer.class))
                .userId(resultSet.getObject("user_id", Integer.class))
                .currencyId(resultSet.getObject("currency_id", Integer.class))
                .balance(resultSet.getObject("balance", BigDecimal.class))
                .createdAt(resultSet.getObject("created_at", LocalDate.class))
                .build();
    }

    private Account buildAccountWithBankAndCurrency(ResultSet resultSet) throws SQLException {
        Account account = buildAccount(resultSet);

        account.setBank(Bank.builder()
                .id(resultSet.getObject("bank_id", Integer.class))
                .name(resultSet.getString("bank_name"))
                .address(resultSet.getString("bank_address"))
                .department(resultSet.getString("bank_department"))
                .build());
        account.setCurrency(Currency.builder()
                .id(resultSet.getObject("currency_id", Integer.class))
                .currencyCode(resultSet.getString("currency_currency_code"))
                .currencyRate(resultSet.getBigDecimal("currency_currency_rate"))
                .build());

        return account;
    }

    public static AccountDao getInstance() {
        return accountDao;
    }
}
