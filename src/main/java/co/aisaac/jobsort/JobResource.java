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

	/**
	 * Change state of a single job
	 *
	 * @param id    id of job
	 * @param state a particular state from a list of states
	 * @return 204 status
	 */
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

	/**
	 * Change state of a bunch of jobs
	 *
	 * @param state a particular state from a list of states
	 * @param ids   ids of all the jobs to update
	 * @return 204 status
	 */
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

	/**
	 * Update the summary of a single job
	 *
	 * @param id      job id
	 * @param summary summary text to update
	 * @return 204
	 */
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

	/**
	 * check or uncheck the title filter
	 *
	 * @param checked is checked or isn't checked
	 * @return 204
	 */
	@PostMapping("titlefilter/{checked}")
	public ResponseEntity titleFilterChecked(@PathVariable boolean checked) {
		jobService.setTitleFilterChecked(checked);
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	/**
	 * apply or unapply a label filter to companies.
	 *
	 * @param filter  one of the predetermined states from a list of states
	 * @param checked is this particular label checked
	 * @return 204
	 */
	@PostMapping("labelfilter/{filter}/{checked}")
	public ResponseEntity labelFilterChecked(@PathVariable String filter,
	                                         @PathVariable boolean checked) {
		jobService.setLabelFilterChecked(filter, checked);
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	/**
	 * black list a company so that it doesn't appear in our results
	 *
	 * @param company company name to blacklist
	 * @return 204
	 */
	@PostMapping("blacklist/{company}")
	public ResponseEntity blacklistCompany(@PathVariable String company) {
		try {
			jobService.addBlackListCompany(company);
		} catch (Exception ex) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	/**
	 * remove the blacklisted company
	 *
	 * @param company company to remove
	 * @return 204
	 */
	@DeleteMapping("blacklist/{company}")
	public ResponseEntity removeBlacklistCompany(@PathVariable String company) {
		try {
			jobService.removeBlackListCompany(company);
		} catch (Exception ex) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}


	/**
	 * apply or unapply a previous search filter to jobs based on the search term
	 *
	 * @param search  the search term
	 * @param checked is this particular search term checked
	 * @return 204
	 */
	@PostMapping("previoussearch/{search}/{checked}")
	public ResponseEntity previousSearchChecked(@PathVariable String search,
	                                            @PathVariable boolean checked) {
		jobService.setPreviousSearchChecked(search, checked);
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}


	@PostMapping("searchterm/{type}/{term}")
	public ResponseEntity previousSearchChecked(@PathVariable String type,
	                                            @PathVariable String term) {
		if (term.equals("blank")) term = "";

		if (type.equals("description")) {
			jobService.setDescriptionSearchTerm(term);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		if (type.equals("title")) {
			jobService.setTitleSearchTerm(term);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		if (type.equals("company")) {
			jobService.setCompanySearchTerm(term);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity(HttpStatus.BAD_REQUEST);
	}


}
