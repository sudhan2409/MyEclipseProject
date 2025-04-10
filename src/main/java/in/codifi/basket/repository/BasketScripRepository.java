package in.codifi.basket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import in.codifi.basket.entity.primary.BasketScripEntity;

public interface BasketScripRepository extends JpaRepository<BasketScripEntity, Long> {

	/**
	 * method to delete Basket Scrip
	 * 
	 * @author Gowthaman M
	 * @returnO
	 */
//	@Transactional
//	@Query(value = "delete from tbl_basket_order_scrip e where e.basketId = :basketId and e.id in (:id)")
//	long deleteBasketScrip(@Param("id") List<Long> list, @Param("basketId") long basketId);

	/**
	 * method to get Retrieve Scrips
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	List<BasketScripEntity> findByBasketId(@Param("basketId") long basketId);

	@SuppressWarnings("unchecked")
	BasketScripEntity saveAndFlush(BasketScripEntity basketScripEntity);

	/**
	 * method to delete expired Scrip
	 * 
	 * @author Gowthaman M
	 * @returnO
	 */
//	@Modifying
//	@Query(value = "DELETE FROM TBL_BASKET_ORDER_SCRIP e where e.expiry < current_date")
//	long deleteExpiredScrip();

}
