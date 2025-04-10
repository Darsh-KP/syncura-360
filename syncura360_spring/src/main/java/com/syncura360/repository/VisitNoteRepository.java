package com.syncura360.repository;

import com.syncura360.model.VisitNote;
import com.syncura360.model.VisitNoteId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitNoteRepository extends JpaRepository<VisitNote, VisitNoteId> {
}
