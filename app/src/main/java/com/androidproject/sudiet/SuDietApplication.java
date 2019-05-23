package com.androidproject.sudiet;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.androidproject.sudiet.activity.HelloActivity;
import com.androidproject.sudiet.db.DatabaseHandler;
import com.androidproject.sudiet.db.User;
import com.androidproject.sudiet.presenter.HelloPresenter;
import com.androidproject.sudiet.tools.LocaleHelper;
import com.androidproject.sudiet.tools.Preferences;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class SuDietApplication extends Application {
    private static SuDietApplication sInstance;

    @Nullable
    private LocaleHelper localeHelper;

    @Nullable
    private Preferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        initFont();
        initLanguage();
    }

    @VisibleForTesting
    protected void initFont() {
        //TODO: convert of using new introduced class Preferences
        // Get Dyslexia preference and adjust font
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDyslexicModeOn = sharedPref.getBoolean("pref_font_dyslexia", false);

        if (isDyslexicModeOn) {
            setFont("fonts/opendyslexic.otf");
        } else {
            setFont("fonts/lato.ttf");
        }
    }

    @VisibleForTesting
    protected void initLanguage() {
        User user = getDBHandler().getUser(1);
        if (user != null) {
            checkBadLocale(user);

            String languageTag = user.getPreferred_language();
            if (languageTag != null) {
                getLocaleHelper().updateLanguage(this, languageTag);
            }
        }
    }

    private void checkBadLocale(User user) {
        Preferences preferences = getPreferences();
        boolean cleanLocaleDone = preferences.isLocaleCleaned();

        if (!cleanLocaleDone) {
            User updatedUser = new User(user);
            updatedUser.setPreferred_language(null);
            //TODO: is it long operation? should we move it to separate thread?
            getDBHandler().updateUser(updatedUser);
            preferences.saveLocaleCleaned();
        }
    }

    private void setFont(String font) {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(font)
                .setFontAttrId(R.attr.fontPath)
                .build());
    }





    @NonNull
    public DatabaseHandler getDBHandler() {
        return new DatabaseHandler(getApplicationContext());
    }



    @NonNull
    public LocaleHelper getLocaleHelper() {
        if (localeHelper == null) {
            localeHelper = new LocaleHelper();
        }
        return localeHelper;
    }

    @NonNull
    public Preferences getPreferences() {
        if (preferences == null) {
            preferences = new Preferences(this);
        }

        return preferences;
    }

    public static SuDietApplication getInstance() {
        if (sInstance == null) {
            sInstance = new SuDietApplication();
        }
        return sInstance;
    }

    @NonNull
    public HelloPresenter createHelloPresenter(@NonNull final HelloActivity activity) {
        return new HelloPresenter(activity, getDBHandler());
    }
}
