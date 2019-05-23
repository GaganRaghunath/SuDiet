package com.androidproject.sudiet.activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.androidproject.sudiet.Constants;
import com.androidproject.sudiet.R;
import com.androidproject.sudiet.db.GlucoseReading;
import com.androidproject.sudiet.presenter.AddGlucosePresenter;
import com.androidproject.sudiet.tools.FormatDateTime;
import com.androidproject.sudiet.tools.GlucosioConverter;
import com.androidproject.sudiet.tools.LabelledSpinner;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;

import android.support.v7.widget.Toolbar;

public class AddGlucoseActivity extends AddReadingActivity {

    private static final int CUSTOM_TYPE_SPINNER_VALUE = 11;

    private TextView readingTextView;
    private EditText typeCustomEditText;
    private EditText notesEditText;
    private LabelledSpinner readingTypeSpinner;
    private boolean isCustomType = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_glucose);
        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(2);
        }

        this.retrieveExtra();

        AddGlucosePresenter presenter = new AddGlucosePresenter(this);
        setPresenter(presenter);
        presenter.setReadingTimeNow();

        readingTypeSpinner = findViewById(R.id.glucose_add_reading_type);
        readingTypeSpinner.setItemsArray(R.array.dialog_add_measured_list);
        readingTextView = findViewById(R.id.glucose_add_concentration);
        typeCustomEditText = findViewById(R.id.glucose_type_custom);
        notesEditText = findViewById(R.id.glucose_add_notes);

        this.createDateTimeViewAndListener();
        this.createFANViewAndListener();

        readingTypeSpinner.setOnItemChosenListener(new LabelledSpinner.OnItemChosenListener() {
            @Override
            public void onItemChosen(View labelledSpinner, AdapterView<?> adapterView, View itemView, int position, long id) {
                // If other is selected
                if (position == CUSTOM_TYPE_SPINNER_VALUE) {
                    typeCustomEditText.setVisibility(View.VISIBLE);
                    isCustomType = true;
                } else {
                    if (typeCustomEditText.getVisibility() == View.VISIBLE) {
                        typeCustomEditText.setVisibility(View.GONE);
                        isCustomType = false;
                    }
                }
            }

            @Override
            public void onNothingChosen(View labelledSpinner, AdapterView<?> adapterView) {

            }
        });

        TextView unitM = findViewById(R.id.glucose_add_unit_measurement);

        if (Constants.Units.MG_DL.equals(presenter.getUnitMeasurement())) {
            unitM.setText(getString(R.string.mg_dL));
        } else {
            unitM.setText(getString(R.string.mmol_L));
        }

        // If an id is passed, open the activity in edit mode
        Calendar cal = Calendar.getInstance();
        FormatDateTime dateTime = new FormatDateTime(getApplicationContext());
        if (this.isEditing()) {
            setTitle(R.string.title_activity_add_glucose_edit);
            GlucoseReading readingToEdit = presenter.getGlucoseReadingById(this.getEditId());

            String readingString;
            if (presenter.getUnitMeasurement().equals(Constants.Units.MG_DL)) {
                readingString = String.valueOf(numberFormat.format(readingToEdit.getReading()));
            } else {
                readingString = String.valueOf(numberFormat.format(GlucosioConverter.glucoseToMmolL(readingToEdit.getReading())));
            }

            readingTextView.setText(readingString);
            notesEditText.setText(readingToEdit.getNotes());
            cal.setTime(readingToEdit.getCreated());
            this.getAddDateTextView().setText(dateTime.getDate(cal));
            this.getAddTimeTextView().setText(dateTime.getTime(cal));
            presenter.updateReadingSplitDateTime(readingToEdit.getCreated());
            // retrieve spinner reading to set the registered one
            String measuredTypeText = readingToEdit.getReading_type();
            Integer measuredId = presenter.retrieveSpinnerID(measuredTypeText, Arrays.asList(getResources().getStringArray(R.array.dialog_add_measured_list)));
            if (measuredId == null) { // if nothing, it a custom type
                this.isCustomType = true;
                readingTypeSpinner.setSelection(CUSTOM_TYPE_SPINNER_VALUE);
            } else {
                readingTypeSpinner.setSelection(measuredId);
            }
            if (this.isCustomType) {
                typeCustomEditText.setText(measuredTypeText);
            }
        } else {
            this.getAddDateTextView().setText(dateTime.getDate(cal));
            this.getAddTimeTextView().setText(dateTime.getTime(cal));
            presenter.updateSpinnerTypeTime();
        }

        // Check if FreeStyle support is enabled in Preferences
    }

    @Override
    protected void dialogOnAddButtonPressed() {
        AddGlucosePresenter presenter = (AddGlucosePresenter) getPresenter();
        String readingType;
        if (isCustomType) {
            readingType = typeCustomEditText.getText().toString();
        } else {
            readingType = readingTypeSpinner.getSpinner().getSelectedItem().toString();
        }

        if (this.isEditing()) {
            presenter.dialogOnAddButtonPressed(this.getAddTimeTextView().getText().toString(),
                    this.getAddDateTextView().getText().toString(), readingTextView.getText().toString(),
                    readingType, notesEditText.getText().toString(), this.getEditId());
        } else {
            presenter.dialogOnAddButtonPressed(this.getAddTimeTextView().getText().toString(),
                    this.getAddDateTextView().getText().toString(), readingTextView.getText().toString(),
                    readingType, notesEditText.getText().toString());
        }
    }

    public void showErrorMessage() {
        View rootLayout = findViewById(android.R.id.content);
        Snackbar.make(rootLayout, getString(R.string.dialog_error2), Snackbar.LENGTH_SHORT).show();
    }


    public void showDuplicateErrorMessage() {
        View rootLayout = findViewById(android.R.id.content);
        Snackbar.make(rootLayout, getString(R.string.dialog_error_duplicate), Snackbar.LENGTH_SHORT).show();
    }

    public void updateSpinnerTypeTime(int selection) {
        readingTypeSpinner.setSelection(selection);
    }

    private void updateSpinnerTypeHour(int hour) {
        AddGlucosePresenter presenter = (AddGlucosePresenter) getPresenter();
        readingTypeSpinner.setSelection(presenter.hourToSpinnerType(hour));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        super.onTimeSet(view, hourOfDay, minute);
        DecimalFormat df = new DecimalFormat("00");
        updateSpinnerTypeHour(Integer.parseInt(df.format(hourOfDay)));
    }
}
