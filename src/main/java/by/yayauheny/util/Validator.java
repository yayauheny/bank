package by.yayauheny.util;

import by.yayauheny.entity.Account;
import by.yayauheny.entity.Currency;
import by.yayauheny.entity.Receipt;
import by.yayauheny.entity.Transaction;
import by.yayauheny.entity.TransactionType;
import by.yayauheny.exception.IncorrectAmountException;
import by.yayauheny.exception.IncorrectPeriodException;
import by.yayauheny.exception.IncorrectTransactionTypeException;
import by.yayauheny.exception.InvalidIdException;
import by.yayauheny.exception.ReceiptBuildingException;
import by.yayauheny.exception.TransactionException;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@UtilityClass
public class Validator {

    public <T extends Number> boolean validateId(T id) {
        if (id == null || id.intValue() < 0) {
            throw new InvalidIdException("Cannot find by id = " + id);
        }

        return true;
    }

    public void validateAccountId(String id) {
        if (!id.matches("^([A-Z\\d]{4}\\s){6}[A-Z\\d]{4}$")) {
            throw new InvalidIdException("Cannot find account by id = " + id);
        }
    }

    //TODO:replace with transaction DTO
    public void validateTransactionFunds(Transaction transaction) {
        Account sender = transaction.getSenderAccount();
        Account receiver = transaction.getReceiverAccount();
        BigDecimal transactionAmount = transaction.getAmount();
        TransactionType transactionType = transaction.getType();

        switch (transactionType) {
            case TRANSFER -> {
                validateAmount(transactionAmount);
                if (sender.getBalance().compareTo(BigDecimal.ZERO) <= 0
                    || sender.getBalance().compareTo(transactionAmount) < 0) {
                    throw new TransactionException("Cannot perform transfer, try again.\nCurrent balance: " + sender.getBalance()
                                                   + "\nTransfer amount: " + transactionAmount);
                }
            }
            case WITHDRAWAL -> {
                validateAmount(transactionAmount);
                if (receiver.getBalance().compareTo(transactionAmount) < 0) {
                    throw new TransactionException("Cannot perform transfer, incorrect amount");
                }
            }
            case DEPOSIT -> validateAmount(transactionAmount);

            default -> throw new IncorrectTransactionTypeException();
        }
    }

    public void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IncorrectAmountException("Cannot perform transaction, amount: " + amount + " is negative or zero");
        }
    }

    public void validateTransactionsPeriod(LocalDateTime from, LocalDateTime to) {
        if (from.isAfter(to)) {
            throw new IncorrectPeriodException("Incorrect period has been passed, " + from + "is before " + to);
        } else if (to.isBefore(from)) {
            throw new IncorrectPeriodException("Incorrect period has been passed, " + to + "is after " + from);
        }
    }

    public void validateTransactionReceiver(Account receiver) {
        if (receiver == null) {
            throw new TransactionException("Exception while processing transaction. Receiver is empty. Try again");
        }
    }

    public void validateTransferParticipants(Account sender, Account receiver) {
        if (sender == null || receiver == null) {
            throw new TransactionException("Exception while processing transaction. Receiver is empty. Try again");
        }
    }

    public void validateTransactionCurrency(Currency currency) {
        if (currency == null) {
            throw new TransactionException("Exception while processing transaction. Currency is empty. Try again");
        }
    }

    public void validateTransaction(Transaction transaction) {
        validateTransactionFunds(transaction);
        validateTransactionCurrency(transaction.getCurrency());

        if (transaction.getType().equals(TransactionType.TRANSFER)) {
            validateTransferParticipants(transaction.getSenderAccount(), transaction.getReceiverAccount());
        } else {
            validateTransactionReceiver(transaction.getReceiverAccount());
        }
    }

    public void validateReceiptForCheck(Receipt receipt) {
        if (receipt.getTransactionType() == null
            || receipt.getCreatedAt() == null
            || receipt.getId() == null
            || receipt.getReceiverAccount() == null
            || receipt.getReceiverAccount().getBank() == null) {
            throw new ReceiptBuildingException();
        }
    }

    public void validateReceiptForMoneyStatement(Receipt receipt) {
        validateReceiptForCheck(receipt);
        if (receipt.getTransactionsFrom() == null || receipt.getTransactionsTo() == null) {
            throw new ReceiptBuildingException();
        }
    }

    //TODO: finish
    public void validateReceiptForSummary(Receipt receipt) {

    }

}
