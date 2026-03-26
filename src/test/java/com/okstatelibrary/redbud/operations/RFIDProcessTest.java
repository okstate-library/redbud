
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

import com.okstatelibrary.redbud.folio.entity.inventory.Item;
import com.okstatelibrary.redbud.service.external.FolioService;

@ExtendWith(MockitoExtension.class)
class RFIDProcessTest {

    @Mock
    private FolioService folioService;

    @InjectMocks
    private RFIDProcess rfidProcess;

    @TempDir
    Path tempDir;

    private Path csvFile;

//    @BeforeEach
//    void setup() throws Exception {
//    	
//        csvFile = tempDir.resolve("test.csv");
//
//        try (FileWriter writer = new FileWriter(csvFile.toFile())) {
//            writer.write("barcode\n");
//            writer.write("36135018220188\n");
//        }
//    }
//
//    @Test
//    void shouldUpdateItemWhenStatisticalCodeMissing() {
//
//        // Arrange
//        Item item = new Item();
//        
//        item.barcode = "12345";
//        item.statisticalCodeIds = new ArrayList<>();
//
//        ArrayList<Item> items = new ArrayList<>();
//        items.add(item);
//
//        try {
//			when(folioService.getItembyBarcode("12345")).thenReturn(items);
//		} catch (RestClientException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        
//        when(folioService.updateItem4Rfid(any())).thenReturn(true);
//
//        // Act
//        rfidProcess.processCsvFile(csvFile);
//
//        // Assert
//        verify(folioService, times(1)).updateItem4Rfid(any());
//    }
//
//    @Test
//    void shouldNotUpdateWhenItemAlreadyHasStatisticalCode() {
//
//        Item item = new Item();
//        
//        item.barcode = "12345";
//        item.statisticalCodeIds = new ArrayList<>();
//        item.statisticalCodeIds.add("existing-code");
//
//        ArrayList<Item> items = new ArrayList<>();
//        items.add(item);
//
//        try {
//			when(folioService.getItembyBarcode("12345")).thenReturn(items);
//		} catch (RestClientException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//        rfidProcess.processCsvFile(csvFile);
//
//        verify(folioService, never()).updateItem4Rfid(any());
//    }
//
//    @Test
//    void shouldWriteToNoItemsFoundFileWhenItemMissing() throws Exception {
//
//        when(folioService.getItembyBarcode("36135005469210")).thenReturn(null);
//
//        rfidProcess.processCsvFile(csvFile);
//
//        Path outputFile = tempDir.resolve("NoItemsFound_test.csv");
//
//        assert(Files.exists(outputFile));
//    }
}