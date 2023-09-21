package by.yayauheny;

import by.yayauheny.entity.Account;
import by.yayauheny.entity.Receipt;
import by.yayauheny.service.AccountService;
import by.yayauheny.service.TransactionService;
import by.yayauheny.service.receipt.ProfitLossSummaryReceiptService;
import by.yayauheny.service.receipt.ReceiptTemplateService;

import java.time.LocalDateTime;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        AccountService accountService = AccountService.getInstance();
        TransactionService transactionService = TransactionService.getInstance();


        System.out.println("start: \n\n");
//        Optional<Account> maybeSenderAccount = accountService.findById("YZ01 2345 6789 AB01 6789 AB01 YZ01");
//        Optional<Account> maybeReceiverAccount = accountService.findById("HT5Y 7C30 28EA KNDU 3YY5 WYTT 8U7Y");
//
//        if (maybeSenderAccount.isPresent() && maybeReceiverAccount.isPresent()) {
//            Account sender = maybeSenderAccount.get();
//            Account receiver = maybeReceiverAccount.get();
//            System.out.println("Sender balance = " + sender.getBalance() + " " + sender.getCurrency().getCurrencyCode());
//            System.out.println("Receiver balance = " + receiver.getBalance() + " " + receiver.getCurrency().getCurrencyCode());
//
//            Transaction transaction = Transaction.builder()
//                    .amount(new BigDecimal("100"))
//                    .currency(sender.getCurrency())
//                    .senderAccount(sender)
//                    .receiverAccount(receiver)
//                    .senderAccountId(sender.getId())
//                    .receiverAccountId(receiver.getId())
//                    .type(TransactionType.TRANSFER)
//                    .currencyId(sender.getCurrency().getId())
//                    .build();
//            accountService.processTransactionAndUpdateAccount(transaction);
//        }
        Optional<Account> maybeSenderAccountF = accountService.findById("CDEF 1234 5678 90AB GHIJ KLMN OP56");
        Optional<Account> maybeReceiverAccountF = accountService.findById("QRST UV34 5678 90AB CDEF 1234 5678");

        if (maybeReceiverAccountF.isPresent() && maybeSenderAccountF.isPresent()) {
            Account sender = maybeSenderAccountF.get();
            Account receiver = maybeReceiverAccountF.get();
            System.out.println("Sender balance = " + sender.getBalance() + " " + sender.getCurrency().getCurrencyCode());
            System.out.println("Receiver balance = " + receiver.getBalance() + " " + receiver.getCurrency().getCurrencyCode());

//            Transaction transaction = Transaction.builder()
//                    .amount(new BigDecimal("50"))
//                    .currency(sender.getCurrency())
//                    .senderAccount(sender)
//                    .receiverAccount(receiver)
//                    .senderAccountId(sender.getId())
//                    .receiverAccountId(receiver.getId())
//                    .type(TransactionType.TRANSFER)
//                    .currencyId(sender.getCurrency().getId())
//                    .build();
//            accountService.processTransactionAndUpdateAccount(transaction);

            ReceiptTemplateService service = ProfitLossSummaryReceiptService.getInstance();
            String s = service.buildReceipt(Receipt.builder()
                    .id(5)
                    .createdAt(LocalDateTime.now())
                    .receiverAccount(receiver)
                    .senderAccount(sender)
                    .transactionsFrom(receiver.getCreatedAt())
                    .transactionsTo(LocalDateTime.now())
                    .build());
            System.out.println(s);

            Optional<Account> maybeSenderAccountA = accountService.findById("CDEF 1234 5678 90AB GHIJ KLMN OP56");
            Optional<Account> maybeReceiverAccountA = accountService.findById("QRST UV34 5678 90AB CDEF 1234 5678");
            System.out.println("Sender balance = " + maybeSenderAccountA.get().getBalance() + " " + maybeSenderAccountA.get().getCurrency().getCurrencyCode());
            System.out.println("Receiver balance = " + maybeReceiverAccountA.get().getBalance() + " " + maybeReceiverAccountA.get().getCurrency().getCurrencyCode());
        }
    }
}