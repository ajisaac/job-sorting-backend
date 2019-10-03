package co.aisaac.jobsort.io;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Insert these phrases into the database.
 */
public class BlockTitleIntoDatabase {
	private final String phrase;

	public BlockTitleIntoDatabase(String phrase) {
		this.phrase = phrase;
	}

	/**
	 * Perform the actual insertion.
	 */
	public void insert() throws Exception {
		if (phrase == null || phrase.isBlank()) {
			return;
		}

		try (Connection connection = Database.getConnection()) {
			String s = "INSERT INTO block_title(name)" +
					" VALUES (?)";

			PreparedStatement statement = connection.prepareStatement(s);

			statement.setString(1, phrase);
			statement.execute();
			statement.close();

		}
	}
}