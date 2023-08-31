package by.yayauheny;

import by.yayauheny.dao.BankDao;

public class Main {
    public static void main(String[] args) {
        System.out.println(BankDao.getInstance().findById(1));
    }
}