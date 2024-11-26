package com.integration.zoy.service;

import com.integration.zoy.entity.ZoyPgBedDetails;
import com.integration.zoy.entity.ZoyPgPropertyFloorDetails;
import com.integration.zoy.entity.ZoyPgRentCycleMaster;
import com.integration.zoy.entity.ZoyPgRoomDetails;
import com.integration.zoy.utils.ErrorDetail;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CSVValidationService {

	private String propertyId;
	
	@Autowired
	OwnerDBImpl ownerDBImpl;
	
	public List<ErrorDetail> validateCSV(InputStream inputStream,String propertyId) {
		this.propertyId=propertyId;
		List<ErrorDetail> errorDetails = new ArrayList<>();
		try (CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream))) {
			List<String[]> records = csvReader.readAll();
			if (records.isEmpty()) {
				return Collections.singletonList(new ErrorDetail(0, 0, "file", "CSV file is empty"));
			}
			String[] headers = records.get(0);
			for (int i = 2; i < records.size(); i++) {
				String[] record = records.get(i);
				validateRow(record, headers, i + 1, errorDetails);
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
		case "Rent Cycle":
			validateRentCycle(value, row, header, columnName, errorDetails);
			validateTable(value, row, header, columnName, 20, errorDetails);
			break;
		case "Deposit Paid":
			validateNumericValue(value, row, header, columnName, errorDetails);
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
		case "Rent Cycle":
			ZoyPgRentCycleMaster rentCycle =ownerDBImpl.findRentCycleName(propertyId,value);
			if(rentCycle==null) {
				List<String[]> cycleName =ownerDBImpl.findRentCycleName(propertyId);
				String result = cycleName.size() > 0 ? cycleName.stream().map(data -> data[1]).collect(Collectors.joining(",")) : "";
				errorDetails.add(new ErrorDetail(row, header, columnName, columnName+" is not available please select from "+result));
			}
			break;
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