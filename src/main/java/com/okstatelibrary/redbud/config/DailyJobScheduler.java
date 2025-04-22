package com.okstatelibrary.redbud.config;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import com.okstatelibrary.redbud.operations.CirculationLogProcess;
import com.okstatelibrary.redbud.operations.InstitutionRecordCountProcess;
import com.okstatelibrary.redbud.operations.UserIntegrationProcess;
import com.okstatelibrary.redbud.operations.UserProertiesUpdateProcess;
import com.okstatelibrary.redbud.service.CirculationLogService;
import com.okstatelibrary.redbud.service.GroupService;
import com.okstatelibrary.redbud.service.InstitutionRecordService;
import com.okstatelibrary.redbud.service.LocationService;
import com.okstatelibrary.redbud.util.AppSystemProperties;
import com.okstatelibrary.redbud.util.CacheMap;
import com.okstatelibrary.redbud.util.Constants;
import com.okstatelibrary.redbud.util.DateUtil;

@Component
public class DailyJobScheduler {

	private final ExecutorService queue = Executors.newSingleThreadExecutor();

	@Scheduled(cron = "0 00 1 * * ?") // Runs at the start of every hour
	public void executeTasksInQueue() {

		if (AppSystemProperties.ScheduleCornJobsRunStatus) {
			queue.execute(this::runInstitutionalResourcesCounting);
			queue.execute(this::runUserPropertyChangeJob);
			queue.execute(this::runCirculationJob);
			// queue.execute(this::runOCLCJob);
		}

	}

	private static final Logger LOG = LoggerFactory.getLogger(DailyJobScheduler.class);

	private GroupService groupService;

	@Autowired
	private LocationService locationService;

	@Autowired
	private InstitutionRecordService institutionalHoldingsService;

	@Autowired
	private CirculationLogService circulationLogService;

	public void runInstitutionalResourcesCounting() {

		try {

			Thread myThread = new Thread(new Runnable() {

				public void run() {

					CacheMap.set("UserIntegrationProcess", "true");

					InstitutionRecordCountProcess oprocess = new InstitutionRecordCountProcess();

					oprocess.printScreen("Beeper starts for user integration process " + DateUtil.getTodayDateAndTime(),
							Constants.ErrorLevel.INFO);

					try {
						oprocess.manipulate(locationService, institutionalHoldingsService);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					CacheMap.set("UserIntegrationProcess", "stop");
				}
			});

			myThread.start();

		} catch (Exception e1) {
			LOG.error(e1.getMessage());
		}

	}

	public void runUserIntegrationJob() {

		try {

			Thread myThread = new Thread(new Runnable() {

				public void run() {

					CacheMap.set("UserIntegrationProcess", "true");

					UserIntegrationProcess oprocess = new UserIntegrationProcess();

					oprocess.printScreen("Beeper starts for user integration process " + DateUtil.getTodayDateAndTime(),
							Constants.ErrorLevel.INFO);

					oprocess.copyFiles();

					oprocess.manipulate(groupService);

					CacheMap.set("UserIntegrationProcess", "stop");
				}
			});

			myThread.start();

		} catch (Exception e1) {
			LOG.error(e1.getMessage());
		}

	}

	public void runUserPropertyChangeJob() {

		try {

			Thread myThread = new Thread(new Runnable() {

				public void run() {

					CacheMap.set("UserPropertiesUpdateProcess", "true");

					UserProertiesUpdateProcess oprocess = new UserProertiesUpdateProcess();

					oprocess.printScreen("Beeper starts for user integration process " + DateUtil.getTodayDateAndTime(),
							Constants.ErrorLevel.INFO);

					oprocess.copyFiles();

					try {
						oprocess.manipulate(groupService);
					} catch (RestClientException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					CacheMap.set("UserPropertiesUpdateProcess", "stop");
				}
			});

			myThread.start();

		} catch (Exception e1) {
			LOG.error(e1.getMessage());
		}

	}

	public void runCirculationJob() {

		try {

			Thread myThread = new Thread(new Runnable() {

				public void run() {

					CacheMap.set("Running-CirculationJob", "true");

					CirculationLogProcess oprocess = new CirculationLogProcess(circulationLogService);

					oprocess.printScreen(
							"Beeper starts for Circulation data extraction " + DateUtil.getTodayDateAndTime(),
							Constants.ErrorLevel.INFO);

					try {
						oprocess.manipulate(locationService, true, "0");
					} catch (RestClientException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					CacheMap.set("Running-CirculationJob", "stop");
				}
			});

			myThread.start();

		} catch (Exception e1) {
			LOG.error(e1.getMessage());
		}

	}

	public void runOCLCJob() {

		try {

			Thread myThread = new Thread(new Runnable() {

				public void run() {

					CacheMap.set("Running-CirculationJob", "true");

					CirculationLogProcess oprocess = new CirculationLogProcess(circulationLogService);

					oprocess.printScreen(
							"Beeper starts for Circulation data extraction " + DateUtil.getTodayDateAndTime(),
							Constants.ErrorLevel.INFO);

					try {
						oprocess.manipulate(locationService, true, "0");
					} catch (RestClientException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					CacheMap.set("Running-CirculationJob", "stop");
				}
			});

			myThread.start();

		} catch (Exception e1) {
			LOG.error(e1.getMessage());
		}
	}

}
