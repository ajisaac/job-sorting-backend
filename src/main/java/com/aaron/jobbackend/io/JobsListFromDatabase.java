package com.aaron.jobbackend.io;

import com.aaron.jobbackend.pojo.Job;
import com.aaron.jobbackend.pojo.JobsList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JobsListFromDatabase {
	public JobsList load() {
		List<Job> l = new ArrayList<>();
		try (
				Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement("SELECT * FROM job");
				ResultSet rs = statement.executeQuery()) {
			while (rs.next()) {
				Job job = new Job();

				job.setId(rs.getInt("id"));
				if (rs.getString("jobstate") == null) {
					job.setJobState("new");
				} else {
					job.setJobState(rs.getString("jobstate"));
				}
				if (rs.getString("url") == null) {
					continue;
				} else {
					job.setUrl(rs.getString("url"));
				}
				if (rs.getString("company") == null) {
					job.setCompany("unknown");
				} else {
					job.setCompany(rs.getString("company"));
				}
				if (rs.getString("location") == null) {
					job.setLocation("unknown");
				} else {
					job.setLocation(rs.getString("location"));
				}
				if (rs.getString("postdate") == null) {
					job.setPostDate("unknown");
				} else {
					job.setPostDate(rs.getString("postdate"));
				}
				if (rs.getString("salary") == null) {
					job.setSalary("");
				} else {
					job.setSalary(rs.getString("salary"));
				}
				if (rs.getString("summary") == null) {
					job.setSummary("");
				} else {
					job.setSummary(rs.getString("summary"));
				}
				if (rs.getString("title") == null) {
					job.setTitle("title");
				} else {
					job.setTitle(rs.getString("title"));
				}
				if (rs.getString("search_term") == null) {
					job.setSearchTerm("searchTerm");
				} else {
					job.setSearchTerm(rs.getString("search_term"));
				}
				if (rs.getString("company_location") == null) {
					job.setCompanyLocation("unknown - unknown");
				} else {
					job.setCompanyLocation(rs.getString("company_location"));
				}

				l.add(job);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return new JobsList(new ArrayList<>());
		}
		return new JobsList(l);
	}
}
