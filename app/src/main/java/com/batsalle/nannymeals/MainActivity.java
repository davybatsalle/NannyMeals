package com.batsalle.nannymeals;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.batsalle.nannymeals.meal.MealContent;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private String mSelectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMealItem();
                Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();

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

        // load today's meal
        loadMealItem(mealDateBox.getDate());
    }

    @Override
    protected void onDestroy() {
        MealContent.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_list) {
            return startListActivity();
        } else if (id == R.id.action_delete) {
            deleteSelectedMeal();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteSelectedMeal() {
        new AlertDialog.Builder(this)
                .setTitle("Delete a meal")
                .setMessage("Do you really want to delete?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        MealContent.removeItem(mSelectedDate);
                        Toast.makeText(MainActivity.this, "Removed", Toast.LENGTH_SHORT).show();

                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    private boolean startListActivity() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);

        return true;
    }


    private void saveMealItem() {
        EditText platBox = (EditText) findViewById(R.id.plat);
        String plat = platBox.getText().toString();

        EditText fromageBox = (EditText) findViewById(R.id.fromage);
        String fromage = fromageBox.getText().toString();

        EditText dessertBox = (EditText) findViewById(R.id.dessert);
        String dessert = dessertBox.getText().toString();

        EditText gouterBox = (EditText) findViewById(R.id.gouter);
        String gouter = gouterBox.getText().toString();

        MealContent.MealItem item = new MealContent.MealItem(this.mSelectedDate,plat,fromage,dessert,gouter);

        MealContent.editOrAddItem(item);
    }

    private void loadMealItem(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        loadMealItem(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
    }

    private void loadMealItem(int year, int month, int dayOfMonth) {
        this.mSelectedDate = MealContent.MealItem.intDateToString(year,month,dayOfMonth);

        //TODO
        MealContent.MealItem item = MealContent.getItem(year,month,dayOfMonth);

        EditText platBox = (EditText) findViewById(R.id.plat);
        EditText fromageBox = (EditText) findViewById(R.id.fromage);
        EditText dessertBox = (EditText) findViewById(R.id.dessert);
        EditText gouterBox = (EditText) findViewById(R.id.gouter);

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
}
