package com.example.myapplication0103;

import android.app.Activity;
import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication0103.ui.acts.ActsFragment;
import com.example.myapplication0103.ui.inventory.InventoryFragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendRequest extends AsyncTask<String, Void, String> {
    int m_i;
    SQLiteDatabase m_database;
    int responseCode = 0;
    final private int connectionTimeoutMs = 10000;
    boolean m_isImpl = false;
    boolean m_isInvent = false;

    public SendRequest (int i, SQLiteDatabase database, boolean isImpl, boolean isInvent) {
        m_i = i;
        m_database = database;
        m_isImpl = isImpl;
        m_isInvent = isInvent;
    }


    @Override
    protected String doInBackground(String... params) {

        try {
            String url = params[0];

            URL obj = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) obj.openConnection();
            urlConnection.setRequestMethod("POST");
            // FIXME где разрывать connection?

            urlConnection.setRequestProperty("Accept-Language", "en-US,en,ru,q=0.5"); // Добавила русский
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setConnectTimeout(connectionTimeoutMs);

            String urlParameters = params[1]; // 0 - url, 1 - имя, 3 - то, что передаем

            // Записываем значение urlParameters в запрос
            DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(urlParameters);

            writer.close();
            outputStream.close();

            // Получение информации из потока ответа
            responseCode = urlConnection.getResponseCode(); // код ответа сервера
            Log.i("ResponseCode", "" + responseCode);


        } catch (Exception e) {
            Log.i("Exception", e.getLocalizedMessage());
            return e.getMessage();
        }
        return Integer.toString(responseCode);
    }

    @Override
    protected void onPostExecute(String message) {

        Log.i("INFO","Мы в onPostExecute");
        Log.i("response = ","" + responseCode);
if (m_isImpl) {
    if (responseCode == 200) {
        // Удалить с базы
        Log.i("Size", "" + ActsFragment.id_arrayList.size());
        Log.i("", "" + m_i);
        Log.i("id_arrayList.get(m_i) =", "" + ActsFragment.id_arrayList.get(m_i));
        DBHelper.deleteFromDatabase(m_database, DBHelper.TABLE_ACTS, m_i);
        DBHelper.deleteFromDatabase(m_database, DBHelper.TABLE_BARCODESACTS, m_i);

        // Удалить с массивов
        //Common.deleteFromArraysById(0);

        ActsFragment.info_textView.setText("Данные отправлены");

    } else {
        Log.i("INFO", "Данные не отправлены, попробуйте позднее");
        ActsFragment.info_textView.setText("Данные не отправлены, попробуйте позднее");
    }


    ActsFragment.outputs_arrayList.clear();

    // Читаем базу и выводим, если что-то осталось
    Cursor cursor = m_database.query(DBHelper.TABLE_ACTS, null, null, null, null, null, null); //пока без сортировок и группировок, поэтому null
    DBHelper.readDBActsForOutputOnly(cursor);
    cursor.close();

    ActsFragment.outputs_arrayAdapter.notifyDataSetChanged();
}


        if (m_isInvent) {

            if (responseCode == 200) {
                // Удалить с базы
                Log.i("Size", "" + InventoryFragment.id_arrayList.size());
                Log.i("", "" + m_i);
                Log.i("id_arrayList.get(m_i) =", "" + InventoryFragment.id_arrayList.get(m_i));
                DBHelperInvent.deleteFromDatabase(m_database, DBHelperInvent.TABLE_ACTS, m_i);
                DBHelperInvent.deleteFromDatabase(m_database, DBHelperInvent.TABLE_BARCODES, m_i);

                // Удалить с массивов
                //Common.deleteFromArraysById(0);

                InventoryFragment.info_textView.setText("Данные отправлены");

            } else {
                Log.i("INFO", "Данные не отправлены, попробуйте позднее");
                InventoryFragment.info_textView.setText("Данные не отправлены, попробуйте позднее");
            }


            InventoryFragment.outputs_arrayList.clear();

            // Читаем базу и выводим, если что-то осталось
            Cursor cursor = m_database.query(DBHelperInvent.TABLE_ACTS, null, null, null, null, null, null); //пока без сортировок и группировок, поэтому null
            DBHelperInvent.readDBActsForOutputOnly(cursor);
            cursor.close();

            InventoryFragment.outputs_arrayAdapter.notifyDataSetChanged();

        }

    }
}
