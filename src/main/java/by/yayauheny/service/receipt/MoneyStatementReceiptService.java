package by.yayauheny.service.receipt;

import by.yayauheny.entity.Account;
import by.yayauheny.entity.Receipt;
import by.yayauheny.entity.Transaction;
import by.yayauheny.service.TransactionService;
import by.yayauheny.util.DateTimeUtils;
import by.yayauheny.util.Validator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MoneyStatementReceiptService extends ReceiptTemplateService {

    private static final MoneyStatementReceiptService receiptService = new MoneyStatementReceiptService();

    @Override
    public String buildTemplate(Receipt receipt) {
        Validator.validateReceiptForMoneyStatement(receipt);
        List<Transaction> transactions = TransactionService.getInstance()
                .findAllByPeriod(receipt.getReceiverAccount(), receipt.getTransactionsFrom(), receipt.getTransactionsTo());
        String transactionPart = buildTransactionPart(transactions, receipt.getReceiverAccount());
        String separator = "-".repeat(65);
        String s = """
                %s | %s | %s
                %s
                %s
                """.formatted(StringUtils.center("Дата", 16),
                StringUtils.center("Примечание", 49),
                StringUtils.rightPad("Сумма", 16),
                separator,
                transactionPart);
        return buildStatementTemplate(receipt, s);
    }

    //TODO: use DTO
    private String buildTransactionPart(List<Transaction> transactions, Account owner) {
        StringBuilder sb = new StringBuilder();

        for (Transaction transaction : transactions) {
            String transactionTemplate = "%s | %s | %s\n";
            String positiveAmount = StringUtils.rightPad(transaction.getAmount()
                                                         + " " + transaction.getCurrency().getCurrencyCode(), 20);
            String negativeAmount = StringUtils.rightPad(transaction.getAmount().negate()
                                                         + " " + transaction.getCurrency().getCurrencyCode(), 20);
            switch (transaction.getType()) {
                case TRANSFER -> {
                    String transactionSenderId = transaction.getSenderAccount().getId();
                    if (transactionSenderId.equals(owner.getId())) {
                        sb.append(transactionTemplate.formatted(StringUtils.center(DateTimeUtils.parseDate(transaction.getCreatedAt()), 12),
                                StringUtils.rightPad("Перевод для " + transaction.getReceiverAccount().getUser().getFullName(),
                                        48),
                                negativeAmount));
                    } else {
                        sb.append(transactionTemplate.formatted(StringUtils.center(DateTimeUtils.parseDate(transaction.getCreatedAt()), 12),
                                StringUtils.rightPad("Перевод от " + transaction.getSenderAccount().getUser().getFullName(),
                                        48),
                                positiveAmount));
                    }
                }
                case DEPOSIT ->
                        sb.append(transactionTemplate.formatted(StringUtils.center(DateTimeUtils.parseDate(transaction.getCreatedAt()), 12),
                                StringUtils.rightPad("Пополнение средств", 48),
                                positiveAmount));
                case WITHDRAWAL ->
                        sb.append(transactionTemplate.formatted(StringUtils.center(DateTimeUtils.parseDate(transaction.getCreatedAt()), 12),
                                StringUtils.rightPad("Снятие средств", 48),
                                negativeAmount));
            }
        }

        return sb.toString();
    }

    public static MoneyStatementReceiptService getInstance() {
        return receiptService;
    }
}
