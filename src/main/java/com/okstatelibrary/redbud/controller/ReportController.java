package com.okstatelibrary.redbud.controller;

import com.okstatelibrary.redbud.entity.CirculationLoan;
import com.okstatelibrary.redbud.entity.CirculationLog;
import com.okstatelibrary.redbud.entity.Institution;
import com.okstatelibrary.redbud.entity.InstitutionRecord;
import com.okstatelibrary.redbud.entity.Location;
import com.okstatelibrary.redbud.entity.PatronGroup;
import com.okstatelibrary.redbud.entity.User;
import com.okstatelibrary.redbud.enums.LoanAction;
import com.okstatelibrary.redbud.folio.entity.Account;
import com.okstatelibrary.redbud.folio.entity.FineAndFeesObject;
import com.okstatelibrary.redbud.folio.entity.FolioUser;
import com.okstatelibrary.redbud.folio.entity.PatronBlockObject;
import com.okstatelibrary.redbud.folio.entity.PatronBlockRoot;
import com.okstatelibrary.redbud.folio.entity.inventory.*;
import com.okstatelibrary.redbud.folio.entity.manualblock.ManualBlock;
import com.okstatelibrary.redbud.model.CirculationLoanReportModel;
import com.okstatelibrary.redbud.service.CampusService;
import com.okstatelibrary.redbud.service.CirculationLoanService;
import com.okstatelibrary.redbud.service.CirculationLogService;
import com.okstatelibrary.redbud.service.GroupService;
import com.okstatelibrary.redbud.service.InstitutionService;
import com.okstatelibrary.redbud.service.InstitutionRecordService;
import com.okstatelibrary.redbud.service.LibraryService;
import com.okstatelibrary.redbud.service.LocationService;
import com.okstatelibrary.redbud.service.ServicePointService;
import com.okstatelibrary.redbud.service.UserService;
import com.okstatelibrary.redbud.service.external.FolioService;
import com.okstatelibrary.redbud.util.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;

import java.util.stream.Collectors;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/reports")
public class ReportController {

	@Autowired
	private UserService userService;

	@Autowired
	protected FolioService folioService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private CirculationLogService circulationLogService;

	@Autowired
	private CirculationLoanService circulationLoanService;

	@Autowired
	private InstitutionService institutionService;

	@Autowired
	private CampusService campusService;

	@Autowired
	private LibraryService libraryService;

	@Autowired
	private LocationService locationService;

	@Autowired
	private ServicePointService servicePointService;

	@Autowired
	private InstitutionRecordService institutionRecordService;

	private String notApplicable = "N/A";

	@GetMapping("/overduefines")
	private String getOverdueFines(Principal principal, Model model) throws IOException {

		User user = userService.findByUsername(principal.getName());

		model.addAttribute("institutionList", institutionService.getInstitutionList());

		model.addAttribute("user", user);

		return "reports/overduefines";
	}

	@GetMapping("/overdueitems")
	private String getOverdueItems(Principal principal, Model model) throws IOException {

		User user = userService.findByUsername(principal.getName());

		model.addAttribute("institutionList", institutionService.getInstitutionList());

		model.addAttribute("user", user);

		return "reports/overdueitems";
	}

	@GetMapping("/patronblocks")
	private String getPatronBlocks(Principal principal, Model model) throws IOException {

		User user = userService.findByUsername(principal.getName());

		model.addAttribute("institutionList", institutionService.getInstitutionList());

		model.addAttribute("user", user);

		return "reports/patronblocks";
	}

	@GetMapping("/inventoryloans")
	public String getElcsInventory(Principal principal, Model model) throws IOException {

		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		// model.addAttribute("locationList", locationService.getLocationList());

		return "reports/inventoryloans";
	}

	@GetMapping("/circulationlog")
	public String getCirculationLog(Principal principal, Model model) throws IOException {

		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

//		CirculationLogProcess process = new CirculationLogProcess();
//		
//		process.maanipulate(circulationLogService);

		model.addAttribute("institutionList", institutionService.getInstitutionList());

		model.addAttribute("campusList", campusService.getCampusList());

		model.addAttribute("libraryList", libraryService.getLibraryList());

		model.addAttribute("locationList", locationService.getLocationList());

		model.addAttribute("materialTypeList", circulationLogService.getDistinctMaterialTypes());

		return "reports/circulationlog";

	}

