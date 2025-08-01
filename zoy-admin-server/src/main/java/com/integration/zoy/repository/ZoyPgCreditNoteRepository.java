package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgCreditNote;

@Repository
public interface ZoyPgCreditNoteRepository extends JpaRepository<ZoyPgCreditNote, String> {
}
