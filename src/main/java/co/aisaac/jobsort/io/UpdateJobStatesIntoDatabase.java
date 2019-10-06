package co.aisaac.jobsort.io;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * update the jobs states into the database
 */
public class UpdateJobStatesIntoDatabase {
	private final List<Long> ids;
	private final String state;

	public UpdateJobStatesIntoDatabase(List<Long> ids, String state) {
		this.ids = ids;
		this.state = state;
	}

	public void update() throws SQLException {

		try (Connection connection = Database.getConnection()) {
			String s = "UPDATE IGNORE job SET jobstate=? WHERE id=?";

			int batchNum = 100;

			PreparedStatement statement = connection.prepareStatement(s);
			int count = 0;
			for (Long id : ids) {

				statement.setString(1, state);
				statement.setLong(2, id);
				statement.addBatch();

				if (++count % batchNum == 0) {
					statement.executeBatch();
				}
			}

			statement.executeBatch();
			statement.close();
		} catch (SQLException e) {
			throw e;
		}
	}
}
