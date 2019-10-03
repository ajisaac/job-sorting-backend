package co.aisaac.jobsort.pojo;

import java.util.List;
import java.util.Objects;

public class Company {
	long id;
	String name;
	List<Job> jobs;

	public Company(long id, String name, List<Job> jobs) {
		this.id = id;
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
				"id=" + id +
				", name='" + name + '\'' +
				", jobs=" + jobs +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Company company = (Company) o;
		return id == company.id &&
				Objects.equals(getName(), company.getName()) &&
				Objects.equals(getJobs(), company.getJobs());
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, getName(), getJobs());
	}
}
