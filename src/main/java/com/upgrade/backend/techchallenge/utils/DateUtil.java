package com.upgrade.backend.techchallenge.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class DateUtil {

    private static final String INTEGER_FORMAT = "yyyyMMdd";
    private static final String STRING_FORMAT = "MM/dd/yyyy";

    public static Date setMidnight(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.AM_PM, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static String toString(Integer date) {
        try {
            SimpleDateFormat toIntegerFormatter = new SimpleDateFormat(INTEGER_FORMAT);
            SimpleDateFormat toStringFormatter = new SimpleDateFormat(STRING_FORMAT);

            return toStringFormatter.format(toIntegerFormatter.parse(String.valueOf(date)));
        } catch (ParseException e) {
            log.error("Problems processing date", e);
            return null;
        }
    }

    public static Integer toInteger(String date) {
        try {
            SimpleDateFormat toIntegerFormatter = new SimpleDateFormat(INTEGER_FORMAT);
            SimpleDateFormat toStringFormatter = new SimpleDateFormat(STRING_FORMAT);

            return Integer.parseInt(toIntegerFormatter.format(toStringFormatter.parse(date)));
        } catch (ParseException e) {
            log.error("Problems processing date", e);
            return null;
        }
    }

    public static Integer toInteger(Date date) {
        SimpleDateFormat toIntegerFormatter = new SimpleDateFormat(INTEGER_FORMAT);

        return Integer.parseInt(toIntegerFormatter.format(date));
    }

    public static String toString(Date date) {
        SimpleDateFormat toStringFormatter = new SimpleDateFormat(STRING_FORMAT);

        return toStringFormatter.format(date);
    }

    public static Date toDate(Integer date) {
        try {
            SimpleDateFormat toIntegerFormatter = new SimpleDateFormat(INTEGER_FORMAT);

            return toIntegerFormatter.parse(String.valueOf(date));
        } catch (ParseException e) {
            log.error("Problems processing date", e);
            return null;
        }
    }

    public static Date addDays(Date date, int quantity) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, quantity);
        return calendar.getTime();
    }

}
