package com.aaron.jobbackend.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class JobIdsList {

	@JsonProperty("jobIds")
	List<Long> ids;

	public List<Long> getIds() {
		return ids;
	}

	void setIds(List<Long> ids) {
		this.ids = ids;
	}
}
