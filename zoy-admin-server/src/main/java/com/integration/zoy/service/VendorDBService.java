package com.integration.zoy.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.integration.zoy.entity.ZoyVendorMaster;
import com.integration.zoy.model.VendorResponseDto;
import com.integration.zoy.repository.ZoyVendorMasterRepository;
import com.integration.zoy.utils.Filter;
import com.integration.zoy.utils.PaginationRequest;

@Service
public class VendorDBService implements VendorDBImpl{


	@Autowired
	ZoyVendorMasterRepository vendorMasterRepository;

	@Value("${app.minio.vendor.doc.bucket.name}")
	String vendorDocBucket;

	@Autowired
	ZoyS3Service s3Service;

	private ObjectMapper mapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

	@Override
	public Page<VendorResponseDto> findAllVendorUsers(PaginationRequest paginationRequest) {
		Map<String, String> sortFieldMapping = new HashMap<>();
		sortFieldMapping.put("companyName", "vendor_company_name");
		sortFieldMapping.put("email", "vendor_email");
		sortFieldMapping.put("firstName", "vendor_first_name");
		sortFieldMapping.put("mobileNo", "vendor_mobile_no");
		sortFieldMapping.put("status", "vendor_status");
		sortFieldMapping.put("createdAt", "created_at");

		String sortKey = paginationRequest.getSortActive();
		String sortDir = paginationRequest.getSortDirection();

		if (sortKey == null || sortKey.isBlank()) {
			sortKey = "companyName";
		}
		if (sortDir == null || sortDir.isBlank()) {
			sortDir = "asc";
		}

		String sortColumn = sortFieldMapping.getOrDefault(sortKey, "vendor_company_name");
		Sort.Order order = Sort.Order.by(sortColumn).with(Sort.Direction.fromString(sortDir));
		if (!"createdAt".equals(sortKey)) {
			order = order.ignoreCase();
		}
		Sort sort = Sort.by(order);
		Pageable pageable = PageRequest.of(paginationRequest.getPageIndex(), paginationRequest.getPageSize(), sort);
		String searchText = Optional.ofNullable(paginationRequest.getFilter())
				.map(Filter::getSearchText)
				.filter(text -> !text.isBlank())
				.orElse("");
		Page<Object[]> resultPage = vendorMasterRepository.findAllVendors(pageable, searchText);

		return resultPage.map(result -> {
			String vendorId = result[0] != null ? result[0].toString() : null;
			String companyName = result[1] != null ? result[1].toString() : null;
			String firstName = result[2] != null ? result[2].toString() : null;
			String lastName = result[3] != null ? result[3].toString() : null;
			String address = result[4] != null ? result[4].toString() : null;
			String mobileNo = result[5] != null ? result[5].toString() : null;
			String altMobile = result[6] != null ? result[6].toString() : null;
			String email = result[7] != null ? result[7].toString() : null;
			String gst = result[8] != null ? result[8].toString() : null;
			String statusLabel = result[9] != null ? result[9].toString() : null;
			String servicesJson = result[10] != null ? result[10].toString() : "[]"; 
			String filePathsStr = result[11] != null ? result[11].toString() : null;

			VendorResponseDto dto = new VendorResponseDto();
			dto.setVendorId(vendorId);
			dto.setVendorCompanyName(companyName);
			dto.setVendorFirstName(firstName);
			dto.setVendorLastName(lastName);
			dto.setVendorAddress(address);
			dto.setVendorMobileNo(mobileNo);
			dto.setVendorAlternativeNo(altMobile);
			dto.setVendorEmail(email);
			dto.setVendorGstRegistrastionNo(gst);
			dto.setVendorStatus(statusLabel);
			if (filePathsStr != null && !filePathsStr.isBlank()) {
				List<String> fileKeys = Arrays.asList(filePathsStr.split(","));
				List<String> preSignedUrls = fileKeys.stream()
						.map(imageUrl -> s3Service.generatePreSignedUrl(vendorDocBucket, imageUrl.trim()))
						.collect(Collectors.toList());
				dto.setVendorFilePaths(preSignedUrls);
			} else {
				dto.setVendorFilePaths(Collections.emptyList());
			}
			if (servicesJson != null && !servicesJson.isBlank()) {
				try {
					List<VendorResponseDto.VendorServiceDto> services = mapper.readValue(
							servicesJson,
							new TypeReference<List<VendorResponseDto.VendorServiceDto>>() {}
							);
					dto.setServices(services != null ? services : Collections.emptyList());
				} catch (Exception e) {
					e.printStackTrace();
					dto.setServices(Collections.emptyList());
				}
			} else {
				dto.setServices(Collections.emptyList());
			}
			return dto;
		});

	}

	@Override
	public Optional<ZoyVendorMaster> findVendor(String vendorId) {
		return vendorMasterRepository.findById(vendorId);
	}

	@Override
	public ZoyVendorMaster saveVendor(ZoyVendorMaster vendor) {
		return vendorMasterRepository.save(vendor);
	}

}
