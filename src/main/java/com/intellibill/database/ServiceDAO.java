package com.intellibill.database;

import com.intellibill.model.UtilityService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

public class ServiceDAO {
    public void seedDefaultServices(Map<Integer, UtilityService> serviceCatalog) throws Exception {
        String sql = """
                INSERT INTO services (service_id, service_name, service_type)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE
                service_name = VALUES(service_name),
                service_type = VALUES(service_type)
                """;

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (UtilityService service : serviceCatalog.values()) {
                statement.setInt(1, service.getServiceId());
                statement.setString(2, service.getServiceName());
                statement.setString(3, service.getServiceType().name());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    public boolean exists(int serviceId) throws Exception {
        String sql = "SELECT COUNT(*) FROM services WHERE service_id = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, serviceId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
            return false;
        }
    }
}
