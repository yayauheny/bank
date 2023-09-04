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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionDao implements Dao<Integer, Transaction> {

    private static final TransactionDao transactionDao = new TransactionDao();

    private static final String FIND_BY_ID = """
            SELECT * FROM transaction
            WHERE id=?;
            """;
    private static final String FIND_ALL_BY_ID = """
            SELECT * FROM transaction;
            """;
    private static final String FIND_ALL_BY_ACCOUNT_ID = """
            SELECT * FROM transaction
            WHERE (transaction.receiver_account_id = ?
               OR transaction.sender_account_id = ?)
            """;
    private static final String SAVE = """
            INSERT INTO transaction(type, description, amount, created_at, currency_id, receiver_account_id, sender_account_id)
            VALUES (?,?,?,?,?,?,?)
            """;
    private static final String UPDATE = """
            UPDATE transaction
            SET type = ?,
                description = ?,
                amount = ?,
                created_at = ?,
                currency_id = ?,
                receiver_account_id = ?,
                sender_account_id = ?
            WHERE id = ?;
            """;
    private static final String DELETE_BY_ID = """
            DELETE FROM transaction
            WHERE id = ?;
            """;

    @Override
    @SneakyThrows
    public Optional<Transaction> findById(Integer id) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID)) {

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
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_ID)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Transaction> transactions = new ArrayList<>();

            while (resultSet.next()) {
                transactions.add(buildTransaction(resultSet));
            }

            return transactions;
        }
    }

    @SneakyThrows
    public List<Transaction> findAllByAccountId(Integer accountId) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_ACCOUNT_ID)) {

            preparedStatement.setObject(1, accountId);
            preparedStatement.setObject(2, accountId);
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
            preparedStatement.setString(2, transaction.getDescription());
            preparedStatement.setBigDecimal(3, transaction.getAmount());
            preparedStatement.setObject(4, transaction.getCreatedAt());
            preparedStatement.setObject(5, transaction.getCurrencyId());
            preparedStatement.setObject(6, transaction.getReceiverAccountId());
            preparedStatement.setObject(7, transaction.getSenderAccountId());

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
            preparedStatement.setString(2, transaction.getDescription());
            preparedStatement.setBigDecimal(3, transaction.getAmount());
            preparedStatement.setObject(4, transaction.getCreatedAt());
            preparedStatement.setObject(5, transaction.getCurrencyId());
            preparedStatement.setObject(6, transaction.getReceiverAccountId());
            preparedStatement.setObject(7, transaction.getSenderAccountId());

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
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)) {

            preparedStatement.setString(1, transaction.getType().name());
            preparedStatement.setString(2, transaction.getDescription());
            preparedStatement.setBigDecimal(3, transaction.getAmount());
            preparedStatement.setObject(4, transaction.getCreatedAt());
            preparedStatement.setObject(5, transaction.getCurrencyId());
            preparedStatement.setObject(6, transaction.getReceiverAccountId());
            preparedStatement.setObject(7, transaction.getSenderAccountId());
            preparedStatement.setObject(8, transaction.getId());

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

    private Transaction buildTransaction(ResultSet resultSet) throws SQLException {
        return Transaction.builder()
                .type(TransactionType.valueOf(resultSet.getString("type")))
                .description(resultSet.getString("description"))
                .amount(resultSet.getBigDecimal("amount"))
                .createdAt(resultSet.getObject("created_at", LocalDate.class))
                .currencyId(resultSet.getObject("currency_id", Integer.class))
                .receiverAccountId(resultSet.getObject("receiver_account_id", Integer.class))
                .senderAccountId(resultSet.getObject("sender_account_id", Integer.class))
                .build();
    }

    public static TransactionDao getInstance() {
        return transactionDao;
    }
}
