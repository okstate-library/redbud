package com.okstatelibrary.redbud.controller;

import com.okstatelibrary.redbud.config.CronJobLister;
import com.okstatelibrary.redbud.entity.CsvFileModel;
import com.okstatelibrary.redbud.entity.FileModel;
import com.okstatelibrary.redbud.entity.User;
import com.okstatelibrary.redbud.operations.*;
import com.okstatelibrary.redbud.service.*;
import com.okstatelibrary.redbud.util.AppSystemProperties;
import com.okstatelibrary.redbud.util.CacheMap;
import com.okstatelibrary.redbud.util.Constants;
import com.okstatelibrary.redbud.util.DateUtil;

import org.apache.http.client.ClientProtocolException;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/settings")
public class SettingsController {

	private static final Logger LOG = LoggerFactory.getLogger(SettingsController.class);

	@Autowired
	CronJobLister lister;

	@Autowired
	private UserService userService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private InstitutionService institutionService;

	@Autowired
	private CampusService campusService;

	@Autowired
	private LibraryService libraryService;

	@Autowired
	private ServicePointService servicePointService;

	@Autowired
	private LocationService locationService;

	@Autowired
	private CirculationLogService circulationLogService;

	@GetMapping("/enableUserIntegration")
	public String enableUserIntegrationCornJob(Principal principal, Model model) throws IOException {
		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		return "operations";
	}

	@GetMapping("/disableUserIntegration")
	public String disableUserIntegrationCornJob(Principal principal, Model model) throws IOException {
		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		return "operations";
	}

	@GetMapping("/groups")
	public String getPatronGroups(Principal principal, Model model) throws IOException {
		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		model.addAttribute("groups", groupService.getGroupList());

		return "groups";
	}

	@GetMapping("/setUpInfra")
	public String setUpInfra(Principal principal, Model model) throws IOException {
		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		InfrastructureSetupProcess infra = new InfrastructureSetupProcess();

		infra.manipulate(institutionService, campusService, libraryService, locationService, servicePointService);

		return "infrastructure";
	}

	@GetMapping("/infrastructure")
	public String getInfrastructure(Principal principal, Model model) throws IOException {
		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		InfrastructureSetupProcess infra = new InfrastructureSetupProcess();

		model.addAttribute("locations",
				infra.getLocations(institutionService, campusService, libraryService, locationService));

		return "infrastructure";
	}

	@GetMapping("/operations")
	public String operations(Principal principal, Model model) throws IOException {
		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		model.addAttribute(CacheMap.process_Execute_Inactive_Users,
				CacheMap.get(CacheMap.process_Execute_Inactive_Users));

		model.addAttribute(CacheMap.process_Send_Test_Email, CacheMap.get(CacheMap.process_Send_Test_Email));

		lister.listCronJobs();

		model.addAttribute("corn_scheduled_jobs", lister.singletonList.getList());

		return "operations";
	}

	@GetMapping("files")
	public String getFiles(Principal principal, Model model) throws IOException {

		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		List<FileModel> files = new ArrayList<>();

		for (CsvFileModel csvFileModel : Constants.csvFileModels) {

			File folder = new File(AppSystemProperties.CvsFilePath + csvFileModel.csvFilePath);

			File[] listOfFiles = folder.listFiles();

			for (File file : listOfFiles) {

				if (file.getName().contains(".csv")) {

					System.out.println(file.getName());

					FileModel newFile = new FileModel();

					newFile.setPath(file.getAbsolutePath());

					Date yourDate = new Date(file.lastModified());

					DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh-MM-ss");

					newFile.setFileName(file.getName());
					newFile.setLastModifiedDate(formatter.format(yourDate));

					newFile.setPath(newFile.getPath());

					files.add(newFile);

				}

			}

		}

		model.addAttribute("csvfiles", files);

		// model.addAttribute("campuses", Constants.csvFileModels.get(0));

		return "files";
	}

