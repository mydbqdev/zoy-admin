package com.integration.zoy.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.AdminUserMaster;

@Repository
public interface AdminUserMasterRepository extends JpaRepository<AdminUserMaster, String> {

	@Query(value = "SELECT " +
	        "um.first_name AS firstName, " +
	        "um.last_name AS lastName, " +
	        "um.user_email AS userEmail, " +
	        "um.contact_number AS contactNumber, " +
	        "um.designation AS designation, " +
	        "um.status AS status, " +
	        "ar.id AS roleId, " +
	        "ar.role_name AS roleName, " +
	        "CASE WHEN ut.is_approve = true THEN 'approved' ELSE 'pending' END AS approveStatus, " +
	        "STRING_AGG(DISTINCT CASE " +
	        "WHEN ut.is_approve = true THEN " +
	        "CASE " +
	        "WHEN rs.read_prv = true AND rs.write_prv = true THEN upper(rs.screen_name) || '_READ,' || upper(rs.screen_name) || '_WRITE' " +
	        "WHEN rs.read_prv = true THEN upper(rs.screen_name) || '_READ' " +
	        "WHEN rs.write_prv = true THEN upper(rs.screen_name) || '_WRITE' " +
	        "ELSE upper(rs.screen_name) " +
	        "END " +
	        "ELSE NULL END, ',') AS approvedPrivilege, " +
	        "STRING_AGG(DISTINCT CASE " +
	        "WHEN ut.is_approve = false THEN " +
	        "CASE " +
	        "WHEN rs.read_prv = true AND rs.write_prv = true THEN upper(rs.screen_name) || '_READ,' || upper(rs.screen_name) || '_WRITE' " +
	        "WHEN rs.read_prv = true THEN upper(rs.screen_name) || '_READ' " +
	        "WHEN rs.write_prv = true THEN upper(rs.screen_name) || '_WRITE' " +
	        "ELSE upper(rs.screen_name) " +
	        "END " +
	        "ELSE NULL END, ',') AS unapprovedPrivilege " +
	        "FROM pgadmin.user_master um " +
	        "LEFT JOIN pgadmin.user_temprory ut ON um.user_email = ut.user_email " +
	        "LEFT JOIN pgadmin.user_role ur ON ur.user_email = um.user_email " +
	        "LEFT JOIN pgadmin.app_role ar ON ur.role_id = ar.id " +
	        "LEFT JOIN pgadmin.role_screen rs ON ar.id = rs.role_id " +
	        "GROUP BY um.user_email, ar.id, ar.role_name, ut.is_approve", 
	        nativeQuery = true)
	List<Object[]> findAllAdminUserPrivilegesOld();

	@Query(value = "select distinct * from ("
			+ "			select um.user_email, ur.role_id  ,'Approved' as is_approve,ar.role_name,\n"
			+ "	        STRING_AGG(DISTINCT CASE  \n"
			+ "	        WHEN rs.read_prv = true AND rs.write_prv = true THEN upper(rs.screen_name) || '_READ,' || upper(rs.screen_name) || '_WRITE'  \n"
			+ "	        WHEN rs.read_prv = true THEN upper(rs.screen_name) || '_READ'  \n"
			+ "	        WHEN rs.write_prv = true THEN upper(rs.screen_name) || '_WRITE'  \n"
			+ "	        ELSE null  \n"
			+ "	        END , ',') AS screens \n"
			+ "	       FROM pgadmin.user_master um  \n"
			+ "	        LEFT JOIN pgadmin.user_role ur ON ur.user_email = um.user_email  \n"
			+ "	        LEFT JOIN pgadmin.app_role ar ON ur.role_id = ar.id  \n"
			+ "	        LEFT JOIN pgadmin.role_screen rs ON ar.id = rs.role_id  \n"
			+ "	         GROUP BY um.user_email ,ur.role_id,ar.role_name\n"
			+ "	        union all\n"
			+ "	         select um.user_email, ut.role_id ,CASE WHEN ut.is_approve THEN 'Approved' ELSE 'Pending' END AS is_approve,art.role_name,\n"
			+ "	        STRING_AGG(DISTINCT CASE    \n"
			+ "	        WHEN rst.read_prv = true AND rst.write_prv = true THEN upper(rst.screen_name) || '_READ,' || upper(rst.screen_name) || '_WRITE'  \n"
			+ "	        WHEN rst.read_prv = true THEN upper(rst.screen_name) || '_READ'  \n"
			+ "	        WHEN rst.write_prv = true THEN upper(rst.screen_name) || '_WRITE'\n"
			+ "	        ELSE null  \n"
			+ "	        END , ',') AS screens \n"
			+ "	        FROM pgadmin.user_master um  \n"
			+ "	        LEFT JOIN pgadmin.user_temprory ut ON um.user_email = ut.user_email\n"
			+ "	        LEFT JOIN pgadmin.app_role art ON ut.role_id = art.id  \n"
			+ "	        LEFT JOIN pgadmin.role_screen rst ON art.id = rst.role_id  \n"
			+ "	       GROUP BY um.user_email ,ut.role_id,ut.is_approve, art.role_name)pre ", nativeQuery = true)	
	List<Object[]> findAllAdminUserPrivileges();

	
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
	
	@Transactional
	@Modifying
	@Query(value = "INSERT INTO user_role (user_email, role_id) " +
            "SELECT user_email, role_id FROM user_temprory " +
            "WHERE is_approve = false AND is_rejected = false AND user_email = :user_email", nativeQuery = true)
        void insertUserDetails(String user_email);
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE user_temprory SET is_approve = TRUE WHERE user_email = :user_email", nativeQuery = true)
	void approveUser(String user_email);
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE user_temprory\r\n"
			+ "	SET is_rejected = TRUE  WHERE user_email = :user_email", nativeQuery = true)
	void rejectUser(String user_email);
}