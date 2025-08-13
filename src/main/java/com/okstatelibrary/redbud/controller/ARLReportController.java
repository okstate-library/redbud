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
@RequestMapping("/arl-reports")
public class ARLReportController {

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

	@GetMapping("/microfilm")
	private String getOverdueFines(Principal principal, Model model) throws IOException {

		User user = userService.findByUsername(principal.getName());

		model.addAttribute("institutionList", institutionService.getInstitutionList());

		model.addAttribute("user", user);

		return "arl-reports/microfilm";
	}

}