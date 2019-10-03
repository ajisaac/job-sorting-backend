package co.aisaac.jobsort.io;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Remove a block title
 */
public class RemoveBlockTitleFromDatabase {
	private final String phrase;

	public RemoveBlockTitleFromDatabase(String phrase) {
		this.phrase= phrase;
	}

	/**
	 * Perform the actual deletion
	 */
	public void remove() {
		if (phrase == null || phrase.isBlank()) {
			return;
		}

		try (Connection connection = Database.getConnection()) {
			String s = "DELETE FROM block_title WHERE " +
					"name = ?;";

			PreparedStatement statement = connection.prepareStatement(s);

			statement.setString(1, phrase);
			statement.execute();
			statement.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}