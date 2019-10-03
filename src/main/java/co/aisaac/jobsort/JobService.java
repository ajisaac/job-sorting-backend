package co.aisaac.jobsort;

import co.aisaac.jobsort.io.*;
import co.aisaac.jobsort.pojo.Company;
import co.aisaac.jobsort.pojo.Job;
import co.aisaac.jobsort.pojo.JobSummary;
import co.aisaac.jobsort.pojo.JobsList;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
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
				new UpdateJobIntoDatabase(j).update();
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
		new UpdateJobsIntoDatabase(jobs).update();
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

	void removeBlacklistedCompany(String companyName) {
		new RemoveBlackListCompanyFromDatabase(companyName).remove();
	}

	void updateJobSummary(Long id, JobSummary jobSummary) {
		for (Job j : jobsList) {
			if (id.equals(j.getId())) {
				j.setSummary(jobSummary.getJobSummary());
				new UpdateJobIntoDatabase(j).update();
				break;
			}
		}
		this.jobsList = new JobsListFromDatabase().load().getJobs();
	}

	void blockTitle(String phrase) throws Exception {
		new BlockTitleIntoDatabase(phrase).insert();
	}

	void removeBlockTitle(String phrase) {
		new RemoveBlockTitleFromDatabase(phrase).remove();
	}

	List<String> getBlockTitles() {
		return new BlockTitlesFromDatabase().load();
	}
}

