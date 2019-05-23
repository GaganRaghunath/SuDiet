package com.androidproject.sudiet.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.androidproject.sudiet.R;
import com.androidproject.sudiet.SuDietApplication;
import com.androidproject.sudiet.adapter.HomePagerAdapter;
import com.androidproject.sudiet.db.DatabaseHandler;
import com.androidproject.sudiet.presenter.MainPresenter;
import com.androidproject.sudiet.tools.LocaleHelper;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;


import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String INTENT_EXTRA_DROPDOWN = "history_dropdown";
    private static final int REQUEST_INVITE = 1;
    private static final String INTENT_EXTRA_PAGER = "pager";

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final String FROM_DATE_DIALOG_TAG = "fromDateDialog";

    private BottomSheetBehavior bottomSheetBehavior;
    private HomePagerAdapter homePagerAdapter;
    private MainPresenter presenter;
    private ViewPager viewPager;
    private BottomSheetDialog bottomSheetAddDialog;
    private View bottomSheetAddDialogView;
    private TabLayout tabLayout;
    private LocaleHelper localeHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SuDietApplication application = (SuDietApplication) getApplication();

        initPresenters(application);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        tabLayout = findViewById(R.id.activity_main_tab_layout);
        viewPager = findViewById(R.id.activity_main_pager);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setTitle("Sudiet");
        }

        homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), getApplicationContext());

        viewPager.setAdapter(homePagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    hideFabAnimation();
                    LinearLayout emptyLayout = findViewById(R.id.activity_main_empty_layout);
                    ViewPager pager = findViewById(R.id.activity_main_pager);
                    if (pager.getVisibility() == View.GONE) {
                        pager.setVisibility(View.VISIBLE);
                        emptyLayout.setVisibility(View.INVISIBLE);
                    }
                } else {
                    showFabAnimation();
                    checkIfEmptyLayout();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        FloatingActionButton fabAddReading = findViewById(R.id.activity_main_fab_add_reading);
        fabAddReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetAddDialog.show();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        bottomSheetAddDialog = new BottomSheetDialog(this);

        // Add Nav Drawer
        final PrimaryDrawerItem itemSettings = new PrimaryDrawerItem().withName(R.string.action_settings).withIcon(VectorDrawableCompat.create(getResources(), R.drawable.ic_settings_grey_24dp, null)).withSelectable(false).withTypeface(Typeface.DEFAULT_BOLD);
        final PrimaryDrawerItem itemExport = new PrimaryDrawerItem().withName(R.string.sidebar_backup_export).withIcon(VectorDrawableCompat.create(getResources(), R.drawable.ic_restaurant_black_24dp, null)).withSelectable(false).withTypeface(Typeface.DEFAULT_BOLD);
        final PrimaryDrawerItem itemDonate = new PrimaryDrawerItem().withName(R.string.about_donate).withIcon(VectorDrawableCompat.create(getResources(), R.drawable.ic_local_hospital_black_24dp, null)).withSelectable(false).withTypeface(Typeface.DEFAULT_BOLD);

        DrawerBuilder drawerBuilder = new DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withAccountHeader(new AccountHeaderBuilder()
                        .withActivity(this)
                        .withHeaderBackground(R.drawable.drawer_header)
                        .build()
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem.equals(itemSettings)) {
                            // Settings
                            openPreferences();
                        }  else if (drawerItem.equals(itemExport)) {
                            // Export
                            startExportActivity();
                        } else if (drawerItem.equals(itemDonate)) {
                            // Donate
                            openDonateIntent();
                        }
                        return false;
                    }
                });

            drawerBuilder.addDrawerItems(
                    itemExport,
                    itemSettings,
                    itemDonate
            )
                    .withSelectedItem(-1)
                    .build();

        // Restore pager position
        Bundle b = getIntent().getExtras();
        if (b != null) {
            viewPager.setCurrentItem(b.getInt("pager"));
        }

        checkIfEmptyLayout();
        bottomSheetAddDialog.setContentView(bottomSheetAddDialogView);
        bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetAddDialogView.getParent());
        bottomSheetBehavior.setHideable(false);

    }


    private void initPresenters(SuDietApplication application) {
        final DatabaseHandler dbHandler = application.getDBHandler();
        localeHelper = new LocaleHelper();
        presenter = new MainPresenter(this, dbHandler);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            } else {
                showExportPermissionError();
            }
        }
    }


    private void openDonateIntent() {
        Intent browserIntent = new Intent(this, MedicineActivity.class);
        startActivity(browserIntent);
    }

    public void startExportActivity() {
        Intent intent = new Intent(this, DietActivity.class);
        startActivity(intent);
        finish();
    }


    public void startHelloActivity() {
        Intent intent = new Intent(this, HelloActivity.class);
        startActivity(intent);
        finish();
    }

    public void openPreferences() {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
        finishActivity();
    }

    public void finishActivity() {
        // dismiss dialog if still expanded
        bottomSheetAddDialog.dismiss();
        // then close activity
        finish();
    }

    public void onGlucoseFabClicked(View v) {
        openNewAddActivity(AddGlucoseActivity.class);
    }


    public void onPressureFabClicked(View v) {
        openNewAddActivity(AddPressureActivity.class);
    }

    public void onHB1ACFabClicked(View v) {
        openNewAddActivity(AddA1CActivity.class);
    }



    public void onWeightFabClicked(View v) {
        openNewAddActivity(AddWeightActivity.class);
    }

    private void openNewAddActivity(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        // Pass pager position to open it again later
        Bundle b = new Bundle();
        b.putInt(INTENT_EXTRA_PAGER, viewPager.getCurrentItem());
        b.putInt(INTENT_EXTRA_DROPDOWN, homePagerAdapter.getHistoryFragment().getHistoryDropdownPosition());
        intent.putExtras(b);
        startActivity(intent);
        finishActivity();
    }

    public void openSupportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.menu_support_title));
        builder.setItems(getResources().getStringArray(R.array.menu_support_options), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Forum
                String url = "http://community.glucosio.org/";
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setPackage("com.android.chrome");
                try {
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    // Chrome is probably not installed
                    // Try with the default browser
                    i.setPackage(null);
                    startActivity(i);
                }
            }
        });
        builder.show();
    }



    public CoordinatorLayout getFabView() {
        return (CoordinatorLayout) findViewById(R.id.activity_main_coordinator_layout);
    }

    public void reloadFragmentAdapter() {
        homePagerAdapter.notifyDataSetChanged();
    }

    public void turnOffToolbarScrolling() {
        Toolbar mToolbar = findViewById(R.id.activity_main_toolbar);
        AppBarLayout appBarLayout = findViewById(R.id.activity_main_appbar_layout);

        //turn off scrolling
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
        toolbarLayoutParams.setScrollFlags(0);
        mToolbar.setLayoutParams(toolbarLayoutParams);

        CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        appBarLayoutParams.setBehavior(new AppBarLayout.Behavior());
        appBarLayout.setLayoutParams(appBarLayoutParams);
    }

    public void turnOnToolbarScrolling() {
        Toolbar mToolbar = findViewById(R.id.activity_main_toolbar);
        AppBarLayout appBarLayout = findViewById(R.id.activity_main_appbar_layout);

        //turn on scrolling
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
        toolbarLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        mToolbar.setLayoutParams(toolbarLayoutParams);

        CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        appBarLayoutParams.setBehavior(new AppBarLayout.Behavior());
        appBarLayout.setLayoutParams(appBarLayoutParams);
    }

    public Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.activity_main_toolbar);
    }

    public LocaleHelper getLocaleHelper() {
        return localeHelper;
    }

    private void hideFabAnimation() {
        final View fab = findViewById(R.id.activity_main_fab_add_reading);
        fab.animate()
                .translationY(-5)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        fab.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void showFabAnimation() {
        final View fab = findViewById(R.id.activity_main_fab_add_reading);
        if (fab.getVisibility() == View.INVISIBLE) {
            // Prepare the View for the animation
            fab.setVisibility(View.VISIBLE);
            fab.setAlpha(0.0f);

            fab.animate()
                    .alpha(1f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            fab.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            // do nothing
            // probably swiping from OVERVIEW to HISTORY tab
        }
    }


    public void checkIfEmptyLayout() {
        LinearLayout emptyLayout = findViewById(R.id.activity_main_empty_layout);
        ViewPager pager = findViewById(R.id.activity_main_pager);

        if (presenter.isdbEmpty()) {
            pager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);

            bottomSheetAddDialogView = getLayoutInflater().inflate(R.layout.fragment_add_bottom_dialog_disabled, null);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (getResources().getConfiguration().orientation == 1) {
                    // If Portrait choose vertical curved line
                    ImageView arrow = findViewById(R.id.activity_main_arrow);
                    arrow.setBackground(getResources().getDrawable(R.drawable.curved_line_vertical));
                } else {
                    // Else choose horizontal one
                    ImageView arrow = findViewById(R.id.activity_main_arrow);
                    arrow.setBackground((getResources().getDrawable(R.drawable.curved_line_horizontal)));
                }
            }
        } else {
            pager.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            bottomSheetAddDialogView = getLayoutInflater().inflate(R.layout.fragment_add_bottom_dialog, null);
        }
    }

    public void showExportedSnackBar(int nReadings) {
        View rootLayout = findViewById(android.R.id.content);
        Snackbar.make(rootLayout, getString(R.string.activity_export_snackbar_1) + " " + nReadings + " " + getString(R.string.activity_export_snackbar_2), Snackbar.LENGTH_SHORT).show();
    }

    public void showNoReadingsSnackBar() {
        View rootLayout = findViewById(android.R.id.content);
        Snackbar.make(rootLayout, getString(R.string.activity_export_no_readings_snackbar), Snackbar.LENGTH_SHORT).show();
    }

    public void showExportError() {
        View rootLayout = findViewById(android.R.id.content);
        Snackbar.make(rootLayout, getString(R.string.activity_export_issue_generic), Snackbar.LENGTH_SHORT).show();
    }

    public void showExportPermissionError() {
        View rootLayout = findViewById(android.R.id.content);
        Snackbar.make(rootLayout, getString(R.string.activity_export_issue_permissions), Snackbar.LENGTH_SHORT).show();
    }

    private void showSnackBar(String text, int lengthLong) {
        View rootLayout = findViewById(android.R.id.content);
        Snackbar.make(rootLayout, text, lengthLong).show();
    }

    public void showShareDialog(Uri uri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setData(uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("*/*");
        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_using)));
    }



    public void onA1cInfoClicked(View view) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(getString(R.string.overview_hb1ac_info))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .show();

    }



    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */



    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }
}