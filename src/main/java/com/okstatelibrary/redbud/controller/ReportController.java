package com.okstatelibrary.redbud.controller;

import com.okstatelibrary.redbud.entity.CirculationLog;
import com.okstatelibrary.redbud.entity.PatronGroup;
import com.okstatelibrary.redbud.entity.User;
import com.okstatelibrary.redbud.folio.entity.Account;
import com.okstatelibrary.redbud.folio.entity.FineAndFeesObject;
import com.okstatelibrary.redbud.folio.entity.FolioUser;
import com.okstatelibrary.redbud.folio.entity.PatronBlockRoot;
import com.okstatelibrary.redbud.folio.entity.inventory.*;
import com.okstatelibrary.redbud.service.CampusService;
import com.okstatelibrary.redbud.service.CirculationLogService;
import com.okstatelibrary.redbud.service.GroupService;
import com.okstatelibrary.redbud.service.InstitutionService;
import com.okstatelibrary.redbud.service.LibraryService;
import com.okstatelibrary.redbud.service.LocationService;
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
	private InstitutionService institutionService;

	@Autowired
	private CampusService campusService;

	@Autowired
	private LibraryService libraryService;

	@Autowired
	private LocationService locationService;

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

		return "reports/circulationlog";

	}

	@RequestMapping(value = "/overdueItems/data", method = RequestMethod.GET)
	private @ResponseBody List<FineAndFeesObject> overDueItemsData(@RequestParam(required = false) String feeFineOwner)
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
	private @ResponseBody ArrayList<Loan> inventoryLoansData(@RequestParam(required = false) String location)
			throws RestClientException, IOException {

		ArrayList<Loan> loans = new ArrayList<Loan>();

		if (location.equalsIgnoreCase("creative_studio")) {

			location = "efa727cb-339c-41bd-8d86-920065dfec37";
			
			Inventory inventory = folioService.getInventoryLoanDetails(location);

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

			Inventory inventory = folioService.getInventoryLoanDetails(location);

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

	@RequestMapping(value = "/circulationlog/data", method = RequestMethod.GET)
	private @ResponseBody List<CirculationLog> getCirculationLogData(@RequestParam(required = false) String institution,
			@RequestParam(required = false) String campus, @RequestParam(required = false) String library,
			@RequestParam(required = false) String location, @RequestParam(required = false) String from_date,
			@RequestParam(required = false) String to_date) throws RestClientException, IOException {

//		System.out.println("institution " + institution);
//		System.out.println("campus " + campus);
//		System.out.println("library " + library);
//		System.out.println("location " + location);
//		System.out.println("from_date " + from_date);
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

		List<CirculationLog> circulationLogList = circulationLogService.getCirculationLogList(locations, from_date,
				to_date);

//		for (CirculationLog log : circulationLogList) {
//			System.out.println(log.getCallNumber());
//		}

		return circulationLogList;
	}

}