package by.yayauheny.service;

import by.yayauheny.dao.AccountDao;
import by.yayauheny.dao.TransactionDao;
import by.yayauheny.entity.Account;
import by.yayauheny.entity.Transaction;
import by.yayauheny.entity.User;
import by.yayauheny.exception.InvalidIdException;
import by.yayauheny.util.Validator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountService implements Service<String, Account> {

    private final AccountDao accountDao = AccountDao.getInstance();
    private final UserService userService = UserService.getInstance();
    private final TransactionDao transactionDao = TransactionDao.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private static final AccountService accountService = new AccountService();

    @Override
    public Optional<Account> findById(String id) throws InvalidIdException {
        Validator.validateAccountId(id);

        Optional<Account> account = accountDao.findById(id);
        Optional<User> user = userService.findById(account.get().getUserId());
        account.get().setUser(user.get());
        return account;
    }

    @Override
    public List<Account> findAll() {
        return accountDao.findAll();
    }

    @Override
    public Account save(Account account) {
        return accountDao.save(account);
    }

    @Override
    public void update(Account account) {
        accountDao.update(account);
    }

    @Override
    public boolean delete(String id) {
        Validator.validateAccountId(id);

        return accountDao.delete(id);
    }

    public void processTransactionAndUpdateAccount(Transaction transaction) {
        switch (transaction.getType()) {
            case TRANSFER -> {
                Validator.validateTransaction(transaction);
                BigDecimal convertedAmount = currencyService.convertTransactionAmount(transaction);

                accountDao.transferMoney(transactionDao, transaction, convertedAmount);
            }
            case DEPOSIT -> {
                Validator.validateTransaction(transaction);

                accountDao.updateBalance(transactionDao, transaction, transaction.getAmount());
            }
            case WITHDRAWAL -> {
                Validator.validateTransaction(transaction);
                BigDecimal transactionAmount = transaction.getAmount().negate();

                accountDao.updateBalance(transactionDao, transaction, transactionAmount);
            }
        }
    }

    public static AccountService getInstance() {
        return accountService;
    }
}
