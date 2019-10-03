package co.aisaac.jobsort.io;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Remove a company from the blacklist
 */
public class RemoveBlackListCompanyFromDatabase {
	private final String companyName;

	public RemoveBlackListCompanyFromDatabase(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * Perform the actual insertion.
	 */
	public void remove() {
		if (companyName == null || companyName.isBlank()) {
			return;
		}

		try (Connection connection = Database.getConnection()) {
			String s = "DELETE FROM blacklisted_company WHERE " +
					"name = ?;";

			PreparedStatement statement = connection.prepareStatement(s);

			statement.setString(1, companyName);
			statement.execute();
			statement.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
