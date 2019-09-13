package com.aaron.jobbackend;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class JobIdsList {

	@JsonProperty("jobIds")
	List<Long> ids;

	List<Long> getIds() {
		return ids;
	}

	void setIds(List<Long> ids) {
		this.ids = ids;
	}
}
