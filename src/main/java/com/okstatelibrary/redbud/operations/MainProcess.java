package com.okstatelibrary.redbud.operations;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.okstatelibrary.redbud.entity.CsvFileModel;
import com.okstatelibrary.redbud.folio.entity.Metadata;
import com.okstatelibrary.redbud.service.external.EmailService;
import com.okstatelibrary.redbud.service.external.FolioService;
import com.okstatelibrary.redbud.util.AppSystemProperties;
import com.okstatelibrary.redbud.util.Constants;
import com.okstatelibrary.redbud.util.DateUtil;

public class MainProcess {

	protected FolioService folioService;

	public MainProcess() {

		folioService = new FolioService();
	}

	/**
	 * Logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(MainProcess.class);

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

		// System.out.println(msg);
	}

	public boolean isStringNullOrEmpty(String str) {

		if (str != null && !str.trim().isEmpty()) {
			return false;
		}

		return true;
	}

	public void sendEmaill(String title, String message) {
		try
		{

			EmailService emailService = new EmailService();

			emailService.sendMail("Redbub report :  " + title + " on " + DateUtil.getCurrentDateAndTime(), message);

			printScreen("##DONE##", Constants.ErrorLevel.INFO);

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void copyFiles() {

		try {
			for (CsvFileModel csvFileModel : Constants.csvFileModels) {

				File file = getLastModified(AppSystemProperties.SftpFilePath + csvFileModel.csvFilePath + "/files/");

				if (file != null) {

					printScreen(file.getName(), Constants.ErrorLevel.INFO);

					// Already Manipulated Files are moving to done folder.

					File alreadyDoneFilesPath = new File(AppSystemProperties.CvsFilePath + csvFileModel.csvFilePath);

					File[] listOfAlreadyDoneFiles = alreadyDoneFilesPath.listFiles();

					for (File alreadyDoneFile : listOfAlreadyDoneFiles) {

						if (alreadyDoneFile.getName().contains(".csv")) {

							printScreen("Already Done Fies " + alreadyDoneFile.getName() + " -- "
									+ alreadyDoneFile.getAbsolutePath(), Constants.ErrorLevel.INFO);

							File doneFilesDestination = new File(AppSystemProperties.CvsFilePath + "/"
									+ csvFileModel.csvFilePath + "/done/" + alreadyDoneFile.getName());

							alreadyDoneFile.renameTo(doneFilesDestination);

							printScreen("File Added to " + doneFilesDestination.getAbsolutePath(),
									Constants.ErrorLevel.INFO);
						}
					}

					// Cut and Paste the latest file to the csv Folder from the sftp folder.

					File destination = new File(AppSystemProperties.CvsFilePath + "/" + csvFileModel.csvFilePath + "/"
							+ csvFileModel.csvFilePath + "_" + DateUtil.getTodayDate() + ".csv");

					if (!destination.exists()) {

						file.renameTo(destination);
					}

				}

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
