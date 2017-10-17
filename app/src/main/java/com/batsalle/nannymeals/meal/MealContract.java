package com.batsalle.nannymeals.meal;

import android.provider.BaseColumns;

/**
 * Created by davyb on 16/10/2017.
 */

public class MealContract {
    private MealContract() {}

    public static class MealTable implements BaseColumns {
        public static final String TABLE_NAME = "meals";
        public static final String COLUMN_DATE = "mealDate";
        public static final String COLUMN_PLAT = "plat";
        public static final String COLUMN_FROMAGE = "fromage";
        public static final String COLUMN_DESSERT = "dessert";
        public static final String COLUMN_GOUTER = "gouter";
    }

}
