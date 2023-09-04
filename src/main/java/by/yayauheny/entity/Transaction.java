package by.yayauheny.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class Transaction {

    private Integer id;
    private TransactionType type;
    @Builder.Default
    private BigDecimal amount = BigDecimal.ZERO;
    private Integer currencyId;
    private Integer receiverAccountId;
    private Integer senderAccountId;
    private Currency currency;
    private Account receiver;
    private Account sender;
    @Builder.Default
    private LocalDate createdAt = LocalDate.now();

    @Builder
    public Transaction(Integer id, String type, BigDecimal amount, Integer currencyId, Integer receiverAccountId, Integer senderAccountId, Currency currency, Account receiver, Account sender, LocalDate createdAt) {
        this.id = id;
        this.type = TransactionType.valueOf(type);
        this.amount = amount;
        this.currencyId = currencyId;
        this.receiverAccountId = receiverAccountId;
        this.senderAccountId = senderAccountId;
        this.currency = currency;
        this.receiver = receiver;
        this.sender = sender;
        this.createdAt = createdAt;
    }
}