	@GetMapping("/UserIntegration")
	public String executeUserIntegration(Principal principal, Model model) {
		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		try {

			Thread myThread = new Thread(new Runnable() {

				public void run() {

					CacheMap.set("ProcessRunning", CacheMap.running);

					UserIntegrationProcess oprocess = new UserIntegrationProcess();

					oprocess.printScreen(
							"Beeper starts for user integration process manually " + DateUtil.getTodayDateAndTime(),
							Constants.ErrorLevel.INFO);

					oprocess.copyFiles();

					oprocess.manipulate(groupService);

					CacheMap.set("ProcessRunning", CacheMap.idle);
				}
			});

			myThread.start();

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

			LOG.error(e1.getMessage());
		}

		return "redirect:/settings/files";
	}

	@GetMapping("/executeinactiveusers")
	public String executeInactiveUsers(Principal principal, Model model) {
		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		try {

			Thread myThread = new Thread(new Runnable() {

				public void run() {

					CacheMap.set(CacheMap.process_Execute_Inactive_Users, CacheMap.running);

					InactiveUserProcess oprocess = new InactiveUserProcess();

					oprocess.printScreen("Beeper starts for inactive user process" + DateUtil.getTodayDateAndTime(),
							Constants.ErrorLevel.INFO);

					oprocess.copyFiles();

					oprocess.manipulate(groupService);

					CacheMap.set(CacheMap.process_Execute_Inactive_Users, CacheMap.idle);
				}
			});

			myThread.start();

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

			LOG.error(e1.getMessage());
		}

		return "redirect:/settings/files";
	}

	@GetMapping("/convertinactiveusers")
	public String convertInactiveUsers(Principal principal, Model model) {
		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		try {

			Thread myThread = new Thread(new Runnable() {

				public void run() {

					InactiveUserProcess oprocess = new InactiveUserProcess();

					oprocess.printScreen("Beeper starts for convert inactive users " + DateUtil.getTodayDateAndTime(),
							Constants.ErrorLevel.INFO);

					oprocess.manipulate(groupService);

					CacheMap.set("ProcessRunning", "stop");
				}
			});

			myThread.start();

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

			LOG.error(e1.getMessage());
		}

		return "redirect:/settings/files";
	}

	@GetMapping("/changepatrongroup")
	public String changepatrongroup(Principal principal, Model model) throws IOException {
		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		ChangePatronGroupProcess oprocess = new ChangePatronGroupProcess();

		oprocess.changeUserPatronGroup();

		return "operations";
	}

