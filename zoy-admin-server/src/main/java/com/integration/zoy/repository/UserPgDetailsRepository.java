package com.integration.zoy.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.NotificationModeMaster;
import com.integration.zoy.entity.UserPgDetails;
import com.integration.zoy.entity.ZoyPgPropertyDetails;

@Repository
public interface UserPgDetailsRepository extends JpaRepository<UserPgDetails, String> {
}
