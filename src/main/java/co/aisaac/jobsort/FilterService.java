package co.aisaac.jobsort;

import co.aisaac.jobsort.io.BlackListCompaniesFromDatabase;
import co.aisaac.jobsort.io.BlockTitlesFromDatabase;
import co.aisaac.jobsort.pojo.Company;
import co.aisaac.jobsort.pojo.Job;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This is where we keep filter state.
 * So from here, anything related to filtering
 * or searching our job list.
 */
@Service
public class FilterService {

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

	public FilterService() {
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
		List<Company> companies = jobs.stream()
				.collect(Collectors.groupingBy(Job::getCompany))
				.entrySet().stream()
				.map(e -> new Company(e.getKey(), e.getValue()))
				.collect(Collectors.toList());

		return companies;
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

	public List<String> getTitleFilters(){
		return titleFitler;
	}
}
