package com.intellibill.database;

import com.intellibill.model.Bill;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {
    public void upsert(Bill bill) throws Exception {
        String sql = """
                INSERT INTO bills (bill_id, consumer_id, service_id, previous_reading, current_reading,
                                   units_consumed, amount, penalty, paid_amount, bill_date, due_date, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                previous_reading = VALUES(previous_reading),
                current_reading = VALUES(current_reading),
                units_consumed = VALUES(units_consumed),
                amount = VALUES(amount),
                penalty = VALUES(penalty),
                paid_amount = VALUES(paid_amount),
                bill_date = VALUES(bill_date),
                due_date = VALUES(due_date),
                status = VALUES(status)
                """;

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bill.getBillId());
            statement.setInt(2, bill.getConsumerId());
            statement.setInt(3, bill.getServiceId());
            statement.setDouble(4, bill.getPreviousReading());
            statement.setDouble(5, bill.getCurrentReading());
            statement.setDouble(6, bill.getUnitsConsumed());
            statement.setDouble(7, bill.getAmount());
            statement.setDouble(8, bill.getPenalty());
            statement.setDouble(9, bill.getPaidAmount());
            statement.setDate(10, Date.valueOf(bill.getBillDate()));
            statement.setDate(11, Date.valueOf(bill.getDueDate()));
            statement.setString(12, bill.getStatus());
            statement.executeUpdate();
        }
    }

    public List<Bill> findAll() throws Exception {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills ORDER BY bill_id";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Bill bill = new Bill();
                bill.setBillId(resultSet.getInt("bill_id"));
                bill.setConsumerId(resultSet.getInt("consumer_id"));
                bill.setServiceId(resultSet.getInt("service_id"));
                bill.setPreviousReading(resultSet.getDouble("previous_reading"));
                bill.setCurrentReading(resultSet.getDouble("current_reading"));
                bill.setUnitsConsumed(resultSet.getDouble("units_consumed"));
                bill.setAmount(resultSet.getDouble("amount"));
                bill.setPenalty(resultSet.getDouble("penalty"));
                bill.setPaidAmount(resultSet.getDouble("paid_amount"));
                bill.setBillDate(resultSet.getDate("bill_date").toLocalDate());
                bill.setDueDate(resultSet.getDate("due_date").toLocalDate());
                bill.setStatus(resultSet.getString("status"));
                bills.add(bill);
            }
        }
        return bills;
    }

    public void deleteById(int billId) throws Exception {
        String sql = "DELETE FROM bills WHERE bill_id = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, billId);
            statement.executeUpdate();
        }
    }
}
