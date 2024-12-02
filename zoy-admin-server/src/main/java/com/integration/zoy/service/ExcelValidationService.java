package com.integration.zoy.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.integration.zoy.utils.ErrorDetail;

@Service
public class ExcelValidationService {

	@Autowired
	OwnerDBImpl ownerDBImpl;

	public List<ErrorDetail> validateExcel(byte[] file) {
		List<ErrorDetail> errors = new ArrayList<>();
		try (ByteArrayInputStream is = new ByteArrayInputStream(file); Workbook workbook = new XSSFWorkbook(is)) {
			Sheet sheet = workbook.getSheetAt(0);
			Row headerRow = sheet.getRow(0);
			if (!validateHeaders(headerRow, errors)) {
				return errors;
			}
			for (int i = 2; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row == null) continue;
				validateRow(row, errors, i + 1);
			}
		} catch (Exception e) {
			errors.add(new ErrorDetail(0, 0, "File", "Error reading Excel file: " + e.getMessage()));
		}
		return errors;
	}

	private boolean validateHeaders(Row row, List<ErrorDetail> errors) {
		if (row == null) {
			errors.add(new ErrorDetail(1, 0, "Header", "Header row is missing."));
			return false;
		}
		String[] expectedHeaders = {
				"Floor Name", "Room Name", "Room Type", "Share Type",
				"Room Area in Sqft", "Available Bed Numbers", "Daily Rent", "Monthly Rent",
				"Room Amenities", "Occupied Available Bed Numbers", "Remarks About Room"
		};

		for (int i = 0; i < expectedHeaders.length; i++) {
			Cell cell = row.getCell(i);
			if (cell == null || !cell.getStringCellValue().equalsIgnoreCase(expectedHeaders[i])) {
				errors.add(new ErrorDetail(1, i + 1, "Header", "Invalid header at column " + (i + 1) + ". Expected: " + expectedHeaders[i]));
				return false;
			}
		}
		return true;
	}

	private void validateRow(Row row, List<ErrorDetail> errors, int rowIndex) {
		validateCell(row.getCell(0), "Floor Name", rowIndex, 1, errors, 20);
		validateCell(row.getCell(1), "Room Name", rowIndex, 2, errors, 20);
		validateRoomTypeCell(row.getCell(2), "Room Type", rowIndex, 3, errors, null);
		validateShareTypeCell(row.getCell(3), "Share Type", rowIndex, 4, errors, null);
		validateNumeric(row.getCell(4), "Room Area in Sqft", rowIndex, 5, errors);
		validateNumeric(row.getCell(6), "Daily Rent", rowIndex, 7, errors);
		validateNumeric(row.getCell(7), "Monthly Rent", rowIndex, 8, errors);
		validateAmenetiesCell(row.getCell(8), "Room Amenities", rowIndex, 9, errors, null);
		Cell availableBed = row.getCell(5);
		Cell occupiedBed = row.getCell(9);
		if(availableBed==null && occupiedBed==null) {
			errors.add(new ErrorDetail(rowIndex, 6, "Available Bed Numbers", "Available Bed Numbers and Occupied Available Bed Numbers are empty."));
			errors.add(new ErrorDetail(rowIndex, 9, "Occupied Available Bed Numbers", "Available Bed Numbers and Occupied Available Bed Numbers are empty."));
		}

		Cell remarksCell = row.getCell(10);
		if (remarksCell != null && remarksCell.getStringCellValue().length() > 300) {
			errors.add(new ErrorDetail(rowIndex, 11, "Remarks About Room", "Remarks must not exceed 300 characters."));
		}
	}

	private void validateCell(Cell cell, String fieldName, int rowIndex, int colIndex, List<ErrorDetail> errors, Integer minLength) {
		if (cell == null || cell.getCellType() != CellType.STRING || (minLength != null && cell.getStringCellValue().length() > minLength)) {
			String errorDesc = minLength != null ? fieldName + " must be with in " + minLength + " characters long." : fieldName + " is required.";
			errors.add(new ErrorDetail(rowIndex, colIndex, fieldName, errorDesc));
		}
	}

	private void validateRoomTypeCell(Cell cell, String fieldName, int rowIndex, int colIndex, List<ErrorDetail> errors, Integer minLength) {
		if (cell == null || cell.getCellType() != CellType.STRING || (minLength != null && cell.getStringCellValue().length() > minLength)) {
			String errorDesc = minLength != null ? fieldName + " must be with in " + minLength + " characters long." : fieldName + " is required.";
			errors.add(new ErrorDetail(rowIndex, colIndex, fieldName, errorDesc));
		} else {
			String roomType=ownerDBImpl.getRoomTypeIdByRoomType(cell.getStringCellValue());
			if(roomType==null)
				errors.add(new ErrorDetail(rowIndex, colIndex, fieldName, fieldName+ " is not available, please enter proper "+ fieldName));
		}
	}

	private void validateAmenetiesCell(Cell cell, String fieldName, int rowIndex, int colIndex, List<ErrorDetail> errors,Integer minLength) {
		if (cell == null || cell.getCellType() != CellType.STRING || (minLength != null && cell.getStringCellValue().length() > minLength)) {
			String errorDesc = minLength != null ? fieldName + " must be with in " + minLength + " characters long." : fieldName + " is required.";
			errors.add(new ErrorDetail(rowIndex, colIndex, fieldName, errorDesc));
		} else {
			List<String> excelAmeneties=cell.getStringCellValue().contains(",")?Arrays.asList(cell.getStringCellValue().split(",")):Arrays.asList(cell.getStringCellValue());
			List<String> ameneties=ownerDBImpl.getNameOfByAmenitiesList(excelAmeneties);
			List<String> missingAmenities = new ArrayList<>(excelAmeneties);
			missingAmenities.removeAll(ameneties);
			if(missingAmenities.size()!=0) {
				String result = missingAmenities.size() > 0 ? missingAmenities.stream().map(data -> data).collect(Collectors.joining(",")) : "";
				errors.add(new ErrorDetail(rowIndex, colIndex, fieldName, fieldName+ " are not available in our system ("+result+")"));
			}
		}
	}

	private void validateShareTypeCell(Cell cell, String fieldName, int rowIndex, int colIndex, List<ErrorDetail> errors,Integer minLength) {
		if (cell == null || cell.getCellType() != CellType.STRING || (minLength != null && cell.getStringCellValue().length() > minLength)) {
			String errorDesc = minLength != null ? fieldName + " must be with in " + minLength + " characters long." : fieldName + " is required.";
			errors.add(new ErrorDetail(rowIndex, colIndex, fieldName, errorDesc));
		} else {
			String shareType=ownerDBImpl.getShareIdByShareType(cell.getStringCellValue());
			if(shareType==null)
				errors.add(new ErrorDetail(rowIndex, colIndex, fieldName, fieldName+ " is not available, please enter proper "+ fieldName));
		}
	}

	private void validateNumeric(Cell cell, String fieldName, int rowIndex, int colIndex, List<ErrorDetail> errors) {
		if (cell == null || (!isNumeric(cell))) {
			errors.add(new ErrorDetail(rowIndex, colIndex, fieldName, fieldName + " must be a valid number."));
		}
	}

	private boolean isNumeric(Cell cell) {
		try {
			if (cell.getCellType() == CellType.NUMERIC) {
				return true;
			}
			Double.parseDouble(cell.getStringCellValue());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
