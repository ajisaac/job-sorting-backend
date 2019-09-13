package com.aaron.jobbackend;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

public class App extends Application<JobConfiguration> {
	public static void main(String[] args) throws Exception {
		new App().run(args);
	}

	@Override
	public void run(JobConfiguration jobConfiguration,
	                Environment environment) throws Exception {

		// Enable CORS headers
		final FilterRegistration.Dynamic cors =
				environment.servlets().addFilter("CORS", CrossOriginFilter.class);

		// Configure CORS parameters
		cors.setInitParameter("allowedOrigins", "*");
		cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
		cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

		// Add URL mapping
		cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");


		final JobService jobService = new JobService();
		final JobResource jobResource = new JobResource(jobService);
		environment.jersey().register(jobResource);

	}
}
