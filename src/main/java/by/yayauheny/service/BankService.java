package by.yayauheny.service;

import by.yayauheny.dao.BankDao;
import by.yayauheny.entity.Bank;
import by.yayauheny.exception.InvalidIdException;
import by.yayauheny.util.Validator;

import java.util.List;
import java.util.Optional;

public class BankService implements Service<Integer, Bank> {

    private final BankDao bankDao;

    public BankService(BankDao bankDao) {
        this.bankDao = bankDao;
    }

    @Override
    public Optional<Bank> findById(Integer id) {
        Validator.validateId(id);

        return bankDao.findById(id);
    }

    @Override
    public List<Bank> findAll() {
        return null;
    }

    @Override
    public Bank save(Bank bank) {
        return null;
    }

    @Override
    public void update(Bank bank) {

    }

    @Override
    public boolean delete(Integer id) {
        return false;
    }


}
