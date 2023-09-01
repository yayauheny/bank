package by.yayauheny.dao;

import by.yayauheny.entity.Currency;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrencyDao implements Dao<Integer, Currency> {

    private static final CurrencyDao currencyDao = new CurrencyDao();
    private static final String FIND_BY_ID = """
            SELECT * FROM currency
            WHERE id=?;
            """;
    private static final String FIND_ALL_BY_ID = """
            SELECT * FROM currency;
            """;
    private static final String SAVE = """
            INSERT INTO currency(currency_code, currency_rate)
            VALUES (?,?);
            """;
    private static final String UPDATE = """
            UPDATE currency
            SET currency_code = ?,
                currency_rate = ?
            WHERE id = ?;
            """;
    private static final String DELETE_BY_ID = """
            DELETE FROM currency
            WHERE id = ?;
            """;

    @Override
    @SneakyThrows
    public Optional<Currency> findById(Integer id) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID)) {

            preparedStatement.setObject(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next()
                    ? Optional.of(buildCurrency(resultSet))
                    : Optional.empty();
        }
    }

    @Override
    @SneakyThrows
    public List<Currency> findAll() {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_ID)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Currency> currencies = new ArrayList<>();

            while (resultSet.next()) {
                currencies.add(buildCurrency(resultSet));
            }

            return currencies;
        }
    }

    @Override
    @SneakyThrows
    public Currency save(Currency currency) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, currency.getCurrencyCode());
            preparedStatement.setBigDecimal(2, currency.getCurrencyRate());

            preparedStatement.executeUpdate();
            ResultSet keys = preparedStatement.getGeneratedKeys();

            if (keys.next()) {
                currency.setId(keys.getObject("id", Integer.class));
            }

            return currency;
        }
    }

    @Override
    @SneakyThrows
    public void update(Currency currency) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)) {

            preparedStatement.setString(1, currency.getCurrencyCode());
            preparedStatement.setBigDecimal(2, currency.getCurrencyRate());
            preparedStatement.setObject(3, currency.getId());

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

    private Currency buildCurrency(ResultSet resultSet) throws SQLException {
        return Currency.builder()
                .id(resultSet.getObject("id", Integer.class))
                .currencyCode(resultSet.getObject("currency_code", String.class))
                .currencyRate(resultSet.getObject("currency_rate", BigDecimal.class))
                .build();
    }

    public static CurrencyDao getInstance() {
        return currencyDao;
    }
}
