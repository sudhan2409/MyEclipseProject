package in.codifi.basket.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.codifi.basket.entity.primary.DeviceMappingEntity;

public interface DeviceMappingRepository extends JpaRepository<DeviceMappingEntity, Long> {

	List<DeviceMappingEntity> findByUserId(String userId);

	@Transactional
	@Query(value = " select distinct userId from TBL_DEVICE_MAPPING")
	List<String> getDistinctUserIds();

}
