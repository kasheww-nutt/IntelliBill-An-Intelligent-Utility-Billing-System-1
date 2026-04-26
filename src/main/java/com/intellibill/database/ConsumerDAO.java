package com.intellibill.database;

import com.intellibill.model.CommercialConsumer;
import com.intellibill.model.Consumer;
import com.intellibill.model.ConsumerType;
import com.intellibill.model.IndustrialConsumer;
import com.intellibill.model.ResidentialConsumer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ConsumerDAO {
    public void upsert(Consumer consumer) throws Exception {
        String sql = """
                INSERT INTO consumers (consumer_id, name, address, consumer_type)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                name = VALUES(name),
                address = VALUES(address),
                consumer_type = VALUES(consumer_type)
                """;

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, consumer.getConsumerId());
            statement.setString(2, consumer.getName());
            statement.setString(3, consumer.getAddress());
            statement.setString(4, consumer.getConsumerType().name());
            statement.executeUpdate();
        }
    }

    public List<Consumer> findAll() throws Exception {
        List<Consumer> consumers = new ArrayList<>();
        String sql = "SELECT consumer_id, name, address, consumer_type FROM consumers ORDER BY consumer_id";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("consumer_id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                ConsumerType type = ConsumerType.valueOf(resultSet.getString("consumer_type"));
                consumers.add(createConsumer(id, name, address, type));
            }
        }
        return consumers;
    }

    public void deleteById(int consumerId) throws Exception {
        String sql = "DELETE FROM consumers WHERE consumer_id = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, consumerId);
            statement.executeUpdate();
        }
    }

    private Consumer createConsumer(int id, String name, String address, ConsumerType type) {
        return switch (type) {
            case RESIDENTIAL -> new ResidentialConsumer(id, name, address);
            case COMMERCIAL -> new CommercialConsumer(id, name, address);
            case INDUSTRIAL -> new IndustrialConsumer(id, name, address);
        };
    }
}
