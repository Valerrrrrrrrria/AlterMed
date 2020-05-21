package com.example.myapplication0103;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Xml;

import com.example.myapplication0103.ui.acts.ActsFragment;
import com.example.myapplication0103.ui.inventory.InventoryFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlSerializer;

import java.io.StringWriter;
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

    public static void splitDualArray (ArrayList<String> barcodes_arrayHelper, final ArrayList<String> pictures_arrayHelper,
                                      ArrayList<String> barcodes_array, final ArrayList<String> pictures_array1, final ArrayList<String> pictures_array2) {

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

    public static String createXMLForImplWithPhoto (Integer actId, String hospitalId, String fio, String cardNum, String date,
                                            String doctor, String comment, String photo, ArrayList<String> barcodesArray,
                                            ArrayList<String> barcodesPhoto1, ArrayList<String> barcodesPhoto2) {

        // поля объекта
        // uuid, код организации, имя пациента, номер истории болезни, Дата операции, ФИО Врача,
        // Комментарий, Фото акта + массив штрихкодов и фото: barcode + photo1 + photo2;

        String uniqueID = UUID.randomUUID().toString(); // Создаем уникальный id
        String message; // ответ

        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "implantationAct");

            serializer.startTag("", "id");
            serializer.text(uniqueID);
            serializer.endTag("", "id");

            serializer.startTag("", "act_num"); // Вот сюда передать номер
            serializer.text(Integer.toString(actId));
            serializer.endTag("", "act_num");

            serializer.startTag("", "card_num");
            serializer.text(cardNum);
            serializer.endTag("", "card_num");

            serializer.startTag("", "date");
            serializer.text(date);
            serializer.endTag("", "date");

            serializer.startTag("", "fio");
            serializer.text(fio);
            serializer.endTag("", "fio");

            serializer.startTag("", "doctor");
            serializer.text(doctor);
            serializer.endTag("", "doctor");

            serializer.startTag("", "hospital");
            serializer.text(hospitalId);
            serializer.endTag("", "hospital");

            serializer.startTag("", "comment");
            serializer.text(comment);
            serializer.endTag("", "comment");

            // Если фото не нулевое, то мы будем отправлять запрос на сервер и получать обратно URL
            // То есть тут будет SendReguest на сервер, POST c ожиданиеам ответа от сервера

            // Вот тут вызвать
            String newUrl = sendPhoto(photo);

            serializer.startTag("", "photo");
            if (photo != null) serializer.text(newUrl); // сюда мы вставляем url
            else serializer.text("");
            serializer.endTag("", "photo");



            serializer.startTag("", "components");

            for (int i = 0; i < barcodesArray.size(); i++) {

                serializer.startTag("", "component");

                serializer.startTag("", "code");
                serializer.text(barcodesArray.get(i));
                serializer.endTag("", "code");

                newUrl = sendPhoto(barcodesPhoto1.get(i));
                serializer.startTag("", "photo1");
                // Если фото не нулевое, то мы будем отправлять запрос на сервер и получать обратно URL
                // То есть тут будет SendReguest на сервер, POST c ожиданиеам ответа от сервера
                if (barcodesPhoto1.get(i) != null) serializer.text(newUrl);
                else serializer.text("");
                serializer.endTag("", "photo1");

                newUrl = sendPhoto(barcodesPhoto2.get(i));
                serializer.startTag("", "photo2");
                // Если фото не нулевое, то мы будем отправлять запрос на сервер и получать обратно URL
                // То есть тут будет SendReguest на сервер, POST c ожиданиеам ответа от сервера
                if (barcodesPhoto2.get(i) != null) serializer.text(newUrl);
                else serializer.text("");
                serializer.endTag("", "photo2");

                serializer.endTag("", "component");

            }


            serializer.endTag("", "components");


            serializer.endTag("", "implantationAct");
            serializer.endDocument();
            return writer.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибочка вышла";
        }

    }


    public static String createXMLForInvent (Integer actId, String hospitalId, String date, String comment, ArrayList<String> barcodesArray,
                                            ArrayList<String> barcodesPhoto1, ArrayList<String> barcodesPhoto2) {

        // поля объекта
        // uuid, код организации, имя пациента, номер истории болезни, Дата операции, ФИО Врача,
        // Комментарий, Фото акта + массив штрихкодов и фото: barcode + photo1 + photo2;

        String uniqueID = UUID.randomUUID().toString(); // Создаем уникальный id
        String message; // ответ
        String newUrl = null;

        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "invent");

            serializer.startTag("", "id");
            serializer.text(uniqueID);
            serializer.endTag("", "id");

            serializer.startTag("", "act_num"); // Вот сюда передать номер
            serializer.text(Integer.toString(actId));
            serializer.endTag("", "act_num");


            serializer.startTag("", "date");
            serializer.text(date);
            serializer.endTag("", "date");


            serializer.startTag("", "hospital");
            serializer.text(hospitalId);
            serializer.endTag("", "hospital");

            serializer.startTag("", "comment");
            serializer.text(comment);
            serializer.endTag("", "comment");

            serializer.startTag("", "components");

            for (int i = 0; i < barcodesArray.size(); i++) {

                serializer.startTag("", "component");

                serializer.startTag("", "code");
                serializer.text(barcodesArray.get(i));
                serializer.endTag("", "code");

                newUrl = sendPhoto(barcodesPhoto1.get(i));
                serializer.startTag("", "photo1");
                if (barcodesPhoto1.get(i) != null) serializer.text(newUrl);
                else serializer.text("");
                serializer.endTag("", "photo1");

                newUrl = sendPhoto(barcodesPhoto2.get(i));
                serializer.startTag("", "photo2");
                if (barcodesPhoto2.get(i) != null) serializer.text(newUrl);
                else serializer.text("");
                serializer.endTag("", "photo2");

                serializer.endTag("", "component");

            }


            serializer.endTag("", "components");


            serializer.endTag("", "invent");
            serializer.endDocument();

            return writer.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибочка вышла";
        }
    }

    public static String sendPhoto(String photo) {
        SendRequestPhoto sendRequestPhoto = new SendRequestPhoto("valeria", "AZdWD89Ej6HNCUmV");
        String photoName = UUID.randomUUID().toString();
        String url = "https://test.4lpu.ru/upl-img/" + photoName + ".b64";
        sendRequestPhoto.execute(url, photo);
        return "https://test.4lpu.ru/data/img/" + photoName + ".b64";
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

    public static void clearArrays() {
        ActsFragment.patName_arrayList.clear();
        ActsFragment.dates_arrayList.clear();
        ActsFragment.comments_arrayList.clear();
        ActsFragment.id_arrayList.clear();
        ActsFragment.drName_arrayList.clear();
        ActsFragment.historyNumb_arrayList.clear();
        ActsFragment.uniq_arrayList.clear();
        ActsFragment.outputs_arrayList.clear();
        ActsFragment.photoOfAct = null;
    }

    public static void clearArraysInvent() {
        InventoryFragment.dates_arrayList.clear();
        InventoryFragment.comments_arrayList.clear();
        InventoryFragment.id_arrayList.clear();
        InventoryFragment.uniq_arrayList.clear();
        InventoryFragment.outputs_arrayList.clear();
    }
}
