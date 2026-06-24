package com.okstatelibrary.redbud.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.okstatelibrary.redbud.entity.ReportUserNote;

import java.util.List;

import javax.transaction.Transactional;

public interface ReportUserNoteDao extends CrudRepository<ReportUserNote, Integer> {

	@SuppressWarnings("unchecked")
	ReportUserNote save(ReportUserNote reportUserNote);

	List<ReportUserNote> findAll();

	@Query(value = "SELECT * FROM report_user_note WHERE note_id= :noteId", nativeQuery = true)
	ReportUserNote getReportUserNoteByNoteId(@Param("noteId") String noteId);

	@Query(value = "SELECT * FROM report_user_note where note_type_id =:noteTypeId", nativeQuery = true)
	List<ReportUserNote> getReportUserNoteByNoteTypeId(@Param("noteTypeId") String noteTypeId);

	@Modifying
	@Transactional
	@Query(value = "Truncate table report_user_note", nativeQuery = true)
	void truncate();
}