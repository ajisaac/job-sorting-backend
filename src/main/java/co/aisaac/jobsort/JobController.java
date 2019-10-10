package co.aisaac.jobsort;

import co.aisaac.jobsort.pojo.Company;
import co.aisaac.jobsort.pojo.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Controller()
@RequestMapping("/jobs")
public class JobController {

	private JobService jobService;

	@Autowired
	public JobController(JobService jobService) {
		this.jobService = jobService;
	}

	@GetMapping("filter/{filter}")
	public ModelAndView displayAllJobs(Map<String, Object> model,
	                                   @PathVariable String filter) {

		List<Company> companies;
		if (filter.equals("all")) {
			companies = jobService.getCompanies("");
		} else {
			companies = jobService.getCompanies(filter);
		}

		model.put("titleFilterChecked", jobService.isTitleFilterChecked());
		model.put(filter, filter);
		model.put("state", filter.toUpperCase());
		model.put("titlefilters", jobService.getTitleFilters());
		model.put("labels", jobService.getLabels().entrySet());
		model.put("previousSearches", jobService.getPreviousSearches().entrySet());
		model.put("numCompanies", companies.size());

		int numJobs = companies.stream()
				.map(c -> c.getJobs().size())
				.reduce(0, Integer::sum);
		model.put("numJobs", numJobs);

		// our slowdown is the browser trying to render 7000 elements
		if (filter.equals("all") || filter.equals("ignored")) {
			companies = companies.subList(0, (Math.min(companies.size(), 200)));
		}
		model.put("companies", companies);

		List<String> blc = jobService.getBlackListedCompanies();
		model.put("blcompanies", blc);

		return new ModelAndView("index", model);
	}

	@GetMapping("add-job")
	public ModelAndView addJob(Map<String, Object> model){
		return new ModelAndView("add-job", model);
	}

	@PostMapping("add-job")
	public ModelAndView postJob(Map<String, Object> model,
	                            Job job){
		job.setSearchTerm("No Search Term");
		job.setPostDate("");
		try {
			jobService.insertJob(job);
		} catch (SQLException e) {
			// todo do something here, return a model with an error and a job
			// todo so we can try again
			e.printStackTrace();
		}
		return new ModelAndView("add-job", model);
	}
}
