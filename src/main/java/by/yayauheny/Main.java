package by.yayauheny;

import by.yayauheny.entity.Transaction;

public class Main {
    public static void main(String[] args) {
//        System.out.println(BankDao.getInstance().findById(1));
        System.out.println(Transaction.builder()
                .type("transfer")
                .build());
    }
}