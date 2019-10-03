package co.aisaac.jobsort.io;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BlockTitlesFromDatabase {
	public List<String> load() {

		try (
				Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement("SELECT * FROM block_title");
				ResultSet rs = statement.executeQuery()) {

			List<String> l = new ArrayList<>();
			while (rs.next()) {
				if (rs.getString("name") != null) {
					l.add(rs.getString("name"));
				}
			}
			return l;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return new ArrayList<>();
	}
}