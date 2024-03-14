package com.okstatelibrary.redbud.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import com.okstatelibrary.redbud.operations.CirculationLogProcess;
import com.okstatelibrary.redbud.operations.UserIntegrationProcess;
import com.okstatelibrary.redbud.service.CirculationLogService;
import com.okstatelibrary.redbud.service.GroupService;
import com.okstatelibrary.redbud.util.CacheMap;
import com.okstatelibrary.redbud.util.Constants;
import com.okstatelibrary.redbud.util.DateUtil;

@Component
public class DailyJobScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(DailyJobScheduler.class);

	@Autowired
	private GroupService groupService;

	@Autowired
	private CirculationLogService circulationLogService;

	// Define the cron expression for 1:30 AM every day
	@Scheduled(cron = "0 30 1 * * *")
	public void runUserIntegrationJob() {

		try {

			Thread myThread = new Thread(new Runnable() {

				public void run() {

					CacheMap.set("ProcessRunning", "true");

					UserIntegrationProcess oprocess = new UserIntegrationProcess();

					oprocess.printScreen("Beeper starts for user integration process " + DateUtil.getTodayDateAndTime(),
							Constants.ErrorLevel.INFO);

					oprocess.copyFiles();

					oprocess.manipulate(groupService);

					CacheMap.set("ProcessRunning", "stop");
				}
			});

			myThread.start();

		} catch (Exception e1) {
			LOG.error(e1.getMessage());
		}

	}

	@Scheduled(cron = "0 00 3 * * *")
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
						oprocess.manipulate();
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
