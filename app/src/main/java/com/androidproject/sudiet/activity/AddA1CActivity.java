package com.androidproject.sudiet.activity;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidproject.sudiet.R;
import com.androidproject.sudiet.db.HB1ACReading;
import com.androidproject.sudiet.presenter.AddA1CPresenter;
import com.androidproject.sudiet.tools.FormatDateTime;

import java.util.Calendar;


public class AddA1CActivity extends AddReadingActivity {

    private TextView readingTextView;
    private TextView unitTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hb1ac);
        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(2);
        }

        this.retrieveExtra();

        AddA1CPresenter presenter = new AddA1CPresenter(this);
        setPresenter(presenter);
        presenter.setReadingTimeNow();

        readingTextView = findViewById(R.id.hb1ac_add_value);
        unitTextView = findViewById(R.id.hb1ac_unit);

        this.createDateTimeViewAndListener();
        this.createFANViewAndListener();

        if (!"percentage".equals(presenter.getA1CUnitMeasuerement())) {
            unitTextView.setText(getString(R.string.mmol_mol));
        }

        // If an id is passed, open the activity in edit mode
        FormatDateTime formatDateTime = new FormatDateTime(getApplicationContext());
        if (this.isEditing()) {
            setTitle(R.string.title_activity_add_hb1ac_edit);
            HB1ACReading readingToEdit = presenter.getHB1ACReadingById(getEditId());
            readingTextView.setText(numberFormat.format(readingToEdit.getReading()));
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
        AddA1CPresenter presenter = (AddA1CPresenter) getPresenter();
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
