

package com.androidproject.sudiet.presenter;

import com.androidproject.sudiet.activity.AddPressureActivity;
import com.androidproject.sudiet.db.DatabaseHandler;
import com.androidproject.sudiet.db.PressureReading;
import com.androidproject.sudiet.tools.ReadingTools;

import java.util.Date;

public class AddPressurePresenter extends AddReadingPresenter {
    private DatabaseHandler dB;
    private AddPressureActivity activity;


    public AddPressurePresenter(AddPressureActivity addPressureActivity) {
        this.activity = addPressureActivity;
        dB = new DatabaseHandler(addPressureActivity.getApplicationContext());
    }

    public void dialogOnAddButtonPressed(String time, String date, String minReading, String maxReading) {
        if (validateDate(date) && validateTime(time) && validatePressure(minReading) && validatePressure(maxReading)) {
            PressureReading pReading = generatePressureReading(minReading, maxReading);
            dB.addPressureReading(pReading);
            activity.finishActivity();
        } else {
            activity.showErrorMessage();
        }
    }

    public void dialogOnAddButtonPressed(String time, String date, String minReading, String maxReading, long oldId) {
        if (validateDate(date) && validateTime(time) && validatePressure(minReading) && validatePressure(maxReading)) {
            PressureReading pReading = generatePressureReading(minReading, maxReading);
            dB.editPressureReading(oldId, pReading);
            activity.finishActivity();
        } else {
            activity.showErrorMessage();
        }
    }

    private PressureReading generatePressureReading(String minReading, String maxReading) {
        Date finalDateTime = getReadingTime();
        double minFinalReading = ReadingTools.safeParseDouble(minReading);
        double maxFinalReading = ReadingTools.safeParseDouble(maxReading);
        return new PressureReading(minFinalReading, maxFinalReading, finalDateTime);
    }

    // Getters and Setters

    public String getUnitMeasuerement() {
        return dB.getUser(1).getPreferred_unit();
    }

    public PressureReading getPressureReadingById(long editId) {
        return dB.getPressureReading(editId);
    }

    // Validator
    private boolean validatePressure(String reading) {
        return validateText(reading);
    }
}
