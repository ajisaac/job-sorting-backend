package co.aisaac.jobsort.pojo;

import java.util.List;
import java.util.Objects;

public class Company {
	String name;
	List<Job> jobs;

	public Company(String name, List<Job> jobs) {
		this.name = name;
		this.jobs = jobs;
	}

	public String getName() {
		return name;
	}

	public List<Job> getJobs() {
		return jobs;
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
