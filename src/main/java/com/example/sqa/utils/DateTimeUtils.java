package com.example.sqa.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateTimeUtils {
    public static final String PATTERN_TIME = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
    public static final String GMT7 = "GMT+07:00";
    public static final String GMT0 = "GMT+00:00";

    public static TimeZone TIMEZONE_VN = TimeZone.getTimeZone(GMT7);
    public static TimeZone TIMEZONE_GMT0 = TimeZone.getTimeZone(GMT0);

    public static TimeZone TIMEZONE_DEFAULT = TIMEZONE_VN;

    private static SimpleDateFormat formatter = new SimpleDateFormat();
    public static final String ISO_8601_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String ISO_8601_FORMAT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_yyyy_mm_dd = "yyyy/MM/dd";
    public static final String FORMAT_mm_dd_yyyy = "MM/dd/yyyy";
    public static final String FORMAT_dd_MM_yyyy = "dd/MM/yyyy";
    public static final String FORMAT_dd_MM_yyyy_hh_mm_ss_mmm = "dd/MM/yyyy hh:mm:ss";
    public static final String FORMAT_TIME = "HH:MM:SS";
    public static final String FORMAT_TIME_Hms = "HH:mm:ss";
    public static final String FORMAT_TIME_Hm = "HH:mm";
    public static final String FORMAT_DATE2 = "dd/MM/yyyy";
    public static final String FORMAT_DATE5 = "yyyy-MM-dd";
    public static final String FORMAT_DATE_TIME2 = "dd/MM/yyy hh:mm:ss";
    public static final String FORMAT_DATE_TIME3 = "dd/MM/yyyy HH:mm:ss";
    public static final String FORMAT_DATE_TIME5 = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DB_TIME = "HH24:MI:SS";
    public static final String FORMAT_DB_DATE = "yyyy-MM-dd";
    public static final String FORMAT_DB_DATE_TIME = "YYYY-MM-DD HH24:MI:SS";
    public static final String FORMAT_DB_YDATE_TIME = "SYYYY-MM-DD HH24:MI:SS";
    public static final String FORMAT_YMDHMS = "yyyyMMddhh24muss";
    public static final String FORMAT_YMD = "yyyyMMdd";
    private static final String FORMAT_DATE_TIME4 = "dd/MM/yyyy HH:mm";
    public static final String FORMAT_MONTH_DAY = "MMdd";


    /**
     * Convert date to string with a pattern Example mm/dd/yyyy
     *
     * @param date
     * @param format
     * @return
     */
    public static String dateToString(Date date, String format) {
        return dateToString(date, format, TIMEZONE_DEFAULT);
    }


    public static String dateToString(Date date, String format, TimeZone timeZone) {
        formatter.setTimeZone(timeZone);
        formatter.applyPattern(format);
        return formatter.format(date);
    }

    /**
     * Convert String to Date object
     *
     * @param dateStr
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date stringToDate(String dateStr, String format) throws ParseException {
        formatter.setLenient(false);
        formatter.applyPattern(format);
        return formatter.parse(dateStr.trim());
    }

    public static Long stringHMSToMillis(String strHMS) {
        if (strHMS == null) {
            return null;
        }

        String[] arrStr = strHMS.split(":");
        if (arrStr.length == 3) {
            int h = Integer.parseInt(arrStr[0]);
            int m = Integer.parseInt(arrStr[1]);
            int s = Integer.parseInt(arrStr[2]);
            long mH = h * (60 * (60 * 1000));
            long mM = m * (60 * 1000);
            long mS = s * 1000;
            return (mH + mM + mS);
        }
        if (arrStr.length == 2) {
            int h = Integer.parseInt(arrStr[0]);
            int m = Integer.parseInt(arrStr[1]);
            int s = 00;
            long mH = h * (60 * (60 * 1000));
            long mM = m * (60 * 1000);
            long mS = s * 1000;
            return (mH + mM + mS);
        } else {
            return null;
        }
    }

    public static String convertHourToDateAndHour(int hour) {

        int day = Math.round(hour / 24);
        int hour2 = hour % 24;

        String dateAndHour = "";

        if (day != 0) {
            dateAndHour = day + "d " + hour2 + "h";

        } else {
            dateAndHour = hour2 + "h";
        }

        return dateAndHour;
    }

    public static int convertDateAndHourToHour(int day, int hour) {
        int rs = 0;

        rs = day * 24 + hour;

        return rs;
    }

    public static String formatStringToHHmmss(int strHMS) {
        int h = strHMS / (3600);
        int m = (strHMS % (3600)) / 60;
        int s = (strHMS % (3600)) % 60;
        String strH = String.format("%02d", h);
        String strM = String.format("%02d", m);
        String strS = String.format("%02d", s);
        return strH + ":" + strM + ":" + strS;
    }

    public static String currentHMS() {
        Calendar cal = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
        return cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
    }

    public static Date timestampToDate(Timestamp stamp) {
        Calendar cal = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
        cal.setTimeInMillis(stamp.getTime());
        return cal.getTime();
    }

    public static String dateToString2(Date date, String strFormat) {
        if (date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(strFormat);
            return simpleDateFormat.format(date);
        } else {
            return null;
        }
    }

    public static String longToString(Long millis, String strFormat) {
        if (millis != null && millis > 0) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(strFormat);
            Calendar cal = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
            cal.setTimeInMillis(millis);
            return simpleDateFormat.format(cal.getTime());
        } else {
            return null;
        }
    }

    public static Date StringToDate2(String dateString, String strFormat) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(strFormat);
            return simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // //////////////////////////////////////////////////////

    /**
     * Check string format with current format locale
     *
     * @param strSource String to check
     * @return boolean true if strSource represent a date, otherwise false
     */
    // //////////////////////////////////////////////////////
    public static boolean isDate(String strSource) {
        return isDate(strSource, DateFormat.getDateInstance());
    }

    // //////////////////////////////////////////////////////

    /**
     * Check string format
     *
     * @param strSource String
     * @param strFormat Format to check
     * @return boolean true if strSource represent a date, otherwise false
     */
    // //////////////////////////////////////////////////////
    public static boolean isDate(String strSource, String strFormat) {
        SimpleDateFormat fmt = new SimpleDateFormat(strFormat);
        fmt.setLenient(false);
        return isDate(strSource, fmt);
    }

    // //////////////////////////////////////////////////////

    /**
     * Check string format
     *
     * @param strSource String
     * @param fmt       Format to check
     * @return boolean true if strSource represent a date, otherwise false
     */
    // //////////////////////////////////////////////////////
    private static boolean isDate(String strSource, DateFormat fmt) {
        try {
            if (fmt.parse(strSource) == null) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    // //////////////////////////////////////////////////////

    /**
     * Convert string to date using current format locale
     *
     * @param strSource String to convert
     * @return Date converted, null if conversion failure
     */
    // //////////////////////////////////////////////////////
    public static Date toDate(String strSource) {
        return toDate(strSource, DateFormat.getDateInstance());
    }

    // //////////////////////////////////////////////////////

    /**
     * Convert string to date
     *
     * @param strSource String to convert
     * @param strFormat Format to convert
     * @return Date converted, null if conversion failure
     */
    // //////////////////////////////////////////////////////
    public static Date toDate(String strSource, String strFormat) {
        SimpleDateFormat fmt = new SimpleDateFormat(strFormat);
        fmt.setLenient(false);
        return toDate(strSource, fmt);
    }

    // //////////////////////////////////////////////////////

    /**
     * Convert string to date
     *
     * @param strSource String to convert
     * @param fmt       Format to convert
     * @return Date converted, null if conversion failure
     */
    // //////////////////////////////////////////////////////
    private static Date toDate(String strSource, DateFormat fmt) {
        try {
            return fmt.parse(strSource);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // //////////////////////////////////////////////////////

    /**
     * Add date value by second
     *
     * @param dt     Date Date to add
     * @param iValue int value to add
     * @return Date after add
     */
    // //////////////////////////////////////////////////////
    public static Date addSecond(Date dt, int iValue) {
        return add(dt, iValue, Calendar.SECOND);
    }

    // //////////////////////////////////////////////////////

    /**
     * Add date value by minute
     *
     * @param dt     Date Date to add
     * @param iValue int value to add
     * @return Date after add
     */
    // //////////////////////////////////////////////////////
    public static Date addMinute(Date dt, int iValue) {
        return add(dt, iValue, Calendar.MINUTE);
    }

    // //////////////////////////////////////////////////////

    /**
     * Add date value by hour
     *
     * @param dt     Date Date to add
     * @param iValue int value to add
     * @return Date after add
     */
    // //////////////////////////////////////////////////////
    public static Date addHour(Date dt, int iValue) {
        return add(dt, iValue, Calendar.HOUR);
    }

    // //////////////////////////////////////////////////////

    /**
     * Add date value by day
     *
     * @param dt     Date Date to add
     * @param iValue int value to add
     * @return Date after add
     */
    // //////////////////////////////////////////////////////
    public static Date addDay(Date dt, int iValue) {
        return add(dt, iValue, Calendar.DATE);
    }

    // //////////////////////////////////////////////////////

    /**
     * Add date value by month
     *
     * @param dt     Date Date to add
     * @param iValue int value to add
     * @return Date after add
     */
    // //////////////////////////////////////////////////////
    public static Date addMonth(Date dt, int iValue) {
        return add(dt, iValue, Calendar.MONTH);
    }

    // //////////////////////////////////////////////////////

    /**
     * Add date value by year
     *
     * @param dt     Date Date to add
     * @param iValue int value to add
     * @return Date after add
     */
    // //////////////////////////////////////////////////////
    public static Date addYear(Date dt, int iValue) {
        return add(dt, iValue, Calendar.YEAR);
    }

    // //////////////////////////////////////////////////////

    /**
     * Add date value
     *
     * @param dt     Date Date to add
     * @param iValue int value to add
     * @param iType  type of unit
     * @return Date after add
     */
    // //////////////////////////////////////////////////////
    private static Date add(Date dt, int iValue, int iType) {
        Calendar cld = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
        cld.setTime(dt);
        cld.add(iType, iValue);
        return cld.getTime();
    }

    public static int getHoursFromDate(Date date) {
        Calendar cld = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
        cld.setTime(date);
        return cld.get(Calendar.HOUR_OF_DAY);
    }

    public static int getDayFromDate(Date date) {
        Calendar cld = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
        cld.setTime(date);
        return cld.get(Calendar.DAY_OF_MONTH);
    }

    public static int getMinuteFromDate(Date date) {
        Calendar cld = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
        cld.setTime(date);
        return cld.get(Calendar.MINUTE);
    }

    public static Date stringToDate(String str) {
        if (org.springframework.util.StringUtils.isEmpty(str)) {
            return null;
        }
        try {
            DateFormat df = new SimpleDateFormat(FORMAT_DB_DATE);
            df.setLenient(false);
            return df.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }


    public static String dateToString(Date date) {
        if (date == null) {
            return null;
        }
        DateFormat df = new SimpleDateFormat(FORMAT_DATE_TIME3);
        df.setTimeZone(TIMEZONE_VN);
        Calendar cal = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
        cal.setTime(date);
        return df.format(cal.getTime());
    }

    // //////////////////////////////////////////////////////

    /**
     * Format date object
     *
     * @param dtImput    date to format
     * @param strPattern format pattern
     * @return formatted string
     * @author
     */
    // //////////////////////////////////////////////////////
    public static String format(Date dtImput, String strPattern) {
        if (dtImput == null) {
            return null;
        }
        SimpleDateFormat fmt = new SimpleDateFormat(strPattern);
        return fmt.format(dtImput);
    }

    public static Date now() {
        return Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT).getTime();
    }

    private static Date[] getDaysOfWeek(Date refDate, int firstDayOfWeek) {
        Calendar calendar = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        Date[] daysOfWeek = new Date[7];
        for (int i = 0; i < 7; i++) {
            daysOfWeek[i] = calendar.getTime();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return daysOfWeek;
    }

    public static Date[] getDaysOfMonth(int refMonth, int refYear) {
        Calendar calendar = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
        int date = 1;
        calendar.set(refYear, refMonth, date, 00, 00, 00);
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Date[] days = new Date[maxDay];

        for (int i = date; i <= maxDay; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            days[i - 1] = calendar.getTime();
        }
        return days;
    }

    public static Date[] getDaysOfMonth(int refMonth) {
        Calendar calendar = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
        int year = calendar.get(Calendar.YEAR);
        int date = 1;
        calendar.set(year, refMonth, date, 00, 00, 00);
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Date[] days = new Date[maxDay];

        for (int i = date; i <= maxDay; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            days[i - 1] = calendar.getTime();
        }
        return days;
    }

    public static Date[] getFromDateToDateByType(String dayType) {
        Date[] dates = new Date[2];
        if ("d".equals(dayType)) {
            Calendar cal = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
            cal.set(Calendar.HOUR_OF_DAY, 00);
            cal.set(Calendar.MINUTE, 00);
            cal.set(Calendar.SECOND, 00);
            dates[0] = cal.getTime();
            cal = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            dates[1] = cal.getTime();
        }
        if ("w".equals(dayType)) {
            Calendar cal = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
            Date[] dTemp = getDaysOfWeek(cal.getTime(), Calendar.MONDAY);
            dates[0] = dTemp[0];
            cal.setTime(dTemp[dTemp.length - 1]);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            dates[1] = cal.getTime();
        }
        if ("m".equals(dayType)) {
            Calendar cal = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
            Date[] dTemp = getDaysOfMonth(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
            dates[0] = dTemp[0];
            cal.setTime(dTemp[dTemp.length - 1]);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            dates[1] = cal.getTime();
        }
        return dates;
    }

    public static Date longToDate(String millis) {
        Calendar cal = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
        cal.setTimeInMillis(Long.valueOf(millis));
        return cal.getTime();
    }

    public static Date longToDate(Long millis) {
        Calendar cal = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
        cal.setTimeInMillis(millis);
        return cal.getTime();
    }

    public static Date convertTimeZone(Date date, String fromTimeZone, String toTimeZone) {
        ZoneId fromZoneId = ZoneId.of(fromTimeZone);
        ZoneId toZoneId = ZoneId.of(toTimeZone);
        LocalDateTime fromDateTime = date.toInstant().atZone(fromZoneId).toLocalDateTime();
        ZonedDateTime fromDate = ZonedDateTime.of(fromDateTime, fromZoneId);
        ZonedDateTime toDate = fromDate.withZoneSameInstant(toZoneId);
        Calendar cal = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
        cal.set(toDate.getYear(), toDate.getMonthValue() - 1, toDate.getDayOfMonth(), toDate.getHour(),
                toDate.getMinute(), toDate.getSecond());
        return cal.getTime();
    }

    public static Date convertToUTC(Date date) {
        ZoneId fromZoneId = ZoneId.of(TimeZone.getDefault().getID());
        ZoneId toZoneId = ZoneId.of(TimeZone.getTimeZone("UTC").getID());
        LocalDateTime fromDateTime = date.toInstant().atZone(fromZoneId).toLocalDateTime();
        ZonedDateTime fromDate = ZonedDateTime.of(fromDateTime, fromZoneId);
        ZonedDateTime toDate = fromDate.withZoneSameInstant(toZoneId);
        Calendar cal = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.set(toDate.getYear(), toDate.getMonthValue() - 1, toDate.getDayOfMonth(), toDate.getHour(),
                toDate.getMinute(), toDate.getSecond());
        Date date2 = cal.getTime();
        System.out.println(date2);
        return cal.getTime();
    }

    public static Date convertToUTCAndShort(Date date) {
        date = toShortDate(date);
        return date;
    }

    public static Integer calculateAge(Date birthOfDate) {
        if (birthOfDate != null) {
            LocalDate lBirthOfDate = birthOfDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return Period.between(lBirthOfDate, LocalDate.now()).getYears();
        }
        return null;
    }

    public static Date toDate(Date date, int hh, int mm, int ss) {
        Calendar cal = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, hh);
        cal.set(Calendar.MINUTE, mm);
        cal.set(Calendar.SECOND, ss);
        return cal.getTime();
    }

    public static Date toShortDate(Date date) {
        Calendar cal = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        cal.set(Calendar.MILLISECOND, 000);
        return cal.getTime();
    }

    public static Date[] daysOfMonth(int month) {
        Calendar calendar = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
        int year = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT).get(Calendar.YEAR);
        int date = 1;
        calendar.set(year, month, date, 00, 00, 00);
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Date[] daysOfmonth = new Date[maxDay];
        for (int i = 0; i < maxDay; i++) {
            calendar.set(year, month, i + 1);
            daysOfmonth[i] = calendar.getTime();
        }
        return daysOfmonth;
    }

    public static Date[] dayOfWeek(int index) {
        LocalDate previousMonday = LocalDate.now(ZoneId.systemDefault())
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        Date mondayDate = Date.from(previousMonday.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Calendar c = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
        c.setTime(mondayDate);
        c.add(Calendar.DAY_OF_WEEK, index * 7);
        c.set(Calendar.HOUR_OF_DAY, 00);
        c.set(Calendar.MINUTE, 00);
        c.set(Calendar.SECOND, 00);
        Date[] arrDate = new Date[7];
        arrDate[0] = c.getTime();
        for (int i = 0; i < 6; i++) {
            c.add(Calendar.DATE, 1);
            arrDate[i + 1] = c.getTime();
        }
        c.getTime();
        return arrDate;
    }

    public static long getMinDate(long[] longDate) {
        long nhonhat = longDate[0];
        long lonnhat = longDate[0];
        for (int i = 1; i < longDate.length; i++) {
            System.out.println(longToString(longDate[i], FORMAT_DATE_TIME5));
            if (longDate[i] > lonnhat) {
                lonnhat = longDate[i];
            }
            if (longDate[i] < nhonhat) {
                nhonhat = longDate[i];
            }
        }
        return nhonhat;
    }

    public static long getMaxDate(long[] longDate) {
        long nhonhat = longDate[0];
        long lonnhat = longDate[0];
        for (int i = 1; i < longDate.length; i++) {
            System.out.println(longToString(longDate[i], FORMAT_DATE_TIME5));
            if (longDate[i] > lonnhat) {
                lonnhat = longDate[i];
            }
            if (longDate[i] < nhonhat) {
                nhonhat = longDate[i];
            }
        }
        return lonnhat;
    }

    public static Date toDate(Date source, String hms, Integer addMinute) {
        String[] arrHms = hms.split(":");
        Calendar cal = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
        cal.setTime(source);
        cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(arrHms[0]));
        cal.set(Calendar.MINUTE, Integer.valueOf(arrHms[0]));
        cal.set(Calendar.SECOND, Integer.valueOf(arrHms[0]));
        if (addMinute != null) {
            cal.add(Calendar.MINUTE, addMinute);
        }
        return cal.getTime();
    }


    public static boolean isFutureDate(Date transDate) {
        try {
            if (transDate == null) {
                return false;
            }
            Calendar cal = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date dateWithoutTime = cal.getTime();
            if (transDate.compareTo(dateWithoutTime) >= 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }


    public static Date toDate(Date source, Integer addMinute) {
        Calendar cal = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
        cal.setTime(source);
        if (addMinute != null) {
            cal.add(Calendar.MINUTE, addMinute);
        }
        return cal.getTime();
    }

    public static Date minusMonths(Date source, int month) {
        if (source == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance(DateTimeUtils.TIMEZONE_DEFAULT);
        cal.setTime(source);
        cal.set(Calendar.MONTH, month);
        return cal.getTime();
    }

    public static boolean checkFormatTimeHHMM(String time) {
        Pattern pattern = Pattern.compile(PATTERN_TIME);
        Matcher matcher = pattern.matcher(time);
        return matcher.matches();
    }

    public static long calcDaysBetween(String _date1, String _date2) {
        return ChronoUnit.DAYS.between(LocalDate.parse(_date1), LocalDate.parse(_date2)) + 1;
    }

    public static long calcMinuteBetween(Date _time1, Date _time2) {
        return Math.toIntExact(_time1.getTime() - _time2.getTime()) / 1000 / 60;
    }

    public static void main(String[] args) {
        System.out.println("calcDaysBetween: " + calcDaysBetween("2022-01-24", "2022-02-23"));
        System.out.println("calcDaysBetween: " + calcDaysBetween("2022-02-24", "2022-03-23"));
        System.out.println("calcDaysBetween: " + calcDaysBetween("2022-03-24", "2022-04-23"));
        System.out.println("calcDaysBetween: " + calcDaysBetween("2022-04-24", "2022-05-23"));
    }

}