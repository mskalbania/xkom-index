CREATE TABLE PRODUCT
(
    id          INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,
    name        VARCHAR(150)                       NOT NULL,
    provider_id VARCHAR(30)                        NOT NULL,
    url         VARCHAR                            NOT NULL
);
CREATE TABLE PRICE
(
    id         INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,
    price      DECIMAL                            NOT NULL,
    timestamp  TIMESTAMP                          NOT NULL,
    product_id INT                                NOT NULL,
    FOREIGN KEY (product_id) REFERENCES PRODUCT (id)
);

INSERT INTO PRODUCT (name, provider_id, url)
VALUES ('name', 'providerId', 'url');

INSERT INTO PRICE (price, timestamp, product_id)
VALUES (10.0, '2020-02-01T20:02', 1);