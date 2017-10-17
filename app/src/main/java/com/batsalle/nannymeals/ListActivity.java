package com.batsalle.nannymeals;

import android.app.SearchManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.batsalle.nannymeals.R;
import com.batsalle.nannymeals.meal.MealContent;

import java.util.Collection;

public class ListActivity extends AppCompatActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mListView = (ListView) findViewById(R.id.mealList);

        //android.R.layout.simple_list_item_1 est une vue disponible de base dans le SDK android,
        //Contenant une TextView avec comme identifiant "@android:id/text1"

        Collection<MealContent.MealItem> col = MealContent.items();
        String[] array = new String[col.size()];
        int index = 0;
        for (MealContent.MealItem value : col) {
            array[index] = value.date + " : " + value.getDetails();
            index++;
        }


        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ListActivity.this,
                android.R.layout.simple_list_item_1, array);
        mListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.meal_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);

        return true;
    }

}
