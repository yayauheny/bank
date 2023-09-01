package by.yayauheny.entity;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class Account {

    private Integer id;
    private Integer bankId;
    private Integer currencyId;
    private LocalDate createdAt;
    private BigDecimal balance;
    private Integer userId;
    private List<Transaction> transactions;
}
