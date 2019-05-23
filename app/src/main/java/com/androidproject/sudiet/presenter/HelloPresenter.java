
package com.androidproject.sudiet.presenter;

import android.text.TextUtils;

import com.androidproject.sudiet.db.DatabaseHandler;
import com.androidproject.sudiet.db.User;
import com.androidproject.sudiet.db.UserBuilder;
import com.androidproject.sudiet.view.HelloView;


public class HelloPresenter {
    private final DatabaseHandler dB;
    private final HelloView helloView;

    private int id;
    private String name;

    public HelloPresenter(final HelloView helloView, final DatabaseHandler dbHandler) {
        this.helloView = helloView;
        dB = dbHandler;
    }

    public void loadDatabase() {
        id = 1; // Id is always 1. We don't support multi-user (for now :D).
        name = "Test Account"; //TODO: add input for name in Tips;
    }

    public void onNextClicked(String age, String gender, String language, String country, int type, String unit) {
        if (validateAge(age)) {
            saveToDatabase(id, name, language, country, Integer.parseInt(age), gender, type, unit);
            helloView.startMainView();
        } else {
            helloView.displayErrorWrongAge();
        }
    }

    private boolean validateAge(String age) {
        if (TextUtils.isEmpty(age)) {
            return false;
        } else if (!TextUtils.isDigitsOnly(age)) {
            return false;
        } else {
            int finalAge = Integer.parseInt(age);
            return finalAge > 0 && finalAge < 120;
        }
    }

    private void saveToDatabase(final int id, final String name, final String language,
                                final String country, final int age, final String gender,
                                final int diabetesType, final String unitMeasurement) {
        User user = new UserBuilder()
                .setId(id)
                .setName(name)
                .setPreferredLanguage(language)
                .setCountry(country)
                .setAge(age)
                .setGender(gender)
                .setDiabetesType(diabetesType)
                .setPreferredUnit(unitMeasurement)
                .setPreferredA1CUnit("percentage")
                .setPreferredWeightUnit("kilograms")
                .setPreferredRange("ADA")
                .setMinRange(70)
                .setMaxRange(180)
                .createUser();
        dB.addUser(user); // We use ADA range by default
    }
}
