package com.aaron.jobbackend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {

	private static Connection connection;

	public static Connection getConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				openConnection();
				return connection;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}

	private static void openConnection() {
		String user = "root";
		String pass = System.getenv("MARIADB_CREDS");
		try {
			connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/jobbackend", user, pass);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
