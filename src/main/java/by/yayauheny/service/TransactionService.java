package by.yayauheny.service;

import by.yayauheny.dao.TransactionDao;
import by.yayauheny.entity.Transaction;
import by.yayauheny.exception.InvalidIdException;
import by.yayauheny.util.Validator;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class TransactionService {

    private final TransactionDao transactionDao;

    public TransactionService() {
        this.transactionDao = TransactionDao.getInstance();
    }

    public Optional<Transaction> findById(Integer id) {
        Validator.validateId(id);

        return transactionDao.findById(id);
    }

    public List<Transaction> findAll() {
        return transactionDao.findAll();
    }

    //TODO: return DTO instead of Transaction
    public List<Transaction> findAllByPeriod(Integer accountId, LocalDate from, LocalDate to) throws InvalidIdException {
        Validator.validateId(accountId);

        return transactionDao.findAllByAccountPeriod(accountId, from, to);
    }

}
