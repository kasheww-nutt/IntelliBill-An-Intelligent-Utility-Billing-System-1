package com.intellibill.database;

import com.intellibill.model.Payment;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {
    public void upsert(Payment payment) throws Exception {
        String sql = """
                INSERT INTO payments (payment_id, bill_id, amount_paid, payment_date, mode)
                VALUES (?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                amount_paid = VALUES(amount_paid),
                payment_date = VALUES(payment_date),
                mode = VALUES(mode)
                """;

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, payment.getPaymentId());
            statement.setInt(2, payment.getBillId());
            statement.setDouble(3, payment.getAmountPaid());
            statement.setDate(4, Date.valueOf(payment.getPaymentDate()));
            statement.setString(5, payment.getMode());
            statement.executeUpdate();
        }
    }

    public List<Payment> findAll() throws Exception {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments ORDER BY payment_id";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Payment payment = new Payment();
                payment.setPaymentId(resultSet.getInt("payment_id"));
                payment.setBillId(resultSet.getInt("bill_id"));
                payment.setAmountPaid(resultSet.getDouble("amount_paid"));
                payment.setPaymentDate(resultSet.getDate("payment_date").toLocalDate());
                payment.setMode(resultSet.getString("mode"));
                payments.add(payment);
            }
        }
        return payments;
    }

    public void deleteById(int paymentId) throws Exception {
        String sql = "DELETE FROM payments WHERE payment_id = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, paymentId);
            statement.executeUpdate();
        }
    }
}
