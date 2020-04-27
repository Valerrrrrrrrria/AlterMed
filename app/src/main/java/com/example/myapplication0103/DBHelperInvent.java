package com.example.myapplication0103;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.myapplication0103.ui.inventory.InventEditorActivity;
import com.example.myapplication0103.ui.inventory.InventoryFragment;

public class DBHelperInvent extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "altermedicaDBInvent(3)"; // Имя БД
    public static final String TABLE_ACTS = "acts"; // Для заголовков столбцов таблицы АКТЫ ИНВЕНТАРИЗАЦИИ
    public static final String TABLE_BARCODES = "invent"; // Для заголовков столбцов таблицы АКТЫ ИНВЕНТАРИЗАЦИИ

    // Поля таблицы ACTS
    public static final String KEY_ID = "_id"; // ID документа, автоматический
    public static final String KEY_ACTID = "_actid"; // ID акта
    public static final String KEY_DATE = "date"; // Дата документа
    public static final String KEY_NUMB = "numb"; // Номер документа
    public static final String KEY_COMMENT = "comment"; // Комментарий
    public static final String KEY_ISSENT = "issent"; // Отправлено или нет, 1 - отправлено (можно удалять), 0 - не отправлено

    // Поля таблицы TABLE_BARCODES
    public static final String KEY_ACTBARID = "_id"; //ID заполняется автоматически
    public static final String KEY_BARCODES = "barcodeis"; // Коды
    public static final String KEY_PHOTO1 = "photo1"; // Фото, тип данных BLOB
    public static final String KEY_PHOTO2 = "photo2"; // Фото, тип данных BLOB

    public DBHelperInvent(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_ACTS + "(" + KEY_ID + " integer primary key,"
                + KEY_ACTID + " integer," + KEY_DATE + " text," + KEY_NUMB + " text," +
                KEY_COMMENT + " text," + KEY_ISSENT + " integer" + ")");

        db.execSQL("create table " + TABLE_BARCODES + "(" + KEY_ACTBARID + " integer,"
                + KEY_BARCODES + " text," + KEY_PHOTO1 + " text," + KEY_PHOTO2 + " text" + ")");

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelperInvent.KEY_ACTID, 1);
        contentValues.put(DBHelperInvent.KEY_DATE, "29.03.2020");
        contentValues.put(DBHelperInvent.KEY_NUMB, "1");
        contentValues.put(DBHelperInvent.KEY_COMMENT, "Комментарий");
        contentValues.put(DBHelperInvent.KEY_ISSENT, 0);
        db.insert(DBHelperInvent.TABLE_ACTS, null, contentValues);

        ContentValues contentValues4 = new ContentValues();
        contentValues4.put(DBHelperInvent.KEY_ACTID, 1);
        contentValues4.put(DBHelperInvent.KEY_DATE, "29.03.2020");
        contentValues4.put(DBHelperInvent.KEY_NUMB, "");
        contentValues4.put(DBHelperInvent.KEY_COMMENT, "Комментарий");
        contentValues4.put(DBHelperInvent.KEY_ISSENT, 0);
        db.insert(DBHelperInvent.TABLE_ACTS, null, contentValues4);


        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(DBHelperInvent.KEY_ACTBARID, 1);
        contentValues1.put(DBHelperInvent.KEY_BARCODES, "111");
        db.insert(DBHelperInvent.TABLE_BARCODES, null, contentValues1);

        ContentValues contentValues2 = new ContentValues();
        contentValues2.put(DBHelperInvent.KEY_ACTBARID, 1);
        contentValues2.put(DBHelperInvent.KEY_BARCODES, "222");
        db.insert(DBHelperInvent.TABLE_BARCODES, null, contentValues2);

        ContentValues contentValues3 = new ContentValues();
        contentValues3.put(DBHelperInvent.KEY_ACTBARID, 2);
        contentValues3.put(DBHelperInvent.KEY_BARCODES, "333");
        db.insert(DBHelperInvent.TABLE_BARCODES, null, contentValues3);

        Log.i("ONCREATE", "Только заполнили TABLE_ACTS");
        Cursor cursor = db.query(DBHelperInvent.TABLE_ACTS, null,null,null,null,null,null); //пока без сортировок и группировок, поэтому null
        outputDBActs(cursor);
        cursor.close();

        Log.i("ONCREATE", "Только заполнили TABLE_BARCODES");
        Cursor cursor1 = db.query(DBHelperInvent.TABLE_BARCODES, null,null,null,null,null,null); //пока без сортировок и группировок, поэтому null
        outputDBBarcodes(cursor1);
        cursor1.close();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_ACTS);
        db.execSQL("drop table if exists " + TABLE_BARCODES);
        onCreate(db);
    }

    // --- Вывод базы ACTS в ЛОГ --- (Всё проверено)
    public static void outputDBActs(Cursor cursor) {
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelperInvent.KEY_ID);
            int idActIndex = cursor.getColumnIndex(DBHelperInvent.KEY_ACTID);
            int idDate = cursor.getColumnIndex(DBHelperInvent.KEY_DATE);
            int idNumb = cursor.getColumnIndex(DBHelperInvent.KEY_NUMB);
            int idComment = cursor.getColumnIndex(DBHelperInvent.KEY_COMMENT);
            int idIsSent = cursor.getColumnIndex(DBHelperInvent.KEY_ISSENT);


            do {
                Log.i("DATADASE LOG TABLE ACTS","ID = " + cursor.getInt(idIndex) +
                        ", ActID = " + cursor.getInt(idActIndex) +
                        ", Date = " + cursor.getString(idDate) +
                        ", NumbOdDoc = " + cursor.getString(idNumb) +
                        ", Comment = " + cursor.getString(idComment) +
                        ", IS SENT? " + cursor.getInt(idIsSent));
            } while (cursor.moveToNext());

        }else {
            Log.i("mlog","0 rows");
        }
    }

    // --- Вывод базы в ЛОГ
    public static void outputDBBarcodes(Cursor cursor) {
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelperInvent.KEY_ID);
            int barcodeIndex = cursor.getColumnIndex(DBHelperInvent.KEY_BARCODES);
            int photoIndex1 = cursor.getColumnIndex(DBHelperInvent.KEY_PHOTO1);
            int photoIndex2 = cursor.getColumnIndex(DBHelperInvent.KEY_PHOTO2);


            do {
                Log.i("LOG TABLE INVENT","ID = " + cursor.getInt(idIndex) +
                        ", Barcode = " + cursor.getString(barcodeIndex) +
                        ", Photo = " + cursor.getString(photoIndex1) +
                        ", Photo2 = " + cursor.getString(photoIndex2));

            } while (cursor.moveToNext());

        }else {
            Log.i("mlog","0 rows");
        }
    }


    public static void readDBActs(Cursor cursor) {
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelperInvent.KEY_ID);
            int idDate = cursor.getColumnIndex(DBHelperInvent.KEY_DATE);
            int idNumb = cursor.getColumnIndex(DBHelperInvent.KEY_NUMB);
            int idComment = cursor.getColumnIndex(DBHelperInvent.KEY_COMMENT);
            int idIsSent = cursor.getColumnIndex(DBHelperInvent.KEY_ISSENT);

            do {
                InventoryFragment.id_arrayList.add(cursor.getInt(idIndex));
                InventoryFragment.uniq_arrayList.add(cursor.getInt(idNumb));
                InventoryFragment.dates_arrayList.add(cursor.getString(idDate));
                InventoryFragment.comments_arrayList.add(cursor.getString(idComment));
                InventoryFragment.outputs_arrayList.add(cursor.getString(idDate) + " " + cursor.getInt(idNumb));
                Log.i("IDDDDD ", "" + cursor.getInt(idNumb));
            } while (cursor.moveToNext());

        }else {
            Log.i("mlog","0 rows");
        }
    }

    // --- Чтение существующей таблицы ACTSBAR
    public static void readMyDBActsBar(Cursor cursor) {

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ACTBARID);
            int barcodeIndex = cursor.getColumnIndex(DBHelper.KEY_BARCODESACTS);
            int photoIndex = cursor.getColumnIndex(DBHelper.KEY_PHOTO);

            do {
                InventEditorActivity.barcodes_listViewArray.add(cursor.getString(barcodeIndex));
                InventEditorActivity.barcodes_arrayAdapter.notifyDataSetChanged();

            } while (cursor.moveToNext());

        }else {
            Log.i("mlog","0 rows");
        }
    }

    public static void addToDBActs(SQLiteDatabase db, int actID, int uniqId, String date, String comment) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelperInvent.KEY_ACTID, actID);
        contentValues.put(DBHelperInvent.KEY_NUMB, uniqId);
        contentValues.put(DBHelperInvent.KEY_DATE, date);
        contentValues.put(DBHelperInvent.KEY_COMMENT, comment);
        contentValues.put(DBHelperInvent.KEY_ISSENT, 0);

        db.insert(DBHelperInvent.TABLE_ACTS, null, contentValues);

        Cursor cursor = db.query(DBHelperInvent.TABLE_ACTS, null,null,null,null,null,null); //пока без сортировок и группировок, поэтому null
        DBHelperInvent.outputDBActs(cursor);

        cursor.moveToLast();
        int idIndex = cursor.getColumnIndex(DBHelperInvent.KEY_ID);
        Log.i("idIndex = ", "" + cursor.getInt(idIndex));

        InventoryFragment.id_arrayList.add(cursor.getInt(idIndex));
        Log.i("Добавление", " в массив отработало");

        cursor.close();
    }


    // Добавление элементов в базу
    public static void addToDBBarcodes(SQLiteDatabase db, int actID, String barcode, String photo, String photo2) {
        ContentValues contentValues = new ContentValues();
        int id = InventoryFragment.id_arrayList.get(actID);
        contentValues.put(DBHelperInvent.KEY_ACTBARID, id);
        contentValues.put(DBHelperInvent.KEY_BARCODES, barcode);
        contentValues.put(DBHelperInvent.KEY_PHOTO1, photo);
        contentValues.put(DBHelperInvent.KEY_PHOTO2, photo2);



        db.insert(DBHelperInvent.TABLE_BARCODES, null, contentValues);
    }

    public static void deleteFromDatabase (SQLiteDatabase db, String tableName, int actID) {
        int posToDel = InventoryFragment.id_arrayList.get(actID); //FIXME почему тут только 1 массив? Проверить не остались ли лишние данные в массиве
        db.delete(tableName, DBHelperInvent.KEY_ID + "=" + posToDel, null);
        //db.delete(DBHelper.TABLE_ACTS, DBHelper.KEY_ID + "=" + posToDel, null);

        Log.i("INFO","--- Поcле удаления элемента id = " + posToDel + " из таблицы" + tableName);

        Cursor cursor = db.query(DBHelperInvent.TABLE_ACTS, null,null,null,null,null,null);
        outputDBActs(cursor);
        cursor.close();

        Cursor cursor1 = db.query(DBHelperInvent.TABLE_BARCODES, null,null,null,null,null,null);
        outputDBBarcodes(cursor1);
        cursor1.close();

    }

    // Очистка базы
    public static void clearDatabase (SQLiteDatabase db, String tableName) {
        db.delete(tableName, null, null);
        Log.i("INFO","--- DATABASASE IS DELETED ---");
    }

    public static void upgradeDataAct (SQLiteDatabase db, int actID, int uniqId, String date, String comment) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelperInvent.KEY_ACTID, actID);
        contentValues.put(DBHelperInvent.KEY_NUMB, uniqId);
        contentValues.put(DBHelperInvent.KEY_DATE, date);
        contentValues.put(DBHelperInvent.KEY_COMMENT, comment);

        int posToUp = InventoryFragment.id_arrayList.get(actID);
        db.update(DBHelperInvent.TABLE_ACTS, contentValues, DBHelper.KEY_ID + "=" + posToUp, null);
        Log.i("INFO",actID + "--- IS UPDATED ---");

        Cursor cursor = db.query(DBHelperInvent.TABLE_ACTS, null,null,null,null,null,null); //пока без сортировок и группировок, поэтому null
        outputDBActs(cursor);
        cursor.close();
    }
}
