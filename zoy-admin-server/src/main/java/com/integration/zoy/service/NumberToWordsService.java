/**
 * 
 */
package com.integration.zoy.service;

/**
 * @author PraveenRamesh
 *
 */
import org.springframework.stereotype.Service;

@Service
public class NumberToWordsService {

    private static final String[] UNITS = {
            "Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"
    };

    private static final String[] TEENS = {
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
            "Seventeen", "Eighteen", "Nineteen"
    };

    private static final String[] TENS = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };

    public String numberToWords(double number) {
        if (number < 0) {
            throw new IllegalArgumentException("Input must be a non-negative number");
        }

        int integerPart = (int) number;
        int decimalPart = (int) Math.round((number - integerPart) * 100);

        StringBuilder result = new StringBuilder(convertIntegerPart(integerPart) + " Rupees");

        if (decimalPart > 0) {
            result.append(" and ").append(convertDecimalPart(decimalPart)).append(" Paisa");
        }

        return result.toString();
    }

    private String convertIntegerPart(int num) {
        if (num == 0) {
            return UNITS[0];
        }
        return largeNumbers(num);
    }

    private String largeNumbers(int num) {
        if (num < 1000) {
            return hundreds(num);
        }
        int thousand = num / 1000;
        int remainder = num % 1000;
        return hundreds(thousand) + " Thousand" + (remainder > 0 ? " " + hundreds(remainder) : "");
    }

    private String hundreds(int num) {
        if (num < 100) {
            return tensAndUnits(num);
        }
        int unit = num / 100;
        int remainder = num % 100;
        return UNITS[unit] + " Hundred" + (remainder > 0 ? " " + tensAndUnits(remainder) : "");
    }

    private String tensAndUnits(int num) {
        if (num < 10) {
            return UNITS[num];
        }
        if (num < 20) {
            return TEENS[num - 10];
        }
        int ten = num / 10;
        int unit = num % 10;
        return TENS[ten] + (unit > 0 ? "-" + UNITS[unit] : "");
    }

    private String convertDecimalPart(int decimal) {
        StringBuilder digits = new StringBuilder();
        String decimalStr = String.valueOf(decimal);
        for (char digit : decimalStr.toCharArray()) {
            digits.append(UNITS[Character.getNumericValue(digit)]).append(" ");
        }
        return digits.toString().trim();
    }
}