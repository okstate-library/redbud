
package com.okstatelibrary.redbud.operations;

import com.okstatelibrary.redbud.entity.LocationModel;
import com.okstatelibrary.redbud.folio.entity.holding.HoldingsRecord2;
import com.okstatelibrary.redbud.folio.entity.instance.Instance;
import com.okstatelibrary.redbud.service.*;
import com.okstatelibrary.redbud.util.DateUtil;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticalCodeUpdateProcess extends MainProcess {

    protected String startTime;

    public void manipulate(InstitutionService institutionService, CampusService campusService,
                           LibraryService libraryService, LocationService locationService, ServicePointService servicePointService,
                           CirculationLogService circulationLogService) throws IOException {

        try {

            System.out.println("start - UpdateCirculationLogRecordsProcess" + DateUtil.getTodayDateAndTime());

            InfrastructureSetupProcess infra = new InfrastructureSetupProcess();

            ArrayList<LocationModel> locations = infra.getLocations(institutionService, campusService, libraryService,
                    locationService);

            String institutionId = "535e9a12-cd83-4a05-a9b8-a14bafbf725b";

            List<LocationModel> filteredLocations = locations.stream()
                    .filter(location -> institutionId.equals(location.institution_id))
                    .collect(Collectors.toList());

            setCirculationProperties(filteredLocations);

            //updateInstance("d69d067b-7acd-5525-85a8-d3c9f9261051");

            System.out.println("end" + DateUtil.getTodayDateAndTime());

        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();

        }

    }

    private void setCirculationProperties(List<LocationModel> locations)
            throws RestClientException, IOException, InterruptedException {

        for (LocationModel location : locations) {

            //if (location.location_id.equals("0f736e9c-5be6-49bc-8715-0f93b1b23351")) {

                ArrayList<HoldingsRecord2> holdings = folioService
                        .getInventoryHoldingsByLocation(location.location_id);

                System.out.println(" Location " + location.location + " Number of holdings " + holdings.size());

//                int count = 0;
//
//                for (HoldingsRecord2 holdingRecord : holdings) {
//                    count++;
//
//                    updateInstance(holdingRecord.getInstanceId());
//
//                    if (count % 100 == 0) {
//                        System.out.println(count);
//                    }
//                }

                //break;
           // }

            //Thread.sleep(3000);

            //break;
        }

    }


    void updateInstance(String instanceId) {

        ArrayList<Instance> instances = folioService.getInstanceByInstanceId(instanceId);

        for (Instance instance : instances) {

            if (instance.statisticalCodeIds != null && !instance.statisticalCodeIds.isEmpty() && instance.statisticalCodeIds.size() > 0) {
                System.out.println(instanceId + " ---  " + instance.statisticalCodeIds.get(0));
            } else {

                // System.out.println("instanceId - " + instanceId);

                ArrayList<String> statisticalCodeIds = new ArrayList<>();

                statisticalCodeIds.add("10be3d97-6303-4fb4-9aa3-c50fbfa1fc5b");

                instance.statisticalCodeIds = statisticalCodeIds;

                if (!folioService.updateInstance(instance)) {
                    System.out.println("Failed to update Instance id: " + instance.id);
                } else {
                    System.out.println("Successfully updated Statistical Code Id " + instance.id);
                }
            }

        }
    }

}
