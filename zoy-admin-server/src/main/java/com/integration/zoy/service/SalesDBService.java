package com.integration.zoy.service;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.integration.zoy.entity.ZoyPgSalesMaster;
import com.integration.zoy.entity.ZoyPgSalesUserLoginDetails;
import com.integration.zoy.exception.WebServiceException;
import com.integration.zoy.model.ZoyPgSalesMasterModel;
import com.integration.zoy.repository.AdminSalesUserLoginDetailsRepository;
import com.integration.zoy.repository.ZoyPgSalesMasterRepository;
import com.integration.zoy.repository.ZoyPgSalesUserLoginDetailsRepository;
import com.integration.zoy.utils.PaginationRequest;

@Service
public class SalesDBService implements SalesDBImpl{

	@Autowired
	ZoyPgSalesUserLoginDetailsRepository zoyPgSalesUserLoginDetailsRepo;
	
	@Autowired
	ZoyPgSalesMasterRepository zoyPgSalesMasterRepo;
	
	@Autowired
	AdminSalesUserLoginDetailsRepository adminSalesUserLoginDetailsRepo;
	
	@Override
	public boolean existsByUserEmail(String userEmail) throws WebServiceException{
		return zoyPgSalesMasterRepo.existsByEmailId(userEmail);
	}
	
	@Override
	public ZoyPgSalesMaster saveAdminSalesUser(ZoyPgSalesMaster master) throws WebServiceException {
		return zoyPgSalesMasterRepo.save(master);
	}
	
	
	@Override
	public ZoyPgSalesUserLoginDetails saveAdminLoginDetails(ZoyPgSalesUserLoginDetails zoyPgSalesUserLoginDetails) throws WebServiceException{
		return zoyPgSalesUserLoginDetailsRepo.save(zoyPgSalesUserLoginDetails);
	}
	
	
	@Override
	public Page<ZoyPgSalesMasterModel> findAllSalesUsers(PaginationRequest paginationRequest) {
	    // Step 1: Mapping sort keys
	    Map<String, String> sortFieldMapping = new HashMap<>();
	    sortFieldMapping.put("emailId", "email_id");
	    sortFieldMapping.put("employeeId", "employee_id");
	    sortFieldMapping.put("fullName", "first_name");
	    sortFieldMapping.put("mobileNo", "mobile_no");
	    sortFieldMapping.put("createdAt", "created_at");

	    // Step 2: Handle nulls for sorting
	    String sortKey = paginationRequest.getSortActive();
	    String sortDir = paginationRequest.getSortDirection();

	    if (sortKey == null || sortKey.isBlank()) {
	        sortKey = "fullName"; // default field
	    }
	    if (sortDir == null || sortDir.isBlank()) {
	        sortDir = "asc"; // default direction
	    }

	    String sortColumn = sortFieldMapping.getOrDefault(sortKey, "first_name");
	    Sort sort = Sort.by(Sort.Order.by(sortColumn)
	            .with(Sort.Direction.fromString(sortDir))
	            .ignoreCase());

	    // Step 3: Build pageable
	    Pageable pageable = PageRequest.of(paginationRequest.getPageIndex(), paginationRequest.getPageSize(), sort);

	    // Step 4: Handle null filter
	    String searchText = "";
	    if (paginationRequest.getFilter() != null && paginationRequest.getFilter().getSearchText() != null) {
	        searchText = paginationRequest.getFilter().getSearchText();
	    }

	    // Step 5: Query database
	    Page<Object[]> results = zoyPgSalesMasterRepo.findAllSalesPeople(pageable, searchText);

	    // Step 6: Map each result to model
	    return results.map(result -> {
	        String emailId = (String) result[0];
	        String employeeId = (String) result[1];
	        String firstName = (String) result[2];
	        String middleName = (String) result[3];
	        String lastName = (String) result[4];
	        String mobileNo = (String) result[5];
	        Timestamp createdAt = (Timestamp) result[6];

	        String fullName = Stream.of(firstName, middleName, lastName)
	                .filter(Objects::nonNull)
	                .collect(Collectors.joining(" ")).trim();

	        String createdAtStr = createdAt != null ? createdAt.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;

	        ZoyPgSalesMasterModel model = new ZoyPgSalesMasterModel();
	        model.setEmailId(emailId);
	        model.setEmployeeId(employeeId);
	        model.setMobileNo(mobileNo);
	        model.setCreatedAt(createdAtStr);
	        model.setFullName(fullName);

	        return model;
	    });
	}

	@Override
	public Optional<ZoyPgSalesMaster> findByEmail(String userEmail) {
		return zoyPgSalesMasterRepo.findByEmailId(userEmail);
	}

	@Override
	public Optional<ZoyPgSalesUserLoginDetails> findLoginDetailsByEmail(String email) {
		return zoyPgSalesUserLoginDetailsRepo.findByUserEmail(email);
	}

}
