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
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BankDao implements Dao<Integer, Bank> {
    private static final BankDao bankDao = new BankDao();
    private static final String FIND_BY_ID = """
            SELECT * FROM bank
            WHERE id=?
            """;

    private Bank buildBank(ResultSet resultSet) throws SQLException {
        return Bank.builder()
                .id(resultSet.getObject("id", Integer.class))
                .name(resultSet.getObject("name", String.class))
                .address(resultSet.getObject("address", String.class))
                .department(resultSet.getObject("department", String.class))
                .build();
    }


    @Override
    @SneakyThrows
    public Optional<Bank> findById(Integer id) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID)) {
            preparedStatement.setObject(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            throw new SQLException("TEST");
//            return resultSet.next()
//                    ? Optional.of(buildBank(resultSet))
//                    : Optional.empty();
        }
    }

    @Override
    public boolean delete(Integer id) {
        return false;
    }

    @Override
    public Bank save(Bank bank) {
        return null;
    }

    @Override
    public void update(Bank bank) {

    }

    @Override
    public List<Optional<Bank>> findAll() {
        return null;
    }

    public static BankDao getInstance() {
        return bankDao;
    }
}
