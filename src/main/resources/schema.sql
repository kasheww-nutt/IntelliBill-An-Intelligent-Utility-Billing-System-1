CREATE DATABASE IF NOT EXISTS intellibill_db;
USE intellibill_db;

CREATE TABLE IF NOT EXISTS consumers (
    consumer_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    consumer_type ENUM('RESIDENTIAL', 'COMMERCIAL', 'INDUSTRIAL') NOT NULL
);

CREATE TABLE IF NOT EXISTS services (
    service_id INT PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    service_type ENUM('ELECTRICITY', 'WATER', 'INTERNET') NOT NULL
);

CREATE TABLE IF NOT EXISTS bills (
    bill_id INT PRIMARY KEY,
    consumer_id INT NOT NULL,
    service_id INT NOT NULL,
    previous_reading DOUBLE NOT NULL,
    current_reading DOUBLE NOT NULL,
    units_consumed DOUBLE NOT NULL,
    amount DOUBLE NOT NULL,
    penalty DOUBLE NOT NULL DEFAULT 0,
    paid_amount DOUBLE NOT NULL DEFAULT 0,
    bill_date DATE NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_bill_consumer FOREIGN KEY (consumer_id) REFERENCES consumers(consumer_id) ON DELETE CASCADE,
    CONSTRAINT fk_bill_service FOREIGN KEY (service_id) REFERENCES services(service_id)
);

CREATE TABLE IF NOT EXISTS payments (
    payment_id INT PRIMARY KEY,
    bill_id INT NOT NULL,
    amount_paid DOUBLE NOT NULL,
    payment_date DATE NOT NULL,
    mode VARCHAR(30) NOT NULL,
    CONSTRAINT fk_payment_bill FOREIGN KEY (bill_id) REFERENCES bills(bill_id) ON DELETE CASCADE
);
