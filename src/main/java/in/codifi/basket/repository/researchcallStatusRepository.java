package in.codifi.basket.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.codifi.basket.entity.primary.ResearchCallStatusEntity;

public interface researchcallStatusRepository extends JpaRepository<ResearchCallStatusEntity, Long> {

	ResearchCallStatusEntity findByStatus(String status);

}
