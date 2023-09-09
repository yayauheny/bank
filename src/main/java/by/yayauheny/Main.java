package by.yayauheny;

import by.yayauheny.entity.Account;
import by.yayauheny.entity.Transaction;
import by.yayauheny.service.AccountService;

import java.util.Optional;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        AccountService service = new AccountService();
        Optional<Account> account = service.findById(1);

        System.out.println(account);


    }
}