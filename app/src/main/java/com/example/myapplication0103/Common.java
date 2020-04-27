package com.example.myapplication0103;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.myapplication0103.ui.acts.ActsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;
import java.util.regex.Pattern;

public class Common {

    public static long getTimeinMillis (String date) {
        long dateInMillis = -256;
        Log.i("DateIN", "" + date);

        String parts[] = date.split(Pattern.quote("."));

        Log.i("LENGTH", "" + parts.length);

        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        dateInMillis = calendar.getTimeInMillis();

        return dateInMillis;
    }

    public static int getAndUpdateUniqueId (SharedPreferences sharedPref) {
        String uniq_id_str = sharedPref.getString("idOfActUniq", "0");
        Integer uniq_id = Integer.parseInt(uniq_id_str);
        Log.i("SharedPref Uniq ID ", "" + uniq_id);

        uniq_id += 1;

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("idOfActUniq", uniq_id.toString());
        editor.commit();
        Log.i("NEW Uniq ID ", "" + uniq_id);

        return uniq_id;
    }

    public static String getDrName (SharedPreferences sharedPref) {
        return sharedPref.getString("drname", null);
    }

    public static String getHospitalId (SharedPreferences sharedPref) {
        return sharedPref.getString("organization", null);
    }

    public static void splitDualArray (ArrayList<String> barcodes_arrayHelper, final ArrayList<byte[]> pictures_arrayHelper,
                                      ArrayList<String> barcodes_array, final ArrayList<byte[]> pictures_array1, final ArrayList<byte[]> pictures_array2) {

        for (int i = 0; i < barcodes_arrayHelper.size(); i+=2) {
            barcodes_array.add(barcodes_arrayHelper.get(i) + barcodes_arrayHelper.get(i+1));
            pictures_array1.add(pictures_arrayHelper.get(i));
            pictures_array2.add(pictures_arrayHelper.get(i+1));
        }
    }

    public static void updateListViewArray (ArrayList<String> barcodes, ArrayList<String> dualBarcodes, ArrayList<String> barcodesForListView) {
        barcodesForListView.clear();

        if (barcodes.size() != 0) {
            for (int i = 0; i < barcodes.size(); i++) {
                barcodesForListView.add(barcodes.get(i));
            }
        } else Log.i("BARCodes ", "is empty");

        if (dualBarcodes.size()!= 0) {
            for (int j = 0; j < dualBarcodes.size(); j++) {
                barcodesForListView.add(dualBarcodes.get(j));
            }
        } else Log.i("DUAL ", "is empty");
    }

    public static String createJSONForImpl (String hospitalId, String fio, String cardNum, String date,
                                            String doctor, String comment, byte[] photo, ArrayList<String> barcodesArray,
                                            ArrayList<byte[]> barcodesPhoto1, ArrayList<byte[]> barcodesPhoto2) {

        // поля объекта
        // uuid, код организации, имя пациента, номер истории болезни, Дата операции, ФИО Врача,
        // Комментарий, Фото акта + массив штрихкодов и фото: barcode + photo1 + photo2;

        String uniqueID = UUID.randomUUID().toString(); // Создаем уникальный id

        String message;
        JSONObject json = new JSONObject();
        try {
            json.put("name", "Акт имплантации");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray array = new JSONArray();
        JSONObject item = new JSONObject();

        JSONArray barcodes = new JSONArray();
        try {
            item.put("uniqueId", uniqueID); // уникальный id
            item.put("hospital", hospitalId); // номер организации
            item.put("fio", fio); // имя пациента
            item.put("cardNum", cardNum); // номер истории болезни
            item.put("date", date); // дата операции
            item.put("doctor", doctor); // док ФИО
            item.put("comment", comment); // комментарий
            item.put("photo", photo); // фото акта

            //Сделать до количества элементов массива со штрихкодами
            for (int i = 0; i < barcodesArray.size(); i++) {
                JSONObject barcodeObject = new JSONObject();
                barcodeObject.put("barcode", barcodesArray.get(i));
                barcodeObject.put("photo1", barcodesPhoto1.get(i));
                barcodeObject.put("photo2", barcodesPhoto2.get(i));

                barcodes.put(barcodeObject);
            }

            item.put("components", barcodes);
            array.put(item);

            json.put("Act", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return message = json.toString(); // Возвращаем нашу JSON строку
    }

    public static void deleteFromArraysById (int id) {
        ActsFragment.id_arrayList.remove(id);
        ActsFragment.uniq_arrayList.remove(id);
        ActsFragment.patName_arrayList.remove(id);
        ActsFragment.dates_arrayList.remove(id);
        ActsFragment.comments_arrayList.remove(id);
        ActsFragment.outputs_arrayList.remove(id);
        ActsFragment.drName_arrayList.remove(id);
        ActsFragment.historyNumb_arrayList.remove(id);

        ActsFragment.outputs_arrayAdapter.notifyDataSetChanged();
    }
}
