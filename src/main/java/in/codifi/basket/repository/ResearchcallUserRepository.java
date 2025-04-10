package in.codifi.basket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.codifi.basket.entity.primary.ReasearchCallUsers;

public interface ResearchcallUserRepository extends JpaRepository<ReasearchCallUsers, Long> {

//	@Query("SELECT r.userId FROM ReasearchCallUsers r WHERE r.researchCallId = :researchCallId")
//	List<String> findUserIdsByResearchCallId(@Param("researchCallId") Long researchCallId);
//
//	@Modifying
//	@Query("DELETE FROM TBL_RESEARCHCALL_USERMAPPING r WHERE r.researchcallId = :researchcallId")
//	void deleteByResearchCallId(@Param("researchcallId") Long researchcallId);
//
//	@Query("SELECT r FROM TBL_RESEARCHCALL_USERMAPPING r WHERE r.userId = :userId OR r.userId = 'ALL'")
//	List<ReasearchCallUsers> findByUserIdOrAll(@Param("userId") String userId);

	@Query("SELECT r.userId FROM ReasearchCallUsers r WHERE r.researchCallId = :researchCallId")
	List<String> findUserIdsByResearchCallId(@Param("researchCallId") Long researchCallId);

	@Modifying
	@Query("DELETE FROM TBL_RESEARCHCALL_USERMAPPING r WHERE r.researchcallId = :researchcallId")
	void deleteByResearchCallId(@Param("researchcallId") Long researchcallId);

	@Query("SELECT r FROM TBL_RESEARCHCALL_USERMAPPING r WHERE r.userId = :userId OR r.userId = 'ALL'")
	List<ReasearchCallUsers> findByUserIdOrAll(@Param("userId") String userId);

	@Query("SELECT r FROM TBL_RESEARCHCALL_USERMAPPING r WHERE r.userId IN (:userIds)")
	List<ReasearchCallUsers> findByUserIdOrAll(@Param("userIds") List<String> userIds);
}
