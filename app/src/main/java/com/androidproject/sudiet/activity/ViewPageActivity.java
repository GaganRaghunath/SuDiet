package com.androidproject.sudiet.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidproject.sudiet.R;
import com.androidproject.sudiet.adapter.ViewPagerAdapter;

public class ViewPageActivity extends AppCompatActivity {
    private LinearLayout linearLayout;
    private TextView[] mDots;
    private Button previous_btn;
    private Button next_btn;
    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            ToggleDotsIndicator(i);

            if (i == 0) {
                next_btn.setEnabled(true);
                previous_btn.setEnabled(false);
                previous_btn.setVisibility(View.INVISIBLE);
                next_btn.setText(getString(R.string.btnString_next));
                previous_btn.setText("");
            } else if (i == mDots.length - 1) {
                next_btn.setEnabled(true);
                previous_btn.setEnabled(true);
                previous_btn.setVisibility(View.VISIBLE);
                next_btn.setText(getString(R.string.btnString_finish));
                previous_btn.setText(getString(R.string.btnString_back));
            } else {
                next_btn.setEnabled(true);
                previous_btn.setEnabled(true);
                previous_btn.setVisibility(View.VISIBLE);
                next_btn.setText(getString(R.string.btnString_next));
                previous_btn.setText(getString(R.string.btnString_back));
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_page);
        ViewPager viewPager = findViewById(R.id.viewPager_main);
        linearLayout = findViewById(R.id.linearLayout_dot);
        previous_btn = findViewById(R.id.btn_prev);
        next_btn = findViewById(R.id.btn_next);

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewPageActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        previous_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        ViewPagerAdapter viewPageAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPageAdapter);

        addDotsIndicator();
        viewPager.addOnPageChangeListener(onPageChangeListener);
    }

    private void addDotsIndicator() {

        mDots = new TextView[3];
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(30);
            if (i == 0) {
                mDots[i].setTextColor(getResources().getColor(R.color.colorWhite));
            } else {
                mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));
            }
            mDots[i].setPadding(10, 10, 10, 10);
            linearLayout.addView(mDots[i]);
        }

    }

    private void ToggleDotsIndicator(int position) {
        if (mDots.length > 0) {
            for (TextView mdot : mDots) {
                mdot.setTextColor(getResources().getColor(R.color.colorTransparentWhite));
            }
            mDots[position].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }
}
