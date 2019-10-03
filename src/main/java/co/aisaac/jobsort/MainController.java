package co.aisaac.jobsort;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller("/")
public class MainController {
	@GetMapping("home")
	public ModelAndView displayJobs(Map<String, Object> model){
		return new ModelAndView("index", model);
	}
}
