package com.okstatelibrary.redbud.service;

import java.util.List;

import com.okstatelibrary.redbud.entity.ReportUserNote;

public interface ReportUserNoteService {

	ReportUserNote saveReportUserNote(ReportUserNote reportUserNote);
	
	boolean deleteReportUserNote(String noteId);

	List<ReportUserNote> getReportUserNoteList(String noteTypeId);

	void truncate();

}