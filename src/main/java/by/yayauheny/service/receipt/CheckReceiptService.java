package by.yayauheny.service.receipt;

import by.yayauheny.entity.Receipt;
import by.yayauheny.entity.TransactionType;
import by.yayauheny.util.DateTimeUtils;
import by.yayauheny.util.Validator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckReceiptService extends ReceiptTemplateService {

    private static final CheckReceiptService receiptService = new CheckReceiptService();
    private final Map<TransactionType, String> transactionTypesMap = this.getTransactionTypesMap();

    @Override
    public String buildTemplate(Receipt receipt) {
        Validator.validateReceiptForCheck(receipt);
        String line = "-".repeat(68);

        return new StringBuilder()
                .append("""
                        %s
                        | %s |
                        | Чек: %s |
                        | %s %s |
                        | Тип транзакции: %s |
                        """.formatted(
                        line,
                        StringUtils.center("Банковский чек", 78),
                        StringUtils.leftPad(String.valueOf(receipt.getId()), 60),
                        DateTimeUtils.parseDate(receipt.getCreatedAt()),
                        StringUtils.leftPad(DateTimeUtils.parseTime(receipt.getCreatedAt()), 54),
                        StringUtils.leftPad(transactionTypesMap.get(receipt.getTransactionType()), 56))
                )
                .append(buildTransactionPart(receipt))
                .append("| Сумма: %s %S |".formatted(StringUtils.leftPad(receipt.getAmount().setScale(2).toString(), 54),
                        receipt.getSenderAccount().getCurrency().getCurrencyCode()))
                .append("\n")
                .append(line)
                .toString();
    }

    private String buildTransactionPart(Receipt receipt) {
        return switch (receipt.getTransactionType()) {
            case TRANSFER -> ("""
                    | Банк отправителя: %s |
                    | Банк получателя: %s |
                    | Счет отправителя: %s |
                    | Счет получателя: %s |
                    """.formatted(StringUtils.leftPad(receipt.getSenderAccount().getBank().getName(), 47),
                    StringUtils.leftPad(receipt.getReceiverAccount().getBank().getName(), 48),
                    StringUtils.leftPad(receipt.getSenderAccount().getId(), 47),
                    StringUtils.leftPad(receipt.getReceiverAccount().getId(), 48))
            );
            case DEPOSIT, WITHDRAWAL -> ("""
                    | Банк получателя: %s |
                    | Счет получателя: %s |
                    """.formatted(StringUtils.leftPad(receipt.getReceiverAccount().getBank().getName(), 48),
                    StringUtils.leftPad(receipt.getReceiverAccount().getId(), 48))
            );
        };
    }

    public static CheckReceiptService getInstance() {
        return receiptService;
    }
}
