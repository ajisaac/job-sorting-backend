package com.aaron.jobbackend.jobslist;

import java.util.*;

import com.aaron.jobbackend.Job;

public class JobsList {
	private final List<Job> jobsList;

	JobsList(List<Job> jl) {
		this.jobsList = jl;

	}

	/**
	 * Remove duplicates. A duplicate is determined by url.
	 */
	public void removeDuplicates() {
		Set<Job> uj = new HashSet<>(jobsList);
		jobsList.clear();
		jobsList.addAll(uj);
	}

	public List<Job> getJobs() {
		return this.jobsList;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		JobsList jobsList1 = (JobsList) o;
		return Objects.equals(jobsList, jobsList1.jobsList);
	}

	@Override
	public int hashCode() {
		return Objects.hash(jobsList);
	}
}
