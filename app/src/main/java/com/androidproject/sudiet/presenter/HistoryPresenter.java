

package com.androidproject.sudiet.presenter;


import com.androidproject.sudiet.db.DatabaseHandler;
import com.androidproject.sudiet.fragment.HistoryFragment;

import java.util.List;

public class HistoryPresenter {

    private DatabaseHandler dB;
    private HistoryFragment fragment;

    public HistoryPresenter(HistoryFragment historyFragment) {
        this.fragment = historyFragment;
        dB = new DatabaseHandler(historyFragment.getContext());
    }

    public boolean isdbEmpty() {
        return dB.getGlucoseReadings().size() == 0;
    }


    public String convertDate(String date) {
        return fragment.convertDate(date);
    }

    public void onDeleteClicked(long idToDelete, int metricID) {
        switch (metricID) {
            // Glucose
            case 0:
                dB.deleteGlucoseReading(dB.getGlucoseReadingById(idToDelete));
                fragment.reloadFragmentAdapter();
                break;
            // HB1AC
            case 1:
                dB.deleteHB1ACReading(dB.getHB1ACReadingById(idToDelete));
                fragment.reloadFragmentAdapter();
                break;
            // Pressure
            case 3:
                dB.deletePressureReading(dB.getPressureReading(idToDelete));
                fragment.reloadFragmentAdapter();
                break;
            // Weight
            case 5:
                dB.deleteWeightReading(dB.getWeightReadingById(idToDelete));
                fragment.reloadFragmentAdapter();
                break;
            default:
                break;
        }
        fragment.notifyAdapter();
        fragment.updateToolbarBehaviour();
    }

    // Getters
    public String getUnitMeasuerement() {
        return dB.getUser(1).getPreferred_unit();
    }

    public String getWeightUnitMeasurement() {
        return dB.getUser(1).getPreferred_unit_weight();
    }

    public String getA1cUnitMeasurement() {
        return dB.getUser(1).getPreferred_unit_a1c();
    }

    public List<Long> getGlucoseId() {
        return dB.getGlucoseIdAsList();
    }

    public List<String> getGlucoseReadingType() {
        return dB.getGlucoseTypeAsList();
    }

    public List<String> getGlucoseNotes() {
        return dB.getGlucoseNotesAsList();
    }

    public List<Double> getGlucoseReading() {
        return dB.getGlucoseReadingAsList();
    }

    public List<String> getGlucoseDateTime() {
        return dB.getGlucoseDateTimeAsList();
    }

    public int getGlucoseReadingsNumber() {
        return dB.getGlucoseReadingAsList().size();
    }


    public List<Long> getHB1ACId() {
        return dB.getHB1ACIdAsArray();
    }

    public List<String> getHB1ACDateTime() {
        return dB.getHB1ACDateTimeAsArray();
    }

    public List<Double> getHB1ACReading() {
        return dB.getHB1ACReadingAsArray();
    }

    public int getHB1ACReadingsNumber() {
        return dB.getHB1ACReadingAsArray().size();
    }

    public int getPressureReadings() {
        return dB.getPressureReadings().size();
    }

    public List<String> getPressureDateTime() {
        return dB.getPressureDateTimeAsArray();
    }

    public List<Long> getPressureId() {
        return dB.getPressureIdAsArray();
    }

    public List<Double> getMinPressureReading() {
        return dB.getMinPressureReadingAsArray();
    }

    public List<Double> getMaxPressureReading() {
        return dB.getMaxPressureReadingAsArray();
    }

    public int getPressureReadingsNumber() {
        return dB.getPressureIdAsArray().size();
    }

    public List<Double> getWeightReadings() {
        return dB.getWeightReadingAsArray();
    }

    public List<String> getWeightDateTime() {
        return dB.getWeightReadingDateTimeAsArray();
    }

    public List<Long> getWeightId() {
        return dB.getWeightIdAsArray();
    }

    public int getWeightReadingsNumber() {
        return dB.getWeightIdAsArray().size();
    }
}
