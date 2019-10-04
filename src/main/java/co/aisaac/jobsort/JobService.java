package co.aisaac.jobsort;

import co.aisaac.jobsort.pojo.Company;
import co.aisaac.jobsort.pojo.JobSummary;
import co.aisaac.jobsort.pojo.UpdateState;

import java.util.*;

public interface JobService {

	void updateJobStatus(UpdateState updateState, String state);

	void updateMultipleJobsStatus(List<UpdateState> ids, String state);

	List<Company> getAllCompanies(String stateFilter);

	void updateJobSummary(Long id, JobSummary jobSummary);

	void blockTitle(String phrase) throws Exception;

	void removeBlockTitle(String phrase);

	List<String> getBlockTitles();

	void blacklistCompany(String companyName);

	void removeBlacklistedCompany(String companyName);

	List<String> getBlackListedCompanies();
}
