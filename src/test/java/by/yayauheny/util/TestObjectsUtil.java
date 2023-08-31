package by.yayauheny.util;

import by.yayauheny.entity.Bank;
import by.yayauheny.entity.Currency;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.List;

@UtilityClass
public class TestObjectsUtil {

    public static final Bank BELINVESTBANK =
            Bank.builder()
                    .id(1)
                    .name("BELINVEST")
                    .address("Kalvariyskaya 14")
                    .department("MINSK-12")
                    .build();
    public static final Bank BELARUSBANK =
            Bank.builder()
                    .id(2)
                    .name("BELARUSBANK")
                    .address("Lenina 32, 5")
                    .department("MINSK-153")
                    .build();
    public static final Bank TEST_BANK =
            Bank.builder()
                    .name("Test")
                    .address("No address")
                    .department("null")
                    .build();
    public static final Currency USD_CURRENCY =
            Currency.builder()
                    .id(1)
                    .currencyCode("USD")
                    .currencyRate(new BigDecimal("3.2200"))
                    .build();
    public static final Currency EUR_CURRENCY =
            Currency.builder()
                    .id(2)
                    .currencyCode("EUR")
                    .currencyRate(new BigDecimal("3.4900"))
                    .build();

    public static final Currency TEST_CURRENCY =
            Currency.builder()
                    .currencyCode("NNN")
                    .currencyRate(new BigDecimal("0.0000"))
                    .build();

    public static final List<Bank> BANKS = List.of(BELINVESTBANK, BELARUSBANK);
    public static final List<Currency> CURRENCIES = List.of(USD_CURRENCY, EUR_CURRENCY);

}
