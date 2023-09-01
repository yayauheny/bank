package by.yayauheny.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class Transaction {

    private Integer id;
    private TransactionType type;
    private TransactionStatus status;
    private BigDecimal amount;
    private String description;
    private Integer currencyId;
    private Integer receiverAccountId;
    private Integer senderAccountId;
    private Currency currency;
    private Account receiver;
    private Account sender;
    private LocalDate createdAt;
}
