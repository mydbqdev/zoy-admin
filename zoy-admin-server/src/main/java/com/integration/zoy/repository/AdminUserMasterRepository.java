package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.AdminUserMaster;

@Repository
public interface AdminUserMasterRepository extends JpaRepository<AdminUserMaster, String> {

	@Query(value="select um.first_name,um.last_name, "
			+ "um.user_email, "
			+ "um.contact_number, "
			+ "um.designation, "
			+ "um.status, "
			+ "STRING_AGG( distinct case when ut.is_approve = true then case when rs.read_prv = true and rs.write_prv = true "
			+ "then upper(rs.screen_name) || '_READ,' || upper(rs.screen_name) || '_WRITE' when rs.read_prv = true "
			+ "then upper(rs.screen_name) || '_READ' when rs.write_prv = true then upper(rs.screen_name) || '_WRITE' "
			+ "else upper(rs.screen_name) end else null end, ',' ) as approved_privilege, "
			+ "STRING_AGG( distinct case when ut.is_approve = false then case when rs.read_prv = true and rs.write_prv = true "
			+ "then upper(rs.screen_name) || '_READ,' || upper(rs.screen_name) || '_WRITE' when rs.read_prv = true "
			+ "then upper(rs.screen_name) || '_READ' when rs.write_prv = true then upper(rs.screen_name) || '_WRITE' "
			+ "else upper(rs.screen_name) end else null end, ',' ) as unapproved_privilege "
			+ "from pgadmin.user_master um "
			+ "join pgadmin.user_temprory ut on "
			+ "um.user_email = ut.user_email "
			+ "join pgadmin.role_screen rs on "
			+ "rs.role_id = ut.role_id "
			+ "group by um.user_email ",nativeQuery = true)
	List<String[]> findAllAdminUserPrevilages();

	
	@Query(value="select um.first_name,um.last_name, "
			+ "um.user_email, "
			+ "um.contact_number, "
			+ "um.designation, "
			+ "um.status,STRING_AGG( distinct case when rs.read_prv = true and rs.write_prv = true "
			+ "then upper(rs.screen_name) || '_READ,' || upper(rs.screen_name) || '_WRITE' when rs.read_prv = true "
			+ "then upper(rs.screen_name) || '_READ' when rs.write_prv = true then upper(rs.screen_name) || '_WRITE' "
			+ "else upper(rs.screen_name) end, ',' )  "
			+ "as approved_privilege  from pgadmin.user_master um join pgadmin.user_role ur on um.user_email =ur.user_email  "
			+ "join pgadmin.role_screen rs on rs.role_id =ur.role_id where um.user_email=:emailId group by um.user_email",nativeQuery = true)
	List<String[]> findAllAdminUserDetails(String emailId);
}