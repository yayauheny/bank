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

    @Override
    @SneakyThrows
    public Optional<Currency> findById(Integer id) {
        String sqlFindById = """
                SELECT * FROM currency
                WHERE id=?;
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlFindById)) {

            preparedStatement.setObject(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next()
                    ? Optional.of(buildCurrency(resultSet))
                    : Optional.empty();
        }
    }

    @SneakyThrows
    public Optional<Currency> findByCode(String code) {
        String sqlFindByCode = """
                SELECT * FROM currency
                WHERE currency_code=?;
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlFindByCode)) {

            preparedStatement.setString(1, code);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next()
                    ? Optional.of(buildCurrency(resultSet))
                    : Optional.empty();
        }
    }

    @Override
    @SneakyThrows
    public List<Currency> findAll() {
        String sqlFindAll = """
                SELECT * FROM currency;
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlFindAll)) {

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
        String sqlSave = """
                INSERT INTO currency(currency_code, currency_rate)
                VALUES (?,?);
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlSave, Statement.RETURN_GENERATED_KEYS)) {

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
        String sqlUpdate = """
                UPDATE currency
                SET currency_code = ?,
                    currency_rate = ?
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate)) {

            preparedStatement.setString(1, currency.getCurrencyCode());
            preparedStatement.setBigDecimal(2, currency.getCurrencyRate());
            preparedStatement.setObject(3, currency.getId());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    @SneakyThrows
    public boolean delete(Integer id) {
        String sqlDeleteById = """
                DELETE FROM currency
                WHERE id = ?;
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlDeleteById)) {
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
