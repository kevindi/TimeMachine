package com.di.kevin.timemachine.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dike on 25/5/2015.
 */
public class DateTimeUtil {

    public static final String DATE_TIME_FORMAT =  "yyyy-MM-dd hh:mm:ss";

    public static String dateToString(Date date, String formatString) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatString);
        return sdf.format(date);
    }
}
