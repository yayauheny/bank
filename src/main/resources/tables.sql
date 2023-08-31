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

D


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
    currency_id INT       NOT NULL REFERENCES currency (id)
        ON UPDATE CASCADE,
    balance     NUMERIC(16, 2),
    created_at  TIMESTAMP NOT NULL
);

CREATE TABLE users_account
(
    user_id    INT REFERENCES users (id),
    account_id INT REFERENCES account (id),
    PRIMARY KEY (user_id, account_id)
);

CREATE TABLE transaction
(
    id                  INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type                VARCHAR(10)    NOT NULL CHECK (type IN ('TRANSFER', 'DEPOSIT', 'WITHDRAWAL')),
    status              VARCHAR(10) CHECK (status IN ('IN_PROCESS', 'COMPLETED', 'CANCELLED')),
    description         VARCHAR(128)   NOT NULL,
    amount              NUMERIC(16, 2) NOT NULL,
    created_at          TIMESTAMP      NOT NULL,
    currency_id         INT            NOT NULL REFERENCES currency (id),
    receiver_account_id INT            NOT NULL REFERENCES users (id),
    sender_account_id   INT REFERENCES users (id)
);

CREATE TABLE account_transaction
(
    account_id     INT REFERENCES account (id),
    transaction_id INT REFERENCES transaction (id),
    PRIMARY KEY (account_id, transaction_id)
);








