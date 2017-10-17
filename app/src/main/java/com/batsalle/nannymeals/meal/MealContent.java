package com.batsalle.nannymeals.meal;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by davyb on 16/10/2017.
 */

public class MealContent {

    public static final Map<String, MealItem> ITEM_MAP = new HashMap<String, MealItem>();
    private static MealDbHelper db;

    public static void init(Context context) {
        MealContent.db = new MealDbHelper(context);
        List<MealItem> items = db.retrieveItems();

        Iterator<MealItem> iterator = items.iterator();
        while (iterator.hasNext()) {
            MealItem item = iterator.next();
            ITEM_MAP.put(item.date, item);
        }
    }

    public static void close() {
        MealContent.db.closeDb();
    }


    public static void editOrAddItem(MealItem item) {
        if (ITEM_MAP.containsKey(item.date)) {
            db.updateItem(item);
        } else {
            db.addItem(item);
        }
        ITEM_MAP.put(item.date, item);
    }

    public static void removeItem(String date) {
        if (!ITEM_MAP.containsKey(date)) {
            return;
        }

        db.removeItem(date);
        ITEM_MAP.remove(date);
    }

    public static MealItem getItem(String date) {
        if (ITEM_MAP.containsKey(date)) {
            return ITEM_MAP.get(date);
        }

        return null;
    }

    public static MealItem getItem(long date) {
        return getItem(MealItem.longDateToString(date));
    }

    public static MealItem getItem(int year, int month, int dayOfMonth) {
        return getItem(MealItem.intDateToString(year,month,dayOfMonth));
    }

    public static Collection<MealItem> items() {
        return ITEM_MAP.values();
    }

    public static class MealItem {
        public String date;
        public String plat;
        public String fromage;
        public String dessert;
        public String gouter;

        public MealItem(String date,String plat, String fromage, String dessert, String gouter) {
            this.date = date;
            this.plat = plat;
            this.fromage = fromage;
            this.dessert = dessert;
            this.gouter = gouter;
        }

        public static String longDateToString(long date) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date dateD = new Date(date);
            return format.format(dateD);
        }

        public static String intDateToString(int year, int month, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year,month,dayOfMonth);
            return longDateToString(calendar.getTime().getTime());
        }

        public MealItem(long date, String plat, String fromage, String dessert, String gouter) {
            this.date = longDateToString(date);
            this.plat = plat;
            this.fromage = fromage;
            this.dessert = dessert;
            this.gouter = gouter;
        }

        public String getDetails() {
            return this.plat + " " + this.fromage + " " + this.dessert + " " + this.gouter;
        }

        public String toString() {
            return getDetails();
        }
    }
}
