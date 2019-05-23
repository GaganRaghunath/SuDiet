

package com.androidproject.sudiet.presenter;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.androidproject.sudiet.Constants;
import com.androidproject.sudiet.activity.AddGlucoseActivity;
import com.androidproject.sudiet.db.DatabaseHandler;
import com.androidproject.sudiet.db.GlucoseReading;
import com.androidproject.sudiet.tools.GlucosioConverter;
import com.androidproject.sudiet.tools.ReadingTools;
import com.androidproject.sudiet.tools.SplitDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddGlucosePresenter extends AddReadingPresenter {
    private static final int UNKNOWN_ID = -1;
    private DatabaseHandler dB;
    private AddGlucoseActivity activity;
    private ReadingTools rTools;

    public AddGlucosePresenter(AddGlucoseActivity addGlucoseActivity) {
        this.activity = addGlucoseActivity;
        dB = new DatabaseHandler(addGlucoseActivity.getApplicationContext());
        rTools = new ReadingTools();
    }

    public void updateSpinnerTypeTime() {
        setReadingTimeNow();
        activity.updateSpinnerTypeTime(timeToSpinnerType());
    }

    private int timeToSpinnerType() {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date formatted = Calendar.getInstance().getTime();

        SplitDateTime addSplitDateTime = new SplitDateTime(formatted, inputFormat);
        int hour = Integer.parseInt(addSplitDateTime.getHour());

        return hourToSpinnerType(hour);
    }

    public int hourToSpinnerType(int hour) {
        return rTools.hourToSpinnerType(hour);
    }

    public void dialogOnAddButtonPressed(String time, String date, String reading, String type, String notes) {
        dialogOnAddButtonPressed(time, date, reading, type, notes, UNKNOWN_ID);
    }

    public void dialogOnAddButtonPressed(String time, String date, String reading, String type, String notes, long oldId) {
        if (validateDate(date) && validateTime(time) && validateGlucose(reading) && validateType(type)) {
            Date finalDateTime = getReadingTime();
            Number number = ReadingTools.parseReading(reading);
            if (number == null) {
                activity.showErrorMessage();
            } else {
                boolean isReadingAdded = createReading(type, notes, oldId, finalDateTime, number);
                if (!isReadingAdded) {
                    activity.showDuplicateErrorMessage();
                } else {
                    activity.finishActivity();
                }
            }
        } else {
            activity.showErrorMessage();
        }
    }

    private boolean createReading(String type, String notes, long oldId, Date finalDateTime, Number number) {
        boolean isReadingAdded;
        double readingValue;
        if (Constants.Units.MG_DL.equals(getUnitMeasurement())) {
            readingValue = number.doubleValue();
        } else {
            readingValue = GlucosioConverter.glucoseToMgDl(number.doubleValue());
        }
        GlucoseReading gReading = new GlucoseReading(readingValue, type, finalDateTime, notes);
        if (oldId == UNKNOWN_ID) {
            isReadingAdded = dB.addGlucoseReading(gReading);
        } else {
            isReadingAdded = dB.editGlucoseReading(oldId, gReading);
        }
        return isReadingAdded;
    }

    public Integer retrieveSpinnerID(String measuredTypeText, List<String> measuredTypelist) {
        int measuredId = 0;
        boolean isFound = false;
        for (String measuredType : measuredTypelist) {
            if (measuredType.equals(measuredTypeText)) {
                isFound = true;
                break;
            }
            measuredId++;
        }
        // if type is not found, it's return null
        return isFound ? measuredId : null;
    }

    public String getUnitMeasurement() {
        return dB.getUser(1).getPreferred_unit();
    }

    public GlucoseReading getGlucoseReadingById(Long id) {
        return dB.getGlucoseReadingById(id);
    }

    // Validator
    private boolean validateGlucose(String reading) {
        if (validateText(reading)) {
            if (Constants.Units.MG_DL.equals(getUnitMeasurement())) {
                // We store data in db in mg/dl
                Double readingValue = ReadingTools.safeParseDouble(reading);
                //TODO: Add custom ranges
                return readingValue > 19 && readingValue < 601;
            } else if (Constants.Units.MMOL_L.equals(getUnitMeasurement())) {
                // Convert mmol/L Unit
                Double readingValue = ReadingTools.safeParseDouble(reading);
                return readingValue > 1.0545 && readingValue < 33.3555;
            } else {
                // IT return always true: we don't have ranges yet.
                return true;
            }
        } else {
            return false;
        }
    }


    private boolean validateType(String type) {
        return validateText(type);
    }
}
