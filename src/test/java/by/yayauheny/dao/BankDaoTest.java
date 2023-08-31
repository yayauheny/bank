package by.yayauheny.dao;

import by.yayauheny.entity.Bank;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static by.yayauheny.util.TestObjectsUtil.BANKS;
import static by.yayauheny.util.TestObjectsUtil.BELINVESTBANK;
import static by.yayauheny.util.TestObjectsUtil.TEST_BANK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BankDaoTest {
    private final BankDao bankDao = BankDao.getInstance();

    @Test
    void shouldReturnExistingBank() {
        Optional<Bank> actualResult = bankDao.findById(BELINVESTBANK.getId());

        assertThat(actualResult).isPresent();
        assertEquals(BELINVESTBANK, actualResult.get());
    }

    @Test
    void shouldReturnAllExistingBanks() {
        List<Bank> actualResult = bankDao.findAll();

        assertThat(actualResult).containsExactlyInAnyOrderElementsOf(BANKS);
    }

    @Test
    void shouldNotExistInDatabase() {
        bankDao.save(TEST_BANK);
        boolean deleted = bankDao.delete(TEST_BANK.getId());

        assertTrue(deleted);
    }

    @Test
    void shouldSaveBankCorrectly() {
        Bank savedBank = bankDao.save(TEST_BANK);

        assertEquals(TEST_BANK, savedBank);
        bankDao.delete(savedBank.getId());
    }

    @Test
    void shouldUpdateBankDepartmentCorrectly() {
        BELINVESTBANK.setDepartment("555");

        bankDao.update(BELINVESTBANK);
        Optional<Bank> actualResult = bankDao.findById(BELINVESTBANK.getId());

        assertThat(actualResult).isPresent();
        assertEquals(BELINVESTBANK, actualResult.get());
    }

    @Test
    void update() {
    }


}