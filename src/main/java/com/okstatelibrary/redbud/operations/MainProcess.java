package com.okstatelibrary.redbud.operations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.okstatelibrary.redbud.entity.CsvFileModel;
import com.okstatelibrary.redbud.folio.entity.Metadata;
import com.okstatelibrary.redbud.service.external.EmailService;
import com.okstatelibrary.redbud.service.external.FolioService;
import com.okstatelibrary.redbud.util.AppSystemProperties;
import com.okstatelibrary.redbud.util.Constants;
import com.okstatelibrary.redbud.util.DateUtil;

public class MainProcess {

	protected static FolioService folioService;

	public MainProcess() {

		folioService = new FolioService();
	}

	/**
	 * Logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(MainProcess.class);

	public void printScreen(String msg) {
		System.out.println(msg);

	}

	public void printScreen(String msg, Constants.ErrorLevel errorLevel) {
		switch (errorLevel) {
		case ERROR:
			LOG.error(msg);
			break;
		case INFO:
			LOG.info(msg);
			break;
		case WARNING:
			LOG.warn(msg);
			break;
		default:
			LOG.warn(msg);
			break;

		}
	}

	public static void printJson(Object object) {

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		Object obj = object;

		try {
			String json = mapper.writeValueAsString(obj);
			System.out.println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isStringNullOrEmpty(String str) {

		if (str != null && !str.trim().isEmpty()) {
			return false;
		}

		return true;
	}

	public void sendEmaill(String title, String message) {
		try {

			EmailService emailService = new EmailService();

			emailService.sendMail("Redbub report :  " + title + " on " + DateUtil.getCurrentDateAndTime(), message);

			printScreen("##DONE##", Constants.ErrorLevel.INFO);

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void copyFiles(String fileName) {

		try {

			for (CsvFileModel csvFileModel : Constants.csvFileModels) {

				// Cut paste all the folders in the operational folder to the completed folder

				String operationalFloderPath = AppSystemProperties.CvsFilePath + "/" + csvFileModel.csvFilePath;

				Path completedFolder = Paths.get(operationalFloderPath + "/done");

				Path operationalFolder = Paths.get(operationalFloderPath);

				Files.list(operationalFolder).filter(Files::isRegularFile)
						.filter(path -> path.toString().toLowerCase().endsWith(".csv"))
						.filter(path -> path.getFileName().toString().toLowerCase().contains(fileName))
						.forEach(file -> {
							try {
								Files.move(file, completedFolder.resolve(file.getFileName()),
										StandardCopyOption.REPLACE_EXISTING);

								System.out.println("Moved: " + file.getFileName());

							} catch (IOException e) {
								e.printStackTrace();
							}
						});

				// Moving the files in the sftp folders to the operational folder.

				String sftpFolderPath = AppSystemProperties.SftpFilePath + csvFileModel.csvFilePath + "/files/";

				Path sftpFolder = Paths.get(sftpFolderPath);

				Files.list(sftpFolder).filter(Files::isRegularFile)
						.filter(path -> path.toString().toLowerCase().endsWith(".csv"))
						.filter(path -> path.getFileName().toString().toLowerCase().contains(fileName)).
						forEach(file -> {
							try {

								Files.move(file, operationalFolder.resolve(file.getFileName()),
										StandardCopyOption.REPLACE_EXISTING);

								System.out.println("Moved: " + file.getFileName());

							} catch (IOException e) {
								e.printStackTrace();
							}
						});

			}

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

			LOG.debug("Debug level log message" + DateUtil.getTodayDate());
			LOG.error("Error level log message" + DateUtil.getTodayDate());

		}

	}

	//
	public static File getLastModified(String directoryFilePath) {

		File directory = new File(directoryFilePath);

		File[] files = directory.listFiles(File::isFile);

		long lastModifiedTime = Long.MIN_VALUE;

		File chosenFile = null;

		if (files != null) {

			for (File file : files) {

				if (file.lastModified() > lastModifiedTime && file.getName().contains(".csv")) {

					chosenFile = file;

					lastModifiedTime = file.lastModified();
				}
			}
		}

		return chosenFile;
	}

	public Metadata getMetadata(Metadata metadata) {

		if (metadata == null) {
			metadata = new Metadata();
			metadata.createdDate = DateUtil.getTodayDate();
			metadata.createdByUserId = "7aa3bff5-615b-4fa2-9061-9cc78f234708";
		}

		metadata.updatedDate = DateUtil.getTodayDate();
		metadata.updatedByUserId = "7aa3bff5-615b-4fa2-9061-9cc78f234708";

		return metadata;
	}

}
