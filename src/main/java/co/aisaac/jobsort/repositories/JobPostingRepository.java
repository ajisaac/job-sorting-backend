package co.aisaac.jobsort.repositories;

import co.aisaac.jobsort.pojo.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {}