	@GetMapping("/circulationloan")
	public String getCirculationLoan(Principal principal, Model model) throws IOException {

		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

//		CirculationLogProcess process = new CirculationLogProcess();
//		
//		process.maanipulate(circulationLogService);

		model.addAttribute("institutionList", institutionService.getInstitutionList());

		model.addAttribute("campusList", campusService.getCampusList());

		model.addAttribute("libraryList", libraryService.getLibraryList());

		model.addAttribute("locationList", locationService.getLocationList());

		model.addAttribute("materialTypeList", circulationLogService.getDistinctMaterialTypes());

		return "reports/circulationloan";

	}

	@GetMapping("/overdueopenloans")
	public String getOverdueOpenLoans(Principal principal, Model model) throws IOException {

		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		model.addAttribute("servicePointList", servicePointService.getServicePointList());

		return "reports/overdueopenloans";
	}

	@RequestMapping(value = "/overdueOpenLoans/data", method = RequestMethod.GET)
	private @ResponseBody List<FineAndFeesObject> overdueOpenLoansData(
			@RequestParam(required = false) String servicepointid) throws RestClientException, IOException {

		System.out.print("dsadsadsadsad");

		List<PatronGroup> groupList = groupService.getGroupList();

		ArrayList<com.okstatelibrary.redbud.folio.entity.loan.Loan> loans = folioService.getClosedLoans(false,
				servicepointid);

		List<FineAndFeesObject> returnObjects = null;

		if (loans != null && loans.size() > 0) {

			List<FolioUser> filteredUserList = new ArrayList<FolioUser>();

			List<PatronBlockRoot> filteredPatronBlockRootList = new ArrayList<PatronBlockRoot>();

			returnObjects = new ArrayList<FineAndFeesObject>();

			for (com.okstatelibrary.redbud.folio.entity.loan.Loan loan : loans) {

				// System.out.println("account.userId" + account.userId);

				FineAndFeesObject fineNFees = new FineAndFeesObject();

				Item item = folioService.getItem(loan.itemId);

				fineNFees.location = "N/A";

				Optional<Location> location = locationService.getLocationList().stream()
						.filter(loc -> loc.getLocation_id().equals(loan.itemEffectiveLocationIdAtCheckOut)).findFirst();

				if (location.isPresent()) {
					Optional<Institution> institution = institutionService.getInstitutionList().stream()
							.filter(loc -> loc.getInstitution_id().equals(location.get().getInstitution_id()))
							.findFirst();

					fineNFees.location = institution.get().getInstitution_name();
				}

//				fineNFees.type = "account.feeFineType";
//				fineNFees.status = "account.status.name";
//				fineNFees.paymentStatus = "account.paymentStatus.name";

				fineNFees.barcode = item.barcode;
				fineNFees.title = item.title;

				fineNFees.date = DateUtil.getShortDate(loan.metadata.createdDate);

				// Get the FOLIO User details.

				FolioUser fillteredUser = filteredUserList.stream().filter(u -> u.id.equals(loan.userId)).findFirst()
						.orElse(null);

				FolioUser folioUser = new FolioUser();

				if (fillteredUser != null) {
					folioUser = fillteredUser;
				} else {
					folioUser = folioService.getUsersById(loan.userId);
					filteredUserList.add(folioUser);
				}

				fineNFees.identifier = folioUser.externalSystemId;
				fineNFees.name = folioUser.personal.firstName + " " + folioUser.personal.lastName;
				fineNFees.phone = folioUser.personal.mobilePhone;
				fineNFees.email = folioUser.personal.email;

				String groupname = folioUser.patronGroup;

				Optional<PatronGroup> group = groupList.stream()
						.filter(selGroup -> selGroup.getFolioGroupId().equals(groupname)).findAny();

				if (group != null && group.isPresent()) {
					fineNFees.group = group.get().getFolioGroupName();
				}

				PatronBlockRoot filterpatronBlockRoot = filteredPatronBlockRootList.stream()
						.filter(u -> u.userId.equals(loan.userId)).findFirst().orElse(null);

				PatronBlockRoot patronBlock = null;

				if (filterpatronBlockRoot != null) {
					patronBlock = filterpatronBlockRoot;
				} else {

					patronBlock = folioService.getAutomatedPatronBlocks(loan.userId);
					patronBlock.userId = loan.userId;

					filteredPatronBlockRootList.add(patronBlock);
				}

				if (patronBlock != null && patronBlock.automatedPatronBlocks.size() > 0) {
					fineNFees.isPatronBlock = "Yes";
				}

				returnObjects.add(fineNFees);

			}
		}

		return returnObjects;

	}

