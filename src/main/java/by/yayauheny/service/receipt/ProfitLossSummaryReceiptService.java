package by.yayauheny.service.receipt;

import by.yayauheny.entity.Receipt;
import by.yayauheny.service.TransactionService;
import by.yayauheny.util.Validator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfitLossSummaryReceiptService extends ReceiptTemplateService {

    private static final ProfitLossSummaryReceiptService receiptService = new ProfitLossSummaryReceiptService();
    private final TransactionService transactionService = TransactionService.getInstance();

    @Override
    public String buildTemplate(Receipt receipt) {
        Validator.validateReceiptForSummary(receipt);
        BigDecimal profit = transactionService.getTotalProfitByPeriod(receipt.getReceiverAccount().getId(),
                receipt.getTransactionsFrom(), receipt.getTransactionsTo());
        BigDecimal loss = transactionService.getTotalLossByPeriod(receipt.getReceiverAccount().getId(),
                receipt.getTransactionsFrom(), receipt.getTransactionsTo());
        String profitLossSummary = buildProfitLossSummary(receipt, profit, loss);

        return buildStatementTemplate(receipt, profitLossSummary);
    }

    private String buildProfitLossSummary(Receipt receipt, BigDecimal profit, BigDecimal loss) {
        String separator = "-".repeat(64);
        return """
                %s | %s
                %s
                %s | %s
                """.formatted(
                StringUtils.center("Приход", 35),
                StringUtils.center("Уход", 35),
                separator,
                StringUtils.center(profit + " "
                                   + receipt.getReceiverAccount().getCurrency().getCurrencyCode(), 29),
                StringUtils.center(loss + " "
                                   + receipt.getReceiverAccount().getCurrency().getCurrencyCode(), 30)
        );
    }

    public static ProfitLossSummaryReceiptService getInstance() {
        return receiptService;
    }
}
