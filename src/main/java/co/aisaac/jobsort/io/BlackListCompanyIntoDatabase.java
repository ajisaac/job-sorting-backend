package co.aisaac.jobsort.io;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Insert these jobs into the database.
 */
public class BlackListCompanyIntoDatabase {
	private final String companyName;

	public BlackListCompanyIntoDatabase(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * Perform the actual insertion.
	 */
	public void insert() throws SQLException {
		if (companyName == null || companyName.isBlank()) {
			return;
		}

		try (Connection connection = Database.getConnection()) {
			String s = "INSERT IGNORE INTO blacklisted_company(name)" +
					" VALUES (?)";

			PreparedStatement statement = connection.prepareStatement(s);

			statement.setString(1, companyName);
			statement.execute();
			statement.close();

		}
	}
}
