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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountDao implements Dao<Integer, Account> {

    private static final AccountDao accountDao = new AccountDao();

    @Override
    @SneakyThrows
    public Optional<Account> findById(Integer id) {
        String sqlFindById = """
                SELECT a.id,
                       b.id AS bank_id,
                       u.id AS user_id,
                       c.id AS currency_id,
                       a.balance,
                       a.created_at,
                       a.expiration_date,
                       b.name AS bank_name,
                       b.address AS bank_address,
                       b.department AS bank_department,
                       c.currency_code AS currency_code,
                       c.currency_rate AS currency_rate
                FROM account a
                         INNER JOIN currency c ON c.id = a.currency_id
                         INNER JOIN bank b ON b.id = a.bank_id
                         INNER JOIN users u ON u.id = a.user_id
                WHERE a.id = ?;
                                
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlFindById)) {

            preparedStatement.setObject(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next()
                    ? Optional.of(buildAccountWithBankAndCurrency(resultSet))
                    : Optional.empty();
        }
    }

    @Override
    @SneakyThrows
    public List<Account> findAll() {
        String sqlFindAll = """
                SELECT * FROM account a;
                INNER JOIN currency c ON c.id = a.currency_id
                INNER JOIN bank b ON b.id = a.bank_id
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlFindAll)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Account> accounts = new ArrayList<>();

            while (resultSet.next()) {
                accounts.add(buildAccountWithBankAndCurrency(resultSet));
            }

            return accounts;
        }
    }

    @SneakyThrows
    public List<Account> findAllByUserId(Integer userId) {
        String sqlFindUsersByAccountId = """
                SELECT * FROM account a
                INNER JOIN currency c ON c.id = a.currency_id
                INNER JOIN bank b ON b.id = a.bank_id
                WHERE user_id=?;
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlFindUsersByAccountId)) {

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
        String sqlSave = """
                INSERT INTO account(bank_id, user_id, currency_id, balance, created_at, expiration_date)
                VALUES (?,?,?,?,?,?);
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlSave, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setObject(1, account.getBankId());
            preparedStatement.setObject(2, account.getUserId());
            preparedStatement.setObject(3, account.getCurrencyId());
            preparedStatement.setBigDecimal(4, account.getBalance());
            preparedStatement.setObject(5, account.getCreatedAt());
            preparedStatement.setObject(6, account.getExpirationDate());

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
        String sqlUpdate = """
                UPDATE account
                SET bank_id = ?,
                    user_id = ?,
                    currency_id = ?,
                    balance = ?,
                    created_at = ?,
                    expiration_date = ?
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate)) {

            preparedStatement.setObject(1, account.getBankId());
            preparedStatement.setObject(2, account.getCurrencyId());
            preparedStatement.setBigDecimal(3, account.getBalance());
            preparedStatement.setObject(4, account.getCreatedAt());
            preparedStatement.setObject(5, account.getExpirationDate());
            preparedStatement.setInt(6, account.getId());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    @SneakyThrows
    public boolean delete(Integer id) {
        String sqlDeleteById = """
                DELETE FROM account
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlDeleteById)) {
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
        String sqlUpdateBalance = """
                UPDATE account
                SET balance = ?;
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdateBalance)) {
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
                .createdAt(resultSet.getObject("created_at", LocalDateTime.class))
                .expirationDate(resultSet.getObject("expiration_date", LocalDateTime.class))
                .build();
    }

    private Account buildAccountWithBankAndCurrency(ResultSet resultSet) throws SQLException {
        Account account = buildAccount(resultSet);

        account.setBank(Bank.builder()
                .id(account.getBankId())
                .name(resultSet.getString("bank_name"))
                .address(resultSet.getString("bank_address"))
                .department(resultSet.getString("bank_department"))
                .build());
        account.setCurrency(Currency.builder()
                .id(account.getCurrencyId())
                .currencyCode(resultSet.getString("currency_code"))
                .currencyRate(resultSet.getBigDecimal("currency_rate"))
                .build());

        return account;
    }

    public static AccountDao getInstance() {
        return accountDao;
    }
}
