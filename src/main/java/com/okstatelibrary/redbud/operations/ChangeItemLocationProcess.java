
package com.okstatelibrary.redbud.operations;

import com.okstatelibrary.redbud.service.*;
import com.okstatelibrary.redbud.util.DateUtil;

import java.io.IOException;

public class ChangeItemLocationProcess extends MainProcess {

    protected String startTime;

    public void manipulate(InstitutionService institutionService, CampusService campusService,
                           LibraryService libraryService, LocationService locationService, ServicePointService servicePointService,
                           CirculationLogService circulationLogService) throws IOException {

        try {

            System.out.println("start - ChangeItemLocationProcess" + DateUtil.getTodayDateAndTime());

            String locationID = "4e4331ec-d652-4591-af7f-4c0dc6ddf485";

            System.out.println("location " + locationID + " ## OKS-OSU LAB");

            folioService.getUpdateItemsByLocationAndUpdateItem(locationID);

            System.out.println("Done");

        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();

        }

    }

};