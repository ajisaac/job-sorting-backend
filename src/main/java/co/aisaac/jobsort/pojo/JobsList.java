package co.aisaac.jobsort.pojo;

import java.util.*;
import java.util.stream.Collectors;

public class JobsList {
	private List<Job> jobsList;

	public JobsList(List<Job> jl) {
		this.jobsList = jl;
	}

	public void removeDuplicates() {
		Set<Job> uj = new HashSet<>(jobsList);
		jobsList.clear();
		jobsList.addAll(uj);

		Map<String, List<Job>> uniqueUrls = jobsList.stream()
				.collect(Collectors.groupingBy(Job::getUrl));

		jobsList.clear();
		for (Map.Entry<String, List<Job>> e : uniqueUrls.entrySet()) {
			jobsList.add(e.getValue().get(0));
		}
	}

	public List<Job> getJobs() {
		return this.jobsList;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		JobsList jobsList1 = (JobsList) o;
		return Objects.equals(jobsList, jobsList1.jobsList);
	}

	@Override
	public int hashCode() {
		return Objects.hash(jobsList);
	}

	/**
	 * get all of the jobs that aren't in jlist
	 */
	public void getUnique(List<Job> jlist) {
		List<Job> unique = new ArrayList<>(jobsList);

		for (Job j : jobsList) {
			boolean add = true;

			// if the job already exists we can scratch it
			for (Job k : jlist) {
				if(k.getUrl().equalsIgnoreCase(j.getUrl())){
					add = false;
					break;
				}
				if(k.equals(j)){
					add = false;
					break;
				}
			}
			if(add) unique.add(j);
		}

		jobsList = unique;
	}
}
