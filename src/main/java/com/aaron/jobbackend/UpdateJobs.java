package com.aaron.jobbackend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class UpdateJobs {

	List<Job> jobs;

	public UpdateJobs(List<Job> jobs) {
		this.jobs = jobs;
	}

	public void update() {
		try (Connection connection = Database.getConnection()) {
			String s = "UPDATE IGNORE job SET url=?, summary=? ,company=? ,location=?" +
					" ,postdate=? ,salary=? ,jobstate=? ,title=? WHERE id=?";

			int batchNum = 100;

			PreparedStatement statement = connection.prepareStatement(s);
			int count = 0;
			for (Job j : jobs) {

				statement.setString(1, j.getUrl());
				statement.setString(2, j.getSummary());
				statement.setString(3, j.getCompany());
				statement.setString(4, j.getLocation());
				statement.setString(5, j.getPostDate());
				statement.setString(6, j.getSalary());
				if (j.getJobState() == null || j.getJobState().isBlank()) {
					statement.setString(7, "new");
				} else {
					statement.setString(7, j.getJobState());
				}
				statement.setString(8, j.getTitle());
				statement.setLong(9, j.getId());
				statement.addBatch();

				if (++count % batchNum == 0) {
					statement.executeBatch();
				}
			}

			statement.executeBatch();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}