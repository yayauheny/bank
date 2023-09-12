CREATE TABLE bank
(
    id         INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name       VARCHAR(128) UNIQUE NOT NULL,
    address    VARCHAR(128) UNIQUE NOT NULL,
    department VARCHAR(32) UNIQUE  NOT NULL
);

CREATE TABLE currency
(
    id            INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    currency_code VARCHAR(3) UNIQUE NOT NULL,
    currency_rate NUMERIC(16, 2)    NOT NULL
);

CREATE TABLE users
(
    id            INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    full_name     VARCHAR(256) NOT NULL,
    date_of_birth DATE         NOT NULL,
    address       VARCHAR(128) NOT NULL
);

CREATE TABLE account
(
    id              VARCHAR(34) PRIMARY KEY,
    bank_id         INT       NOT NULL REFERENCES bank (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    user_id         INT       NOT NULL REFERENCES users (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    currency_id     INT       NOT NULL REFERENCES currency (id)
        ON UPDATE CASCADE,
    balance         NUMERIC(16, 2),
    created_at      TIMESTAMP NOT NULL,
    expiration_date TIMESTAMP NOT NULL
);

CREATE TABLE transaction
(
    id                  INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type                VARCHAR(10)    NOT NULL CHECK (type IN ('TRANSFER', 'DEPOSIT', 'WITHDRAWAL')),
    amount              NUMERIC(16, 2) NOT NULL,
    created_at          TIMESTAMP      NOT NULL,
    currency_id         INT            NOT NULL REFERENCES currency (id)
        ON DELETE NO ACTION
        ON UPDATE CASCADE,
    receiver_account_id VARCHAR(34)    NOT NULL REFERENCES account (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    sender_account_id   VARCHAR(34) REFERENCES account (id)
);

INSERT INTO bank(name, address, department)
VALUES ('BELINVEST', 'Kalvariyskaya 14', 'MINSK-12'),
       ('BELARUSBANK', 'Lenina 32, 5', 'MINSK-153');

INSERT INTO currency(currency_code, currency_rate)
VALUES ('USD', 3.23),
       ('EUR', 3.69),
       ('BYN', 1);

INSERT INTO users(full_name, date_of_birth, address)
VALUES ('Ivanov Ivan Ivanovich', '1993.05.10', 'Minsk, Belarus'),
       ('Pollo Andrew Johnson', '2000.11.3', 'New York, USA'),
       ('Smith John Michael', '1988-07-15', 'Los Angeles, USA'),
       ('Brown Emily Davis', '1995-03-22', 'London, UK'),
       ('Lee David Kim', '1990-09-18', 'Seoul, South Korea');

INSERT INTO account (id, bank_id, user_id, currency_id, balance, created_at, expiration_date)
VALUES ('HT5Y 7C30 28EA KNDU 3YY5 WYTT 8U7Y', 2, 1, 2, 568.00, '2022-08-15 09:30', '2027-08-15 09:30'),
       ('3YY5 WYTT 8U7Y ABCD 1234 5678 90AB', 1, 1, 1, 124.00, '2023-02-28 14:15', '2028-02-28 14:15'),
       ('WXYZ UVWX YZ01 2345 PQRS TUVW XYZ2', 2, 2, 3, 788.00, '2021-11-10 17:45', '2026-11-10 17:45'),
       ('QWER TYUI OP12 MNOP QRST UV34 5678', 2, 2, 2, 234.00, '2022-05-20 11:00', '2027-05-20 11:00'),
       ('QRST UV34 5678 90AB CDEF 1234 5678', 2, 3, 1, 988.00, '2023-09-01 08:30', '2028-09-01 08:30'),
       ('CDEF 1234 5678 90AB GHIJ KLMN OP56', 1, 3, 3, 322.00, '2021-12-05 13:45', '2026-12-05 13:45'),
       ('GHIJ KLMN OP56 2345 6789 AB01 YZ01', 2, 4, 1, 654.00, '2023-06-12 10:20', '2028-06-12 10:20'),
       ('6789 AB01 YZ01 2345 PQRS TUVW XYZ2', 1, 4, 2, 876.00, '2022-03-25 16:10', '2027-03-25 16:10'),
       ('YZ01 2345 6789 AB01 6789 AB01 YZ01', 2, 5, 1, 456.00, '2023-08-08 12:05', '2028-08-08 12:05'),
       ('PQRS TUVW XYZ2 ABCD 1234 5678 90AB', 1, 5, 3, 544.00, '2022-09-30 09:50', '2027-09-30 09:50');






