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

/**
 * Component responsible for running scheduled background jobs daily/ monthly.
 * <p>
 * This scheduler uses a single-threaded {@link ExecutorService} to queue and
 * execute multiple asynchronous jobs sequentially. The execution is triggered
 * based on a cron schedule defined in {@code @Scheduled}.
 * </p>
 *
 * <p>
 * Jobs include:
 * <ul>
 * <li>Institutional Resource Counting</li>
 * <li>User Property Change Updates</li>
 * <li>Circulation Data Logging</li>
 * <li>User Integration (optional, currently not scheduled)</li>
 * <li>OCLC Job (optional, commented out)</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Execution is controlled via the
 * {@code AppSystemProperties.ScheduleCornJobsRunStatus} flag.
 * </p>
 * 
 * @author [Your Name]
 */
@Component
public class DailyJobScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(DailyJobScheduler.class);

	/** A single-threaded executor to manage job execution in sequence */
	private final ExecutorService queue = Executors.newSingleThreadExecutor();

	@Autowired
	private LocationService locationService;

	@Autowired
	private InstitutionRecordService institutionalHoldingsService;

	@Autowired
	private CirculationLogService circulationLogService;

	private GroupService groupService;

	/**
	 * Scheduled method that runs daily at 1:00 AM.
	 * <p>
	 * It conditionally triggers multiple job executions by submitting them to the
	 * executor queue.
	 * </p>
	 */
	@Scheduled(cron = "0 00 1 * * ?") // Runs daily at 1:00 AM
	public void executeTasksInQueue() {
		if (AppSystemProperties.ScheduleCornJobsRunStatus) {
			queue.execute(this::runInstitutionalResourcesCounting);
			queue.execute(this::runUserPropertyChangeJob);
			queue.execute(this::runCirculationJob);
			// queue.execute(this::runOCLCJob); // Optional, currently disabled
		}
	}

	/**
	 * Runs the job that counts institutional resources and updates data
	 * accordingly. Uses {@link InstitutionRecordCountProcess} to perform business
	 * logic.
	 */
	private void runInstitutionalResourcesCounting() {
		try {
			Thread myThread = new Thread(() -> {
				CacheMap.set("UserIntegrationProcess", "true");

				InstitutionRecordCountProcess oprocess = new InstitutionRecordCountProcess();
				oprocess.printScreen(
						"Beeper starts for institutional resource count job " + DateUtil.getTodayDateAndTime(),
						Constants.ErrorLevel.INFO);

				try {
					oprocess.manipulate(locationService, institutionalHoldingsService);
				} catch (IOException e) {
					e.printStackTrace();
				}

				CacheMap.set("UserIntegrationProcess", "stop");
			});
			myThread.start();
		} catch (Exception e1) {
			LOG.error(e1.getMessage());
		}
	}

	/**
	 * Executes a job that integrates external user data into the system. Uses
	 * {@link UserIntegrationProcess} for processing.
	 */
	private void runUserIntegrationJob() {
		try {
			Thread myThread = new Thread(() -> {
				CacheMap.set("UserIntegrationProcess", "true");

				UserIntegrationProcess oprocess = new UserIntegrationProcess();
				oprocess.printScreen("Beeper starts for user integration process " + DateUtil.getTodayDateAndTime(),
						Constants.ErrorLevel.INFO);

				oprocess.copyFiles();
				oprocess.manipulate(groupService);

				CacheMap.set("UserIntegrationProcess", "stop");
			});
			myThread.start();
		} catch (Exception e1) {
			LOG.error(e1.getMessage());
		}
	}

	/**
	 * Executes a job that updates user properties. Uses
	 * {@link UserProertiesUpdateProcess} for processing.
	 */
	private void runUserPropertyChangeJob() {
		try {
			Thread myThread = new Thread(() -> {
				CacheMap.set("UserPropertiesUpdateProcess", "true");

				UserProertiesUpdateProcess oprocess = new UserProertiesUpdateProcess();
				oprocess.printScreen("Beeper starts for user property update job " + DateUtil.getTodayDateAndTime(),
						Constants.ErrorLevel.INFO);

				oprocess.copyFiles();

				try {
					oprocess.manipulate(groupService);
				} catch (RestClientException | IOException e) {
					e.printStackTrace();
				}

				CacheMap.set("UserPropertiesUpdateProcess", "stop");
			});
			myThread.start();
		} catch (Exception e1) {
			LOG.error(e1.getMessage());
		}
	}

	/**
	 * Executes a job that logs circulation data for reporting or integration. Uses
	 * {@link CirculationLogProcess} to process logs.
	 */
	private void runCirculationJob() {
		try {
			Thread myThread = new Thread(() -> {
				CacheMap.set("Running-CirculationJob", "true");

				CirculationLogProcess oprocess = new CirculationLogProcess(circulationLogService);
				oprocess.printScreen("Beeper starts for Circulation data extraction " + DateUtil.getTodayDateAndTime(),
						Constants.ErrorLevel.INFO);

				try {
					oprocess.manipulate(locationService, true, "0");
				} catch (RestClientException | IOException e) {
					e.printStackTrace();
				}

				CacheMap.set("Running-CirculationJob", "stop");
			});
			myThread.start();
		} catch (Exception e1) {
			LOG.error(e1.getMessage());
		}
	}

	/**
	 * Executes a placeholder job for OCLC integration. Currently not invoked.
	 */
	private void runOCLCJob() {
		try {
			Thread myThread = new Thread(() -> {
				CacheMap.set("Running-CirculationJob", "true");

				CirculationLogProcess oprocess = new CirculationLogProcess(circulationLogService);
				oprocess.printScreen("Beeper starts for Circulation data extraction " + DateUtil.getTodayDateAndTime(),
						Constants.ErrorLevel.INFO);

				try {
					oprocess.manipulate(locationService, true, "0");
				} catch (RestClientException | IOException e) {
					e.printStackTrace();
				}

				CacheMap.set("Running-CirculationJob", "stop");
			});
			myThread.start();
		} catch (Exception e1) {
			LOG.error(e1.getMessage());
		}
	}
}
