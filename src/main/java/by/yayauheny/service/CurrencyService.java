package by.yayauheny.service;

import by.yayauheny.dao.CurrencyDao;
import by.yayauheny.entity.Currency;
import by.yayauheny.entity.Transaction;
import by.yayauheny.entity.TransactionType;
import by.yayauheny.util.Validator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrencyService implements Service<Integer, Currency> {

    private final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private static final CurrencyService currencyService = new CurrencyService();

    @Override
    public Optional<Currency> findById(Integer id) {
        Validator.validateId(id);

        return currencyDao.findById(id);
    }

    public Optional<Currency> findByCode(String code) {
        return currencyDao.findByCode(code);
    }

    @Override
    public List<Currency> findAll() {
        return currencyDao.findAll();
    }

    @Override
    public Currency save(Currency currency) {
        return currencyDao.save(currency);
    }

    @Override
    public void update(Currency currency) {
        currencyDao.update(currency);
    }

    @Override
    public boolean delete(Integer id) {
        return currencyDao.delete(id);
    }

    //TODO:replace with transaction DTO
    public BigDecimal convertTransactionAmount(Transaction transaction) {
        TransactionType transactionType = transaction.getType();
        BigDecimal amount = transaction.getAmount();

        if (transactionType.equals(TransactionType.TRANSFER)) {
            Currency senderCurrency = transaction.getSenderAccount().getCurrency();
            Currency receiverCurrency = transaction.getReceiverAccount().getCurrency();
            return convertTransferAmount(senderCurrency, receiverCurrency, amount);
        } else {
            return amount;
        }
    }

    public BigDecimal convertToByn(Currency currency, BigDecimal amount) {
        return amount.multiply(currency.getCurrencyRate());
    }

    public BigDecimal convertFromByn(Currency currency, BigDecimal amount) {
        return amount.divide(currency.getCurrencyRate(), RoundingMode.HALF_UP);
    }

    private BigDecimal convertTransferAmount(Currency senderCurrency, Currency receiverCurrency, BigDecimal amount) {
        if (!senderCurrency.equals(receiverCurrency)) {
            BigDecimal convertedToByn = convertToByn(senderCurrency, amount);

            return convertFromByn(receiverCurrency, convertedToByn);
        } else return amount;
    }

    public static CurrencyService getInstance() {
        return currencyService;
    }
}
