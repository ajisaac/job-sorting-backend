package com.aaron.jobbackend;

import com.aaron.jobbackend.io.*;
import com.aaron.jobbackend.pojo.Company;
import com.aaron.jobbackend.pojo.Job;
import com.aaron.jobbackend.pojo.JobsList;

import java.util.*;
import java.util.stream.Collectors;

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

	void updateJobStatus(Long id, String state) {
		for (Job j : jobsList) {
			if (id.equals(j.getId())) {
				j.setJobState(state);
				new UpdateJob(j).update();
				break;
			}
		}
		this.jobsList = new JobsListFromDatabase().load().getJobs();
	}

	void updateMultipleJobsStatus(List<Long> ids, String state) {
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
		this.jobsList = new JobsListFromDatabase().load().getJobs();
	}

	void blacklistCompany(String companyName) {
		new BlackListCompanyIntoDatabase(companyName).insert();
	}

	List<Company> getAllCompanies() {
		return jobsToCompanies(this.jobsList);
	}

	List<String> getBlackListedCompanies() {
		return new BlackListCompaniesFromDatabase().load();
	}

	private List<Company> jobsToCompanies(List<Job> jobs) {
		//group by company
		Map<String, List<Job>> i = jobs.stream().collect(Collectors.groupingBy(Job::getCompany));

		//from map to companies for
		Iterator<Map.Entry<String, List<Job>>> j = i.entrySet().iterator();
		List<Company> companies = new ArrayList<>();
		List<String> blacklist = new BlackListCompaniesFromDatabase().load();
		long l = 1;
		while (j.hasNext()) {
			//the key is just for react to use
			Map.Entry<String, List<Job>> kv = j.next();
			//filter, if the company is blacklisted just don't add it
			if (blacklist.contains(kv.getKey())) continue;
			companies.add(new Company(l++, kv.getKey(), kv.getValue()));
		}

		//sort by size
		companies.sort(Comparator.comparingInt(o -> o.getJobs().size() * -1));
//		companies = companies.subList(0,20);
		return companies;
	}

	public void removeBlacklistedCompany(String companyName) {
		new RemoveBlackListCompanyFromDatabase(companyName).remove();
	}
}

