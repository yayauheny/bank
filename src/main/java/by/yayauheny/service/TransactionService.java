package by.yayauheny.service;

import by.yayauheny.dao.TransactionDao;
import by.yayauheny.entity.Account;
import by.yayauheny.entity.Currency;
import by.yayauheny.entity.Transaction;
import by.yayauheny.util.Validator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionService {

    private final TransactionDao transactionDao = TransactionDao.getInstance();
    private final AccountService accountService = AccountService.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private static final TransactionService transactionService = new TransactionService();

    public Optional<Transaction> findById(Integer id) {
        Validator.validateId(id);

        return transactionDao.findById(id);
    }

    public List<Transaction> findAll() {
        return transactionDao.findAll();
    }

    //TODO: return DTO instead of Transaction
    public List<Transaction> findAllByPeriod(Account account, LocalDateTime from, LocalDateTime to) {
        Validator.validateAccountId(account.getId());
        Validator.validateTransactionsPeriod(from, to);

        List<Transaction> transactions = transactionDao.findAllByAccountPeriod(account.getId(), from, to);
        transactions.forEach(t -> {
            t.setCurrency(currencyService.findById(t.getCurrencyId()).orElseThrow());
            t.setReceiverAccount(accountService.findById(t.getReceiverAccountId()).orElseThrow());
            t.setSenderAccount(t.getSenderAccountId() == null
                    ? null
                    : accountService.findById(t.getSenderAccountId()).orElseThrow());
        });
        account.setTransactions(transactions);

        return transactions;
    }

    public BigDecimal getTotalProfitByPeriod(String id, LocalDateTime from, LocalDateTime to) {
        Validator.validateAccountId(id);
        Validator.validateTransactionsPeriod(from, to);
        List<Transaction> transactions = transactionDao.findAllReplenishmentsByPeriod(id, from, to);
        return calculateTotalAmount(transactions);
    }

    public BigDecimal getTotalLossByPeriod(String id, LocalDateTime from, LocalDateTime to) {
        Validator.validateAccountId(id);
        Validator.validateTransactionsPeriod(from, to);
        List<Transaction> transactions = transactionDao.findAllLossesByPeriod(id, from, to);
        return calculateTotalAmount(transactions).negate();
    }

    private BigDecimal calculateTotalAmount(List<Transaction> transactions) {
        BigDecimal totalAmount = new BigDecimal(BigInteger.ZERO);
        transactions.forEach(this::setTransactionDependencies);

        for (Transaction t : transactions) {
            Currency transactionCurrency = t.getCurrency();
            Currency receiverCurrency = t.getReceiverAccount().getCurrency();
            if (transactionCurrency.equals(receiverCurrency)) {
                totalAmount = totalAmount.add(t.getAmount());
            } else {
                BigDecimal transactionAmount = t.getAmount();
                BigDecimal convertedToByn = currencyService.convertToByn(transactionCurrency, transactionAmount);
                BigDecimal receiverAmount = currencyService.convertFromByn(receiverCurrency, convertedToByn);
                totalAmount = totalAmount.add(receiverAmount);
            }
        }

        return totalAmount.setScale(2, RoundingMode.HALF_UP);
    }

    private void setTransactionDependencies(Transaction t) {
        t.setCurrency(currencyService.findById(t.getCurrencyId()).orElseThrow());
        t.setReceiverAccount(accountService.findById(t.getReceiverAccountId()).orElseThrow());
    }

    public static TransactionService getInstance() {
        return transactionService;
    }
}
