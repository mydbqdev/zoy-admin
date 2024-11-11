package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.AdminUserMaster;

@Repository
public interface AdminUserMasterRepository extends JpaRepository<AdminUserMaster, String> {

	@Query(value = "SELECT um.first_name, um.last_name, um.user_email, um.contact_number, "
            + "um.designation, um.status, "
            + "CASE WHEN ut.is_approve = true THEN 'approved' ELSE 'pending' END as approve_status, "
            + "ar.id as role_id, ar.role_name as role_name "
            + "FROM pgadmin.user_master um "
            + "left JOIN pgadmin.user_temprory ut ON um.user_email = ut.user_email "
            + "left JOIN pgadmin.user_role ur ON ur.user_email = um.user_email "
            + "left JOIN pgadmin.app_role ar ON ur.role_id = ar.id "
            + "GROUP BY um.user_email, ar.id, ar.role_name, ut.is_approve", nativeQuery = true)
	List<Object[]> findAllAdminUserPrevilages();

	
	@Query(value="select um.first_name,um.last_name, "
			+ "um.user_email, "
			+ "um.contact_number, "
			+ "um.designation, "
			+ "um.status,STRING_AGG( distinct case when rs.read_prv = true and rs.write_prv = true "
			+ "then upper(rs.screen_name) || '_READ,' || upper(rs.screen_name) || '_WRITE' when rs.read_prv = true "
			+ "then upper(rs.screen_name) || '_READ' when rs.write_prv = true then upper(rs.screen_name) || '_WRITE' "
			+ "else upper(rs.screen_name) end, ',' )  "
			+ "as approved_privilege  from pgadmin.user_master um left join pgadmin.user_role ur on um.user_email =ur.user_email  "
			+ "left join pgadmin.role_screen rs on rs.role_id =ur.role_id where um.user_email=:emailId group by um.user_email",nativeQuery = true)
	List<String[]> findAllAdminUserDetails(String emailId);
}