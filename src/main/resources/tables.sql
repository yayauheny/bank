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
    id          INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    bank_id     INT       NOT NULL REFERENCES bank (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    user_id     INT       NOT NULL REFERENCES users (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    currency_id INT       NOT NULL REFERENCES currency (id)
        ON UPDATE CASCADE,
    balance     NUMERIC(16, 2),
    created_at  TIMESTAMP NOT NULL
);

CREATE TABLE transaction
(
    id                  INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type                VARCHAR(10)    NOT NULL CHECK (type IN ('TRANSFER', 'DEPOSIT', 'WITHDRAWAL')),
    status              VARCHAR(10) CHECK (status IN ('IN_PROCESS', 'COMPLETED', 'CANCELLED')),
    description         VARCHAR(128)   NOT NULL,
    amount              NUMERIC(16, 2) NOT NULL,
    created_at          TIMESTAMP      NOT NULL,
    currency_id         INT            NOT NULL REFERENCES currency (id)
        ON DELETE NO ACTION
        ON UPDATE CASCADE,
    receiver_account_id INT            NOT NULL REFERENCES users (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    sender_account_id   INT REFERENCES users (id)
);

INSERT INTO bank(name, address, department)
VALUES ('BELINVEST', 'Kalvariyskaya 14', 'MINSK-12'),
       ('BELARUSBANK', 'Lenina 32, 5', 'MINSK-153');

INSERT INTO currency(currency_code, currency_rate)
VALUES ('USD', 3.22),
       ('EUR', 3.49);

INSERT INTO users(full_name, date_of_birth, address)
VALUES ('Ivanov Ivan Ivanovich', '1993.05.10', 'Minsk, Belarus'),
       ('Pollo Andrew Johnson', '2000.11.3', 'New York, USA');






