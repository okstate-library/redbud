package com.okstatelibrary.redbud.operations;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.okstatelibrary.redbud.entity.*;
import com.okstatelibrary.redbud.enums.UserStatusCheck;
import com.okstatelibrary.redbud.folio.entity.*;
import com.okstatelibrary.redbud.folio.entity.loan.Loan;
import com.okstatelibrary.redbud.folio.entity.manualblock.ManualBlock;
import com.okstatelibrary.redbud.service.*;
import com.okstatelibrary.redbud.util.Constants;
import com.okstatelibrary.redbud.util.DateUtil;
import com.okstatelibrary.redbud.util.StringHelper;

public class DeleteInctiveUsers extends MainProcess {

	protected String startTime;

	private ArrayList<String> messageList;

	public void manipulate(GroupService groupService) {

		startTime = DateUtil.getTodayDateAndTime();

		messageList = new ArrayList<>();

		messageList.add("Change Expiration Date Of Active Users" + "<br/>");

		messageList.add("New expiry date will be " + DateUtil.get9MonthsAfterTodayDate() + "<br/>");

		messageList.add("Start Time " + DateUtil.getTodayDateAndTime() + "<br/>");

		try {

			printScreen("New expiry date" + DateUtil.get9MonthsAfterTodayDate(), Constants.ErrorLevel.INFO);

			List<PatronGroup> groupList = groupService.getGroupList();

			int totalUserCountDelete = 0;

			ArrayList<Loan> allOpenLoans = folioService.getAllOpenLoans();

			printScreen("allOpenLoans count" + allOpenLoans.size());

			ArrayList<ManualBlock> manualBlocks = folioService.getAllManualBlocks();

			printScreen("manualBlocks count" + manualBlocks.size());

			for (CsvFileModel csvFileModel : Constants.csvFileModels) {

				for (String institueCode : csvFileModel.institueCodes) {

					// && selGroup.getFolioGroupId().equals("02609d66-4b2a-47f6-988a-cf7b5b2932c7")

					List<PatronGroup> selGroupList = groupList.stream()
							.filter(selGroup -> selGroup.getInstitutionCode().equals(institueCode)
									&& selGroup.isFolioOnly() == 0)
							// && selGroup.getFolioGroupId().equals("c88e6e42-9544-4e5e-ae94-a50c07b9dfbf"))
							.collect(Collectors.toList());

					try {

						for (PatronGroup group : selGroupList) {

							messageList.add(group.getFolioGroupId() + "   " + group.getFolioGroupName());

							Root folioRoot = folioService.getUsersbyPatronGroup(group.getFolioGroupId(),
									UserStatusCheck.BOTH);

							printScreen("Folio Users in Group - '" + group.getFolioGroupName()
									+ "' both active and inactive users " + folioRoot.users.size());

							int groupDeleteUserCount = 0;

							printScreen(
									"CWID, Username , Expiry Date , Loans,  Automated Blocks , Manual Blocks , Process");

							for (FolioUser folioUser : folioRoot.users) {

								if (!folioUser.active) {

									PatronBlockRoot automatedBlocks = folioService
											.getAutomatedPatronBlocksByUser(folioUser.id);

									List<Loan> userLoans = allOpenLoans.stream()
											.filter(loan -> folioUser.id.equals(loan.userId))
											.collect(Collectors.toList());

									// ArrayList<Loan> loans = folioService.getLoansByUser(folioUser.id);

									int loanCount = userLoans != null ? userLoans.size() : 0;

									int autoBlockCount = automatedBlocks != null
											? automatedBlocks.automatedPatronBlocks != null
													? automatedBlocks.automatedPatronBlocks.size()
													: 0
											: 0;

									List<ManualBlock> usermanualBlocks = manualBlocks.stream()
											.filter(block -> folioUser.id.equals(block.userId))
											.collect(Collectors.toList());

									int manualBlockCount = usermanualBlocks != null ? usermanualBlocks.size() : 0;

									if (!StringHelper.isStringNullOrEmpty(folioUser.expirationDate)) {
										LocalDate inputDate = OffsetDateTime.parse(folioUser.expirationDate)
												.toLocalDate();

										LocalDate oneYearAgo = LocalDate.now().minusYears(1);

										if (inputDate.isBefore(oneYearAgo)) {

											totalUserCountDelete++;

											groupDeleteUserCount++;

											int counts = loanCount + autoBlockCount + manualBlockCount;

											if (counts == 0) {

//												folioService.deleteUser(folioUser.id);
//
//												printScreen(folioUser.externalSystemId + ", " + folioUser.username
//														+ ", " + folioUser.expirationDate.split("T")[0] + ", "
//														+ loanCount + ", " + autoBlockCount + ", " + manualBlockCount
//														+ ", Deleted");

												// break;

											} else {
												printScreen(folioUser.externalSystemId + ", " + folioUser.username
														+ ", " + folioUser.expirationDate.split("T")[0] + ", "
														+ loanCount + ", " + autoBlockCount + ", " + manualBlockCount
														+ ", Can't Delete");
											}

										}
									} else {
										printScreen(folioUser.externalSystemId + ", " + folioUser.username
												+ ", Expire date null, " + loanCount + ", " + autoBlockCount + ", "
												+ manualBlockCount + ", Error in records");
									}

								}

								// break;
							}

							printScreen("Delete number fo user for group " + group.getFolioGroupName()
									+ " user count : " + groupDeleteUserCount);

							printScreen("");

							// break;
						}

					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					// break;
				}

				// break;
			}

			printScreen("Total Number of Users to delete " + totalUserCountDelete);

		} catch (Exception e1) {
			// TODO Auto-generated catch blocks
			e1.printStackTrace();
		}

	}

}
