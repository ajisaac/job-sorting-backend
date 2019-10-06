package co.aisaac.jobsort;

import co.aisaac.jobsort.pojo.Company;
import co.aisaac.jobsort.pojo.Summary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
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
		validStates.add("exclude");
		validStates.add("applied");
		validStates.add("save");

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
		validStates.add("exclude");
		validStates.add("save");

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
		jobService.updateSummary(id, summary.getSummary());
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

}
