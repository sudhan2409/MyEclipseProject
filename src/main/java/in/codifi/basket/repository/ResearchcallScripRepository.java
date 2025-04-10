package in.codifi.basket.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.codifi.basket.entity.primary.ResearchcallScripEntity;

public interface ResearchcallScripRepository extends JpaRepository<ResearchcallScripEntity, Long> {

	@SuppressWarnings("unchecked")
	ResearchcallScripEntity saveAndFlush(ResearchcallScripEntity ResearchcallScripEntity);

//	List<ResearchcallScripEntity> findByResearchcallId(@Param("id") long researchcallId);

	/**
	 *
	 * Method to get scrips list from scriptable by researchcall id
	 *
	 * @author gokul raj
	 *
	 * @param researchCallId
	 * @return
	 */
	@Query("SELECT r FROM TBL_RESEARCH_SCRIP r WHERE r.researchcallId = :researchCallId")
	List<ResearchcallScripEntity> findByResearchcallId(@Param("researchCallId") long researchCallId);

//	@Transactional
//	@Query("SELECT a FROM TBL_RESEARCH_SCRIP a where a.status <> 'close' and a.researchcallId = :id")
//	List<ResearchcallScripEntity> getByResearchcallId(@Param("id") long pId);

	@Transactional
	@Query("SELECT a FROM TBL_RESEARCH_SCRIP a where a.researchcallId = :id ORDER BY a.createdOn DESC")
	List<ResearchcallScripEntity> getByResearchcallId(@Param("id") int pId);

	@Transactional
	@Modifying
	@Query("UPDATE TBL_RESEARCH_SCRIP SET status = 'close' WHERE expiry_date = :expiryDate and id>1")
	int updateActiveStatusForExpiredScrips(@Param("expiryDate") String expiryDate);

}
