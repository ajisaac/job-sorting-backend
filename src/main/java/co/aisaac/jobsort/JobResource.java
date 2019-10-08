package co.aisaac.jobsort;

import co.aisaac.jobsort.pojo.Company;
import co.aisaac.jobsort.pojo.Summary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
		return jobService.getCompanies("");
	}

	@PostMapping("state/{state}/{id}")
	public ResponseEntity changeJobState(@PathVariable long id,
	                                     @PathVariable String state) {

		List<String> validStates = new ArrayList<>();
		validStates.add("ignored");
		validStates.add("rejected");
		validStates.add("excluded");
		validStates.add("applied");
		validStates.add("saved");
		validStates.add("interviewing");

		if (!validStates.contains(state))
			return new ResponseEntity(HttpStatus.BAD_REQUEST);

		try {
			jobService.updateJobStatus(id, state);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@PostMapping("multiplestates/{state}")
	public ResponseEntity changeMultipleJobStates(@PathVariable String state,
	                                              @RequestBody List<Long> ids) {
		List<String> validStates = new ArrayList<>();
		validStates.add("ignored");
		validStates.add("excluded");
		validStates.add("saved");

		if (!validStates.contains(state))
			return new ResponseEntity(HttpStatus.BAD_REQUEST);

		try {
			jobService.updateJobsStatus(ids, state);
		} catch (Exception e) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@PostMapping("summary/{id}")
	public ResponseEntity updateSummary(@PathVariable Long id,
	                                    @RequestBody Summary summary) {
		try {
			jobService.updateSummary(id, summary.getSummary());
		} catch (Exception e) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@PostMapping("blacklist/{company}")
	public ResponseEntity blacklistCompany(@PathVariable String company) {
		try {
			jobService.addBlackListCompany(company);
		} catch (Exception ex) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@DeleteMapping("blacklist/{company}")
	public ResponseEntity removeBlacklistCompany(@PathVariable String company) {
		try {
			jobService.removeBlackListCompany(company);
		} catch (Exception ex) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
}
