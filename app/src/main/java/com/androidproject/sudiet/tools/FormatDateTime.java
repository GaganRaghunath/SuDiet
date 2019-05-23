

package com.androidproject.sudiet.tools;

import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FormatDateTime {

    private Context context;

    public FormatDateTime(Context mContext) {
        this.context = mContext;
    }

    public String convertDateTime(String date) {
        //TODO use joda.time
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DateFormat finalDataFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        DateFormat finalTimeFormat;

        if (android.text.format.DateFormat.is24HourFormat(context)) {
            finalTimeFormat = new SimpleDateFormat("HH:mm");
        } else {
            finalTimeFormat = new SimpleDateFormat("hh:mm a");
        }

        Date parsed = null;
        try {
            parsed = inputFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String finalData = finalDataFormat.format(parsed);
        String finalTime = finalTimeFormat.format(parsed);
        return finalData + " " + finalTime;
    }

    public String formatDate(Date date) {
        DateFormat finalDataFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        DateFormat finalTimeFormat;

        if (android.text.format.DateFormat.is24HourFormat(context)) {
            finalTimeFormat = new SimpleDateFormat("HH:mm");
        } else {
            finalTimeFormat = new SimpleDateFormat("hh:mm a");
        }

        String finalData = finalDataFormat.format(date);
        String finalTime = finalTimeFormat.format(date);
        return finalData + " " + finalTime;
    }

    public String convertDateToMonthOverview(String date) {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DateFormat finalDataFormat = new SimpleDateFormat("MMMM");

        Date parsed = null;
        try {
            parsed = inputFormat.parse(date);
            // Because database's average is the end of the month
            // we need to remove 1 month from final date
            Calendar cal = Calendar.getInstance();
            cal.setTime(parsed);
            cal.add(Calendar.MONTH, -1);
            parsed = cal.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String finalData = finalDataFormat.format(parsed);
        return finalData + " ";
    }


    public String convertDate(String datetime) {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat finalDataFormat = DateFormat.getDateInstance(DateFormat.SHORT);

        Date parsed = null;
        try {
            parsed = inputFormat.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String finalData = finalDataFormat.format(parsed);
        return finalData + "";
    }

    public String convertRawDate(Date datetime) {
        DateFormat finalDataFormat = DateFormat.getDateInstance(DateFormat.SHORT);

        return finalDataFormat.format(datetime);
    }

    public String convertRawTime(Date datetime) {
        DateFormat finalTimeFormat;

        if (android.text.format.DateFormat.is24HourFormat(context)) {
            finalTimeFormat = new SimpleDateFormat("HH:mm");
        } else {
            finalTimeFormat = new SimpleDateFormat("hh:mm a");
        }

        return finalTimeFormat.format(datetime);
    }

    public String getCurrentTime() {
        Calendar cal = Calendar.getInstance();

        java.text.DateFormat finalTimeFormat;

        finalTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);


        String finalTime = finalTimeFormat.format(cal.getTime());
        return finalTime + "";
    }

    public String getCurrentDate() {
        Calendar cal = Calendar.getInstance();

        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);

        String finalTime = dateFormat.format(cal.getTime());
        return finalTime + "";
    }

    public String getTime(Calendar cal) {
        java.text.DateFormat finalTimeFormat;

        finalTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);


        String finalTime = finalTimeFormat.format(cal.getTime());
        return finalTime + "";
    }

    public String getDate(Calendar cal) {
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);

        String finalTime = dateFormat.format(cal.getTime());
        return finalTime + "";
    }

}
