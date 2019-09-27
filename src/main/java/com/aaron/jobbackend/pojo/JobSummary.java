package com.aaron.jobbackend.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class JobSummary {
	@JsonProperty("jobSummary")
	private String JobSummary;

	public JobSummary() {
	}

	public void setJobSummary(String jobSummary) {
		JobSummary = jobSummary;
	}

	public String getJobSummary() {
		return JobSummary;
	}

	@Override
	public String toString() {
		return "JobSummary{" +
				"JobSummary='" + JobSummary + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		JobSummary that = (JobSummary) o;
		return Objects.equals(getJobSummary(), that.getJobSummary());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getJobSummary());
	}
}
