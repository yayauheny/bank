package by.yayauheny.dao;

import by.yayauheny.entity.Transaction;
import by.yayauheny.entity.TransactionType;
import by.yayauheny.util.ConnectionManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionDao implements Dao<Integer, Transaction> {

    private static final TransactionDao transactionDao = new TransactionDao();
    private static final String SAVE = """
            INSERT INTO transaction(type, amount, created_at, currency_id, receiver_account_id, sender_account_id)
            VALUES (?,?,?,?,?,?)
            """;

    @Override
    @SneakyThrows
    public Optional<Transaction> findById(Integer id) {
        String sqlFindById = """
                SELECT * FROM transaction
                WHERE id=?;
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlFindById)) {

            preparedStatement.setObject(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next()
                    ? Optional.of(buildTransaction(resultSet))
                    : Optional.empty();
        }
    }

    @Override
    @SneakyThrows
    public List<Transaction> findAll() {
        String sqlFindAll = """
                SELECT * FROM transaction;
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlFindAll)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Transaction> transactions = new ArrayList<>();

            while (resultSet.next()) {
                transactions.add(buildTransaction(resultSet));
            }

            return transactions;
        }
    }

    @SneakyThrows
    public List<Transaction> findAllByAccountPeriod(String accountId, LocalDateTime from, LocalDateTime to) {
        String sqlFindAllByAccountPeriod = """
                SELECT * FROM transaction t
                INNER JOIN currency c ON c.id = t.currency_id
                INNER JOIN account sender ON sender.id = t.sender_account_id
                INNER JOIN account receiver ON receiver.id = t.receiver_account_id
                WHERE (receiver_account_id = ?
                   OR sender_account_id = ?)
                   AND (t.created_at > ? AND t.created_at < ?)
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlFindAllByAccountPeriod)) {

            preparedStatement.setString(1, accountId);
            preparedStatement.setString(2, accountId);
            preparedStatement.setObject(3, from);
            preparedStatement.setObject(4, to);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Transaction> transactions = new ArrayList<>();

            while (resultSet.next()) {
                transactions.add(buildTransaction(resultSet));
            }

            return transactions;
        }
    }

    @Override
    @SneakyThrows
    public Transaction save(Transaction transaction) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, transaction.getType().name());
            preparedStatement.setBigDecimal(2, transaction.getAmount());
            preparedStatement.setObject(3, transaction.getCreatedAt());
            preparedStatement.setObject(4, transaction.getCurrencyId());
            preparedStatement.setObject(5, transaction.getReceiverAccountId());
            preparedStatement.setObject(6, transaction.getSenderAccountId());

            preparedStatement.executeUpdate();
            ResultSet keys = preparedStatement.getGeneratedKeys();

            if (keys.next()) {
                transaction.setId(keys.getObject("id", Integer.class));
            }

            return transaction;
        }
    }

    @SneakyThrows
    public Transaction save(Transaction transaction, Connection connection) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, transaction.getType().name());
            preparedStatement.setBigDecimal(2, transaction.getAmount());
            preparedStatement.setObject(3, transaction.getCreatedAt());
            preparedStatement.setObject(4, transaction.getCurrencyId());
            preparedStatement.setObject(5, transaction.getReceiverAccountId());
            preparedStatement.setObject(6, transaction.getSenderAccountId());

            preparedStatement.executeUpdate();
            ResultSet keys = preparedStatement.getGeneratedKeys();

            if (keys.next()) {
                transaction.setId(keys.getObject("id", Integer.class));
            }

            return transaction;
        }
    }

    @Override
    @SneakyThrows
    public void update(Transaction transaction) {
        String sqlUpdate = """
                UPDATE transaction
                SET type = ?,
                    amount = ?,
                    created_at = ?,
                    currency_id = ?,
                    receiver_account_id = ?,
                    sender_account_id = ?
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate)) {

            preparedStatement.setString(1, transaction.getType().name());
            preparedStatement.setBigDecimal(2, transaction.getAmount());
            preparedStatement.setObject(3, transaction.getCreatedAt());
            preparedStatement.setObject(4, transaction.getCurrencyId());
            preparedStatement.setObject(5, transaction.getReceiverAccount().getId());
            preparedStatement.setObject(6, transaction.getSenderAccount().getId());
            preparedStatement.setObject(7, transaction.getId());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    @SneakyThrows
    public boolean delete(Integer id) {
        String sqlDeleteById = """
                DELETE FROM transaction
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlDeleteById)) {
            preparedStatement.setObject(1, id);

            return preparedStatement.executeUpdate() > 0;
        }
    }

    @SneakyThrows
    public List<Transaction> findAllReplenishmentsByPeriod(String accountId, LocalDateTime from, LocalDateTime to) {
        String sqlFindAllReplenishments = """
                SELECT * FROM transaction t
                INNER JOIN currency c ON c.id = t.currency_id
                INNER JOIN account sender ON sender.id = t.sender_account_id
                INNER JOIN account receiver ON receiver.id = t.receiver_account_id
                WHERE (t.created_at > ? AND t.created_at < ?)
                  AND (receiver_account_id = ? AND type = 'TRANSFER')
                  OR (receiver_account_id = ? AND type = 'DEPOSIT')
                """;
        return findTransactionsByTypeAndPeriod(accountId, from, to, sqlFindAllReplenishments);
    }

    @SneakyThrows
    public List<Transaction> findAllLossesByPeriod(String accountId, LocalDateTime from, LocalDateTime to) {
        String sqlFindAllWithdraws = """
                SELECT * FROM transaction t
                INNER JOIN currency c ON c.id = t.currency_id
                INNER JOIN account sender ON sender.id = t.sender_account_id
                INNER JOIN account receiver ON receiver.id = t.receiver_account_id
                WHERE (t.created_at > ? AND t.created_at < ?)
                  AND (sender_account_id = ? AND type = 'TRANSFER')
                  OR (receiver_account_id = ? AND type = 'WITHDRAWAL')
                """;
        return findTransactionsByTypeAndPeriod(accountId, from, to, sqlFindAllWithdraws);
    }

    @SneakyThrows
    private List<Transaction> findTransactionsByTypeAndPeriod(String accountId, LocalDateTime from, LocalDateTime to, String sqlQuery) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

            preparedStatement.setObject(1, from);
            preparedStatement.setObject(2, to);
            preparedStatement.setString(3, accountId);
            preparedStatement.setString(4, accountId);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Transaction> transactions = new ArrayList<>();

            while (resultSet.next()) {
                transactions.add(buildTransaction(resultSet));
            }

            return transactions;
        }
    }

    private Transaction buildTransaction(ResultSet resultSet) throws SQLException {
        return Transaction.builder()
                .id(resultSet.getObject("id", Integer.class))
                .type(TransactionType.valueOf(resultSet.getString("type")))
                .amount(resultSet.getBigDecimal("amount"))
                .createdAt(resultSet.getObject("created_at", LocalDateTime.class))
                .currencyId(resultSet.getObject("currency_id", Integer.class))
                .receiverAccountId(resultSet.getString("receiver_account_id"))
                .senderAccountId(resultSet.getString("sender_account_id"))
                .build();
    }

    public static TransactionDao getInstance() {
        return transactionDao;
    }
}
