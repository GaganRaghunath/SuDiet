

package com.androidproject.sudiet.presenter;


import com.androidproject.sudiet.activity.AddA1CActivity;
import com.androidproject.sudiet.db.DatabaseHandler;
import com.androidproject.sudiet.db.HB1ACReading;
import com.androidproject.sudiet.tools.GlucosioConverter;
import com.androidproject.sudiet.tools.ReadingTools;

import java.util.Date;

public class AddA1CPresenter extends AddReadingPresenter {
    private DatabaseHandler dB;
    private AddA1CActivity activity;

    public AddA1CPresenter(AddA1CActivity addA1CActivity) {
        this.activity = addA1CActivity;
        dB = new DatabaseHandler(addA1CActivity.getApplicationContext());
    }

    public void dialogOnAddButtonPressed(String time, String date, String reading) {
        if (validateDate(date) && validateTime(time) && validateA1C(reading)) {

            HB1ACReading hReading = generateHB1ACReading(reading);
            dB.addHB1ACReading(hReading);

            activity.finishActivity();
        } else {
            activity.showErrorMessage();
        }
    }

    public void dialogOnAddButtonPressed(String time, String date, String reading, long oldId) {
        if (validateDate(date) && validateTime(time) && validateText(reading)) {

            HB1ACReading hReading = generateHB1ACReading(reading);
            dB.editHB1ACReading(oldId, hReading);

            activity.finishActivity();
        } else {
            activity.showErrorMessage();
        }
    }

    private HB1ACReading generateHB1ACReading(String reading) {
        Date finalDateTime = getReadingTime();

        double finalReading;
        if ("percentage".equals(getA1CUnitMeasuerement())) {
            finalReading = ReadingTools.safeParseDouble(reading);
        } else {
            finalReading = GlucosioConverter.a1cIfccToNgsp(ReadingTools.safeParseDouble(reading));
        }

        return new HB1ACReading(finalReading, finalDateTime);
    }

    public String getA1CUnitMeasuerement() {
        return dB.getUser(1).getPreferred_unit_a1c();
    }

    public HB1ACReading getHB1ACReadingById(Long id) {
        return dB.getHB1ACReadingById(id);
    }

    // Validator
    private boolean validateA1C(String reading) {
        return validateText(reading);
    }
}
