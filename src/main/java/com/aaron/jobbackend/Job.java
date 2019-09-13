package com.aaron.jobbackend;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Job {
	private long id;
	private String title;
	private String summary;
	private String url;
	private String company;
	private String location;
	private String postDate;
	private String salary;
	private String jobState;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPostDate() {
		return postDate;
	}

	public void setPostDate(String postDate) {
		this.postDate = postDate;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public long getId() {
		return id;
	}

	public void setId(long i) {
		this.id = i;
	}

	public String getJobState() {
		return this.jobState;
	}

	public void setJobState(String jobState) {
		this.jobState = jobState;
	}


	@Override
	public String toString() {
		return "Job{" +
				"id=" + id +
				", title='" + title + '\'' +
				", summary='" + summary + '\'' +
				", url='" + url + '\'' +
				", company='" + company + '\'' +
				", location='" + location + '\'' +
				", postDate='" + postDate + '\'' +
				", salary='" + salary + '\'' +
				", jobState='" + jobState+ '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Job job = (Job) o;
		return Objects.equals(getUrl(), job.getUrl());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getUrl());
	}


}
