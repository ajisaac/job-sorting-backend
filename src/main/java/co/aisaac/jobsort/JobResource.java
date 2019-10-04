package co.aisaac.jobsort;

import co.aisaac.jobsort.pojo.Company;
import co.aisaac.jobsort.pojo.JobIdsList;
import co.aisaac.jobsort.pojo.JobSummary;
import co.aisaac.jobsort.pojo.UpdateState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobResource {
	private final JobService jobService;

	@Autowired
	public JobResource(JobService jobService) {
		this.jobService = jobService;
	}

	@GetMapping("bycompany")
	public List<Company> getJobsByCompany() {
		return jobService.getAllCompanies("");
	}

	@PostMapping("updatemultiplejobsstatus/{state}")
	public void updateMultipleJobsStatus(
			@RequestBody List<UpdateState> ids, @PathVariable String state) {
		jobService.updateMultipleJobsStatus(ids, state);
	}

	@PostMapping("updatesinglejobstatus/{state}")
	public void updateSingleJobStatus(@RequestBody UpdateState updateState,
	                                  @PathVariable String state) {
		jobService.updateJobStatus(updateState, state);
	}

	@PostMapping("updatesinglejobsummary/{id}")
	public void updateSingleJobSummary(@RequestBody JobSummary jobSummary,
	                                   @PathVariable long id) {

		jobService.updateJobSummary(id, jobSummary);
	}

	@PostMapping("blacklistcompany/{companyName}")
	public void blacklistCompany(@PathVariable String companyName) {
		jobService.blacklistCompany(companyName);
	}

	@DeleteMapping("blacklistcompany/{companyName}")
	public void reoveBlacklistedCompany(@PathVariable String companyName) {
		jobService.removeBlacklistedCompany(companyName);
	}

	@GetMapping("blacklistedcompanies")
	public List<String> getBlackListedCompanies() {
		return jobService.getBlackListedCompanies();
	}

	@PostMapping("blocktitle/{phrase}")
	public void blockTitle(@PathVariable String phrase) throws Exception {
		jobService.blockTitle(phrase);
	}

	@DeleteMapping("blocktitle/{phrase}")
	public void removeBlocklistedCompany(@PathVariable String phrase) {
		jobService.removeBlockTitle(phrase);
	}

	@GetMapping("blocktitles")
	public List<String> getBlockTitles() {
		return jobService.getBlockTitles();
	}

}
