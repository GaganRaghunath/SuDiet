package com.androidproject.sudiet.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidproject.sudiet.R;
import com.androidproject.sudiet.db.WeightReading;
import com.androidproject.sudiet.presenter.AddWeightPresenter;
import com.androidproject.sudiet.tools.FormatDateTime;
import com.androidproject.sudiet.tools.GlucosioConverter;

import java.util.Calendar;


public class AddWeightActivity extends AddReadingActivity {

    private TextView readingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean needUnitConversion = false;

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_weight);
        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(2);
        }

        this.retrieveExtra();

        AddWeightPresenter presenter = new AddWeightPresenter(this);
        this.setPresenter(presenter);
        presenter.setReadingTimeNow();

        readingTextView = findViewById(R.id.weight_add_value);
        TextView unitTextView = findViewById(R.id.weight_add_unit_measurement);

        this.createDateTimeViewAndListener();
        this.createFANViewAndListener();

        if (!"kilograms".equals(presenter.getWeightUnitMeasuerement())) {
            unitTextView.setText("lbs");
            needUnitConversion = true;
        }

        // If an id is passed, open the activity in edit mode
        FormatDateTime formatDateTime = new FormatDateTime(getApplicationContext());
        if (this.isEditing()) {
            setTitle(R.string.title_activity_add_weight_edit);
            WeightReading readingToEdit = presenter.getWeightReadingById(this.getEditId());
            double weightVal = readingToEdit.getReading();
            if (needUnitConversion)
                weightVal = GlucosioConverter.kgToLb(weightVal);
            readingTextView.setText(numberFormat.format(weightVal));
            Calendar cal = Calendar.getInstance();
            cal.setTime(readingToEdit.getCreated());
            this.getAddDateTextView().setText(formatDateTime.getDate(cal));
            this.getAddTimeTextView().setText(formatDateTime.getTime(cal));
            presenter.updateReadingSplitDateTime(readingToEdit.getCreated());
        } else {
            this.getAddDateTextView().setText(formatDateTime.getCurrentDate());
            this.getAddTimeTextView().setText(formatDateTime.getCurrentTime());
        }

    }

    @Override
    protected void dialogOnAddButtonPressed() {
        AddWeightPresenter presenter = (AddWeightPresenter) this.getPresenter();
        if (this.isEditing()) {
            presenter.dialogOnAddButtonPressed(this.getAddTimeTextView().getText().toString(),
                    this.getAddDateTextView().getText().toString(), readingTextView.getText().toString(), this.getEditId());
        } else {
            presenter.dialogOnAddButtonPressed(this.getAddTimeTextView().getText().toString(),
                    this.getAddDateTextView().getText().toString(), readingTextView.getText().toString());

        }
    }

    public void showErrorMessage() {
        Toast.makeText(getApplicationContext(), getString(R.string.dialog_error2), Toast.LENGTH_SHORT).show();
    }
}