	@RequestMapping(value = "/overdueItems/data", method = RequestMethod.GET)
	private @ResponseBody List<FineAndFeesObject> overDueItemsData(@RequestParam(required = false) String feeFineOwner)
			throws RestClientException, IOException {

		// System.out.print("DAmith ");

		List<PatronGroup> groupList = groupService.getGroupList();

		ArrayList<Account> accounts = folioService.getOverDueAccounts(feeFineOwner);

		List<FineAndFeesObject> returnObjects = null;

		List<FolioUser> filteredUserList = new ArrayList<FolioUser>();

		List<PatronBlockRoot> filteredPatronBlockRootList = new ArrayList<PatronBlockRoot>();

		if (accounts != null && accounts.size() > 0) {

			returnObjects = new ArrayList<FineAndFeesObject>();

			for (Account account : accounts) {

				// System.out.println("account.userId" + account.userId);

				FineAndFeesObject fineNFees = new FineAndFeesObject();

				fineNFees.location = account.location;
				fineNFees.type = account.feeFineType;
				fineNFees.status = account.status.name;
				fineNFees.paymentStatus = account.paymentStatus.name;

				fineNFees.barcode = account.barcode;
				fineNFees.title = account.title;
				fineNFees.amount = account.amount;
				fineNFees.remaningAmount = account.remaining;

				fineNFees.date = DateUtil.getShortDate(account.metadata.createdDate);

				// Get the FOLIO User details.

				FolioUser fillteredUser = filteredUserList.stream().filter(u -> u.id.equals(account.userId)).findFirst()
						.orElse(null);

				FolioUser folioUser = new FolioUser();

				if (fillteredUser != null) {
					folioUser = fillteredUser;
				} else {
					folioUser = folioService.getUsersById(account.userId);
					filteredUserList.add(folioUser);
				}

				fineNFees.identifier = folioUser.externalSystemId;
				fineNFees.name = folioUser.personal.firstName + " " + folioUser.personal.lastName;
				fineNFees.phone = folioUser.personal.mobilePhone;
				fineNFees.email = folioUser.personal.email;

				String groupname = folioUser.patronGroup;

				Optional<PatronGroup> group = groupList.stream()
						.filter(selGroup -> selGroup.getFolioGroupId().equals(groupname)).findAny();

				if (group != null && group.isPresent()) {
					fineNFees.group = group.get().getFolioGroupName();
				}

				PatronBlockRoot filterpatronBlockRoot = filteredPatronBlockRootList.stream()
						.filter(u -> u.userId.equals(account.userId)).findFirst().orElse(null);

				PatronBlockRoot patronBlock = null;

				if (filterpatronBlockRoot != null) {
					patronBlock = filterpatronBlockRoot;
				} else {

					patronBlock = folioService.getAutomatedPatronBlocks(account.userId);
					patronBlock.userId = account.userId;

					filteredPatronBlockRootList.add(patronBlock);
				}

				if (patronBlock != null && patronBlock.automatedPatronBlocks.size() > 0) {
					fineNFees.isPatronBlock = "Yes";
				}

				returnObjects.add(fineNFees);

			}
		}

		return returnObjects;
	}

