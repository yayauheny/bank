package by.yayauheny.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {

    private Integer id;
    private LocalDateTime createdAt;
    private LocalDateTime transactionsFrom;
    private LocalDateTime transactionsTo;
    private String receiptName;
    private TransactionType transactionType;
    private Account senderAccount;
    private Account receiverAccount;
    private BigDecimal amount;
}
