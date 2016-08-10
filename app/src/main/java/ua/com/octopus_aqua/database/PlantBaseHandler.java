package ua.com.octopus_aqua.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import ua.com.octopus_aqua.MainActivity;


/*
singleton for db operations
 */

public class PlantBaseHandler {

    private static PlantBaseHandler instance;

    public static synchronized PlantBaseHandler getInstance() {
        if (instance == null) {
            instance = new PlantBaseHandler(MainActivity.getAppContext());
        }
        return instance;
    }

    public static final String ID_COLUMN = "_id";
    public static final String NAME_COLUMN = "name";
    public static final String TYPE_COLUMN = "type";
    public static final String INFO_COLUMN = "info";
    public static final String PIC_COLUMN = "picture";
    public static final String GROUP_COLUMN = "groupname";


    public final String[] KEYS = {ID_COLUMN, NAME_COLUMN, TYPE_COLUMN, INFO_COLUMN, PIC_COLUMN, GROUP_COLUMN};

    public static final String DATABASE_NAME = "plantBase.db";
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_TABLE = "plants";

    private PlantBaseCreator plantBaseCreator;
    private SQLiteDatabase db;

    private PlantBaseHandler(Context context) {
        plantBaseCreator = new PlantBaseCreator(context);
        db = plantBaseCreator.getWritableDatabase();
        Log.d("MY_TAG", "created db: " + plantBaseCreator.getDatabaseName());
        Log.d("MY_TAG", "total rows: " + getCount());
    }

    /*public synchronized long insertRow(String name, String type, String info, String pic) {
        ContentValues cv = new ContentValues();

        cv.put(NAME_COLUMN, name);
        cv.put(TYPE_COLUMN, type);
        cv.put(INFO_COLUMN, info);
        cv.put(PIC_COLUMN, pic);

        Log.d("MY_TAG", "Data inserted");
        return db.insert(DATABASE_TABLE, null, cv);
    }*/

    public synchronized boolean insertRow(PlantRow row) {
        ContentValues cv = new ContentValues();

        cv.put(ID_COLUMN, row.getId());
        cv.put(NAME_COLUMN, row.getName());
        cv.put(TYPE_COLUMN, row.getType());
        cv.put(INFO_COLUMN, row.getInfo());
        cv.put(PIC_COLUMN, row.getPic());
        cv.put(GROUP_COLUMN, row.getGroup());

        db.insert(DATABASE_TABLE, null, cv);
        Log.d("MY_TAG", "Data inserted");
        return true;
    }

    public synchronized PlantRow getRow(int id) {
        Log.d("MY_TAG", "ID got: " + id);
        String where = ID_COLUMN + "=" + id;

        Cursor cursor = db.query(false, DATABASE_TABLE, KEYS, where, null, null, null, null, null);

        cursor.moveToFirst();

        String name = cursor.getString(cursor.getColumnIndexOrThrow(PlantBaseHandler.NAME_COLUMN));
        String type = cursor.getString(cursor.getColumnIndexOrThrow(PlantBaseHandler.TYPE_COLUMN));
        String info = cursor.getString(cursor.getColumnIndexOrThrow(PlantBaseHandler.INFO_COLUMN));
        String picPath = cursor.getString(cursor.getColumnIndexOrThrow(PlantBaseHandler.PIC_COLUMN));
        String group = cursor.getString(cursor.getColumnIndexOrThrow(PlantBaseHandler.GROUP_COLUMN));

        cursor.close();

        return new PlantRow(id, name, info, type, picPath, group);
    }


    public synchronized void updateRow(long id, String name, String type, String info, String pic, String group) {
        ContentValues cv = new ContentValues();

        cv.put(NAME_COLUMN, name);
        cv.put(TYPE_COLUMN, type);
        cv.put(INFO_COLUMN, info);
        cv.put(PIC_COLUMN, pic);
        cv.put(GROUP_COLUMN, group);

        db.update(DATABASE_TABLE, cv, ID_COLUMN + "=" + id, null);
        Log.d("MY_TAG", "row " + id + " updated");
    }

