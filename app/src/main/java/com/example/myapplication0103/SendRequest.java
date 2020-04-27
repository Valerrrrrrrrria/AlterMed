package com.example.myapplication0103;

import android.app.Activity;
import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication0103.ui.acts.ActsFragment;

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

    public SendRequest (int i, SQLiteDatabase database) {
        m_i = i;
        m_database = database;
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

        if (responseCode == 200) {
            // Удалисть с базы
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
        Cursor cursor = m_database.query(DBHelper.TABLE_ACTS, null,null,null,null,null,null); //пока без сортировок и группировок, поэтому null
        DBHelper.readDBActsForOutputOnly(cursor);
        cursor.close();

        ActsFragment.outputs_arrayAdapter.notifyDataSetChanged();


        // Вот сюда.

    }
}
