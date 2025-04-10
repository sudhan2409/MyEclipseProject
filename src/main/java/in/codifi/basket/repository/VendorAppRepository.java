package in.codifi.basket.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.codifi.basket.entity.primary.VendorAppEntity;

public interface VendorAppRepository extends JpaRepository<VendorAppEntity, Long> {

	VendorAppEntity findByApiKey(String apiKey);

}
