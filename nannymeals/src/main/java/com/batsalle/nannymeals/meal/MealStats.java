package com.batsalle.nannymeals.meal;

import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by davyb on 19/10/2017.
 */

public class MealStats {
    public Map<String, List<MealStatItem>> mStats = new HashMap<>();
    private MealDbHelper mDb;
    private Context mContext;
    private Integer mNbMonths;

    public static class MealStatItem {
        public Integer counter;
        public String value;
        public String category;

        public MealStatItem() {}
    }

    public MealStats(Context context, Integer nbOfMonths) {
        mContext = context;
        mNbMonths = nbOfMonths;
        init();
    }

    public void init() {
        mDb = new MealDbHelper(mContext);

        mStats.put(MealContract.MealTable.COLUMN_PLAT,
                mDb.retrieveStats(MealContract.MealTable.COLUMN_PLAT, mNbMonths));
        mStats.put(MealContract.MealTable.COLUMN_FROMAGE,
                mDb.retrieveStats(MealContract.MealTable.COLUMN_FROMAGE, mNbMonths));
        mStats.put(MealContract.MealTable.COLUMN_DESSERT,
                mDb.retrieveStats(MealContract.MealTable.COLUMN_DESSERT, mNbMonths));
        mStats.put(MealContract.MealTable.COLUMN_GOUTER,
                mDb.retrieveStats(MealContract.MealTable.COLUMN_GOUTER, mNbMonths));
    }

}
