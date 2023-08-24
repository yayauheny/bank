package by.yayauheny.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Currency {

    private Integer id;
    private String name;
    private double currencyRate;
}
