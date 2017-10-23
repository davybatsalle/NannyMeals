package com.batsalle.nannymeals.activity.stats;

import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.batsalle.nannymeals.R;
import com.batsalle.nannymeals.activity.meallist.ListActivity;
import com.batsalle.nannymeals.meal.MealContent;
import com.batsalle.nannymeals.meal.MealContract;
import com.batsalle.nannymeals.meal.MealStats;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatsActivity extends AppCompatActivity {

    private MealStats mStats;
    private ListView mListView;
    private StatItemAdapter mAdapter;

    private Integer mNbMonths = 1;
    private String mCategory = MealContract.MealTable.COLUMN_PLAT;

    private static final Map<String,String> DROPDOWN_TR = new HashMap<>();
    private static final Map<String,Integer> DROPDOWN_MONTHS = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);


        mListView = (ListView) findViewById(R.id.statList);
        registerSettingsChange();

        initCategoryDropDown();
        initMonthDropDown();


    }


    private void registerSettingsChange() {
        mStats = new MealStats(getApplicationContext(), mNbMonths);
        mAdapter = new StatItemAdapter(mStats.mStats.get(mCategory), getApplicationContext());
        mListView.setAdapter(mAdapter);
        mListView.invalidate();
    }

    private void initMonthDropDown() {
        Spinner dropdown = (Spinner)findViewById(R.id.nbMonths);
        String[] items = new String[]{
                getString(R.string.oneMonth),
                getString(R.string.threeMonth),
                getString(R.string.sixMonth)
        };
        DROPDOWN_MONTHS.put(getString(R.string.oneMonth), 1);
        DROPDOWN_MONTHS.put(getString(R.string.threeMonth), 3);
        DROPDOWN_MONTHS.put(getString(R.string.sixMonth), 6);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Integer nbMonths = DROPDOWN_MONTHS.get(parent.getItemAtPosition(position).toString());
                if (nbMonths != mNbMonths) {
                    mNbMonths = nbMonths;
                    registerSettingsChange();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initCategoryDropDown() {
        Spinner dropdown = (Spinner)findViewById(R.id.category);
        String[] items = new String[]{
                getString(R.string.mainCourse_dropDown),
                getString(R.string.cheese_dropDown),
                getString(R.string.pudding_dropDown),
                getString(R.string.snck_dropDown)};
        DROPDOWN_TR.put(getString(R.string.mainCourse_dropDown), MealContract.MealTable.COLUMN_PLAT);
        DROPDOWN_TR.put(getString(R.string.cheese_dropDown), MealContract.MealTable.COLUMN_FROMAGE);
        DROPDOWN_TR.put(getString(R.string.pudding_dropDown), MealContract.MealTable.COLUMN_DESSERT);
        DROPDOWN_TR.put(getString(R.string.snck_dropDown), MealContract.MealTable.COLUMN_GOUTER);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = DROPDOWN_TR.get(parent.getItemAtPosition(position).toString());
                if (mCategory != category) {
                    mCategory = category;
                    registerSettingsChange();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_stats, menu);

        MenuItem searchItem = menu.findItem(R.id.stat_search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchQuery) {
                mAdapter.filter(searchQuery.trim());
                mListView.invalidate();
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true;  // Return true to expand action view
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.stat_search) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    class StatItemAdapter extends BaseAdapter {

        class ViewHolder {
            TextView item, counter;
        }

        private List<MealStats.MealStatItem> mStatsItems;
        private Context mContext;
        ArrayList<MealStats.MealStatItem> mStatsArray;

        private StatItemAdapter(List<MealStats.MealStatItem> data, Context context) {
            this.mStatsItems = data;
            this.mContext = context;
            this.mStatsArray = new ArrayList<>();
            this.mStatsArray.addAll(data);
        }

        @Override
        public int getCount() {
            return this.mStatsItems.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            StatItemAdapter.ViewHolder viewHolder;

            if (rowView == null) {
                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.stat_item_list, null);
                // configure view holder
                viewHolder = new StatItemAdapter.ViewHolder();
                viewHolder.item = (TextView) rowView.findViewById(R.id.statItem);
                viewHolder.counter = (TextView) rowView.findViewById(R.id.statCounter);
                rowView.setTag(viewHolder);

            } else {
                viewHolder = (StatItemAdapter.ViewHolder) convertView.getTag();
            }

            MealStats.MealStatItem item = this.mStatsItems.get(position);

            viewHolder.counter.setText(String.valueOf(item.counter) + getString(R.string.times));
            viewHolder.item.setText(item.value);
            return rowView;
        }

        void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());

            this.mStatsItems.clear();
            if (charText.length() == 0) {
                this.mStatsItems.addAll(this.mStatsArray);

            } else {
                for (MealStats.MealStatItem item : this.mStatsArray) {
                    if (charText.length() != 0 && item.value.toLowerCase(Locale.getDefault()).contains(charText)) {
                        this.mStatsItems.add(item);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

}
