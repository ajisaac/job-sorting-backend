package co.aisaac.jobsort;

import co.aisaac.jobsort.io.*;
import co.aisaac.jobsort.pojo.Company;
import co.aisaac.jobsort.pojo.Job;
import co.aisaac.jobsort.pojo.JobsList;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This is where we keep filter state.
 * So from here, anything related to filtering
 * or searching our job list.
 */
@Service
public class JobService {

	// <JobStatus, List of jobs>
	private Map<String, List<Job>> jobs;

	// <label name, is selected>
	private Map<String, Boolean> labels = new HashMap<>();

	// inferred this from our jobs list
	private Map<String, Boolean> previousSearches = new HashMap<>();

	// these are stored
	private List<String> titleFitlers = new ArrayList<>();
	private boolean titleFilterChecked = false;

	// these are stored
	private List<String> blackListedCompanies = new ArrayList<>();

	private List<String> titleTerms = new ArrayList<>();
	private List<String> companyTerms = new ArrayList<>();
	private List<String> searchTerms = new ArrayList<>();

	public JobService() {

		// 1 load our db jobs
		List<Job> jlist = JobsListFromDatabase.load().getJobs();

		// 2 load up our files and get the unique jobs
		JobsList fromDir = new JobsListFromDirectory("/home/aaron/documents/jobsearch").load();
		fromDir.removeDuplicates();
		fromDir.getUnique(jlist);

		// 3 push the new jobs into the database
		new JobsIntoDatabase(fromDir).insert();

		// 4 reload from the database
		jlist = JobsListFromDatabase.load().getJobs();

		// 5 map them by jobStatus for easy access by certain operations
		jobs = jlist.stream()
				.collect(Collectors.groupingBy(Job::getJobState));

		// 6 derive our previous searches into a set
		Set<String> s = jlist.stream()
				.map(Job::getSearchTerm)
				.collect(Collectors.toSet());
		previousSearches = s.stream()
				.collect(Collectors.toMap(Function.identity(), j -> false));

		// 7 load up our blacklisted companies
		blackListedCompanies = new BlackListCompaniesFromDatabase().load();

		// 8 load up our blocked titles
		titleFitlers = new BlockTitlesFromDatabase().load();

		// 9 prepare the labels for our label filter
		initLabels();
	}

	private void initLabels() {
		labels.put("new", false);
		labels.put("saved", false);
		labels.put("applied", false);
		labels.put("interviewing", false);
		labels.put("excluded", false);
		labels.put("rejected", false);
		labels.put("ignored", false);
	}

	// this runs each time we load the page
	public List<Company> getCompanies(String filter) {

		// 1 make a list of all our jobs regardless of status
		List<Job> jList = new ArrayList<>();
		this.jobs.values().forEach(jList::addAll);

		// 2 group them up into companies
		List<Company> companies = jList.stream()
				.collect(Collectors.groupingBy(Job::getCompany))
				.entrySet().stream()
				.map(e -> new Company(e.getKey(), e.getValue()))
				.collect(Collectors.toList());

		// get the checked label filters
		List<String> f = this.labels
				.entrySet().stream()
				.filter(Map.Entry::getValue)
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());

		if (f.size() != 0) {

			// 2.5 get rid of companies that don't match the filters label filters
			companies = companies.stream()
					.filter(company -> {
						for (String s : company.getLabels()) {
							if (f.contains(s)) {
								return true;
							}
						}
						return false;
					})
					.collect(Collectors.toList());
		}

		// 3 filter out our blacklisted companies
		companies = companies.stream()
				.filter(company -> !blackListedCompanies.contains(company.getName()))
				.collect(Collectors.toList());

		// 4 further filter by jobState
		if (!filter.equals("")) {
			companies.forEach(company -> company.filter(filter));
		}

		// 5 run through the title filter if we're checked
		// if not checked we'll allow them to be included
		if (titleFilterChecked) {
			companies.forEach(company -> company.applyTitleFilters(titleFitlers));
		}

