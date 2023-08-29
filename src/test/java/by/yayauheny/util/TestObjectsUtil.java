package by.yayauheny.util;

import by.yayauheny.entity.Bank;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestObjectsUtil {

    public static final Bank BELINVESTBANK =
            Bank.builder()
                    .id(1)
                    .name("BELINVEST")
                    .address("Kalvariyskaya 14")
                    .department("MINSK-12")
                    .build();
}
