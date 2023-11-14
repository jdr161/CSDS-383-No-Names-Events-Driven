package com.nonames.cli.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class FormatUtils {
    private static final String VALID_TIME_REGEX = "^(1[0-2]|0?[1-9]):[0-5][0-9](\\s)?(?i)(AM|PM)$";
    private static final String VALID_DATE_REGEX = "^\\d{4}-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$";
    private static final String VALID_EMAIL_REGEX = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    private static boolean isValidDate(String s) {
        return s.matches(VALID_DATE_REGEX);
    }

    private static boolean isValidTime(String s) {
        return s.matches(VALID_TIME_REGEX);
    }

    public static boolean isValidEmail(String email) {
        return email.matches(VALID_EMAIL_REGEX);
    }


    /**
     * Validates and formats a String representation of a time to HH:MM AM/PM
     *
     * @param time
     * @return
     * @throws IllegalArgumentException
     * @throws ParseException
     */
    public static String formatTime(String time) throws IllegalArgumentException, ParseException {
        if (!isValidTime(time)) {
            throw new IllegalArgumentException("Time format does not match HH:MM AM/PM");
        }
        DateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
        return timeFormatter.format(timeFormatter.parse(time));
    }

    /**
     * Validates and formats a String representation of a date to YYYY-MM-DD
     *
     * @param date
     * @return
     * @throws IllegalArgumentException
     * @throws ParseException
     */
    public static String formatDate(String date) throws IllegalArgumentException, ParseException {
        if (!isValidDate(date)) {
            throw new IllegalArgumentException("Date format does not match YYYY-MM-DD");
        }
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormatter.format(dateFormatter.parse(date));
    }

}
