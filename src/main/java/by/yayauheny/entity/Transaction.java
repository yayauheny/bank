package by.yayauheny.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    private Integer id;
    private TransactionType type;
    @Builder.Default
    private BigDecimal amount = BigDecimal.ZERO;
    private Integer currencyId;
    private String receiverAccountId;
    private String senderAccountId;
    private Currency currency;
    private Account receiverAccount;
    private Account senderAccount;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

