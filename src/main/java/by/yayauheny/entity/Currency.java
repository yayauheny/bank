package by.yayauheny.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Currency {

    private Integer id;
    private String currencyCode;
    private BigDecimal currencyRate;
}
