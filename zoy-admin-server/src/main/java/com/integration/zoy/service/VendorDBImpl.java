package com.integration.zoy.service;

import java.util.Optional;

import org.springframework.data.domain.Page;

import com.integration.zoy.entity.ZoyVendorMaster;
import com.integration.zoy.model.VendorResponseDto;
import com.integration.zoy.utils.PaginationRequest;

public interface  VendorDBImpl {

	Page<VendorResponseDto> findAllVendorUsers(PaginationRequest paginationRequest);

	Optional<ZoyVendorMaster> findVendor(String vendorId);

	ZoyVendorMaster saveVendor(ZoyVendorMaster vendor);

}
