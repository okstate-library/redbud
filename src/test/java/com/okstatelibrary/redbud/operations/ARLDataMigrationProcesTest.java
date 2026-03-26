package com.okstatelibrary.redbud.operations;

import static org.mockito.Mockito.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.junit.jupiter.api.extension.ExtendWith;

import com.okstatelibrary.redbud.entity.LocationModel;
import com.okstatelibrary.redbud.folio.entity.inventory.Item;
import com.okstatelibrary.redbud.service.*;
import com.okstatelibrary.redbud.service.external.FolioService;

public class ARLDataMigrationProcesTest {

	@InjectMocks
    private ARLDataMigrationProces process;

    @Mock
    private InstitutionService institutionService;
    @Mock
    private CampusService campusService;
    @Mock
    private LibraryService libraryService;
    @Mock
    private LocationService locationService;
    @Mock
    private ServicePointService servicePointService;
    @Mock
    private CirculationLogService circulationLogService;

    @Mock
    private FolioService folioService;

//    @Test
//    void shouldWriteSingleRowWhenCountGreaterThanZero() throws Exception {
//
//        // Mock location
//        LocationModel location = new LocationModel();
//        location.location_id = "loc1";
//        location.institution = "Inst";
//        location.campus = "Campus";
//        location.library = "Library";
//        location.location = "Stacks";
//
//        ArrayList<LocationModel> locations = new ArrayList<>();
//        locations.add(location);
//
//        // Mock InfrastructureSetupProcess
//        InfrastructureSetupProcess infra = mock(InfrastructureSetupProcess.class);
//        when(infra.getLocations(any(), any(), any(), any()))
//                .thenReturn(locations);
////
////        // Mock format
////        InstanceFormat format = new InstanceFormat();
////        format.id = "f1";
////        format.name = "Print";
////
////        ArrayList<InstanceFormat> formats = new ArrayList<>();
////        formats.add(format);
////
////        // Mock type
////        InstanceType type = new InstanceType();
////        type.id = "t1";
////        type.name = "Book";
////
////        ArrayList<InstanceType> types = new ArrayList<>();
////        types.add(type);
//
////        when(folioService.getInstanceFormats()).thenReturn(formats);
////        when(folioService.getInstanceTypes()).thenReturn(types);
////        when(folioService.getInstanceCountByFormatAndType("f1", "t1", "loc1"))
////                .thenReturn(5);
//
//        // Execute
//        process.manipulate(
//                institutionService,
//                campusService,
//                libraryService,
//                locationService,
//                servicePointService,
//                circulationLogService
//        );
//
//        // Verify
//        verify(folioService, times(1))
//                .getInstanceCountByFormatAndType("f1", "t1", "loc1");
//    }
}