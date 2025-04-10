package in.codifi.basket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.codifi.basket.entity.primary.ResearchcallOrderEntity;

public interface ResearchOrderRepository extends JpaRepository<ResearchcallOrderEntity, Long> {

	/**
	 * 
	 * Method to get by users
	 *
	 * @author Pugal
	 *
	 * @param clientId
	 * @param all
	 * @return
	 */
//	@Transactional
//	@Query(value = "SELECT a FROM tbl_researchcall_order a WHERE user_id IN (:userIds)")
//	List<ResearchcallOrderEntity> getByUserId(@Param("userIds") List<String> userIds);

	@Query(value = " select a from TBL_RESEARCHCALL_MASTER a where USER_ID in (:clientId,:all)")
	List<ResearchcallOrderEntity> getByUserId(String clientId, String all);

	@Query(value = " select a from TBL_RESEARCHCALL_MASTER a where USER_ID in (:clientId,:all)")
	List<ResearchcallOrderEntity> getById(String clientId, String all);

}
