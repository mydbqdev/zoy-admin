package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.RoleScreen;

@Repository
public interface RoleScreenRepository extends JpaRepository<RoleScreen, Long> {

	@Query(value="select * from pgadmin.role_screen rs where rs.role_id =:id", nativeQuery = true)
	List<RoleScreen> findRoleScreen(Long id);

	@Query(value="select STRING_AGG( distinct case when rs.read_prv = true and rs.write_prv = true then upper(rs.screen_name) || '_READ,' "
			+ "|| upper(rs.screen_name) || '_WRITE' when rs.read_prv = true then upper(rs.screen_name) || '_READ' when rs.write_prv = true "
			+ "then upper(rs.screen_name) || '_WRITE' else upper(rs.screen_name) end, ',' ) "
			+ "as privilege from pgadmin.user_role ur join pgadmin.role_screen rs on rs.role_id =ur.role_id join pgadmin.user_temprory ut on "
			+ "ur.user_email = ut.user_email and ut.is_approve = true and ur.user_email =:email", nativeQuery = true)
	String findUserRole(String email);
}
