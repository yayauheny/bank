package by.yayauheny.service.receipt;

import by.yayauheny.entity.Account;
import by.yayauheny.entity.Receipt;
import by.yayauheny.entity.TransactionType;
import by.yayauheny.util.DateTimeUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Getter
@Setter
public abstract class ReceiptTemplateService {

    private final Map<TransactionType, String> transactionTypesMap = Map.ofEntries(
            Map.entry(TransactionType.TRANSFER, "Перевод"),
            Map.entry(TransactionType.WITHDRAWAL, "Снятие"),
            Map.entry(TransactionType.DEPOSIT, "Пополнение")
    );

    public String buildReceipt(Receipt receipt) {
        String completedReceipt = buildTemplate(receipt);
        saveReceiptAsTxt(completedReceipt);

        return completedReceipt;
    }

    private void saveReceiptAsTxt(String receipt) {
        //do save
    }

    protected String buildStatementTemplate(Receipt receipt, String secondPart) {
        Account receiverAccount = receipt.getReceiverAccount();
        String separator = "-".repeat(65);

        return """
                %s
                %s
                %s
                %s
                %s | %s
                %s | %s
                %s | %s
                %s | %s
                %s | %s
                %s | %s
                %s | %s
                %s
                """.formatted(
                separator,
                StringUtils.center("Выписка", 80),
                StringUtils.center(receiverAccount.getBank().getName(), 73),
                separator,
                StringUtils.rightPad("Клиент", 39),
                receiverAccount.getUser().getFullName(),
                StringUtils.rightPad("Счет", 37),
                receiverAccount.getId(),
                StringUtils.rightPad("Валюта", 39),
                receiverAccount.getCurrency().getCurrencyCode(),
                StringUtils.rightPad("Дата открытия", 45),
                DateTimeUtils.parseDate(receiverAccount.getCreatedAt()),
                StringUtils.rightPad("Период", 39),
                (DateTimeUtils.parseDate(receiverAccount.getCreatedAt())
                 + "-" + DateTimeUtils.parseDate(receiverAccount.getExpirationDate())),
                StringUtils.rightPad("Дата и время формирования", 55),
                (DateTimeUtils.parseDate(receipt.getCreatedAt())
                 + ", " + DateTimeUtils.parseTime(receipt.getCreatedAt())),
                StringUtils.rightPad("Остаток", 40),
                receiverAccount.getBalance() + " " + receiverAccount.getCurrency().getCurrencyCode(),
                secondPart);
    }

    //TODO: finish logic
    public abstract String buildTemplate(Receipt receipt);
}
