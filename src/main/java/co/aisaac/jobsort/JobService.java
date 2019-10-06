package co.aisaac.jobsort;

import co.aisaac.jobsort.io.BlackListCompaniesFromDatabase;
import co.aisaac.jobsort.io.BlockTitlesFromDatabase;
import co.aisaac.jobsort.io.UpdateJobStateIntoDatabase;
import co.aisaac.jobsort.io.UpdateJobStatesIntoDatabase;
import co.aisaac.jobsort.pojo.Company;
import co.aisaac.jobsort.pojo.Job;
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

	private Map<String, List<Job>> jobs;

	// these are known
	private Map<String, Boolean> labels = new HashMap<>();

	// inferred this from our jobs list
	private Map<String, Boolean> previousSearches = new HashMap<>();

	// these are stored
	private List<String> titleFitler = new ArrayList<>();

	// these are stored
	private List<String> blackListedCompanies = new ArrayList<>();

	private List<String> titleTerms = new ArrayList<>();
	private List<String> companyTerms = new ArrayList<>();
	private List<String> searchTerms = new ArrayList<>();

	public JobService() {
	}

	public void initFilterService(List<Job> jlist) {

		jobs = jlist.stream()
				.collect(Collectors.groupingBy(Job::getJobState));

		Set<String> s = jlist.stream()
				.map(Job::getSearchTerm)
				.collect(Collectors.toSet());


		previousSearches = s.stream()
				.collect(Collectors.toMap(Function.identity(), j -> false));

		blackListedCompanies = new BlackListCompaniesFromDatabase().load();

		titleFitler = new BlockTitlesFromDatabase().load();

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

	public List<Company> getCompanies(String filter) {

		List<Job> jList;

		if (filter.equals("")) {
			jList = new ArrayList<>();
			this.jobs.values().forEach(jList::addAll);
		} else {
			jList = this.jobs.get(filter);
		}

		jList = filterBlackListCompanies(jList);

		return jobsToCompanies(jList);

	}

	private List<Company> jobsToCompanies(List<Job> jobs) {
		//group by company
		return jobs.stream()
				.collect(Collectors.groupingBy(Job::getCompany))
				.entrySet().stream()
				.map(e -> new Company(e.getKey(), e.getValue()))
				.collect(Collectors.toList());
	}


	public void addBlackListCompany(String companyName) {
		// we will add this company and thus kick off filtering

	}

	public void removeBlackListCompany(String companyName) {
		// undo the blacklisting
	}

	public List<String> getBlackListedCompanies() {
		return this.blackListedCompanies;
	}

	private List<Job> filterBlackListCompanies(List<Job> jobs) {
		return jobs.stream()
				.filter(j -> !blackListedCompanies.contains(j.getCompany()))
				.collect(Collectors.toList());
	}

	public Map<String, Boolean> getPreviousSearches() {
		return previousSearches;
	}

	public Map<String, Boolean> getLabels() {
		return labels;
	}

	public List<String> getTitleFilters() {
		return titleFitler;
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

	public void updateSummary(Long id, String summary) {
		System.out.println("Update Summary for " + id + " " + summary);
	}


}
