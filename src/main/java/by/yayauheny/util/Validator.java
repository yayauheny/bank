package by.yayauheny.util;

import by.yayauheny.entity.Account;
import by.yayauheny.entity.Transaction;
import by.yayauheny.exception.IncorrectAmountException;
import by.yayauheny.exception.IncorrectPeriodException;
import by.yayauheny.exception.InvalidIdException;
import by.yayauheny.exception.TransactionException;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;

@UtilityClass
public class Validator {

    public <T extends Number> boolean validateId(T id) throws InvalidIdException {
        if (id == null || id.intValue() < 0) {
            throw new InvalidIdException();
        }

        return true;
    }

    public static void validateTransaction(Transaction transaction) throws TransactionException {
        Account sender = transaction.getSender();
        Account receiver = transaction.getReceiver();
        BigDecimal transactionAmount = transaction.getAmount();

        switch (transaction.getType()) {
            case TRANSFER -> {
                if (sender.getBalance().compareTo(BigDecimal.ZERO) <= 0
                    || sender.getBalance().compareTo(transactionAmount) < 0
                    || transactionAmount.compareTo(BigDecimal.ZERO) < 0) {
                    throw new TransactionException("Cannot perform transfer, try again.\nCurrent balance: " + sender.getBalance()
                                                   + "\nTransfer amount: " + transactionAmount);
                }
            }

            case WITHDRAWAL -> {
                if (receiver.getBalance().compareTo(transactionAmount) < 0
                    || transactionAmount.compareTo(BigDecimal.ZERO) < 0) {
                    throw new TransactionException("Cannot perform transfer, incorrect amount");
                }
            }
        }
    }

    public void validateAmount(BigDecimal amount) throws IncorrectAmountException {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IncorrectAmountException("Cannot perform transaction, amount: " + amount + " is negative");
        }
    }

    public void validateTransactionsPeriod(LocalDate from, LocalDate to) throws IncorrectPeriodException {
        if (from.isAfter(to)) {
            throw new IncorrectPeriodException("Incorrect period has been passed, " + from + "is before " + to);
        } else if (to.isBefore(from)) {
            throw new IncorrectPeriodException("Incorrect period has been passed, " + to + "is after " + from);
        }
    }
}
