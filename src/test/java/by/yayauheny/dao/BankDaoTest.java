package by.yayauheny.dao;

import by.yayauheny.entity.Bank;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static by.yayauheny.util.TestObjectsUtil.BELINVESTBANK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BankDaoTest {
    private final BankDao bankDao = BankDao.getInstance();

    @Test
    void shouldReturnExistingBank(){
        Optional<Bank> actualResult = bankDao.findById(BELINVESTBANK.getId());

        assertThat(actualResult).isPresent();
        assertEquals(BELINVESTBANK, actualResult.get());
    }

}