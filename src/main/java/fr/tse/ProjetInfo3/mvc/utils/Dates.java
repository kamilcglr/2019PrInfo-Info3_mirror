package fr.tse.ProjetInfo3.mvc.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Dates {
    public static SimpleDateFormat frenchSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    public static SimpleDateFormat twitterRequestFormat = new SimpleDateFormat("YYYY-MM-DD");
    public static SimpleDateFormat hoursAndDateFormat = new SimpleDateFormat("dd/MM/YYYY à HH:mm:ss");

    /**
     * @return Returns a Date
     * @author Sergiy
     * {@code This method returns Date, created by adding time (in minutes) to an another Date}
     */
    public static Date addMinutesToDate(Date date, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);

        return calendar.getTime();
    }

    /**
     * @return Returns a Date
     * @author Sergiy
     * {@code This method returns Date within the interval list that is the closest to the parameter.
     * In other terms, it allows to place the tweets in clusters of time}
     */
    public static Date approximateInterval(List<Date> dateIntervals, Date tweetDate) {
        long minDifference = Long.MAX_VALUE;
        Date closestDate = null;

        for (Date date : dateIntervals) {
            long differenceInMillis = Math.abs(date.getTime() - tweetDate.getTime());

            if (differenceInMillis < minDifference) {
                minDifference = differenceInMillis;
                closestDate = date;
            }
        }
        return closestDate;
    }

    /**
     * @return Returns an integer
     * @author Sergiy
     * {@code This method calculates a difference (in minutes between two dates)}
     */
    public static int minutesDifference(Date start, Date end) {
        final int MILLIS_TO_HOUR = 1000 * 60;

        return (int) ((start.getTime() - end.getTime()) / MILLIS_TO_HOUR);
    }

    /**
     * @return Returns a Date
     * @author Sergiy
     * {@code This method rounds a Date so that everything below hours is rounded to 0}
     */
    public static Date roundDateToHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }
}