	@RequestMapping(value = "/overdueFines/data", method = RequestMethod.GET)
	private @ResponseBody List<FineAndFeesObject> overDueFinesData(@RequestParam(required = false) String feeFineOwner)
			throws RestClientException, IOException {

		List<PatronGroup> groupList = groupService.getGroupList();

		ArrayList<Account> accounts = folioService.getOverDueAccounts(feeFineOwner);

		List<FineAndFeesObject> returnObjects = null;

		List<FolioUser> filteredUserList = new ArrayList<FolioUser>();
		List<PatronBlockRoot> filteredPatronBlockRootList = new ArrayList<PatronBlockRoot>();

		if (accounts != null && accounts.size() > 0) {

			returnObjects = new ArrayList<FineAndFeesObject>();

			for (Account account : accounts) {

				FineAndFeesObject fineNFees = new FineAndFeesObject();

				fineNFees.location = account.location;
				fineNFees.type = account.feeFineType;
				fineNFees.status = account.status.name;
				fineNFees.paymentStatus = account.paymentStatus.name;

				fineNFees.barcode = account.barcode;
				fineNFees.title = account.title;
				fineNFees.amount = account.amount;
				fineNFees.remaningAmount = account.remaining;

				fineNFees.date = DateUtil.getShortDate(account.metadata.createdDate);

				// Get the FOLIO User details.

				FolioUser fillteredUser = filteredUserList.stream().filter(u -> u.id.equals(account.userId)).findFirst()
						.orElse(null);

				FolioUser folioUser = new FolioUser();

				if (fillteredUser != null) {
					folioUser = fillteredUser;
				} else {
					folioUser = folioService.getUsersById(account.userId);
					filteredUserList.add(folioUser);
				}

				fineNFees.identifier = folioUser.externalSystemId;
				fineNFees.name = folioUser.personal.firstName + " " + folioUser.personal.lastName;
				fineNFees.phone = folioUser.personal.mobilePhone;
				fineNFees.email = folioUser.personal.email;

				String groupname = folioUser.patronGroup;

				Optional<PatronGroup> group = groupList.stream()
						.filter(selGroup -> selGroup.getFolioGroupId().equals(groupname)).findAny();

				if (group != null && group.isPresent()) {
					fineNFees.group = group.get().getFolioGroupName();
				}

				PatronBlockRoot filterpatronBlockRoot = filteredPatronBlockRootList.stream()
						.filter(u -> u.userId.equals(account.userId)).findFirst().orElse(null);

				PatronBlockRoot patronBlock = null;

				if (filterpatronBlockRoot != null) {
					patronBlock = filterpatronBlockRoot;
				} else {

					patronBlock = folioService.getAutomatedPatronBlocks(account.userId);
					patronBlock.userId = account.userId;

					filteredPatronBlockRootList.add(patronBlock);
				}

				if (patronBlock != null && patronBlock.automatedPatronBlocks.size() > 0) {
					fineNFees.isPatronBlock = "Yes";
				}

				returnObjects.add(fineNFees);

			}
		}

		return returnObjects;
	}

	@RequestMapping(value = "/inventoryloans/data", method = RequestMethod.GET)
	private @ResponseBody ArrayList<Loan> inventoryLoansData(@RequestParam(required = false) String location,
			@RequestParam(required = false) String year) throws RestClientException, IOException {

		ArrayList<Loan> loans = new ArrayList<Loan>();

		if (location.equalsIgnoreCase("creative_studio")) {

			location = "efa727cb-339c-41bd-8d86-920065dfec37";

			Inventory inventory = folioService.getInventoryLoanDetails(location, year);

			for (Loan loan : inventory.loans) {

				if (loan.item.materialType.name.trim().equalsIgnoreCase("equipment")
						|| loan.item.materialType.name.trim().equalsIgnoreCase("room")) {

					if (loan.borrower.barcode != null && !loan.borrower.barcode.trim().isEmpty()) {

						loan.borrower.externalSystemId = folioService
								.getUsersByBarcode(loan.borrower.barcode).externalSystemId;
					} else {
						loan.borrower.externalSystemId = notApplicable;
					}

					loans.add(loan);
				}

			}

		} else if (location.equalsIgnoreCase("laptops")) {

			location = "129dec9a-10f5-4ac2-9228-5d091e817116";

			Inventory inventory = folioService.getInventoryLoanDetails(location, year);

			for (Loan loan : inventory.loans) {

				if (loan.borrower.barcode != null && !loan.borrower.barcode.trim().isEmpty()) {

					loan.borrower.externalSystemId = folioService
							.getUsersByBarcode(loan.borrower.barcode).externalSystemId;
				} else {
					loan.borrower.externalSystemId = notApplicable;
				}

			}

			loans = inventory.loans;
		}

		return loans;
	}

