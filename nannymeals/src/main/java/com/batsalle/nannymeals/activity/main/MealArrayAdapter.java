package com.batsalle.nannymeals.activity.main;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.batsalle.nannymeals.meal.MealContent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by davyb on 19/10/2017.
 */

public class MealArrayAdapter extends ArrayAdapter<MealContent.MealItem> {
    private final Context mContext;
    private final List<MealContent.MealItem> mMeals;
    private final List<MealContent.MealItem> mMeals_all;
    private final Map<String, MealContent.MealItem> mMeals_suggestion;
    private final int mLayoutResourceId;
    private final String mField;

    public MealArrayAdapter(Context context, int resource, List<MealContent.MealItem> meals, String field) {
        super(context, resource, meals);
        this.mContext = context;
        this.mLayoutResourceId = resource;
        this.mMeals = new ArrayList<>(meals);
        this.mMeals_all = new ArrayList<>(meals);
        this.mMeals_suggestion = new HashMap<>();
        this.mField = field;
    }

    public int getCount() {
        return mMeals.size();
    }

    public MealContent.MealItem getItem(int position) {
        return mMeals.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    private String getFieldValue(MealContent.MealItem meal) {
        try {
            Field field = MealContent.MealItem.class.getField(mField);
            return (String)field.get(meal);
        } catch (NoSuchFieldException e) {
            // should not happen
        } catch (IllegalAccessException e) {
            // should not happen
        }

        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(mLayoutResourceId, parent, false);
            }
            MealContent.MealItem meal = getItem(position);
            TextView text = (TextView) convertView.findViewById(android.R.id.text1);
            text.setText(getFieldValue(meal));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            public String convertResultToString(Object resultValue) {
                return getFieldValue((MealContent.MealItem) resultValue);
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    mMeals_suggestion.clear();
                    for (MealContent.MealItem meal : mMeals_all) {
                        String mealField = getFieldValue(meal);
                        if (mealField.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                            mMeals_suggestion.put(mealField, meal);
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mMeals_suggestion;
                    filterResults.count = mMeals_suggestion.size();
                    return filterResults;
                } else {
                    return new FilterResults();
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mMeals.clear();
                if (results != null && results.count > 0) {
                    Map<?,?> result = (Map<?,?>) results.values;
                    for (Map.Entry<?,?> pair : result.entrySet()) {
                        if (pair.getValue() instanceof MealContent.MealItem) {
                            mMeals.add((MealContent.MealItem) pair.getValue());
                        }
                    }
                } else if (constraint == null) {
                    // no filter, add entire original list back in
                    mMeals.addAll(mMeals_all);
                }
                notifyDataSetChanged();
            }
        };
    }
}