package fr.tse.ProjetInfo3.mvc.viewer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TwitterDateParser {
    public static String twitterFormat = "EEE MMM dd HH:mm:ss ZZZ yyyy";

    public static Date parseTwitterUTC(String date)
            throws ParseException {

        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZ yyyy";

        // Important note. Only ENGLISH Locale works.
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        return sf.parse(date);
    }
}