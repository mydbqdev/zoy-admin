package com.integration.zoy.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.integration.zoy.entity.ZoyPgBedDetails;
import com.integration.zoy.entity.ZoyPgPropertyFloorDetails;
import com.integration.zoy.entity.ZoyPgRentCycleMaster;
import com.integration.zoy.entity.ZoyPgRoomDetails;
import com.integration.zoy.exception.WebServiceException;
import com.integration.zoy.utils.ErrorDetail;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

@Service
public class CSVValidationService {

	private String propertyId;
	
	@Autowired
	OwnerDBImpl ownerDBImpl;
	
	public List<ErrorDetail> validateCSV(byte[] file,String propertyId) throws WebServiceException{
		this.propertyId=propertyId;
		List<ErrorDetail> errorDetails = new ArrayList<>();
		Map<String, List<Integer>> emailTracker = new HashMap<>();
		Map<String, List<Integer>> phoneTracker = new HashMap<>();

		try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(file);
				InputStreamReader inputStreamReader = new InputStreamReader(byteArrayInputStream);
				CSVReader csvReader = new CSVReaderBuilder(inputStreamReader).build()) {
			List<String[]> records = csvReader.readAll();
			if (records.isEmpty()) {
				return Collections.singletonList(new ErrorDetail(0, 0, "file", "CSV file is empty"));
			}
			if (records.size() < 3) {
		        return Collections.singletonList(new ErrorDetail(3, 0, "file", "CSV file is empty (No data) "));
		    }
			String[] headers = records.get(0);
			int emailIndex = Arrays.asList(headers).indexOf("eMail");
			int phoneIndex = Arrays.asList(headers).indexOf("Phone Number");
			for (int i = 2; i < records.size(); i++) {
				String[] record = records.get(i);
				validateRow(record, headers, i + 1, errorDetails);

				if (emailIndex >= 0 && emailIndex < record.length) {
					String email = record[emailIndex].trim();
					if (!email.isEmpty()) {
						emailTracker.computeIfAbsent(email, k -> new ArrayList<>()).add(i + 1);
					}
				}
				if (phoneIndex >= 0 && phoneIndex < record.length) {
					String phone = record[phoneIndex].trim();
					if (!phone.isEmpty()) {
						phoneTracker.computeIfAbsent(phone, k -> new ArrayList<>()).add(i + 1);
					}
				}
			}
			
			for (Map.Entry<String, List<Integer>> entry : phoneTracker.entrySet()) {
				List<Integer> rows = entry.getValue();
				if (rows.size() > 1) {
					for (int row : rows) {
						errorDetails.add(new ErrorDetail(row, phoneIndex + 1, "Phone Number","Duplicate Phone Number found: " + entry.getKey() + " in rows " + rows));
					}
				}
			}
			for (Map.Entry<String, List<Integer>> entry : emailTracker.entrySet()) {
				List<Integer> rows = entry.getValue();
				if (rows.size() > 1) {
					for (int row : rows) {
						errorDetails.add(new ErrorDetail(row, emailIndex + 1, "eMail","Duplicate email found: " + entry.getKey() + " in rows " + rows));
					}
				}
			}

		} catch (IOException | CsvException e) {
			e.printStackTrace();
		}
		return errorDetails;
	}

	private void validateRow(String[] record, String[] headers, int rowNum, List<ErrorDetail> errorDetails) {
		for (int col = 0; col < headers.length; col++) {
			String columnName = headers[col];
			String cellValue = record.length > col ? record[col] : "";

			if (cellValue == null || cellValue.trim().isEmpty()) {
				setValidation(cellValue, rowNum, col + 1, columnName, errorDetails);
			}

			validateFieldByType(cellValue, columnName, rowNum, col + 1, errorDetails);
		}
	}

	private void validateFieldByType(String value, String columnName, int row, int header, List<ErrorDetail> errorDetails) {
		switch (columnName) {
		case "First Name":
		case "Last Name":
			validateAlphabets(value, row, header, columnName, errorDetails);
			break;
		case "Phone Number":
			validatePhoneNumber(value, row, header, columnName, errorDetails);
			break;
		case "eMail":
			validateEmail(value, row, header, columnName, errorDetails);
			break;
		case "Date Of Birth":
		case "In Date":
		case "Out Date":
			validateDateFormat(value, row, header, columnName, errorDetails);
			break;
		case "Gender":
			validateGender(value, row, header, columnName, errorDetails);
			break;
		case "Permanent Address":
			validateLength(value, row, header, columnName, 200, errorDetails);
			break;
		case "Floor":
		case "Room":
		case "Bed Number":
			validateLength(value, row, header, columnName, 20, errorDetails);
			validateTable(value, row, header, columnName, 20, errorDetails);
			break;
//		case "Rent Cycle":
//			validateRentCycle(value, row, header, columnName, errorDetails);
//			validateTable(value, row, header, columnName, 20, errorDetails);
//			break;
		case "Deposit Paid":
			validateNumericValue(value, row, header, columnName, errorDetails);
			break;
		case "Rent Paid":
			validateLength(value, row, header, columnName,20, errorDetails);
			break;
		default:
			break;
		}
	}

	private void validateTable(String value, int row, int header, String columnName, int i,List<ErrorDetail> errorDetails) {
		switch (columnName) {
		case "Floor":
			ZoyPgPropertyFloorDetails floorDetails =ownerDBImpl.findFloorDetails(propertyId,value);
			if(floorDetails==null) 
				errorDetails.add(new ErrorDetail(row, header, columnName, columnName+" is not available for the property"));
			break;
		case "Room":
			ZoyPgRoomDetails roomDetails =ownerDBImpl.findRoomDetails(propertyId,value);
			if(roomDetails==null) 
				errorDetails.add(new ErrorDetail(row, header, columnName, columnName+" is not available for the property"));
			break;
		case "Bed Number":
			List<ZoyPgBedDetails> bedDetails =ownerDBImpl.findBedDetails(propertyId,value);
			if(bedDetails.size()==0) 
				errorDetails.add(new ErrorDetail(row, header, columnName, columnName+" is not available for the property"));
			break;
//		case "Rent Cycle":
//			ZoyPgRentCycleMaster rentCycle =ownerDBImpl.findRentCycleName(propertyId,value);
//			if(rentCycle==null) {
//				List<String[]> cycleName =ownerDBImpl.findRentCycleName(propertyId);
//				String result = cycleName.size() > 0 ? cycleName.stream().map(data -> data[1]).collect(Collectors.joining(",")) : "";
//				errorDetails.add(new ErrorDetail(row, header, columnName, columnName+" is not available please select from "+result));
//			}
//			break;
		default:
			break;
		}
	}

	private void setValidation(String value, int row, int header, String columnName, List<ErrorDetail> errorDetails) {
		errorDetails.add(new ErrorDetail(row, header, columnName, "Field is missing or empty"));
	}

	private void validateAlphabets(String value, int row, int header, String columnName, List<ErrorDetail> errorDetails) {
		if (value != null && !value.matches("^[a-zA-Z]{1,20}$")) {
			errorDetails.add(new ErrorDetail(row, header, columnName, "Must contain 1 to 20 alphabetic characters"));
		}
	}

	private void validatePhoneNumber(String phoneValue, int row, int header, String columnName, List<ErrorDetail> errorDetails) {
		if (phoneValue != null && !phoneValue.matches("\\d{10}")) {
			errorDetails.add(new ErrorDetail(row, header, columnName, "Invalid phone number (must be 10 digits)"));
		}
	}

	private void validateEmail(String email, int row, int header, String columnName, List<ErrorDetail> errorDetails) {
		if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
			errorDetails.add(new ErrorDetail(row, header, columnName, "Invalid email format"));
		}
	}

	private void validateDateFormat(String dateValue, int row, int header, String columnName, List<ErrorDetail> errorDetails) {
		if (dateValue != null && !dateValue.trim().isEmpty() && !isValidDate(dateValue)) {
			errorDetails.add(new ErrorDetail(row, header, columnName, "Invalid date format (expected dd-MM-yyyy)"));
		}
		if(columnName.equals("Out Date") && isValidDate(dateValue)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	        LocalDate dateToCheck = LocalDate.parse(dateValue, formatter);
	        LocalDate currentDate = LocalDate.now();
	        boolean isSameMonthAndYear = currentDate.getYear() == dateToCheck.getYear() && currentDate.getMonth() == dateToCheck.getMonth();
	        if(isSameMonthAndYear) {
	        	errorDetails.add(new ErrorDetail(row, header, columnName, "Out Date cannot be same month, please remove or change the Out Date "+ dateValue ));
	        } else if(dateToCheck.isBefore(currentDate)) {
	        	errorDetails.add(new ErrorDetail(row, header, columnName, "Out Date cannot be old date, please remove or change the Out Date "+ dateValue ));
	        }
		}
		if(columnName.equals("In Date") && isValidDate(dateValue)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	        LocalDate dateToCheck = LocalDate.parse(dateValue, formatter);
	        LocalDate currentDate = LocalDate.now();
	        boolean isSameMonthAndYear = currentDate.getYear() == dateToCheck.getYear() && currentDate.getMonth() == dateToCheck.getMonth();
	        if(!isSameMonthAndYear) {
	        	errorDetails.add(new ErrorDetail(row, header, columnName, "In Date cannot be past or future month, please remove or change the In Date "+ dateValue ));
	        } else if(dateToCheck.isAfter(currentDate)) {
	        	errorDetails.add(new ErrorDetail(row, header, columnName, "In Date cannot be future date, please remove or change the In Date "+ dateValue ));
	        }
		}
	}

	private boolean isValidDate(String dateValue) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		dateFormat.setLenient(false);
		try {
			dateFormat.parse(dateValue.trim());
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	private void validateGender(String gender, int row, int header, String columnName, List<ErrorDetail> errorDetails) {
		if (!gender.equalsIgnoreCase("Male") && !gender.equalsIgnoreCase("Female") && !gender.equalsIgnoreCase("Transgender")) {
			errorDetails.add(new ErrorDetail(row, header, columnName, "Invalid gender value"));
		}
	}

	private void validateLength(String value, int row, int header, String columnName, int maxLength, List<ErrorDetail> errorDetails) {
		if (value != null && value.length() > maxLength) {
			errorDetails.add(new ErrorDetail(row, header, columnName, "Value exceeds maximum length of " + maxLength + " characters"));
		}
	}

	private void validateRentCycle(String rentCycle, int row, int header, String columnName, List<ErrorDetail> errorDetails) {
		if (!rentCycle.matches("\\d{2}-\\d{2}")) {
			errorDetails.add(new ErrorDetail(row, header, columnName, "Invalid rent cycle format (expected DD-MM)"));
		}
	}

	private void validateNumericValue(String numericValue, int row, int header, String columnName, List<ErrorDetail> errorDetails) {
		try {
			new BigDecimal(numericValue);
		} catch (NumberFormatException e) {
			errorDetails.add(new ErrorDetail(row, header, columnName, "Invalid numeric value"));
		}
	}
}
