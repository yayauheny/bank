package by.yayauheny.service;

import by.yayauheny.dao.TransactionDao;
import by.yayauheny.entity.Transaction;
import by.yayauheny.exception.InvalidIdException;
import by.yayauheny.util.Validator;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class TransactionService {

    private final TransactionDao transactionDao;

    public Optional<Transaction> findById(Integer id) throws InvalidIdException {
        Validator.validateId(id);

        return transactionDao.findById(id);
    }

    public List<Transaction> findAll() {
        return transactionDao.findAll();
    }

    //TODO: return DTO instead of Transaction
    public List<Transaction> findAllByAccountId(Integer id) throws InvalidIdException {
        Validator.validateId(id);

        return transactionDao.findAllByAccountId(id);
    }

}
