package co.aisaac.jobsort.repositories;

import co.aisaac.jobsort.pojo.BlacklistedCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlacklistedCompanyRepository extends JpaRepository<BlacklistedCompany, Long> {
  List<BlacklistedCompany> findByName(String name);
}