    public synchronized boolean deleteRow(long id) {
        Log.d("MY_TAG", "row " + id + " deleted");
        return db.delete(DATABASE_TABLE, ID_COLUMN + "=" + id, null) > 0;
    }

    public synchronized String[] getColumn(String column) throws CursorIndexOutOfBoundsException {
        ArrayList<String> row = new ArrayList<>();
        Cursor c = getAllRows();
        c.moveToFirst();
        Log.d("MY_TAG", "Taking column: "+column+" where are "+c.getColumnCount()+" columns");
        String buff;
        do {
            buff = c.getString(c.getColumnIndexOrThrow(column));
            Log.d("MY_TAG", "from db, "+column+": " + buff);
            row.add(buff);
            //row.add(c.getString(c.getColumnIndex(column)));
        } while (c.moveToNext());
        return row.toArray(new String[row.size()]);
    }

    public synchronized long getCount() {
        //get total amount of elements
        return DatabaseUtils.queryNumEntries(db, DATABASE_TABLE);
    }

    public synchronized Cursor getAllRows() {
        Cursor c = db.query(true, DATABASE_TABLE, KEYS, null, null, null, null, null, null);
        c.moveToFirst();
        return c;
    }

    public synchronized Cursor getCursorForGroup(String group) {
        Cursor c = db.rawQuery("select * from " + DATABASE_TABLE + " where " + GROUP_COLUMN + " ='" + group + "'", null);
        Log.d("MY_TAG", "Group "+group+" has rows: "+c.getCount());
        c.moveToFirst();
        return c;
    }

    public synchronized PlantRow[] getRowsByGroup(String group) {
        Cursor c = getCursorForGroup(group);
        c.moveToFirst();

        List<PlantRow> list = new ArrayList<>();
        do {
            list.add(new PlantRow(
                    c.getInt(c.getColumnIndexOrThrow(PlantBaseHandler.ID_COLUMN)),
                    c.getString(c.getColumnIndexOrThrow(PlantBaseHandler.NAME_COLUMN)),
                    c.getString(c.getColumnIndexOrThrow(PlantBaseHandler.TYPE_COLUMN)),
                    c.getString(c.getColumnIndexOrThrow(PlantBaseHandler.INFO_COLUMN)),
                    c.getString(c.getColumnIndexOrThrow(PlantBaseHandler.PIC_COLUMN)),
                    c.getString(c.getColumnIndexOrThrow(PlantBaseHandler.GROUP_COLUMN))
            ));
        } while(c.moveToNext());

        return (PlantRow[]) list.toArray();
    }

    public synchronized Cursor getColumnCursor(String column){
        return db.rawQuery("select "+column+" from "+DATABASE_TABLE,null);
    }

    public synchronized HashSet<String> groupsAsSet(){
        return new HashSet<>(
                Arrays.asList(
                        getColumn(
                                PlantBaseHandler.GROUP_COLUMN)));
    }

    public synchronized void dropTable() {
        plantBaseCreator.onUpgrade(db, DATABASE_VERSION, Integer.valueOf(DATABASE_VERSION + 2));
    }


    private class PlantBaseCreator extends SQLiteOpenHelper {

        PlantBaseCreator(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createTable = "create table " + DATABASE_TABLE + " ("
                    + ID_COLUMN + " integer primary key, "
                    + NAME_COLUMN + " text, "
                    + INFO_COLUMN + " text, "
                    + TYPE_COLUMN + " text, "
                    + PIC_COLUMN + " text, "
                    + GROUP_COLUMN + " text);";
            db.execSQL(createTable);
            Log.d("MY_TAG", createTable);
            Log.d("MY_TAG", "DataBase created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            Log.d("MY_TAG", "DataBase wasted");
            onCreate(db);
        }
    }

}
