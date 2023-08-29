package by.yayauheny;

import by.yayauheny.dao.BankDao;
import by.yayauheny.entity.Bank;
import by.yayauheny.util.ConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello bank!");

        BankDao.getInstance().findById(1);

        System.out.println("End of programm");
    }

    public static void testFindAll() {
        try (Connection connection = ConnectionManager.getConnection();
             var preparedStatement = connection.prepareStatement("SELECT * FROM bank")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Bank> banks = new ArrayList<>();

            while (resultSet.next()) {
                Bank bank = Bank.builder()
                        .id(resultSet.getInt("id"))
                        .address(resultSet.getString("address"))
                        .name(resultSet.getString("name"))
                        .department(resultSet.getString("department"))
                        .build();
                banks.add(bank);
            }
            banks.forEach(System.out::println);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}