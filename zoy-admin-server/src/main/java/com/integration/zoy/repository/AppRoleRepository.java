package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.AppRole;

@Repository
public interface AppRoleRepository extends JpaRepository<AppRole, Long> {

	@Query(value="select * from pgadmin.app_role ar where ar.role_name =:roleName",nativeQuery = true)
	AppRole findAppRole(String roleName);

}
