package co.aisaac.jobsort.io;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Update the job in the database.
 */
public class UpdateJobStateIntoDatabase {
	private final long id;
	private final String state;

	public UpdateJobStateIntoDatabase(long id, String state) {
		this.id = id;
		this.state = state;
	}

	public void update() throws SQLException {

		try (Connection connection = Database.getConnection()) {
			String s = "UPDATE IGNORE job SET jobstate=? WHERE id=?";

			PreparedStatement statement = connection.prepareStatement(s);

			statement.setString(1, state);
			statement.setLong(2, id);

			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
}