		// 6 get rid of companies without jobs
		companies = companies.stream()
				.filter(company -> company.getJobs().size() > 0)
				.collect(Collectors.toList());


		return companies;
	}

	public void addBlackListCompany(String companyName) throws SQLException {
		new BlackListCompanyIntoDatabase(companyName).insert();
		blackListedCompanies.add(companyName);
	}

	public void removeBlackListCompany(String companyName) throws SQLException {
		new RemoveBlackListCompanyFromDatabase(companyName).remove();
		for (int i = 0, blackListedCompaniesSize = blackListedCompanies.size(); i < blackListedCompaniesSize; i++) {
			String blackListedCompany = blackListedCompanies.get(i);
			if (blackListedCompany.equalsIgnoreCase(companyName)) {
				blackListedCompanies.remove(blackListedCompany);
			}
		}
	}

	public List<String> getBlackListedCompanies() {
		return this.blackListedCompanies;
	}

	public Map<String, Boolean> getPreviousSearches() {
		return previousSearches;
	}

	public Map<String, Boolean> getLabels() {
		return labels;
	}

	public List<String> getTitleFilters() {
		return titleFitlers;
	}

	public void setLabelFilterChecked(String filter, boolean checked) {
		this.labels.put(filter, checked);
	}

	public boolean isTitleFilterChecked() {
		return titleFilterChecked;
	}

	public void setTitleFilterChecked(boolean checked) {
		this.titleFilterChecked = checked;
	}

	public void updateJobStatus(long id, String state) throws Exception {

		new UpdateJobStateIntoDatabase(id, state).update();

		Job jobHolder = null;

		outer:
		for (Map.Entry<String, List<Job>> j : this.jobs.entrySet()) {
			for (Job job : j.getValue()) {
				if (job.getId() == id) {
					jobHolder = job;
					break outer;
				}
			}
		}

		if (jobHolder == null) {
			throw new Exception();
		}

		jobs.get(jobHolder.getJobState()).remove(jobHolder);
		jobHolder.setJobState(state);
		jobs.get(state).add(jobHolder);

	}

	public void updateJobsStatus(List<Long> ids,
	                             String state) throws Exception {
		new UpdateJobStatesIntoDatabase(ids, state).update();

		String stateHolder = null;
		outer:
		for (Map.Entry<String, List<Job>> j : this.jobs.entrySet()) {
			for (Job job : j.getValue()) {
				if (job.getId() == ids.get(0)) {
					stateHolder = job.getJobState();
					break outer;
				}
			}
		}
		if (stateHolder == null) throw new Exception();

		// likely the list that we want to modify
		List<Job> stateList = jobs.get(stateHolder);
		// a copy of the job ids list
		List<Long> idsCopy = new ArrayList<>(ids);
		// jobs that match our ids
		List<Job> idsJobList = new ArrayList<>();
		for (Job job : stateList) {
			long l = job.getId();
			if (idsCopy.contains(l)) {
				idsJobList.add(job);
				idsCopy.remove(l);
			}
		}

		// there's some jobs that had a different state
		// in this case we'll need to go into the other state
		// lists and find the jobs in there. This is unlikely.
		if (idsCopy.size() > 0) {
			// todo
			// do something here
			System.out.println(idsCopy);
		}

		stateList.removeAll(idsJobList);
		idsJobList.forEach(j -> j.setJobState(state));
		jobs.get(state).addAll(idsJobList);
	}

	public void updateSummary(Long id, String summary) throws Exception {
		Job job = null;
		// update database
		for (Map.Entry<String, List<Job>> stringListEntry : jobs.entrySet()) {
			for (Job j : stringListEntry.getValue()) {
				if (j.getId() == id) {
					job = j;
				}
			}
		}
		if (job == null) throw new Exception();

		job.setSummary(summary);
		new UpdateJobIntoDatabase(job).update();

		// update jobs list

	}


	public void insertJob(Job job) throws SQLException {
		new InsertJobIntoDatabase(job).insert();

		long id = new GetJobIdFromDatabase(job).query();
		job.setId(id);

		this.jobs.get(job.getJobState()).add(job);
	}
}
