package com.aaron.jobbackend;

import java.util.*;
import java.util.stream.Collectors;

import com.aaron.jobbackend.jobslist.*;

class JobService {
	private final String jobsDirectoryLocation = "/home/aaron/documents/jobsearch";

	private List<Job> jobsList;

	JobService() {
		// load all of our jobs from json into the database
		JobsList newJobsList = new JobsListFromDirectory(jobsDirectoryLocation).load();
		// this just removes based upon hashcode of 4 properties
		newJobsList.removeDuplicates();

		// hashcode is unique in our database, so if we had an entry in file
		// and it was identical to an entry in the database, it will ignore
		new JobsIntoDatabase(newJobsList).insert();

		JobsList dbJobsList = new JobsListFromDatabase().load();

		this.jobsList = dbJobsList.getJobs();

	}

	List<Job> getAllJobs() {
		return this.jobsList;
	}

	List<Company> getAllCompanies() {
		Map<String, List<Job>> i = jobsList.stream().collect(Collectors.groupingBy(Job::getCompany));
		Iterator<Map.Entry<String, List<Job>>> j = i.entrySet().iterator();
		List<Company> companies = new ArrayList<>();
		long l = 1;
		while (j.hasNext()) {
			Map.Entry<String, List<Job>> kv = j.next();
			companies.add(new Company(l++, kv.getKey(), kv.getValue()));
		}
		companies.sort(Comparator.comparingInt(o -> o.getJobs().size() * -1));
		return companies;
	}

	void updateJobStatus(Long id, String state) {
		for (Job j : jobsList) {
			if (id.equals(j.getId())) {
				j.setJobState(state);
				new UpdateJob(j).update();
				break;
			}
		}
	}

	public void updateMultipleJobsStatus(List<Long> ids, String state) {
		ArrayList<Job> jobs = new ArrayList<>();
		for (Long id : ids) {
			for (Job j : jobsList) {
				if (id.equals(j.getId())) {
					j.setJobState(state);
					jobs.add(j);
					break;
				}
			}
		}
		new UpdateJobs(jobs).update();
	}
}



















