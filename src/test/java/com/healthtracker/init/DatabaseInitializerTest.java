package com.healthtracker.init;

import com.healthtracker.util.DBConfig;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DatabaseInitializerTest {

    @Test
    void testDatabaseTablesCreation() throws SQLException {
        DatabaseInitializer.initialise();

        try (Connection connection = DBConfig.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            List<String> tables = new ArrayList<>();
            
            try (ResultSet rs = metaData.getTables(null, null, null, new String[]{"TABLE"})) {
                while (rs.next()) {
                    tables.add(rs.getString("TABLE_NAME").toUpperCase());
                }
            }

            assertThat(tables).contains("USER");
            assertThat(tables).contains("FOOD_LOG");
            assertThat(tables).contains("WORKOUT_LOG");
            assertThat(tables).contains("WEIGHT_LOG");
        }
    }
}