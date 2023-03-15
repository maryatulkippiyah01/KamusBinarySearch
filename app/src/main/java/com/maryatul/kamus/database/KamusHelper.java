package com.maryatul.kamus.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.maryatul.kamus.model.ModelKamus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import static android.provider.BaseColumns._ID;
import static com.maryatul.kamus.database.DatabaseContract.KamusColumns.DESKRIPSI;
import static com.maryatul.kamus.database.DatabaseContract.KamusColumns.KATA;
import static com.maryatul.kamus.database.DatabaseContract.TABLE_INDONESIA;
import static com.maryatul.kamus.database.DatabaseContract.TABLE_MELAYU;


public class KamusHelper {

    private Context context;
    private DatabaseHelper dataBaseHelper;

    private SQLiteDatabase database;

    private String table;

    public KamusHelper(Context context){
        this.context = context;
    }

    private void checkLanguage(boolean language){
        if (language){ //language is true (1)
            table = TABLE_MELAYU;
        }
        else { //language is false (0)
            table = TABLE_INDONESIA;

        }
    }

    public KamusHelper open() throws SQLException {
        dataBaseHelper = new DatabaseHelper(context);
        database = dataBaseHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dataBaseHelper.close();
    }

    public ArrayList<ModelKamus> selectAll(boolean language){
        checkLanguage(language);

        Cursor cursor = database.query(table, null, null, null, null, null, "kata ASC", null);
        cursor.moveToFirst();
        ArrayList<ModelKamus> arrayList = new ArrayList<>();
        ModelKamus kamus;
        if (cursor.getCount()>0) {
            do {
                kamus = new ModelKamus(cursor.getString(cursor.getColumnIndexOrThrow(KATA)), cursor.getString(cursor.getColumnIndexOrThrow(DESKRIPSI)));
                arrayList.add(kamus);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }

    public ArrayList<ModelKamus> selectByKata(String kata, boolean language){
        checkLanguage(language);

        Cursor cursor = database.rawQuery("SELECT * FROM "+table+" WHERE kata LIKE '"+kata+"%'", null);
        cursor.moveToFirst();
        ArrayList<ModelKamus> arrayList = new ArrayList<>();
        ModelKamus kamus;
        if (cursor.getCount()>0) {
            do {
                kamus = new ModelKamus(cursor.getString(cursor.getColumnIndexOrThrow(KATA)), cursor.getString(cursor.getColumnIndexOrThrow(DESKRIPSI)));
                arrayList.add(kamus);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }

    public ArrayList<ModelKamus> selectByKataBinarySearch(String kata, boolean language) {
        ArrayList<ModelKamus> items = selectAll(language);

        if (kata.equals("")) {
            return items;
        }

        ArrayList<ModelKamus> filteredItems = new ArrayList<>();

        while (!items.isEmpty()) {
            int index = binarySearch(items, kata);

            if (index == -1) {
                break;
            }

            ModelKamus itemToBeAdded = items.get(index);

            items.remove(index);
            filteredItems.add(itemToBeAdded);
        }

        Collections.sort(filteredItems, new Comparator<ModelKamus>() {
            @Override
            public int compare(ModelKamus o1, ModelKamus o2) {
                String kata1 = o1.getKata();
                String kata2 = o2.getKata();

                return kata1.compareTo(kata2);
            }
        });

        return filteredItems;
    }

    private int binarySearch(ArrayList<ModelKamus> items, String keyword) {
        int left = 0;
        int right = items.size() - 1;

        while (left <= right) {
            int mid = (left + right) / 2;

            String kata = items.get(mid).getKata();

            if (kata.startsWith(keyword)) {
                return mid;
            }

            int compare = kata.compareTo(keyword);

            if (compare < 0) {
                left = mid + 1;
            } else if (compare > 0) {
                right = mid - 1;
            }
        }

        return -1;
    }

    public long insert(ModelKamus kamus, boolean language){
        checkLanguage(language);

        ContentValues initialValues =  new ContentValues();
        initialValues.put(KATA, kamus.getKata());
        initialValues.put(DESKRIPSI, kamus.getDeskripsi());
        return database.insert(table, null, initialValues);
    }

    public int update(ModelKamus kamus, boolean language){
        checkLanguage(language);

        ContentValues args = new ContentValues();
        args.put(KATA, kamus.getKata());
        args.put(DESKRIPSI, kamus.getDeskripsi());
        return database.update(table, args, _ID + "= '" + kamus.getId() + "'", null);
    }

    public int delete(int id ,boolean language){
        checkLanguage(language);
        return database.delete(table, _ID + " = '"+id+"'", null);
    }

    public void insertTransaction(ArrayList<ModelKamus> listKamus, boolean language){
        checkLanguage(language);

        String sql = "INSERT INTO "+table+" ("+KATA+", "+DESKRIPSI
                +") VALUES (?, ?)";

        database.beginTransaction();

        SQLiteStatement stmt = database.compileStatement(sql);
        for (int i = 0; i < listKamus.size(); i++) {
            stmt.bindString(1, listKamus.get(i).getKata());
            stmt.bindString(2, listKamus.get(i).getDeskripsi());
            stmt.execute();
            stmt.clearBindings();
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }
}
