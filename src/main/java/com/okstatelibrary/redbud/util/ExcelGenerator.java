package com.okstatelibrary.redbud.util;

public class ExcelGenerator {

//	public static ByteArrayInputStream statDataToExcel(List<HeadCountStat> statList) throws IOException {
//
//		String[] COLUMNs = { "Time", "Type", "Live count", "Count", "User added" };
//
//		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
//			CreationHelper createHelper = workbook.getCreationHelper();
//
//			Sheet sheet = workbook.createSheet("stats");
//
//			Font headerFont = workbook.createFont();
//			headerFont.setBold(true);
//			headerFont.setColor(IndexedColors.BLUE.getIndex());
//
//			CellStyle headerCellStyle = workbook.createCellStyle();
//			headerCellStyle.setFont(headerFont);
//
//			// Row for Header
//			Row headerRow = sheet.createRow(0);
//
//			// Header
//			for (int col = 0; col < COLUMNs.length; col++) {
//				Cell cell = headerRow.createCell(col);
//				cell.setCellValue(COLUMNs[col]);
//				cell.setCellStyle(headerCellStyle);
//			}
//
//			// CellStyle for Age
////			CellStyle ageCellStyle = workbook.createCellStyle();
////			ageCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#"));
//
//			int rowIdx = 1;
//
//			for (HeadCountStat stat : statList) {
//				Row row = sheet.createRow(rowIdx++);
//
//				// date columns
//				CellStyle cellStyle = workbook.createCellStyle();
//				cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
//				Cell dateCell = row.createCell(0);
//				dateCell.setCellValue(stat.getDate());
//				dateCell.setCellStyle(cellStyle);
//
//				// type - Entry or Exit
//				row.createCell(1).setCellValue(stat.isEntry() ? "Entry" : "Exit");
//
//				// live count
//				row.createCell(2).setCellValue(stat.getLiveCount());
//
//				// count
//				row.createCell(3).setCellValue(stat.getCount());
//
//				// added user column
//				Cell addedUserCell = row.createCell(4);
//				addedUserCell.setCellValue(stat.getUser().getFirstName() + " " + stat.getUser().getLastName());
//
//			}
//
//			workbook.write(out);
//			return new ByteArrayInputStream(out.toByteArray());
//		}
//	}
//
//	public static ByteArrayInputStream GraphDataToExcel(GraphDataModel getGraphDataModel) throws IOException {
//
//		String[] columns = { "Time", "Initial count", "Entry count", "Exit count", "Reset count" };
//
//		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
//			Sheet sheet = workbook.createSheet("stats");
//
//			Font headerFont = workbook.createFont();
//			headerFont.setBold(true);
//			headerFont.setColor(IndexedColors.BLUE.getIndex());
//
//			CellStyle headerCellStyle = workbook.createCellStyle();
//			headerCellStyle.setFont(headerFont);
//
//			// Row for Header
//			Row headerRow = sheet.createRow(0);
//
//			// Header
//			for (int col = 0; col < columns.length; col++) {
//				Cell cell = headerRow.createCell(col);
//				cell.setCellValue(columns[col]);
//				cell.setCellStyle(headerCellStyle);
//			}
//
//			int rowIdx = 1;
//
//			for (Map.Entry<Integer, Integer> data : getGraphDataModel.EntryMap.entrySet()) {
//
//				Row row = sheet.createRow(rowIdx++);
//
//				Integer index = data.getKey();
//
//				// Category name
//				row.createCell(0).setCellValue(getGraphDataModel.Categories[index]);
//
//				// Hour initial count
//				row.createCell(1).setCellValue(getGraphDataModel.LiveCountMap.get(index));
//
//				// Enter count
//				row.createCell(2).setCellValue(getGraphDataModel.EntryMap.get(index));
//
//				// exit count
//				row.createCell(3).setCellValue(getGraphDataModel.ExitMap.get(index));
//
//				// exit count
//				row.createCell(4).setCellValue(getGraphDataModel.ResetMap.get(index));
//			}
//
//			workbook.write(out);
//			return new ByteArrayInputStream(out.toByteArray());
//		}
//	}
}
