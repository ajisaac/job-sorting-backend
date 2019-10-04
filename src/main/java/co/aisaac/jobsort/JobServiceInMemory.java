package co.aisaac.jobsort;

import co.aisaac.jobsort.io.*;
import co.aisaac.jobsort.pojo.Company;
import co.aisaac.jobsort.pojo.Job;
import co.aisaac.jobsort.pojo.JobSummary;
import co.aisaac.jobsort.pojo.UpdateState;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
class JobServiceInMemory implements JobService {

	private FilterService filterService;

	JobServiceInMemory(FilterService filterService) {
		this.filterService = filterService;
		var j = JobsListFromDatabase.load().getJobs();
		filterService.initFilterService(j);
	}

	@Override
	public void updateJobStatus(UpdateState updateState, String state) {
		//update this jobs status in our database
		//find the job in our hashMap<list>
		//pop it from the list
		//push it to the new state list

	}

	@Override
	public void updateMultipleJobsStatus(List<UpdateState> ids, String state) {
		//update all of these in our database in one fell swoop
		//group them into map<state, list<id>>
		//pop them all into a single list
		//update their internal state
		//push them all to new list

		//in the filter service we want to update the filter
		//for each job we will run it through the filter

	}

	public List<Company> getAllCompanies(String filter) {
		return filterService.getCompanies(filter);
	}

	@Override
	public void updateJobSummary(Long id, JobSummary jobSummary) {

	}

	@Override
	public void blockTitle(String phrase) throws Exception {

	}

	@Override
	public void removeBlockTitle(String phrase) {

	}

	@Override
	public List<String> getBlockTitles() {
		return null;
	}

	@Override
	public void blacklistCompany(String companyName) {
		// add company to database
		try {
			new BlackListCompanyIntoDatabase(companyName).insert();
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}

		filterService.addBlackListCompany(companyName);
	}

	@Override
	public void removeBlacklistedCompany(String companyName) {

	}

	@Override
	public List<String> getBlackListedCompanies() {
		return null;
	}

}

