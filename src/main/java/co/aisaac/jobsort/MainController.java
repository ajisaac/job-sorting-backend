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
public class MainController {

	private JobService jobService;
	private FilterService filterService;

	@Autowired
	public MainController(JobService jobService,
	                      FilterService filterService) {
		this.jobService = jobService;
		this.filterService = filterService;
	}

	@GetMapping("filter/{filter}")
	public ModelAndView displayAllJobs(Map<String, Object> model,
	                                   @PathVariable String filter) {

		List<Company> companies;
		if (filter.equals("all")) {
			companies = jobService.getAllCompanies("");
		} else {
			companies = jobService.getAllCompanies(filter);
		}

		model.put("titlefilters", filterService.getTitleFilters());
		model.put("labels", filterService.getLabels().entrySet());
		model.put("previousSearches", filterService.getPreviousSearches().entrySet());
		model.put("numCompanies", companies.size());

		int numJobs = companies.stream()
				.map(c -> c.getJobs().size())
				.reduce(0, Integer::sum);
		model.put("numJobs", numJobs);

		// our slowdown is the browser trying to render 7000 elements
		if (filter.equals("all") || filter.equals("ignored")) {
			companies = companies.subList(0, (Math.min(companies.size(), 20)));
		} else {
			companies = companies.subList(0, (Math.min(companies.size(), 50)));
		}
		model.put("companies", companies);

		List<String> blc = filterService.getBlackListedCompanies();
		model.put("blcompanies", blc);

		return new ModelAndView("index", model);
	}
}
