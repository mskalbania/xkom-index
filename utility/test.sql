CREATE TABLE PRODUCT(id SERIAL PRIMARY KEY NOT NULL, name VARCHAR(150) NOT NULL, provider_id VARCHAR (30) NOT NULL, url VARCHAR NOT NULL);
CREATE TABLE PRICE(id SERIAL PRIMARY KEY NOT NULL, price DECIMAL NOT NULL, timestamp TIMESTAMP NOT NULL, product_id INTEGER, FOREIGN KEY (product_id) REFERENCES PRODUCT(id));
