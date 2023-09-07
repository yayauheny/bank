package by.yayauheny.service;

import by.yayauheny.dao.BankDao;
import by.yayauheny.entity.Bank;
import by.yayauheny.util.Validator;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class BankService implements Service<Integer, Bank> {

    private final BankDao bankDao;

    public BankService() {
        this.bankDao = BankDao.getInstance();
    }

    @Override
    public Optional<Bank> findById(Integer id) {
        Validator.validateId(id);

        return bankDao.findById(id);
    }

    @Override
    public List<Bank> findAll() {
        return bankDao.findAll();
    }

    @Override
    public Bank save(Bank bank) {
        return bankDao.save(bank);
    }

    @Override
    public void update(Bank bank) {
        bankDao.update(bank);
    }

    @Override
    public boolean delete(Integer id) {
        return bankDao.delete(id);
    }
}
