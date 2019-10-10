package co.aisaac.jobsort.io;


import co.aisaac.jobsort.pojo.Job;
import co.aisaac.jobsort.pojo.JobsList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Insert these jobs into the database.
 */
public class JobsIntoDatabase {
	private final JobsList jobsList;

	public JobsIntoDatabase(JobsList newJobsList) {
		this.jobsList = newJobsList;
	}

	/**
	 * Perform the actual insertion.
	 */
	public void insert() {
		if (jobsList == null) {
			return;
		}

		try (Connection connection = Database.getConnection()) {
			String s = "INSERT IGNORE INTO job (url ,summary ,company ,location ,postdate ,salary ,jobstate ,title,  search_term)" +
					" VALUES (?,?,?,?,?,?,?,?,?)";

			PreparedStatement statement = connection.prepareStatement(s);

			int batchSize = 100;
			int count = 0;
			for (Job j : jobsList.getJobs()) {

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
				statement.setString(9, j.getSearchTerm());

				statement.addBatch();

				if (++count % batchSize == 0) {
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
