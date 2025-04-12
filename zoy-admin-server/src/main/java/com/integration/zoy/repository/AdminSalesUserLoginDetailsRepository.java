package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.AdminSalesUserLoginDetails;

@Repository
public interface AdminSalesUserLoginDetailsRepository extends JpaRepository<AdminSalesUserLoginDetails, String> {

}
