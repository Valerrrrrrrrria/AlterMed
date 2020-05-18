package com.example.myapplication0103;

import android.app.Activity;
import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Base64;
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

public class SendRequestPhoto extends AsyncTask<String, Void, String> {
    int m_i;
    SQLiteDatabase m_database;
    int responseCode = 0;
    final private int connectionTimeoutMs = 10000;
    boolean m_isImpl = false;
    boolean m_isInvent = false;
    String m_user;
    String m_pass;
    String m_photo;

    public SendRequestPhoto (String user, String pass) {
        m_pass = pass;
        m_user = user;
    }


    @Override
    protected String doInBackground(String... params) {

        try {
            String url = params[0];

            URL obj = new URL(url);
            String authStr = m_user + ":" + m_pass;

            HttpURLConnection urlConnection = (HttpURLConnection) obj.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            //urlConnection.setRequestProperty("Accept-Language", "en-US,en,ru,q=0.5");
            urlConnection.setRequestProperty("Content-Type", "application/xml");
            //urlConnection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
            urlConnection.setRequestProperty("Authorization", "Basic " + Base64.encodeToString(authStr.getBytes(), Base64.NO_WRAP));
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
                Log.i("INFO", "Изображение отправлено");

            } else {
                Log.i("INFO", "Изображение не отправлено");
            }

    }
}
