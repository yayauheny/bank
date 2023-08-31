package by.yayauheny.dao;

import by.yayauheny.entity.Currency;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static by.yayauheny.util.TestObjectsUtil.CURRENCIES;
import static by.yayauheny.util.TestObjectsUtil.TEST_CURRENCY;
import static by.yayauheny.util.TestObjectsUtil.USD_CURRENCY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurrencyDaoTest {
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    @Test
    void shouldReturnExistingCurrency() {
        Optional<Currency> actualResult = currencyDao.findById(USD_CURRENCY.getId());

        assertThat(actualResult).isPresent();
        assertEquals(USD_CURRENCY, actualResult.get());
    }

    @Test
    void shouldReturnAllExistingCurrencies() {
        List<Currency> actualResult = currencyDao.findAll();

        assertThat(actualResult).containsExactlyInAnyOrderElementsOf(CURRENCIES);
    }

    @Test
    void shouldNotExistInDatabase() {
        currencyDao.save(TEST_CURRENCY);
        boolean deleted = currencyDao.delete(TEST_CURRENCY.getId());

        assertTrue(deleted);
    }

    @Test
    void shouldSaveCurrencyCorrectly() {
        Currency savedCurrency = currencyDao.save(TEST_CURRENCY);

        assertEquals(TEST_CURRENCY, savedCurrency);
        currencyDao.delete(savedCurrency.getId());
    }

    @Test
    void shouldUpdateCurrencyCorrectly() {
        currencyDao.save(TEST_CURRENCY);
        TEST_CURRENCY.setCurrencyRate(new BigDecimal("555555.99"));

        currencyDao.update(TEST_CURRENCY);
        Optional<Currency> actualResult = currencyDao.findById(TEST_CURRENCY.getId());

        assertThat(actualResult).isPresent();
        assertEquals(TEST_CURRENCY, actualResult.get());
        currencyDao.delete(TEST_CURRENCY.getId());
    }
}