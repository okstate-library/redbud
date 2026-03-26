package com.okstatelibrary.redbud.operations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.okstatelibrary.redbud.entity.*;
import com.okstatelibrary.redbud.service.*;
import com.okstatelibrary.redbud.util.Constants;

public class InfrastructureSetupProcessTest {

    @InjectMocks
    private InfrastructureSetupProcess process;

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

    @BeforeEach
    void setup() {
        //MockitoAnnotations.openMocks(this);
    }

    // ===============================
    // ✅ TEST: manipulate() flow
    // ===============================
//    @Test
//    void testManipulate_shouldCallAllSetupMethods() throws Exception {
//
//        InfrastructureSetupProcess spyProcess = Mockito.spy(process);
//
//        doNothing().when(spyProcess).manipulate(
//                any(), any(), any(), any(), any());
//
//        spyProcess.manipulate(
//                institutionService,
//                campusService,
//                libraryService,
//                locationService,
//                servicePointService
//        );
//
//        verify(spyProcess, times(1))
//                .manipulate(any(), any(), any(), any(), any());
//    }
//
//    // ===============================
//    // ✅ TEST: getLocations mapping
//    // ===============================
//    @Test
//    void testGetLocations_shouldMapCorrectly() throws Exception {
//
//        // Mock data
//        Institution institution = new Institution();
//        institution.setInstitution_id("inst1");
//        institution.setInstitution_name("Inst Name");
//
////        Campus campus = new Campus();
////        campus.setCampus_id("camp1");
////        campus.setCampus_name("Campus Name");
////
////        Library library = new Library();
////        library.setLibrary_id("lib1");
////        library.setLibrary_name("Library Name");
////
////        Location location = new Location();
////        location.setLocation_id("loc1");
////        location.setLocation_name("Location Name");
////        location.setInstitution_id("inst1");
////        location.setCampus_id("camp1");
////        location.setLibrary_id("lib1");
//
//        //when(institutionService.getInstitutionList()).thenReturn(List.of(institution));
////        when(campusService.getCampusList()).thenReturn(List.of(campus));
////        when(libraryService.getLibraryList()).thenReturn(List.of(library));
////        when(locationService.getLocationList()).thenReturn(List.of(location));
//
//        // Act
//        ArrayList<LocationModel> result = process.getLocations(
//                institutionService,
//                campusService,
//                libraryService,
//                locationService
//        );
//
//        // Assert
//        assertEquals(1, result.size());
//
//        LocationModel model = result.get(0);
//        assertEquals("loc1", model.location_id);
//        assertEquals("Location Name", model.location);
//        assertEquals("Library Name", model.library);
//        assertEquals("Campus Name", model.campus);
//        assertEquals("Inst Name", model.institution);
//    }
//
//    // ===============================
//    // ✅ TEST: No new institutions added
//    // ===============================
//    @Test
//    void testSetupInstitution_whenExists_shouldNotInsert() {
//
//    	Institution existing = new com.okstatelibrary.redbud.entity.Institution();
//        
//        existing.setInstitution_id("1");
//
////        when(institutionService.getInstitutionList())
////                .thenReturn(List.of(existing));
//
//        process.setupInstitution(institutionService);
//
//        verify(institutionService, never()).saveInstitution(any());
//    }
//
//    // ===============================
//    // ✅ TEST: Insert new institution
//    // ===============================
//    @Test
//    void testSetupInstitution_whenNotExists_shouldInsert() {
//
//        when(institutionService.getInstitutionList())
//                .thenReturn(Collections.emptyList());
//
//        process.setupInstitution(institutionService);
//
//        verify(institutionService, atLeastOnce()).saveInstitution(any());
//    }
//
//    // ===============================
//    // ✅ TEST: Insert new campus
//    // ===============================
//    @Test
//    void testSetupCampus_shouldInsertIfMissing() {
//
//        when(campusService.getCampusList())
//                .thenReturn(Collections.emptyList());
//
//        process.setupCampus(campusService);
//
//        verify(campusService, atLeastOnce()).saveCampus(any());
//    }
//
//    // ===============================
//    // ✅ TEST: Insert new library
//    // ===============================
//    @Test
//    void testSetupLibrary_shouldInsertIfMissing() {
//
//        when(libraryService.getLibraryList())
//                .thenReturn(Collections.emptyList());
//
//        process.setupLibrary(libraryService);
//
//        verify(libraryService, atLeastOnce()).saveLibrary(any());
//    }
//
//    // ===============================
//    // ✅ TEST: Insert new location
//    // ===============================
//    @Test
//    void testSetupLocation_shouldInsertIfMissing() {
//
//        when(locationService.getLocationList())
//                .thenReturn(Collections.emptyList());
//
//        process.setupLocation(locationService);
//
//        verify(locationService, atLeastOnce()).saveLocation(any());
//    }
//
//    // ===============================
//    // ✅ TEST: Insert new service point
//    // ===============================
//    @Test
//    void testSetupServicePoint_shouldInsertIfMissing() {
//
//        when(servicePointService.getServicePointList())
//                .thenReturn(Collections.emptyList());
//
//        process.setupServicePoint(servicePointService);
//
//        verify(servicePointService, atLeastOnce()).saveServicePoint(any());
//    }
}