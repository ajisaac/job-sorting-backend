package com.aaron.jobbackend;


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
	public List<Job> getJobs() {
		return jobService.getAllJobs();
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


}
