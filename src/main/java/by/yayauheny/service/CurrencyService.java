package by.yayauheny.service;

import by.yayauheny.dao.CurrencyDao;
import by.yayauheny.entity.Currency;
import by.yayauheny.entity.Transaction;
import by.yayauheny.entity.TransactionType;
import by.yayauheny.util.Validator;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class CurrencyService implements Service<Integer, Currency> {

    private final CurrencyDao currencyDao;

    public CurrencyService() {
        this.currencyDao = CurrencyDao.getInstance();
    }

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
            Currency receiverCurrency = transaction.getReceiver().getCurrency();
            Currency senderCurrency = transaction.getSender().getCurrency();
            return convertTransferAmount(senderCurrency, receiverCurrency, amount);
        } else {
            return amount;
        }
    }

    private BigDecimal convertToByn(Currency currency, BigDecimal amount) {
        return amount.multiply(currency.getCurrencyRate());
    }

    private BigDecimal convertFromByn(Currency currency, BigDecimal amount) {
        return amount.multiply(currency.getCurrencyRate());
    }

    private BigDecimal convertTransferAmount(Currency senderCurrency, Currency receiverCurrency, BigDecimal amount) {
        if (!senderCurrency.equals(receiverCurrency)) {
            BigDecimal convertedToByn = convertToByn(senderCurrency, amount);

            return convertFromByn(receiverCurrency, convertedToByn);
        } else return amount;
    }
}
