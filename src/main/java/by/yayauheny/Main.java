package by.yayauheny;

import by.yayauheny.dao.BankDao;

public class Main {
    public static void main(String[] args) {

        BankDao.getInstance().delete(2);

    }


}