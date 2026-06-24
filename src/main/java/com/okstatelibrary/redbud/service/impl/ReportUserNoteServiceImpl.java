package com.okstatelibrary.redbud.service.impl;

import com.okstatelibrary.redbud.entity.ReportUserNote;
import com.okstatelibrary.redbud.repository.ReportUserNoteDao;
import com.okstatelibrary.redbud.service.ReportUserNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReportUserNoteServiceImpl implements ReportUserNoteService {

	// private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private ReportUserNoteDao reportUserNoteDao;

	@Override
	public ReportUserNote saveReportUserNote(ReportUserNote reportUserNote) {
		return reportUserNoteDao.save(reportUserNote);
	}

	@Override
	public List<ReportUserNote> getReportUserNoteList(String noteTypeId) {
		return reportUserNoteDao.getReportUserNoteByNoteTypeId(noteTypeId);
	}

	@Override
	public boolean deleteReportUserNote(String noteId) {

		ReportUserNote userNote = reportUserNoteDao.getReportUserNoteByNoteId(noteId);

		reportUserNoteDao.delete(userNote);

		return true;
	}

	@Override
	public void truncate() {
		reportUserNoteDao.truncate();
	}

}