package by.yayauheny;

import by.yayauheny.entity.Account;
import by.yayauheny.entity.Transaction;
import by.yayauheny.entity.TransactionType;
import by.yayauheny.service.AccountService;

import java.math.BigDecimal;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        AccountService accountService = new AccountService();
        System.out.println("start: \n\n");
        Optional<Account> maybeSenderAccount = accountService.findById("YZ01 2345 6789 AB01 6789 AB01 YZ01");
        Optional<Account> maybeReceiverAccount = accountService.findById("HT5Y 7C30 28EA KNDU 3YY5 WYTT 8U7Y");

        if (maybeSenderAccount.isPresent() && maybeReceiverAccount.isPresent()) {
            Account sender = maybeSenderAccount.get();
            Account receiver = maybeReceiverAccount.get();
            System.out.println("Sender balance = " + sender.getBalance() + " " + sender.getCurrency().getCurrencyCode());
            System.out.println("Receiver balance = " + receiver.getBalance() + " " + receiver.getCurrency().getCurrencyCode());

            Transaction transaction = Transaction.builder()
                    .amount(new BigDecimal("100"))
                    .currency(sender.getCurrency())
                    .senderAccount(sender)
                    .receiverAccount(receiver)
                    .senderAccountId(sender.getId())
                    .receiverAccountId(receiver.getId())
                    .type(TransactionType.TRANSFER)
                    .currencyId(sender.getCurrency().getId())
                    .build();
            accountService.processTransactionAndUpdateAccount(transaction);
        }
        Optional<Account> maybeSenderAccountF = accountService.findById("YZ01 2345 6789 AB01 6789 AB01 YZ01");
        Optional<Account> maybeReceiverAccountF = accountService.findById("HT5Y 7C30 28EA KNDU 3YY5 WYTT 8U7Y");
        if (maybeReceiverAccountF.isPresent() && maybeSenderAccountF.isPresent()) {
            Account sender = maybeSenderAccountF.get();
            Account receiver = maybeReceiverAccountF.get();
            System.out.println("Sender balance = " + sender.getBalance() + " " + sender.getCurrency().getCurrencyCode());
            System.out.println("Receiver balance = " + receiver.getBalance() + " " + receiver.getCurrency().getCurrencyCode());
        }

        System.out.println("finish. \n\n");
    }
}