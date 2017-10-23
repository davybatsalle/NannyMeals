package com.batsalle.nannymeals.activity.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.batsalle.nannymeals.R;
import com.batsalle.nannymeals.activity.meallist.ListActivity;
import com.batsalle.nannymeals.activity.stats.StatsActivity;
import com.batsalle.nannymeals.meal.MealContent;

import java.text.ParseException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String mSelectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrypoint);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMealItem();
                Toast.makeText(MainActivity.this, R.string.saved, Toast.LENGTH_SHORT).show();

            }
        });


        CalendarView mealDateBox = (CalendarView) findViewById(R.id.mealDate);
        mealDateBox.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                loadMealItem(year,month,dayOfMonth);
            }
        });

        MealContent.init(getApplicationContext());

        // init autocomplete fields
        initAutocompleteTextEdits();

        // load today's meal
        loadMealItem(mealDateBox.getDate());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

       return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_listmeals) {
            startListActivity();
        } else if (id == R.id.nav_stats) {
            startStatsActivity();
        } /*else if (id == R.id.nav_settings) {
            startSettingsActivity();
        }*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initAutocompleteTextEdits() {
        AutoCompleteTextView platBox = (AutoCompleteTextView) findViewById(R.id.plat);
        AutoCompleteTextView fromageBox = (AutoCompleteTextView) findViewById(R.id.fromage);
        AutoCompleteTextView dessertBox = (AutoCompleteTextView) findViewById(R.id.dessert);
        AutoCompleteTextView gouterBox = (AutoCompleteTextView) findViewById(R.id.gouter);

        platBox.setThreshold(3);
        fromageBox.setThreshold(3);
        dessertBox.setThreshold(3);
        gouterBox.setThreshold(3);

        platBox.setAdapter(new MealArrayAdapter(MainActivity.this,android.R.layout.simple_dropdown_item_1line,MealContent.itemList(),"plat"));
        fromageBox.setAdapter(new MealArrayAdapter(MainActivity.this,android.R.layout.simple_dropdown_item_1line,MealContent.itemList(),"fromage"));
        dessertBox.setAdapter(new MealArrayAdapter(MainActivity.this,android.R.layout.simple_dropdown_item_1line,MealContent.itemList(),"dessert"));
        gouterBox.setAdapter(new MealArrayAdapter(MainActivity.this,android.R.layout.simple_dropdown_item_1line,MealContent.itemList(),"gouter"));
    }

    @Override
    protected void onDestroy() {
        MealContent.close();
        super.onDestroy();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            deleteSelectedMeal();
            return true;
        } else if (id == R.id.action_delete_all) {
            deleteAllMeals();
            return true;
        } else if (id == R.id.action_share) {
            shareSelectedMeal();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void deleteAllMeals() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.deleteAll)
                .setMessage(R.string.deleteConfirm)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        MealContent.removeAllItems();
                        Toast.makeText(MainActivity.this, R.string.Removed, Toast.LENGTH_SHORT).show();
                        loadMealFromItem(null);
                    }})
                .setNegativeButton(android.R.string.no, null).show();}

    private void deleteSelectedMeal() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.deleteSingle)
                .setMessage(R.string.deleteConfirm)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        MealContent.removeItem(mSelectedDate);
                        Toast.makeText(MainActivity.this, R.string.Removed, Toast.LENGTH_SHORT).show();
                        loadMealFromItem(null);
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }


    private void shareSelectedMeal() {
        MealContent.MealItem meal = MealContent.getItem(mSelectedDate);
        if (meal == null || meal.getDetails().length() == 0) {
            return;
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);

        String date = "";
        try {
            if (meal.isToday()) {
                date = getString(R.string.shareBodyStart) + MealContent.MealItem.beautifyDate(meal.date) + "), ";
            } else {
                date = getString(R.string.shareBodyStartAny) + MealContent.MealItem.beautifyDate(meal.date) + ", ";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String extra = date+getString(R.string.shareBody)+"\n";
        if (meal.plat.length() > 0) {
            extra += "- "+getString(R.string.mainCourse_dropDown)+" : "+meal.plat+"\n";
        }
        if (meal.fromage.length() > 0) {
            extra+= "- "+getString(R.string.cheese_dropDown) +" : " + meal.fromage+"\n";
        }
        if (meal.dessert.length() > 0) {
            extra += "- "+getString(R.string.pudding_dropDown)+" : " + meal.dessert+"\n";
        }
        if (meal.gouter.length() > 0) {
            extra += "- "+getString(R.string.snck_dropDown)+" : " + meal.gouter;
        }
        intent.putExtra(Intent.EXTRA_TEXT, extra);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }

    private boolean startListActivity() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);

        return true;
    }

    private boolean startStatsActivity() {
        Intent intent = new Intent(this, StatsActivity.class);
        startActivity(intent);

        return true;
    }


    private void saveMealItem() {
        AutoCompleteTextView platBox = (AutoCompleteTextView) findViewById(R.id.plat);
        String plat = platBox.getText().toString();

        AutoCompleteTextView fromageBox = (AutoCompleteTextView) findViewById(R.id.fromage);
        String fromage = fromageBox.getText().toString();

        AutoCompleteTextView dessertBox = (AutoCompleteTextView) findViewById(R.id.dessert);
        String dessert = dessertBox.getText().toString();

        AutoCompleteTextView gouterBox = (AutoCompleteTextView) findViewById(R.id.gouter);
        String gouter = gouterBox.getText().toString();

        MealContent.MealItem item = new MealContent.MealItem(this.mSelectedDate,plat,fromage,dessert,gouter);

        MealContent.editOrAddItem(item);

        closeKeyboard();
    }

    private void loadMealItem(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        loadMealItem(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
    }

    private void loadMealItem(int year, int month, int dayOfMonth) {
        closeKeyboard();
        this.mSelectedDate = MealContent.MealItem.intDateToString(year,month,dayOfMonth);

        MealContent.MealItem item = MealContent.getItem(year,month,dayOfMonth);

        loadMealFromItem(item);
    }

    private void loadMealFromItem(MealContent.MealItem item) {
        AutoCompleteTextView platBox = (AutoCompleteTextView) findViewById(R.id.plat);
        AutoCompleteTextView fromageBox = (AutoCompleteTextView) findViewById(R.id.fromage);
        AutoCompleteTextView dessertBox = (AutoCompleteTextView) findViewById(R.id.dessert);
        AutoCompleteTextView gouterBox = (AutoCompleteTextView) findViewById(R.id.gouter);

        if (item != null) {
            platBox.setText(item.plat, TextView.BufferType.EDITABLE);
            fromageBox.setText(item.fromage, TextView.BufferType.EDITABLE);
            dessertBox.setText(item.dessert, TextView.BufferType.EDITABLE);
            gouterBox.setText(item.gouter, TextView.BufferType.EDITABLE);
        } else {
            platBox.setText("", TextView.BufferType.EDITABLE);
            fromageBox.setText("",TextView.BufferType.EDITABLE);
            dessertBox.setText("", TextView.BufferType.EDITABLE);
            gouterBox.setText("", TextView.BufferType.EDITABLE);
        }
    }

    private void closeKeyboard() {
        View mainView = findViewById(R.id.mainLayout);
        mainView.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mainView.getWindowToken(), 0);

    }
}
