package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgPropertyShareTypes;
import com.integration.zoy.entity.ZoyPgPropertyShareTypesId;

@Repository
public interface ZoyPgPropertyShareTypesRepository extends JpaRepository<ZoyPgPropertyShareTypes, ZoyPgPropertyShareTypesId> {


}