	@GetMapping("/changeLoanDueDatesForPatronGroup")
	public String changeLoanDueDatesForPatronGroup(Principal principal, Model model) throws IOException {

		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		try {

			Thread myThread = new Thread(new Runnable() {

				public void run() {

					CacheMap.set("Change_LoanDueDates_ForPatronGroup", CacheMap.running);

					LoanDueDateUpdateProcess oprocess = new LoanDueDateUpdateProcess();

					try {

						// c88e6e42-9544-4e5e-ae94-a50c07b9dfbf OKS-OSU-admin
						// 02609d66-4b2a-47f6-988a-cf7b5b2932c7 - OKS-OSU-faculty
						// e8f2c425-2daa-44fd-a813-7bfd2329e8cc - OKS-OSU-faculty -ret

						oprocess.manipulate("e8f2c425-2daa-44fd-a813-7bfd2329e8cc");

					} catch (RestClientException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					CacheMap.set("Change_LoanDueDates_ForPatronGroup", CacheMap.idle);
				}
			});

			myThread.start();

		} catch (Exception e1) {
			LOG.error(e1.getMessage());
		}

		return "operations";
	}

	@GetMapping("/circulationDataStore")
	public String circulationDataStore(Principal principal, Model model) throws IOException {

		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		try {

			Thread myThread = new Thread(new Runnable() {

				public void run() {

					CacheMap.set("Process_CirculationLog_API_Data_Extraction", CacheMap.running);

					CirculationLogProcess oprocess = new CirculationLogProcess(circulationLogService);

					oprocess.printScreen(
							"Beeper starts for initiall Circulation data extraction. " + DateUtil.getTodayDateAndTime(),
							Constants.ErrorLevel.INFO);

					try {

						oprocess.manipulate();

					} catch (RestClientException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					oprocess.printScreen(
							"Beeper ends for initiall Circulation data extraction. " + DateUtil.getTodayDateAndTime(),
							Constants.ErrorLevel.INFO);

					CacheMap.set("Process_CirculationLog_API_Data_Extraction", CacheMap.idle);
				}
			});

			myThread.start();

		} catch (Exception e1) {
			LOG.error(e1.getMessage());
		}

		return "operations";
	}

	@GetMapping("/sendTestEmail")
	public String sendTestEmail(Principal principal, Model model) throws IOException {

		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		try {

			Thread myThread = new Thread(new Runnable() {

				public void run() {

					CacheMap.set(CacheMap.process_Send_Test_Email, CacheMap.running);

					MainProcess oprocess = new MainProcess();

					oprocess.printScreen("Beeper send email " + DateUtil.getTodayDateAndTime(),
							Constants.ErrorLevel.INFO);

					try {

						oprocess.sendEmaill("Test Title", "Test Message");

					} catch (RestClientException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					oprocess.printScreen("Beeper ends for send email " + DateUtil.getTodayDateAndTime(),
							Constants.ErrorLevel.INFO);

					CacheMap.set(CacheMap.process_Send_Test_Email, CacheMap.idle);
				}
			});

			myThread.start();

		} catch (Exception e1) {
			LOG.error(e1.getMessage());
		}

		return "operations";
	}

	@GetMapping("/oclcMetaDataProcess")
	public String oclcMetaDataProcess(Principal principal, Model model) throws IOException {

		System.out.println("oclcMetaDataProcess ");

		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		try {

			Thread myThread = new Thread(new Runnable() {

				public void run() {

					CacheMap.set(CacheMap.process_OCLC_NUmbers_Process, CacheMap.running);

					try {
						OCLCMetadataProcess oprocess = new OCLCMetadataProcess();

						oprocess.getOCLCItems("");

					} catch (OAuthSystemException | OAuthProblemException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					CacheMap.set(CacheMap.process_OCLC_NUmbers_Process, CacheMap.idle);
				}
			});

			System.out.println("oclcMetaDataProcess -finished");

			myThread.start();

		} catch (Exception e1) {
			LOG.error(e1.getMessage());
		}

		return "operations";
	}

	@GetMapping("/userFieldsUpdateProcess")
	public String userFieldsUpdateProcess(Principal principal, Model model) throws IOException {

		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		try {

			Thread myThread = new Thread(new Runnable() {

				public void run() {

					CacheMap.set(CacheMap.process_Send_Test_Email, CacheMap.running);

					UserFieldsUpdateProcess oprocess = new UserFieldsUpdateProcess();

					oprocess.manipulate(groupService);

					CacheMap.set(CacheMap.process_Send_Test_Email, CacheMap.idle);
				}
			});

			myThread.start();

		} catch (Exception e1) {
			LOG.error(e1.getMessage());
		}

		return "operations";
	}

	/**
	 * Update the user's new bar codes depending on their's old bar code values.
	 * 
	 * @param principal
	 * @param model
	 * @return to the same page that operates.
	 * @throws IOException
	 */

	@GetMapping("/userBarcodesUpdateProcess")
	public String userBarcodesUpdateProcess(Principal principal, Model model) throws IOException {

		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		try {

			Thread myThread = new Thread(new Runnable() {

				public void run() {

					CacheMap.set(CacheMap.process_Send_Test_Email, CacheMap.running);

					UserFieldsUpdateProcess oprocess = new UserFieldsUpdateProcess();

					oprocess.manipulateBarcode();

					CacheMap.set(CacheMap.process_Send_Test_Email, CacheMap.idle);
				}
			});

			myThread.start();

		} catch (Exception e1) {
			LOG.error(e1.getMessage());
		}

		return "operations";
	}

	@GetMapping("/userPatronGroupUpdateProcess")
	public String userPatronGroupUpdateProcess(Principal principal, Model model) {

		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		try {

			Thread myThread = new Thread(new Runnable() {

				public void run() {

					CacheMap.set("ProcessRunning", CacheMap.running);

					UserProertiesUpdateProcess oprocess = new UserProertiesUpdateProcess();

					try {
						oprocess.manipulate(groupService);
					} catch (RestClientException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					CacheMap.set("ProcessRunning", CacheMap.idle);
				}
			});

			myThread.start();

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

			LOG.error(e1.getMessage());
		}

		return "operations";
	}

	@GetMapping("/userPatronGroupReportProcess")
	public String userPatronGroupReportProcess(Principal principal, Model model) {

		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		try {

			Thread myThread = new Thread(new Runnable() {

				public void run() {

					CacheMap.set("ProcessRunning", CacheMap.running);

					UserPatronGroupReportProcess oprocess = new UserPatronGroupReportProcess();

					try {
						oprocess.manipulate(groupService);
					} catch (RestClientException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					CacheMap.set("ProcessRunning", CacheMap.idle);
				}
			});

			myThread.start();

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

			LOG.error(e1.getMessage());
		}

		return "operations";
	}

	@GetMapping("/nullUserPropertyCheckProcess")
	public String nullUserPropertyCheckProcess(Principal principal, Model model) {

		System.out.println("null chekc user ");

		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		try {

			Thread myThread = new Thread(new Runnable() {

				public void run() {

					CacheMap.set("ProcessRunning", CacheMap.running);

					NullUserPropertyCheckProcess oprocess = new NullUserPropertyCheckProcess();

					oprocess.manipulate(groupService);

					CacheMap.set("ProcessRunning", CacheMap.idle);
				}
			});

			myThread.start();

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

			LOG.error(e1.getMessage());
		}

		return "operations";
	}

	@GetMapping("/govDocsLocationUpdateProcess")
	public String govDocsLocationUpdateProcess(Principal principal, Model model) {

		System.out.println("null chekc user ");

		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		try {

			Thread myThread = new Thread(new Runnable() {

				public void run() {

					CacheMap.set("ProcessRunning", CacheMap.running);

					GovDocsLocationUpdateProcess oprocess = new GovDocsLocationUpdateProcess();

					try {
						oprocess.manipulate(groupService);
					} catch (RestClientException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					CacheMap.set("ProcessRunning", CacheMap.idle);
				}
			});

			myThread.start();

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

			LOG.error(e1.getMessage());
		}

		return "operations";
	}

	@GetMapping("/almaLoanCountUpdateProcess")
	public String almaLoanCountUpdateProcess(Principal principal, Model model) {

		System.out.println("Running the ALMA loan count process");

		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		try {

			Thread myThread = new Thread(new Runnable() {

				public void run() {

					CacheMap.set(CacheMap.process_Alma_Loan_Count, CacheMap.running);

					AlmaLoanCountUpdateProcess oprocess = new AlmaLoanCountUpdateProcess();

					try {
						oprocess.manipulate(circulationLogService);
					} catch (RestClientException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					CacheMap.set(CacheMap.process_Alma_Loan_Count, CacheMap.idle);
				}
			});

			myThread.start();

			model.addAttribute(CacheMap.process_Alma_Loan_Count, CacheMap.get(CacheMap.process_Alma_Loan_Count));

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

			LOG.error(e1.getMessage());
		}

		return "operations";
	}
}