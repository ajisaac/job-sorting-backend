package co.aisaac.jobsort.io;

import co.aisaac.jobsort.pojo.Job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertJobIntoDatabase {
	private final Job job;

	public InsertJobIntoDatabase(Job job) {
		this.job = job;
	}

	/**
	 * Perform the actual insertion.
	 */
	public void insert() throws SQLException {
		if (job == null) {
			return;
		}

		try (Connection connection = Database.getConnection()) {
			String s = "INSERT IGNORE INTO job (url ,summary ,company ,location ,postdate ,salary ,jobstate ,title,  search_term)" +
					" VALUES (?,?,?,?,?,?,?,?,?)";

			PreparedStatement statement = connection.prepareStatement(s);

			statement.setString(1, job.getUrl());
			statement.setString(2, job.getSummary());
			statement.setString(3, job.getCompany());
			statement.setString(4, job.getLocation());
			statement.setString(5, job.getPostDate());
			statement.setString(6, job.getSalary());
			if (job.getJobState() == null || job.getJobState().isBlank()) {
				statement.setString(7, "new");
			} else {
				statement.setString(7, job.getJobState());
			}
			statement.setString(8, job.getTitle());
			statement.setString(9, job.getSearchTerm());

			statement.execute();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
}
