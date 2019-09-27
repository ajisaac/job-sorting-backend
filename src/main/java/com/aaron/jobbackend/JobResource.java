package com.aaron.jobbackend;


import com.aaron.jobbackend.pojo.Company;
import com.aaron.jobbackend.pojo.Job;
import com.aaron.jobbackend.pojo.JobIdsList;
import com.aaron.jobbackend.pojo.JobSummary;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/jobs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JobResource {
	final JobService jobService;

	public JobResource(JobService jobService) {
		this.jobService = jobService;
	}

	@GET
	@Path("bycompany")
	public List<Company> getJobsByCompany() {
		return jobService.getAllCompanies();
	}

	@POST
	@Path("updatemultiplejobsstatus/{state}")
	public void updateMultipleJobsStatus(@PathParam("state") String jobState,
	                                     JobIdsList ids) {
		jobService.updateMultipleJobsStatus(ids.getIds(), jobState);
	}

	@POST
	@Path("updatesinglejobstatus/{id}/{state}")
	public void updateSingleJobStatus(@PathParam("state") String jobState,
	                                  @PathParam("id") Long id) {
		jobService.updateJobStatus(id, jobState);
	}

	@POST
	@Path("updatesinglejobsummary/{id}")
	public void updateSingleJobSummary(@PathParam("id") Long id, JobSummary jobSummary) {

		jobService.updateJobSummary(id, jobSummary);
	}

	@POST
	@Path("blacklistcompany/{companyname}")
	public void blacklistCompany(@PathParam("companyname") String companyName){
		jobService.blacklistCompany(companyName);
	}

	@DELETE
	@Path("blacklistcompany/{companyname}")
	public void removeBlacklistedCompany(@PathParam("companyname") String companyName){
		jobService.removeBlacklistedCompany(companyName);
	}

	@GET
	@Path("blacklistedcompanies")
	public List<String> getBlackListedCompanies(){
		return jobService.getBlackListedCompanies();
	}

}
