package co.aisaac.jobsort;

import co.aisaac.jobsort.pojo.*;
import co.aisaac.jobsort.repositories.DatabaseService;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JobService {

  private final DatabaseService databaseService;

  @Autowired
  public JobService(DatabaseService databaseService) {
    this.databaseService = databaseService;

  }

  /**
   * Gets all the jobs.
   *
   * @return all the jobs.
   */
  public List<JobPosting> getAllJobs() {
    return databaseService.getAllJobPostings();
  }

  /**
   * Gets all the jobs, but contained within a Company pojo as a list.
   *
   * @return All the companies or an empty list.
   */
  public Companies getAllJobsByCompany() {
    List<JobPosting> jobPostings = databaseService.getAllJobPostings();
    Map<String, List<JobPosting>> companyMap = new HashMap<>();

    // sort by company
    for(JobPosting job : jobPostings){
      String company = Strings.nullToEmpty(job.getCompany());
      if(company.isBlank()){
        company = "unknown";
      }
      if(companyMap.containsKey(company)){
        companyMap.get(company).add(job);
      }else{
        List<JobPosting> list = new ArrayList<>();
        list.add(job);
        companyMap.put(company, list);
      }
    }

    // map the map to list of companies
    long id = 1;
    Companies companies = new Companies();
    for(Map.Entry<String, List<JobPosting>> entrySet : companyMap.entrySet()){
      Company company = new Company(id++, entrySet.getKey(), entrySet.getValue());
      companies.addCompany(company);
    }

    return companies;
  }

  public JobPosting updateJobStatus(Long id, String status) {
    Status s = Status.getStatusByName(status);
    if(s == null){
      return null;
    }
    Optional<JobPosting> optionalJobStatus = databaseService.getJobById(id);
    if(optionalJobStatus.isEmpty()){
      return null;
    }
    JobPosting jobPosting = optionalJobStatus.get();
    jobPosting.setStatus(s.getLowercase());
    jobPosting = databaseService.updateJobPosting(jobPosting);
    return jobPosting;
  }

  public List<JobPosting> updateMultipleJobStatuses(List<Long> jobStatuses, String status) {
    List<JobPosting> jobPostings = new ArrayList<>();
    for(Long id : jobStatuses){
      JobPosting jobPosting = updateJobStatus(id, status);
      if(jobPosting != null){
        jobPostings.add(jobPosting);
      }
    }
    return jobPostings;
  }

  public List<String> getBlacklistedCompanies() {
    return databaseService.getAllBlacklistedCompanies();
  }

  public BlacklistedCompany addBlacklistedCompany(BlacklistedCompany blc) {
    return databaseService.addBlacklistedCompany(blc);
  }

  public void deleteBlacklistedCompany(BlacklistedCompany blc) {
    databaseService.removeBlacklistedCompany(blc);
  }
}