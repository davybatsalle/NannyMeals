package com.batsalle.nannymeals.activity.meallist;

import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import com.batsalle.nannymeals.R;
import com.batsalle.nannymeals.meal.MealContent;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListActivity extends AppCompatActivity {

    private ListView mListView;
    private MealAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mListView = (ListView) findViewById(R.id.mealList);
        List<MealContent.MealItem> list = MealContent.itemList();

        mAdapter = new MealAdapter(list, ListActivity.this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);

        MenuItem searchItem = menu.findItem(R.id.meal_search);

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
        if (id == R.id.meal_search) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MealAdapter extends BaseAdapter {
        class ViewHolder {
            TextView date, plat, gouter, dessert, fromage;
        }

        private List<MealContent.MealItem> mMealList;
        private Context mContext;
        ArrayList<MealContent.MealItem> mMealArrayList;

        private MealAdapter(List<MealContent.MealItem> data, Context context) {
            this.mMealList = data;
            this.mContext = context;
            this.mMealArrayList = new ArrayList<>();
            this.mMealArrayList.addAll(data);
        }

        @Override
        public int getCount() {
            return this.mMealList.size();
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
            ViewHolder viewHolder;

            if (rowView == null) {
                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.meal_item_list, null);
                // configure view holder
                viewHolder = new ViewHolder();
                viewHolder.date = (TextView) rowView.findViewById(R.id.dateItem);
                viewHolder.plat = (TextView) rowView.findViewById(R.id.platItem);
                viewHolder.fromage = (TextView) rowView.findViewById(R.id.fromageItem);
                viewHolder.dessert = (TextView) rowView.findViewById(R.id.dessertItem);
                viewHolder.gouter = (TextView) rowView.findViewById(R.id.gouterItem);
                rowView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            MealContent.MealItem item = this.mMealList.get(position);


            try {
                viewHolder.date.setText(MealContent.MealItem.beautifyDate(item.date));
            } catch (ParseException e) {
                // should not happen
            }
            viewHolder.plat.setText(item.plat);
            viewHolder.fromage.setText(item.fromage);
            viewHolder.dessert.setText(item.dessert);
            viewHolder.gouter.setText(item.gouter);
            return rowView;
        }

        void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());

            this.mMealList.clear();
            if (charText.length() == 0) {
                this.mMealList.addAll(this.mMealArrayList);

            } else {
                for (MealContent.MealItem item : this.mMealArrayList) {
                    if (charText.length() != 0 && item.getDetails().toLowerCase(Locale.getDefault()).contains(charText)) {
                        this.mMealList.add(item);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }
}
