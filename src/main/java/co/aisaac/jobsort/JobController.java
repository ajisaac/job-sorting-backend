package co.aisaac.jobsort;

import co.aisaac.jobsort.pojo.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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
}
