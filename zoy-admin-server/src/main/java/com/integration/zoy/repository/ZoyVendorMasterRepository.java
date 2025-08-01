package com.integration.zoy.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyVendorMaster;

@Repository
public interface ZoyVendorMasterRepository extends JpaRepository<ZoyVendorMaster, String> {

	@Query(value = "select * from pgvendor.zoy_vendor_master zvm where zvm.vendor_email =:vendorEmail", nativeQuery = true)
	Optional<ZoyVendorMaster> findVendorByEmail(String vendorEmail);

	@Query(
			value = "SELECT  "
					+ "			zvm.vendor_id, "
					+ "			zvm.vendor_company_name, "
					+ "			zvm.vendor_first_name, "
					+ "			zvm.vendor_last_name, "
					+ "			zvm.vendor_address, "
					+ "			zvm.vendor_mobile_no, "
					+ "			zvm.vendor_alternative_no, "
					+ "			zvm.vendor_email, "
					+ "			zvm.vendor_gst_registrastion_no, "
					+ "			CASE  "
					+ "			WHEN zvm.vendor_status = 'P' THEN 'Pending for approval' "
					+ "			WHEN zvm.vendor_status = 'A' THEN 'Approved' "
					+ "			WHEN zvm.vendor_status = 'R' THEN 'Rejected' "
					+ "			ELSE 'Unknown' "
					+ "			END AS vendor_status_label, "
					+ "			CAST(JSON_AGG( "
					+ "				JSON_BUILD_OBJECT( "
					+ "					'vendor_service_id', zvsm.vendor_service_master_id, "
					+ "					'service_name', zvsm.vendor_service_name "
					+ "						) "
					+ "				) AS TEXT) AS services, "
					+ "			STRING_AGG(zvf.vendor_file_path, ',') AS file_paths "
					+ "			FROM pgvendor.zoy_vendor_master zvm "
					+ "			LEFT JOIN pgvendor.zoy_vendor_service zvs ON zvs.vendor_id = zvm.vendor_id "
					+ "			LEFT JOIN pgvendor.zoy_vendor_service_master zvsm ON zvsm.vendor_service_master_id = zvs.vendor_service_id "
					+ "			LEFT JOIN pgvendor.zoy_vendor_files zvf ON zvf.vendor_id = zvm.vendor_id "
					+ "			WHERE (:searchText IS NULL OR "
					+ "					LOWER(zvm.vendor_company_name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR "
					+ "					LOWER(zvm.vendor_first_name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR "
					+ "					LOWER(zvm.vendor_last_name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR "
					+ "					LOWER(zvm.vendor_email) LIKE LOWER(CONCAT('%', :searchText, '%')) OR "
					+ "					LOWER(zvm.vendor_mobile_no \\:\\: TEXT) LIKE LOWER(CONCAT('%', :searchText, '%')) OR "
					+ "					LOWER(zvm.vendor_gst_registrastion_no) LIKE LOWER(CONCAT('%', :searchText, '%')) "
					+ "					) "
					+ "			GROUP BY  "
					+ "			zvm.vendor_id, "
					+ "			zvm.vendor_company_name, "
					+ "			zvm.vendor_first_name, "
					+ "			zvm.vendor_last_name, "
					+ "			zvm.vendor_address, "
					+ "			zvm.vendor_mobile_no, "
					+ "			zvm.vendor_alternative_no, "
					+ "			zvm.vendor_email, "
					+ "			zvm.vendor_gst_registrastion_no, "
					+ "			zvm.vendor_status",
					countQuery = "SELECT COUNT(DISTINCT zvm.vendor_id) "
							+ "			FROM pgvendor.zoy_vendor_master zvm "
							+ "			LEFT JOIN pgvendor.zoy_vendor_service zvs ON zvs.vendor_id = zvm.vendor_id "
							+ "			LEFT JOIN pgvendor.zoy_vendor_service_master zvsm ON zvsm.vendor_service_master_id = zvs.vendor_service_id "
							+ "			WHERE (:searchText IS NULL OR "
							+ "					LOWER(zvm.vendor_company_name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR "
							+ "					LOWER(zvm.vendor_first_name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR "
							+ "					LOWER(zvm.vendor_last_name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR "
							+ "					LOWER(zvm.vendor_email) LIKE LOWER(CONCAT('%', :searchText, '%')) OR "
							+ "					LOWER(zvm.vendor_mobile_no\\:\\: TEXT) LIKE LOWER(CONCAT('%', :searchText, '%')) OR "
							+ "					LOWER(zvm.vendor_gst_registrastion_no) LIKE LOWER(CONCAT('%', :searchText, '%')) "
							+ "					)",
							nativeQuery = true
			)
	Page<Object[]> findAllVendors(Pageable pageable, String searchText);
}
