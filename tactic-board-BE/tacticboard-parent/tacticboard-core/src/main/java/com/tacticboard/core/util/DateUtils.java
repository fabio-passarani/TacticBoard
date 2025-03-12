package com.tacticboard.core.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public final class DateUtils {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private DateUtils() {
        throw new AssertionError("DateUtils class should not be instantiated");
    }
    
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }
    
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : null;
    }
    
    public static LocalDate parseDate(String dateStr) {
        return dateStr != null ? LocalDate.parse(dateStr, DATE_FORMATTER) : null;
    }
    
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return dateTimeStr != null ? LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER) : null;
    }
    
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }
    
    public static LocalDate getFirstDayOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }
    
    public static LocalDate getLastDayOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }
    
    public static LocalDate getFirstDayOfWeek(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }
    
    public static LocalDate getLastDayOfWeek(LocalDate date) {
        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }
    
    public static List<LocalDate> getDatesBetween(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = startDate;
        
        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        
        return dates;
    }
    
    public static boolean isDateInRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
    
    public static boolean isExpired(LocalDate expiryDate) {
        return expiryDate.isBefore(LocalDate.now());
    }
    
    public static boolean isExpired(LocalDateTime expiryDateTime) {
        return expiryDateTime.isBefore(LocalDateTime.now());
    }
}