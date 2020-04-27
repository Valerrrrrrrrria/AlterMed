package com.example.myapplication0103;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.myapplication0103.ui.acts.ActsEditorActivity;
import com.example.myapplication0103.ui.acts.ActsFragment;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "altermedicaDB2003(7)"; // Имя БД
    public static final String TABLE_ACTS = "acts"; // Для заголовков столбцов таблицы АКТЫ ИМПЛАНТАЦИИ
    public static final String TABLE_BARCODESACTS = "barcodesacts"; // Для заголовков столбцов таблицы ШТРИХКОДЫ В АКТАХ ИМПЛАНТАЦИИ


    // Поля таблицы ACTS
    public static final String KEY_ID = "_id"; // ID документа, автоматический
    public static final String KEY_ACTID = "_actid"; // ID акта
    public static final String KEY_DATE = "date"; // Дата документа
    public static final String KEY_NUMB = "numb"; // Номер документа
    public static final String KEY_DRNAME = "drname"; // ФИО врача
    public static final String KEY_PATNAME = "patname"; // ФИО пациента
    public static final String KEY_HISTORY = "history"; // Номер истории болезни
    public static final String KEY_COMMENT = "comment"; // Комментарий
    public static final String KEY_ACTPHOTO = "photoOfAct"; // Фото, тип данных BLOB


    public static final String KEY_ISSENT = "issent"; // Отправлено или нет, 1 - отправлено (можно удалять), 0 - не отправлено

    // Поля таблицы TABLE_BARCODESACTS
    public static final String KEY_ACTBARID = "_id"; //ID документа = ID документа таблицы ACTS
    public static final String KEY_BARCODESACTS = "barcodeis"; // Коды
    public static final String KEY_PHOTO = "photo"; // Фото, тип данных BLOB
    public static final String KEY_PHOTO2 = "photo2"; // Фото, тип данных BLOB





    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_ACTS + "(" + KEY_ID + " integer primary key,"
                + KEY_ACTID + " integer," + KEY_DATE + " text," + KEY_NUMB + " text," + KEY_DRNAME
                + " text," + KEY_PATNAME + " text," + KEY_HISTORY + " text,"
                + KEY_COMMENT + " text," + KEY_ACTPHOTO + " text," + KEY_ISSENT + " integer" + ")");

        db.execSQL("create table " + TABLE_BARCODESACTS + "(" + KEY_ACTBARID + " integer,"
                + KEY_BARCODESACTS + " text," + KEY_PHOTO + " blob," + KEY_PHOTO2 + " blob" + ")");

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_ACTID, 1);
        contentValues.put(DBHelper.KEY_DATE, "29.03.2020");
        contentValues.put(DBHelper.KEY_NUMB, "");
        contentValues.put(DBHelper.KEY_DRNAME, "");
        contentValues.put(DBHelper.KEY_PATNAME, "Имя пациента1");
        contentValues.put(DBHelper.KEY_HISTORY, "123455443212124");
        contentValues.put(DBHelper.KEY_COMMENT, "Комментарий");
        contentValues.put(DBHelper.KEY_ISSENT, 0);
        db.insert(DBHelper.TABLE_ACTS, null, contentValues);

        ContentValues contentValues4 = new ContentValues();
        contentValues4.put(DBHelper.KEY_ACTID, 1);
        contentValues4.put(DBHelper.KEY_DATE, "29.03.2020");
        contentValues4.put(DBHelper.KEY_NUMB, "");
        contentValues4.put(DBHelper.KEY_DRNAME, ""); //Посмотреть, как считать из Shared Pref
        contentValues4.put(DBHelper.KEY_PATNAME, "Имя пациента2");
        contentValues.put(DBHelper.KEY_HISTORY, "123455443212124");
        contentValues4.put(DBHelper.KEY_COMMENT, "Комментарий");
        contentValues4.put(DBHelper.KEY_ISSENT, 0);
        db.insert(DBHelper.TABLE_ACTS, null, contentValues4);


        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(DBHelper.KEY_ACTBARID, 1);
        contentValues1.put(DBHelper.KEY_BARCODESACTS, "111");
        db.insert(DBHelper.TABLE_BARCODESACTS, null, contentValues1);

        ContentValues contentValues2 = new ContentValues();
        contentValues2.put(DBHelper.KEY_ACTBARID, 1);
        contentValues2.put(DBHelper.KEY_BARCODESACTS, "222");
        db.insert(DBHelper.TABLE_BARCODESACTS, null, contentValues2);

        ContentValues contentValues3 = new ContentValues();
        contentValues3.put(DBHelper.KEY_ACTBARID, 2);
        contentValues3.put(DBHelper.KEY_BARCODESACTS, "333");
        db.insert(DBHelper.TABLE_BARCODESACTS, null, contentValues3);



        Log.i("ONCREATE", "Только заполнили TABLE_ACTS");
        Cursor cursor = db.query(DBHelper.TABLE_ACTS, null,null,null,null,null,null); //пока без сортировок и группировок, поэтому null
        outputMyDatabaseActs(cursor);
        cursor.close();

        Log.i("ONCREATE", "Только заполнили TABLE_BARCODESACTS");
        Cursor cursor1 = db.query(DBHelper.TABLE_BARCODESACTS, null,null,null,null,null,null); //пока без сортировок и группировок, поэтому null
        outputMyDBActsBar(cursor1);
        cursor1.close();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_ACTS);
        db.execSQL("drop table if exists " + TABLE_BARCODESACTS);
        onCreate(db);
    }

    // --- Вывод базы ACTS в ЛОГ --- (Всё проверено)
    public static void outputMyDatabaseActs(Cursor cursor) {
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int idActIndex = cursor.getColumnIndex(DBHelper.KEY_ACTID);
            int idDate = cursor.getColumnIndex(DBHelper.KEY_DATE);
            int idNumb = cursor.getColumnIndex(DBHelper.KEY_NUMB);
            int idDrName = cursor.getColumnIndex(DBHelper.KEY_DRNAME);
            int idPatName = cursor.getColumnIndex(DBHelper.KEY_PATNAME);
            int idHistory = cursor.getColumnIndex(DBHelper.KEY_HISTORY);
            int idComment = cursor.getColumnIndex(DBHelper.KEY_COMMENT);
            int idPhoto = cursor.getColumnIndex(DBHelper.KEY_ACTPHOTO);
            int idIsSent = cursor.getColumnIndex(DBHelper.KEY_ISSENT);


            do {
                Log.i("DATADASE LOG TABLE ACTS","ID = " + cursor.getInt(idIndex) +
                        ", ActID = " + cursor.getInt(idActIndex) +
                        ", Date = " + cursor.getString(idDate) +
                        ", NumbOdDoc = " + cursor.getString(idNumb) +
                        ", DRname = " + cursor.getString(idDrName) +
                        ", PatName = " + cursor.getString(idPatName) +
                        ", History= " + cursor.getString(idHistory) +
                        ", Comment = " + cursor.getString(idComment) +
                        ", Photo = " + cursor.getString(idPhoto) +
                        ", IS SENT? " + cursor.getInt(idIsSent));
            } while (cursor.moveToNext());

        }else {
            Log.i("mlog","0 rows");
        }
    }

    // --- Вывод базы ACTSBAR в ЛОГ
    public static void outputMyDBActsBar(Cursor cursor) {
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ACTBARID);
            int barcodeIndex = cursor.getColumnIndex(DBHelper.KEY_BARCODESACTS);
            int photoIndex = cursor.getColumnIndex(DBHelper.KEY_PHOTO);
            int photoIndex2 = cursor.getColumnIndex(DBHelper.KEY_PHOTO2);



            do {
                Log.i("DATADASE LOG TABLE ACTS","ID = " + cursor.getInt(idIndex) +
                        ", Barcode = " + cursor.getString(barcodeIndex) +
                        ", Photo = " + cursor.getBlob(photoIndex) +
                        ", Photo = " + cursor.getBlob(photoIndex2));

            } while (cursor.moveToNext());

        }else {
            Log.i("mlog","0 rows");
        }
    }

    // --- Чтение существующей таблицы ACTS --- (Всё проверено)
    public static void readMyDatabaseActs(Cursor cursor) {
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int idDate = cursor.getColumnIndex(DBHelper.KEY_DATE);
            int idNumb = cursor.getColumnIndex(DBHelper.KEY_NUMB);
            int idDrName = cursor.getColumnIndex(DBHelper.KEY_DRNAME);
            int idPatName = cursor.getColumnIndex(DBHelper.KEY_PATNAME);
            int idHistory = cursor.getColumnIndex(DBHelper.KEY_HISTORY);
            int idComment = cursor.getColumnIndex(DBHelper.KEY_COMMENT);
            int idPhoto = cursor.getColumnIndex(DBHelper.KEY_ACTPHOTO);
            int idIsSent = cursor.getColumnIndex(DBHelper.KEY_ISSENT);

            do {
                ActsFragment.id_arrayList.add(cursor.getInt(idIndex));
                ActsFragment.dates_arrayList.add(cursor.getString(idDate));
                ActsFragment.uniq_arrayList.add(cursor.getInt(idNumb));
                ActsFragment.drName_arrayList.add(cursor.getString(idDrName));
                ActsFragment.patName_arrayList.add(cursor.getString(idPatName));
                ActsFragment.historyNumb_arrayList.add(cursor.getString(idHistory));
                ActsFragment.comments_arrayList.add(cursor.getString(idComment));
                ActsFragment.photoOfAct = cursor.getString(idPhoto);
                ActsFragment.outputs_arrayList.add(cursor.getString(idDate) + " " + cursor.getInt(idNumb) + " " + cursor.getString(idPatName));
                Log.i("IDDDDD ", "" + cursor.getInt(idNumb));
            } while (cursor.moveToNext());

        }else {
            Log.i("mlog","0 rows");
        }
    }

    public static void readDBActsForOutputOnly(Cursor cursor) {
        if (cursor.moveToFirst()) {
            int idDate = cursor.getColumnIndex(DBHelper.KEY_DATE);
            int idNumb = cursor.getColumnIndex(DBHelper.KEY_NUMB);
            int idPatName = cursor.getColumnIndex(DBHelper.KEY_PATNAME);

            do {
                ActsFragment.outputs_arrayList.add(cursor.getString(idDate) + " " + cursor.getInt(idNumb) + " " + cursor.getString(idPatName));
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
            int photoIndex1 = cursor.getColumnIndex(DBHelper.KEY_PHOTO);
            int photoIndex2 = cursor.getColumnIndex(DBHelper.KEY_PHOTO2);

            do {
                ActsEditorActivity.barcodes_listViewArray.add(cursor.getString(barcodeIndex));
                ActsEditorActivity.barcodes_arrayAdapter.notifyDataSetChanged();

            } while (cursor.moveToNext());

        }else {
            Log.i("mlog","0 rows");
        }
    }

    // Чтение уже существующей ACTSBAR для отправки данных
    public static void readDBActsBarForJSON (Cursor cursor) {

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ACTBARID);
            int barcodeIndex = cursor.getColumnIndex(DBHelper.KEY_BARCODESACTS);
            int photoIndex1 = cursor.getColumnIndex(DBHelper.KEY_PHOTO);
            int photoIndex2 = cursor.getColumnIndex(DBHelper.KEY_PHOTO2);

            do {
                ActsFragment.barcodes_arrayList.add(cursor.getString(barcodeIndex));
                ActsFragment.photo1_arrayList.add(cursor.getBlob(photoIndex1));
                ActsFragment.photo2_arrayList.add(cursor.getBlob(photoIndex2));

            } while (cursor.moveToNext());

        }else {
            Log.i("mlog","0 rows");
        }
    }

    public static void addToDatabaseActs(SQLiteDatabase db, int actID, int uniqId, String historyNum, String date, String patName, String drName, String photo, String comment) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_ACTID, actID);
        contentValues.put(DBHelper.KEY_NUMB, uniqId);
        contentValues.put(DBHelper.KEY_HISTORY, historyNum);
        contentValues.put(DBHelper.KEY_DATE, date);
        contentValues.put(DBHelper.KEY_PATNAME, patName);
        contentValues.put(DBHelper.KEY_DRNAME, drName);
        contentValues.put(DBHelper.KEY_COMMENT, comment);
        contentValues.put(DBHelper.KEY_ACTPHOTO, photo);
        contentValues.put(DBHelper.KEY_ISSENT, 0);

        db.insert(DBHelper.TABLE_ACTS, null, contentValues);

        Cursor cursor = db.query(DBHelper.TABLE_ACTS, null,null,null,null,null,null); //пока без сортировок и группировок, поэтому null
        DBHelper.outputMyDatabaseActs(cursor);

        cursor.moveToLast();
        int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
        Log.i("idIndex = ", "" + cursor.getInt(idIndex));

        ActsFragment.id_arrayList.add(cursor.getInt(idIndex));
        Log.i("Добавление", " в массив отработало");

        cursor.close();
    }

    public static void addToDBActsBar(SQLiteDatabase db, int actID, String barcode, byte[] photo, byte[] photo2) {
        ContentValues contentValues = new ContentValues();
        int id = ActsFragment.id_arrayList.get(actID);
        contentValues.put(DBHelper.KEY_ACTBARID, id);
        contentValues.put(DBHelper.KEY_BARCODESACTS, barcode);
        contentValues.put(DBHelper.KEY_PHOTO, photo);
        contentValues.put(DBHelper.KEY_PHOTO2, photo2);



        db.insert(DBHelper.TABLE_BARCODESACTS, null, contentValues);
    }

    public static void clearDatabase (SQLiteDatabase db, String tableName) {
        db.delete(tableName, null, null);
        Log.i("INFO","--- DATABASASE IS DELETED ---");
    }

    public static void deleteFromDatabase (SQLiteDatabase db, String tableName, int actID) {
        int posToDel = ActsFragment.id_arrayList.get(actID); //FIXME почему тут только 1 массив? Проверить не остались ли лишние данные в массиве

        db.delete(tableName, DBHelper.KEY_ID + "=" + posToDel, null);
        //db.delete(DBHelper.TABLE_ACTS, DBHelper.KEY_ID + "=" + posToDel, null);

        Log.i("INFO","--- Поcле удаления элемента id = " + posToDel + " из таблицы" + tableName);

        Cursor cursor = db.query(DBHelper.TABLE_ACTS, null,null,null,null,null,null);
        outputMyDatabaseActs(cursor);
        cursor.close();

        Cursor cursor1 = db.query(DBHelper.TABLE_BARCODESACTS, null,null,null,null,null,null);
        outputMyDBActsBar(cursor1);
        cursor1.close();

    }

    public static void upgradeDataAct (SQLiteDatabase db, int actID, int uniqId, String historyNumb, String date, String patName, String drName, String comment) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_ACTID, actID);
        contentValues.put(DBHelper.KEY_NUMB, uniqId);
        contentValues.put(DBHelper.KEY_HISTORY, historyNumb);
        contentValues.put(DBHelper.KEY_DATE, date);
        contentValues.put(DBHelper.KEY_PATNAME, patName);
        contentValues.put(DBHelper.KEY_DRNAME, drName);
        contentValues.put(DBHelper.KEY_COMMENT, comment);

        int posToUp = ActsFragment.id_arrayList.get(actID);
        db.update(DBHelper.TABLE_ACTS, contentValues, DBHelper.KEY_ID + "=" + posToUp, null);
        Log.i("INFO",actID + "--- IS UPDATED ---");

        Cursor cursor = db.query(DBHelper.TABLE_ACTS, null,null,null,null,null,null); //пока без сортировок и группировок, поэтому null
        outputMyDatabaseActs(cursor);
        cursor.close();
    }


}