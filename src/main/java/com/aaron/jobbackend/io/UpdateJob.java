package com.aaron.jobbackend.io;

import com.aaron.jobbackend.pojo.Job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Update the jobs in the database.
 */
public class UpdateJob {
	Job job;

	public UpdateJob(Job j) {
		this.job = j;
	}

	public void update() {

		try (Connection connection = Database.getConnection()) {
			String s = "UPDATE IGNORE job SET url=?, summary=? ,company=? ,location=?" +
					" ,postdate=? ,salary=? ,jobstate=? ,title=?, search_term=?, company_location=? WHERE id=?";

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
			statement.setString(10, job.getCompanyLocation());
			statement.setLong(11, job.getId());

			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
