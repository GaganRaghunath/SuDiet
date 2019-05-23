package com.androidproject.sudiet.db;

import android.content.Context;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import net.danlew.android.joda.JodaTimeAndroid;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatabaseHandler {

    private static RealmConfiguration mRealmConfig;
    private Context mContext;
    private Realm realm;

    public DatabaseHandler(Context context) {
        this.mContext = context;
        Realm.init(context);
        this.realm = getNewRealmInstance();
    }

    public Realm getNewRealmInstance() {
        if (mRealmConfig == null) {
            mRealmConfig = new RealmConfiguration.Builder()
                    .schemaVersion(5)
                    .migration(new Migration())
                    .build();
        }
        return Realm.getInstance(mRealmConfig); // Automatically run migration if needed
    }

    public Realm getRealmInstance() {
        return realm;
    }

    public void addUser(User user) {
        realm.beginTransaction();
        realm.copyToRealm(user);
        realm.commitTransaction();
    }

    public User getUser(long id) {
        return realm.where(User.class)
                .equalTo("id", id)
                .findFirst();
    }

    public User getUser(Realm realm, long id) {
        return realm.where(User.class)
                .equalTo("id", id)
                .findFirst();
    }

    public void updateUser(User user) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(user);
        realm.commitTransaction();
    }




    public boolean addGlucoseReading(GlucoseReading reading) {
        // generate record Id
        String id = generateIdFromDate(reading.getCreated(), reading.getId());

        // Check for duplicates
        if (getGlucoseReadingById(Long.parseLong(id)) != null) {
            return false;
        } else {
            realm.beginTransaction();
            reading.setId(Long.parseLong(id));
            realm.copyToRealm(reading);
            realm.commitTransaction();
            return true;
        }
    }

    private String generateIdFromDate(Date created, long readingId) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(created);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        return "" + year + month + day + hours + minutes + readingId;
    }

    public void addNGlucoseReadings(int n) {
        for (int i = 0; i < n; i++) {
            Calendar calendar = Calendar.getInstance();
            GlucoseReading gReading = new GlucoseReading(50 + i, "Debug reading", calendar.getTime(), "");

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);
            String id = "" + year + month + day + hours + minutes + gReading.getReading();

            // Check for duplicates
            if (getGlucoseReadingById(Long.parseLong(id)) == null) {
                realm.beginTransaction();
                gReading.setId(Long.parseLong(id));
                realm.copyToRealm(gReading);
                realm.commitTransaction();
            }
        }
    }

    public boolean editGlucoseReading(long oldId, GlucoseReading reading) {
        // First delete the old reading
        deleteGlucoseReading(getGlucoseReadingById(oldId));
        // then save the new one
        return addGlucoseReading(reading);
    }

    public void deleteGlucoseReading(GlucoseReading reading) {
        realm.beginTransaction();
        reading.deleteFromRealm();
        realm.commitTransaction();
    }

    public GlucoseReading getLastGlucoseReading() {
        RealmResults<GlucoseReading> results =
                realm.where(GlucoseReading.class)
                        .sort(RealmField.CREATED.key(), Sort.DESCENDING)
                        .findAll();
        return results.get(0);
    }

    public List<GlucoseReading> getGlucoseReadings() {
        RealmResults<GlucoseReading> results =
                realm.where(GlucoseReading.class)
                        .sort(RealmField.CREATED.key(), Sort.DESCENDING)
                        .findAll();
        return new ArrayList<>(results);
    }

    public List<GlucoseReading> getGlucoseReadings(Realm realm) {
        RealmResults<GlucoseReading> results =
                realm.where(GlucoseReading.class)
                        .sort(RealmField.CREATED.key(), Sort.DESCENDING)
                        .findAll();
        return new ArrayList<>(results);
    }

    private ArrayList<GlucoseReading> getGlucoseReadings(Date from, Date to) {
        RealmResults<GlucoseReading> results =
                realm.where(GlucoseReading.class)
                        .between(RealmField.CREATED.key(), from, to)
                        .sort(RealmField.CREATED.key(), Sort.DESCENDING)
                        .findAll();
        return new ArrayList<>(results);
    }

    public List<GlucoseReading> getGlucoseReadings(Realm realm, Date from, Date to) {
        RealmResults<GlucoseReading> results =
                realm.where(GlucoseReading.class)
                        .between(RealmField.CREATED.key(), from, to)
                        .sort(RealmField.CREATED.key(), Sort.DESCENDING)
                        .findAll();
        return new ArrayList<>(results);
    }

    public GlucoseReading getGlucoseReadingById(long id) {
        return realm.where(GlucoseReading.class)
                .equalTo("id", id)
                .findFirst();
    }

    public List<Long> getGlucoseIdAsList() {
        List<GlucoseReading> glucoseReading = getGlucoseReadings();
        List<Long> idArray = new ArrayList<>(glucoseReading.size());

        for (GlucoseReading aGlucoseReading : glucoseReading) {
            long id = aGlucoseReading.getId();
            idArray.add(id);
        }

        return idArray;
    }

    public List<Double> getGlucoseReadingAsList() {
        List<GlucoseReading> glucoseReading = getGlucoseReadings();
        ArrayList<Double> readingArray = new ArrayList<>(glucoseReading.size());
        int i;

        for (i = 0; i < glucoseReading.size(); i++) {
            GlucoseReading singleReading = glucoseReading.get(i);
            double reading = singleReading.getReading();
            readingArray.add(reading);
        }

        return readingArray;
    }

    public List<String> getGlucoseTypeAsList() {
        List<GlucoseReading> glucoseReading = getGlucoseReadings();
        List<String> typeArray = new ArrayList<>(glucoseReading.size());

        for (GlucoseReading aGlucoseReading : glucoseReading) {
            String reading = aGlucoseReading.getReading_type();
            typeArray.add(reading);
        }

        return typeArray;
    }

    public List<String> getGlucoseNotesAsList() {
        List<GlucoseReading> glucoseReading = getGlucoseReadings();
        ArrayList<String> notesArray = new ArrayList<>(glucoseReading.size());

        for (GlucoseReading aGlucoseReading : glucoseReading) {
            String reading = aGlucoseReading.getNotes();
            notesArray.add(reading);
        }

        return notesArray;
    }

    public List<String> getGlucoseDateTimeAsList() {
        List<GlucoseReading> glucoseReading = getGlucoseReadings();
        ArrayList<String> datetimeArray = new ArrayList<>(glucoseReading.size());
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (GlucoseReading singleReading : glucoseReading) {
            String reading = inputFormat.format(singleReading.getCreated());
            datetimeArray.add(reading);
        }

        return datetimeArray;
    }

    public List<Double> getAverageGlucoseReadingsByWeek() {
        JodaTimeAndroid.init(mContext);

        DateTime maxDateTime = new DateTime(realm.where(GlucoseReading.class).maximumDate(RealmField.CREATED.key()).getTime());
        DateTime minDateTime = new DateTime(realm.where(GlucoseReading.class).minimumDate(RealmField.CREATED.key()).getTime());

        DateTime currentDateTime = minDateTime;
        DateTime newDateTime;

        int weeksNumber = Weeks.weeksBetween(minDateTime, maxDateTime).getWeeks();
        List<Double> averageReadings = new ArrayList<>(weeksNumber);

        // The number of weeks is at least 1 since we do have average for the current week even if incomplete
        for (int i = 0; i < weeksNumber + 1; i++) {
            newDateTime = currentDateTime.plusWeeks(1);
            RealmResults<GlucoseReading> readings = realm.where(GlucoseReading.class)
                    .between(RealmField.CREATED.key(), currentDateTime.toDate(), newDateTime.toDate())
                    .findAll();
            averageReadings.add(readings.average("reading"));
            currentDateTime = newDateTime;
        }
        return averageReadings;
    }

    public List<String> getGlucoseDatetimesByWeek() {
        JodaTimeAndroid.init(mContext);

        DateTime maxDateTime = new DateTime(realm.where(GlucoseReading.class).maximumDate(RealmField.CREATED.key()).getTime());
        DateTime minDateTime = new DateTime(realm.where(GlucoseReading.class).minimumDate(RealmField.CREATED.key()).getTime());

        DateTime currentDateTime = minDateTime;
        DateTime newDateTime;

        List<String> finalWeeks = new ArrayList<>();

        // The number of weeks is at least 1 since we do have average for the current week even if incomplete
        int weeksNumber = Weeks.weeksBetween(minDateTime, maxDateTime).getWeeks() + 1;

        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (int i = 0; i < weeksNumber; i++) {
            newDateTime = currentDateTime.plusWeeks(1);
            finalWeeks.add(inputFormat.format(newDateTime.toDate()));
            currentDateTime = newDateTime;
        }
        return finalWeeks;
    }

    public List<Double> getAverageGlucoseReadingsByMonth() {
        JodaTimeAndroid.init(mContext);

        DateTime maxDateTime = new DateTime(realm.where(GlucoseReading.class).maximumDate(RealmField.CREATED.key()).getTime());
        DateTime minDateTime = new DateTime(realm.where(GlucoseReading.class).minimumDate(RealmField.CREATED.key()).getTime());

        DateTime currentDateTime = minDateTime;
        DateTime newDateTime;

        int months = Months.monthsBetween(minDateTime, maxDateTime).getMonths();
        List<Double> averageReadings = new ArrayList<>(months);

        // The number of months is at least 1 since we do have average for the current week even if incomplete
        int monthsNumber = months + 1;

        for (int i = 0; i < monthsNumber; i++) {
            newDateTime = currentDateTime.plusMonths(1);
            RealmResults<GlucoseReading> readings = realm.where(GlucoseReading.class)
                    .between(RealmField.CREATED.key(), currentDateTime.toDate(), newDateTime.toDate())
                    .findAll();
            averageReadings.add(readings.average("reading"));
            currentDateTime = newDateTime;
        }
        return averageReadings;
    }

    public List<String> getGlucoseDatetimesByMonth() {
        JodaTimeAndroid.init(mContext);

        DateTime maxDateTime = new DateTime(realm.where(GlucoseReading.class).maximumDate(RealmField.CREATED.key()).getTime());
        DateTime minDateTime = new DateTime(realm.where(GlucoseReading.class).minimumDate(RealmField.CREATED.key()).getTime());

        DateTime currentDateTime = minDateTime;
        DateTime newDateTime;

        ArrayList<String> finalMonths = new ArrayList<>();

        // The number of months is at least 1 because current month is incomplete
        int monthsNumber = Months.monthsBetween(minDateTime, maxDateTime).getMonths() + 1;

        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (int i = 0; i < monthsNumber; i++) {
            newDateTime = currentDateTime.plusMonths(1);
            finalMonths.add(inputFormat.format(newDateTime.toDate()));
            currentDateTime = newDateTime;
        }
        return finalMonths;
    }

    public List<GlucoseReading> getLastMonthGlucoseReadings() {
        JodaTimeAndroid.init(mContext);

        DateTime todayDateTime = DateTime.now();
        DateTime minDateTime = DateTime.now().minusMonths(1).minusDays(15);

        return getGlucoseReadings(minDateTime.toDate(), todayDateTime.toDate());
    }

    public void addHB1ACReading(HB1ACReading reading) {
        realm.beginTransaction();
        reading.setId(getNextKey("hb1ac"));
        realm.copyToRealm(reading);
        realm.commitTransaction();
    }

    public void deleteHB1ACReading(HB1ACReading reading) {
        realm.beginTransaction();
        reading.deleteFromRealm();
        realm.commitTransaction();
    }

    public HB1ACReading getHB1ACReadingById(long id) {
        return realm.where(HB1ACReading.class)
                .equalTo("id", id)
                .findFirst();
    }

    public void editHB1ACReading(long oldId, HB1ACReading reading) {
        // First delete the old reading
        deleteHB1ACReading(getHB1ACReadingById(oldId));
        // then save the new one
        addHB1ACReading(reading);
    }

    public RealmResults<HB1ACReading> getrHB1ACRawReadings() {
        return realm.where(HB1ACReading.class)
                .sort(RealmField.CREATED.key(), Sort.DESCENDING)
                .findAll();
    }

    public List<HB1ACReading> getHB1ACReadings() {
        RealmResults<HB1ACReading> results =
                realm.where(HB1ACReading.class)
                        .sort(RealmField.CREATED.key(), Sort.DESCENDING)
                        .findAll();

        return new ArrayList<>(results);
    }

    public List<Long> getHB1ACIdAsArray() {
        List<HB1ACReading> readings = getHB1ACReadings();
        ArrayList<Long> idArray = new ArrayList<>();
        int i;

        for (i = 0; i < readings.size(); i++) {
            long id;
            HB1ACReading singleReading = readings.get(i);
            id = singleReading.getId();
            idArray.add(id);
        }

        return idArray;
    }

    public List<Double> getHB1ACReadingAsArray() {
        List<HB1ACReading> readings = getHB1ACReadings();
        ArrayList<Double> readingArray = new ArrayList<>();
        int i;

        for (i = 0; i < readings.size(); i++) {
            double reading;
            HB1ACReading singleReading = readings.get(i);
            reading = singleReading.getReading();
            readingArray.add(reading);
        }

        return readingArray;
    }

    public List<String> getHB1ACDateTimeAsArray() {
        List<HB1ACReading> readings = getHB1ACReadings();
        ArrayList<String> datetimeArray = new ArrayList<>();
        int i;
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (i = 0; i < readings.size(); i++) {
            String reading;
            HB1ACReading singleReading = readings.get(i);
            reading = inputFormat.format(singleReading.getCreated());
            datetimeArray.add(reading);
        }

        return datetimeArray;
    }




    public void addPressureReading(PressureReading reading) {
        realm.beginTransaction();
        reading.setId(getNextKey("pressure"));
        realm.copyToRealm(reading);
        realm.commitTransaction();
    }

    public PressureReading getPressureReading(long id) {
        return realm.where(PressureReading.class)
                .equalTo("id", id)
                .findFirst();
    }

    public void deletePressureReading(PressureReading reading) {
        realm.beginTransaction();
        reading.deleteFromRealm();
        realm.commitTransaction();
    }

    public void editPressureReading(long oldId, PressureReading reading) {
        // First delete the old reading
        deletePressureReading(getPressureReading(oldId));
        // then save the new one
        addPressureReading(reading);
    }

    public List<PressureReading> getPressureReadings() {
        RealmResults<PressureReading> results =
                realm.where(PressureReading.class)
                        .sort(RealmField.CREATED.key(), Sort.DESCENDING)
                        .findAll();

        return new ArrayList<>(results);
    }

    public List<Long> getPressureIdAsArray() {
        List<PressureReading> readings = getPressureReadings();
        ArrayList<Long> idArray = new ArrayList<>();
        int i;

        for (i = 0; i < readings.size(); i++) {
            long id;
            PressureReading singleReading = readings.get(i);
            id = singleReading.getId();
            idArray.add(id);
        }

        return idArray;
    }

    public List<Double> getMinPressureReadingAsArray() {
        List<PressureReading> readings = getPressureReadings();
        ArrayList<Double> readingArray = new ArrayList<>(readings.size());
        int i;

        for (i = 0; i < readings.size(); i++) {
            PressureReading singleReading = readings.get(i);
            double reading = singleReading.getMinReading();
            readingArray.add(reading);
        }

        return readingArray;
    }

    public List<Double> getMaxPressureReadingAsArray() {
        List<PressureReading> readings = getPressureReadings();
        ArrayList<Double> readingArray = new ArrayList<>(readings.size());
        int i;

        for (i = 0; i < readings.size(); i++) {
            PressureReading singleReading = readings.get(i);
            double reading = singleReading.getMaxReading();
            readingArray.add(reading);
        }

        return readingArray;
    }

    public List<String> getPressureDateTimeAsArray() {
        List<PressureReading> readings = getPressureReadings();
        ArrayList<String> datetimeArray = new ArrayList<>();
        int i;
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (i = 0; i < readings.size(); i++) {
            String reading;
            PressureReading singleReading = readings.get(i);
            reading = inputFormat.format(singleReading.getCreated());
            datetimeArray.add(reading);
        }

        return datetimeArray;
    }

    public void addWeightReading(WeightReading reading) {
        realm.beginTransaction();
        reading.setId(getNextKey("weight"));
        realm.copyToRealm(reading);
        realm.commitTransaction();
    }

    public void editWeightReading(long oldId, WeightReading reading) {
        // First delete the old reading
        deleteWeightReading(getWeightReadingById(oldId));
        // then save the new one
        addWeightReading(reading);
    }

    public WeightReading getWeightReadingById(long id) {
        return realm.where(WeightReading.class)
                .equalTo("id", id)
                .findFirst();
    }

    public void deleteWeightReading(WeightReading reading) {
        realm.beginTransaction();
        reading.deleteFromRealm();
        realm.commitTransaction();
    }

    public List<WeightReading> getWeightReadings() {
        RealmResults<WeightReading> results =
                realm.where(WeightReading.class)
                        .sort(RealmField.CREATED.key(), Sort.DESCENDING)
                        .findAll();

        return new ArrayList<>(results);
    }

    public List<Long> getWeightIdAsArray() {
        List<WeightReading> readings = getWeightReadings();
        ArrayList<Long> idArray = new ArrayList<>();
        int i;

        for (i = 0; i < readings.size(); i++) {
            long id;
            WeightReading singleReading = readings.get(i);
            id = singleReading.getId();
            idArray.add(id);
        }

        return idArray;
    }

    public List<Double> getWeightReadingAsArray() {
        List<WeightReading> readings = getWeightReadings();
        ArrayList<Double> readingArray = new ArrayList<>(readings.size());
        int i;

        for (i = 0; i < readings.size(); i++) {
            WeightReading singleReading = readings.get(i);
            double reading = singleReading.getReading();
            readingArray.add(reading);
        }

        return readingArray;
    }

    public List<String> getWeightReadingDateTimeAsArray() {
        List<WeightReading> readings = getWeightReadings();
        ArrayList<String> datetimeArray = new ArrayList<>();
        int i;
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (i = 0; i < readings.size(); i++) {
            String reading;
            WeightReading singleReading = readings.get(i);
            reading = inputFormat.format(singleReading.getCreated());
            datetimeArray.add(reading);
        }

        return datetimeArray;
    }




    private long getNextKey(String where) {
        Number maxId = null;
        switch (where) {
            case "glucose":
                maxId = realm.where(GlucoseReading.class)
                        .max("id");
                break;
            case "weight":
                maxId = realm.where(WeightReading.class)
                        .max("id");
                break;
            case "hb1ac":
                maxId = realm.where(HB1ACReading.class)
                        .max("id");
                break;
            case "pressure":
                maxId = realm.where(PressureReading.class)
                        .max("id");
                break;
        }
        if (maxId == null) {
            return 0;
        } else {
            return Long.parseLong(maxId.toString()) + 1;
        }
    }
}
