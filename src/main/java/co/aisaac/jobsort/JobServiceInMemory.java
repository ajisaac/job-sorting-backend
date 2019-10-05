package co.aisaac.jobsort;

import co.aisaac.jobsort.io.*;
import co.aisaac.jobsort.pojo.UpdateState;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
class JobServiceInMemory{

	private JobService jobService;

	JobServiceInMemory(JobService jobService) {
		this.jobService = jobService;
		var j = JobsListFromDatabase.load().getJobs();
		jobService.initFilterService(j);
	}

	public void updateJobStatus(UpdateState updateState, String state) {
		//update this jobs status in our database
		//find the job in our hashMap<list>
		//pop it from the list
		//push it to the new state list

	}

	public void updateMultipleJobsStatus(List<UpdateState> ids, String state) {
		//update all of these in our database in one fell swoop
		//group them into map<state, list<id>>
		//pop them all into a single list
		//update their internal state
		//push them all to new list

		//in the filter service we want to update the filter
		//for each job we will run it through the filter

	}
}

