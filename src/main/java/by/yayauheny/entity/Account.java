package by.yayauheny.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    private Integer id;
    private Integer bankId;
    private Integer currencyId;
    private Integer userId;
    private Bank bank;
    private Currency currency;
    private User user;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime expirationDate = LocalDateTime.now().plusYears(5);
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;
    private List<Transaction> transactions;
}
