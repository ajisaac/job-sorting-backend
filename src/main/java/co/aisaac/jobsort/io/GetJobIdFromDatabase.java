package co.aisaac.jobsort.io;

import co.aisaac.jobsort.pojo.Job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetJobIdFromDatabase {
	Job job;

	public GetJobIdFromDatabase(Job job) {
		this.job = job;
	}

	public long query() throws SQLException {
		String query = "SELECT id FROM job WHERE url=? and title=? and company=? and location=?;";
		try (Connection connection = Database.getConnection()) {

			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, job.getUrl());
			statement.setString(2, job.getTitle());
			statement.setString(3, job.getCompany());
			statement.setString(4, job.getLocation());
			ResultSet rs = statement.executeQuery();

			if (!rs.first()) throw new SQLException("No Results");

			return rs.getLong("id");

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
}
