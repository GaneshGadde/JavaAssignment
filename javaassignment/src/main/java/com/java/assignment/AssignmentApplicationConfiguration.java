package com.java.assignment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;

@Configuration
@ComponentScan(basePackages={"com.java.assignment"})
public class AssignmentApplicationConfiguration {
	private static Logger log = LoggerFactory.getLogger(AssignmentApplicationConfiguration.class);

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

	@Bean
	public Connection getconnection() {
		try {
			 Class.forName("org.hsqldb.jdbc.JDBCDriver");
			//Class.forName("org.h2.Driver");
			Connection connection = DriverManager.getConnection("jdbc:hsqldb:test;ifexists=false", "sa", "");
			//Connection connection = DriverManager.getConnection("jdbc:h2:mem:eventdb;ifexists=false", "sa", "");
			connection.createStatement().execute("CREATE TABLE IF NOT EXISTS event (id VARCHAR(20), duration INTEGER, type VARCHAR(50), host VARCHAR(50), alert BOOLEAN)");
			return connection;
		} catch (Exception e) {
			log.error("Failure initialising HSQL JDBCDriver");
			throw new BeanCreationException("Connection", "Failed to create a Connection", e);
		}
	}
}
