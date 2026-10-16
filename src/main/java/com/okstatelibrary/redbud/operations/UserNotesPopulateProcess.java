package com.okstatelibrary.redbud.operations;

import java.util.ArrayList;
import java.util.List;

import com.okstatelibrary.redbud.entity.ReportUserNote;
import com.okstatelibrary.redbud.entity.UserNoteType;
import com.okstatelibrary.redbud.folio.entity.*;
import com.okstatelibrary.redbud.service.*;
import com.okstatelibrary.redbud.util.DateUtil;

public class UserNotesPopulateProcess extends MainProcess {

    private ReportUserNoteService reportUserNoteService;

    private UserNoteTypeService userNoteTypeService;

    public UserNotesPopulateProcess(ReportUserNoteService reportUserNoteService,
                                    UserNoteTypeService userNoteTypeService) {
        this.reportUserNoteService = reportUserNoteService;
        this.userNoteTypeService = userNoteTypeService;
    }

    //
    public void manipulate(boolean isCompareDateRange) {

        startTime = DateUtil.getTodayDateAndTime();

        try {

            System.out.println("Start UserNotesPopulateProcess Process");

            if (!isCompareDateRange) {
                reportUserNoteService.truncate();
            } else {
                System.out.println("Time Period : " + DateUtil.getYesterdayDateWithTime(true) + " to " +
                        DateUtil.getYesterdayDateWithTime(false));
            }

            List<UserNoteType> userNoteTypes = userNoteTypeService.getUserNoteTypeList();

            for (UserNoteType userNoteType : userNoteTypes) {

                ArrayList<UserNote> userNotes = folioService.getUserNotes(userNoteType.getUserNoteTypeId(),
                        isCompareDateRange);

                for (UserNote userNote : userNotes) {

                    if (userNote.links != null && userNote.links.size() > 0) {

                        String userId = userNote.links.get(0).id;

                        FolioUser folioUser = folioService.getUsersById(userNote.links.get(0).id);

                        if (folioUser != null) {

                            ReportUserNote reportUserNote = getReportUserNote(userNoteType, userNote, folioUser);

                            reportUserNoteService.saveReportUserNote(reportUserNote);

                        } else {
                            System.out.println("Null user id " + userId);
                        }

                    } else {

                        System.out.println("User links are" + userNote.id);
                    }

                }

                System.out.println("Note type: " + userNoteType.getName() + " # notes: " + userNotes.size());
            }

            System.out.println("End");

        } catch (Exception e1) {
            // TODO Auto-generated catch blocks
            e1.printStackTrace();
        }

    }

    private static ReportUserNote getReportUserNote(UserNoteType userNoteType, UserNote userNote, FolioUser folioUser) {

        ReportUserNote reportUserNote = new ReportUserNote();
        reportUserNote.setNoteId(userNote.id);
        reportUserNote.setNoteTypeId(userNoteType.getId());
        reportUserNote.setName(folioUser.personal.firstName + " " + folioUser.personal.lastName);
        reportUserNote.setPrimaryId(folioUser.externalSystemId);
        reportUserNote.setCreatedDate(userNote.metadata.createdDate);
        reportUserNote.setTitle(userNote.title);
        reportUserNote.setContent(userNote.content);
        return reportUserNote;

    }

};