package com.aaron.jobbackend.jobslist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import com.aaron.jobbackend.Job;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JobsListFromDirectory implements JobsListLoader {
	private final String directory;

	/**
	 * a construct for loading jobs from an array of json files in directory
	 * @param directory
	 */
	public JobsListFromDirectory(String directory) {
		this.directory = directory;
	}

	/**
	 * load the files and return
	 * @return a JobsList with all of the jobs in it
	 * potentially a JobsList with no jobs
	 */
	public JobsList load() {
		try {
			List<Path> paths = getJsonFilesFromDirectory(directory);
			List<Job> jobs = parseJobsFromFiles(paths);
			return new JobsList(jobs);
		} catch (IOException e) {
			e.printStackTrace();
			return new JobsList(new ArrayList<>());
		}
	}

	private List<Job> parseJobsFromFiles(List<Path> paths) throws IOException {
		// parse the json into jobs list
		List<Job> jobs = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();

		for (Path p : paths) {
			List<Job> j = objectMapper.readValue(p.toFile(),
					objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, Job.class));

			jobs.addAll(j);
		}
		return jobs;
	}

	private static List<Path> getJsonFilesFromDirectory(String directory) {
		try {
			return Files.walk(Paths.get(directory), 1)
					.filter(Files::isRegularFile)
					.filter((path) -> path.getFileName().toString().endsWith(".json"))
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}

	}
}
