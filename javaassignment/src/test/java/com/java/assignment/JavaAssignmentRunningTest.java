package com.java.assignment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.assignment.mapper.EventMapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static junit.framework.TestCase.assertEquals;


public class JavaAssignmentRunningTest {
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS event (id VARCHAR(20), duration INTEGER, type VARCHAR(50), host VARCHAR(50), alert BOOLEAN)";
    private ObjectMapper objectMapper;
    private EventMapper eventConverter;
    private JavaAssignmentApplication applicationRunner;

    @BeforeClass
    public static void init() throws SQLException, ClassNotFoundException {
        Class.forName("org.hsqldb.jdbc.JDBCDriver");
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(CREATE_TABLE);
            connection.commit();
        }
    }

    @AfterClass
    public static void destroy() throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE event");
            connection.commit();
            statement.executeUpdate("SHUTDOWN");
            connection.commit();
        }
    }

    @Before
    public void setup() {
        objectMapper = new ObjectMapper();
        eventConverter = new EventMapper();

    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM event");
            connection.commit();
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:test;ifexists=false", "sa", "");
    }

    @Test
    public void testRun__ProcessesLogFileAndSavesResultsToDatabase() throws IOException, SQLException {
        String[] args = {"src/test/resources/test.txt"};
        Connection connection = getConnection();

        applicationRunner = new JavaAssignmentApplication(objectMapper, connection, eventConverter);
        applicationRunner.run(args);

        try (Connection assertConnection = getConnection(); Statement statement = assertConnection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS total FROM event");

            assertEquals("Should save 3 records", 3, getSize(resultSet));

            ResultSet result1 = statement.executeQuery("SELECT COUNT(*) AS total FROM event WHERE id='scsmbstgra' AND duration=5 AND type='APPLICATION_LOG' AND host='12345' AND alert=true");
            assertEquals("Should have 1 record", 1, getSize(result1));

            ResultSet result2 = statement.executeQuery("SELECT COUNT(*) AS total FROM event WHERE id='scsmbstgrb' AND duration=3 AND alert=false");
            assertEquals("Should have 1 record", 1, getSize(result2));

            ResultSet result3 = statement.executeQuery("SELECT COUNT(*) AS total FROM event WHERE id='scsmbstgrc' AND duration=8 AND alert=true");
            assertEquals("Should save 1 record", 1, getSize(result3));
        }
    }

    @Test
    public void testRun__DoesntProcessInvalidJsonAndDoesntSavesResultsToDatabase() throws IOException, SQLException {
        String[] args = {"src/test/resources/invalid_json.txt"};
        Connection connection = getConnection();

        applicationRunner = new JavaAssignmentApplication(objectMapper, connection, eventConverter);
        applicationRunner.run(args);

        try (Connection assertConnection = getConnection(); Statement statement = assertConnection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS total FROM event");

            assertEquals("Should save 0 records", 0, getSize(resultSet));
        }
    }

    @Test
    public void testRun__ProcessJsonWithAdditionalFieldsAndSavesResultsToDatabase() throws IOException, SQLException {
        String[] args = {"src/test/resources/test_additional_fields.txt"};
        Connection connection = getConnection();

        applicationRunner = new JavaAssignmentApplication(objectMapper, connection, eventConverter);
        applicationRunner.run(args);

        try (Connection assertConnection = getConnection(); Statement statement = assertConnection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS total FROM event");

            assertEquals("Should save 3 records", 3, getSize(resultSet));

            ResultSet result1 = statement.executeQuery("SELECT COUNT(*) AS total FROM event WHERE id='scsmbstgra' AND duration=5 AND type='APPLICATION_LOG' AND host='12345' AND alert=true");
            assertEquals("Should have 1 record", 1, getSize(result1));

            ResultSet result2 = statement.executeQuery("SELECT COUNT(*) AS total FROM event WHERE id='scsmbstgrb' AND duration=3 AND alert=false");
            assertEquals("Should have 1 record", 1, getSize(result2));

            ResultSet result3 = statement.executeQuery("SELECT COUNT(*) AS total FROM event WHERE id='scsmbstgrc' AND duration=8 AND alert=true");
            assertEquals("Should save 1 record", 1, getSize(result3));
        }
    }

    private int getSize(ResultSet resultSet) throws SQLException {
        resultSet.next();
        return resultSet.getInt("total");
    }
}