	@RequestMapping(value = "/patronBlocks/data", method = RequestMethod.GET)
	private @ResponseBody List<PatronBlockObject> patronBlocksData(@RequestParam(required = false) String institution)
			throws RestClientException, IOException {

		List<PatronGroup> groups = groupService.getGroupListByInstituteCode(institution);

		ArrayList<ManualBlock> blocks = folioService.getPatronsBlocks();

		List<PatronBlockObject> returnObjects = null;

		try {

			if (blocks != null && blocks.size() > 0) {

				returnObjects = new ArrayList<PatronBlockObject>();

				for (ManualBlock manualBlock : blocks) {

					// Get the FOLIO User details.

					if (manualBlock.userId != null && !manualBlock.userId.trim().isEmpty()) {

						FolioUser folioUser = folioService.getUsersById(manualBlock.userId);

						// FolioUser folioUser = new FolioUser();

						if (folioUser != null) {

							boolean match = groups.stream()
									.anyMatch(s -> s.getFolioGroupId().equals(folioUser.patronGroup));

							if (match) {

								PatronBlockObject patronBlockObject = new PatronBlockObject();

								patronBlockObject.type = manualBlock.type;
								patronBlockObject.code = manualBlock.code;
								patronBlockObject.desc = manualBlock.desc;
								patronBlockObject.borrowing = manualBlock.borrowing;

								patronBlockObject.renewals = manualBlock.renewals;
								patronBlockObject.requests = manualBlock.requests;

								patronBlockObject.identifier = folioUser.externalSystemId;
								patronBlockObject.name = folioUser.personal.firstName + " "
										+ folioUser.personal.lastName;
								patronBlockObject.email = folioUser.personal.email;

								returnObjects.add(patronBlockObject);

							}

						}

					} else {
						System.out.println("manualBlock.userId" + manualBlock.userId);
					}

				}
			}

			return returnObjects;
		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	@RequestMapping(value = "/circulationlog/data", method = RequestMethod.GET)
	private @ResponseBody List<CirculationLog> getCirculationLogData(@RequestParam(required = false) String institution,
			@RequestParam(required = false) String campus, @RequestParam(required = false) String library,
			@RequestParam(required = false) String location, @RequestParam(required = false) String from_date,
			@RequestParam(required = false) String to_date, @RequestParam(required = false) String isEmptyDateWants,
			@RequestParam(required = false) String isOpenLoans, @RequestParam(required = false) String materialType)
			throws RestClientException, IOException {

//		System.out.println("institution " + institution);
//		System.out.println("campus " + campus);
//		System.out.println("library " + library);
//		System.out.println("location " + location);
//		System.out.println("from_date " + from_date);4241320158837339
//		System.out.println("to_date " + to_date);

		System.out.println("isEmptyDateWants : " + Boolean.parseBoolean(isEmptyDateWants));

		List<String> locations = new ArrayList<String>();

		if (location != null && !location.trim().isEmpty() && !location.equals("0")) {
			System.out.println("Looking for Location");
			locations.add(location);
		} else if (library != null && !library.trim().isEmpty() && !library.equals("0")) {
			System.out.println("Looking for Library");
			locations = locationService.getLocationListByLibraryId(library).stream()
					.map(com.okstatelibrary.redbud.entity.Location::getLocation_id).collect(Collectors.toList());
		} else if (campus != null && !campus.trim().isEmpty() && !campus.equals("0")) {
			System.out.println("Looking for Campus");
			locations = locationService.getLocationListByCampusId(campus).stream()
					.map(com.okstatelibrary.redbud.entity.Location::getLocation_id).collect(Collectors.toList());
		} else if (institution != null && !institution.trim().isEmpty() && !institution.equals("0")) {
			System.out.println("Looking for institution");
			locations = locationService.getLocationListByInstitutionId(institution).stream()
					.map(com.okstatelibrary.redbud.entity.Location::getLocation_id).collect(Collectors.toList());
		}

		List<CirculationLog> circulationLogList = circulationLogService.getCirculationLogList(locations, from_date,
				to_date, Boolean.parseBoolean(isEmptyDateWants), Boolean.parseBoolean(isOpenLoans), materialType);

		return circulationLogList;
	}

	@RequestMapping(value = "/circulationloan/data", method = RequestMethod.GET)
	private @ResponseBody List<CirculationLoanReportModel> getCirculationLoanData(
			@RequestParam(required = false) String institution, @RequestParam(required = false) String campus,
			@RequestParam(required = false) String library, @RequestParam(required = false) String location,
			@RequestParam(required = false) String from_date, @RequestParam(required = false) String to_date,
			@RequestParam(required = false) String loanAction, @RequestParam(required = false) String materialType)
			throws RestClientException, IOException {

//		System.out.println("institution " + institution);
//		System.out.println("campus " + campus);
//		System.out.println("library " + library);
//		System.out.println("location " + location);
//		System.out.println("from_date " + from_date);4241320158837339
//		System.out.println("to_date " + to_date);

		List<String> locations = new ArrayList<String>();

		if (location != null && !location.trim().isEmpty() && !location.equals("0")) {
			System.out.println("Looking for Location");
			locations.add(location);
		} else if (library != null && !library.trim().isEmpty() && !library.equals("0")) {
			System.out.println("Looking for Library");
			locations = locationService.getLocationListByLibraryId(library).stream()
					.map(com.okstatelibrary.redbud.entity.Location::getLocation_id).collect(Collectors.toList());
		} else if (campus != null && !campus.trim().isEmpty() && !campus.equals("0")) {
			System.out.println("Looking for Campus");
			locations = locationService.getLocationListByCampusId(campus).stream()
					.map(com.okstatelibrary.redbud.entity.Location::getLocation_id).collect(Collectors.toList());
		} else if (institution != null && !institution.trim().isEmpty() && !institution.equals("0")) {
			System.out.println("Looking for institution");
			locations = locationService.getLocationListByInstitutionId(institution).stream()
					.map(com.okstatelibrary.redbud.entity.Location::getLocation_id).collect(Collectors.toList());
		}

		List<CirculationLoan> circulationLogList = circulationLoanService.getCirculationLoanList(locations, from_date,
				to_date, loanAction, materialType);

		List<CirculationLoanReportModel> reportModelList = new ArrayList<>();

		for (CirculationLoan loan : circulationLogList) {

			CirculationLoanReportModel reportModel = new CirculationLoanReportModel();
			reportModel.setLocation(loan.getRowId());
			reportModel.setBarcode(loan.getCirculationLog().getBarcode());
			reportModel.setCallNumber(loan.getCirculationLog().getCallNumber());
			reportModel.setMaterialType(loan.getCirculationLog().getMaterialType());
			reportModel.setTitle(loan.getCirculationLog().getTitle());

			reportModel.setDate(loan.getDate().toString().substring(0, 10));
			reportModel.setRenewalCount(loan.getRenewalCount());
			reportModel.setOpen(loan.isOpen());
			reportModel.setAction(LoanAction.fromCode(loan.getAction()).toString());

			reportModelList.add(reportModel);
		}

		return reportModelList;
	}

	@GetMapping("/institutionRecords")
	private String getInstitutionRecords(Principal principal, Model model) throws IOException {

		User user = userService.findByUsername(principal.getName());

		model.addAttribute("institutionList", institutionService.getInstitutionList());

		model.addAttribute("user", user);

		return "reports/institutionRecords";
	}

	@RequestMapping(value = "/institutionRecords/data", method = RequestMethod.GET)
	private @ResponseBody List<InstitutionRecord> getInstitutionalholdings(
			@RequestParam(required = false) String institution) throws RestClientException, IOException {

		// User user = userService.findByUsername(principal.getName());

		System.out.print("institution -- " + institution);

		List<Location> locations = locationService.getLocationList();

		List<Institution> institutions = institutionService.getInstitutionList();

		List<InstitutionRecord> records = institutionRecordService.findAllbyInstitutionId(institution);

		for (InstitutionRecord record : records) {

			Optional<Location> selectedLocation = Optional.ofNullable(locations.parallelStream()
					.filter(l -> l.getLocation_id().equals(record.getLocationId())).findAny().orElse(null));

			if (selectedLocation.isPresent()) {
				record.location = selectedLocation.get().getLocation_name();
			}

			Optional<Institution> selectedInstitution = Optional.ofNullable(institutions.parallelStream()
					.filter(l -> l.getInstitution_id().equals(record.getInstitutionId())).findAny().orElse(null));

			if (selectedInstitution.isPresent()) {
				record.institution = selectedInstitution.get().getInstitution_name();
			}

		}

//		model.addAttribute("institutionalRecordCounts", records);
//
//		model.addAttribute("user", user);

		return records; // "reports/institutionRecords";
	}

}