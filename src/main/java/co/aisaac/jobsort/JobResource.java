package co.aisaac.jobsort;

import co.aisaac.jobsort.pojo.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
		return jobService.getCompanies("");
	}

	@PostMapping("state/{state}/{id}")
	public ResponseEntity changeJobState(@PathVariable long id,
	                                     @PathVariable String state) {
		if (state.equals("save")) {
			jobService.saveJob(id);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		if (state.equals("applied")) {
			jobService.appliedJob(id);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		if (state.equals("exclude")) {
			jobService.excludeJob(id);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		if (state.equals("rejected")) {
			jobService.rejectJob(id);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		if (state.equals("ignored")) {
			jobService.ignoreJob(id);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity(HttpStatus.BAD_REQUEST);
	}

	@PostMapping("multiplestates/{state}")
	public ResponseEntity changeMultipleJobStates(@PathVariable String state,
	                                              @RequestBody List<Long> ids) {
		if (state.equals("save")) {
			jobService.saveJobs(ids);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		if (state.equals("exclude")) {
			jobService.excludeJobs(ids);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		if (state.equals("ignored")) {
			jobService.ignoreJobs(ids);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity(HttpStatus.BAD_REQUEST);
	}
}
