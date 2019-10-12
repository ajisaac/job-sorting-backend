package co.aisaac.jobsort.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Company {
	String name;
	List<Job> jobs;
	List<String> labels;

	private static String[] lArr = {
			"new",
			"saved",
			"applied",
			"interviewing",
			"rejected",
			"excluded",
			"ignored"
	};

	public Company(String name, List<Job> jobs) {
		this.name = name;
		this.jobs = jobs;
		List<String> l = jobs.stream()
				.map(Job::getJobState)
				.distinct()
				.collect(Collectors.toList());

		labels = new ArrayList<>();

		for (String s : lArr) {
			if (l.contains(s)) labels.add(s);
		}
	}

	public void applyTitleSearchTerm(String titleSearchTerm) {
		// for each job, include if we match the search term
		this.jobs = this.jobs.stream()
				.filter(job -> job.getTitle().toLowerCase().contains(
						titleSearchTerm.trim().toLowerCase())
				)
				.collect(Collectors.toList());
	}

	public void applyDescriptionSearchTerm(String descriptionSearchTerm) {
		// for each job, include if we match the description search term
		this.jobs = this.jobs.stream()
				.filter(job -> job.getSummary().toLowerCase().contains(
						descriptionSearchTerm.trim().toLowerCase())
				)
				.collect(Collectors.toList());
	}

	// remove the jobs that don't have this state
	public void filter(String filter) {
		this.jobs = this.jobs.stream()
				.filter(job -> job.getJobState().equals(filter))
				.collect(Collectors.toList());
	}

	// remove jobs who have
	public void applyTitleFilters(List<String> titleFitlers) {
		// for each job, if it contains one of the title filters, include, else don't
		this.jobs = this.jobs.stream()
				.filter(job -> {
					for (String titleFitler : titleFitlers) {
						if (job.getTitle().toLowerCase().contains(titleFitler.trim().toLowerCase())) {
							return true;
						}
					}
					return false;
				})
				.collect(Collectors.toList());
	}

	public String getName() {
		return name;
	}

	public List<Job> getJobs() {
		return jobs;
	}

	public List<String> getLabels() {
		return labels;
	}


	@Override
	public String toString() {
		return "Company{" +
				", name='" + name + '\'' +
				", jobs=" + jobs +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Company company = (Company) o;
		return Objects.equals(getName(), company.getName()) &&
				Objects.equals(getJobs(), company.getJobs());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName(), getJobs());
	}


}
