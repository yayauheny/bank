package by.yayauheny.service;

import by.yayauheny.dao.AccountDao;
import by.yayauheny.dao.TransactionDao;
import by.yayauheny.entity.Account;
import by.yayauheny.entity.Transaction;
import by.yayauheny.exception.InvalidIdException;
import by.yayauheny.util.Validator;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class AccountService implements Service<Integer, Account> {

    private final AccountDao accountDao;
    private final TransactionDao transactionDao;
    private final CurrencyService currencyService;

    public AccountService() {
        this.accountDao = AccountDao.getInstance();
        this.transactionDao = TransactionDao.getInstance();
        this.currencyService = new CurrencyService();
    }

    @Override
    public Optional<Account> findById(Integer id) throws InvalidIdException {
        Validator.validateId(id);

        return accountDao.findById(id);
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
    public boolean delete(Integer id) {
        return accountDao.delete(id);
    }

    public void processTransactionAndUpdateAccount(Transaction transaction) {
        switch (transaction.getType()) {
            case TRANSFER -> {
                Validator.validateTransactionFunds(transaction);
                BigDecimal convertedAmount = currencyService.convertTransactionAmount(transaction);
                accountDao.transferMoney(transactionDao, transaction, convertedAmount);
            }
            case DEPOSIT, WITHDRAWAL -> {
                Validator.validateTransactionFunds(transaction);
                accountDao.updateBalance(transactionDao, transaction);
            }
        }
    }
}
