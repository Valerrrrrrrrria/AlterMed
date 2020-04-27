package com.example.myapplication0103.ui.inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapplication0103.BarcodeCaptureActivityImpl;
import com.example.myapplication0103.Common;
import com.example.myapplication0103.DBHelperInvent;
import com.example.myapplication0103.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class InventEditorActivity extends AppCompatActivity {

    int actId;
    int uniq_id;
    boolean isUpdate;

    // список для вывода в ListView
    public static ArrayList<String> barcodes_listViewArray = new ArrayList<String>();

    // Списки для одинарных штрихкодов
    public static ArrayList<String> barcodes_array = new ArrayList<String>();
    public static ArrayList<String> barcodes_pictures = new ArrayList<String>();

    // списки для двойных штрихкодов
    public static ArrayList<String> dualBarcodes_array = new ArrayList<String>();
    public static ArrayList<String> dualBarcodes_arrayHelper = new ArrayList<String>();

    // списки для хранения фото двойных штрихкодов
    public static ArrayList<String> dualBarcodes_pictures1 = new ArrayList<String>();
    public static ArrayList<String> dualBarcodes_pictures2 = new ArrayList<String>();
    public static ArrayList<String> dualBarcodes_picturesHelper = new ArrayList<String>();

    public static ArrayAdapter barcodes_arrayAdapter;
    public static ListView barcodes_listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invent_editor);

        barcodes_listView = (ListView) findViewById(R.id.inventBarcodes_listView);
        barcodes_arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, barcodes_listViewArray);

        barcodes_listViewArray.clear();
        barcodes_array.clear();
        dualBarcodes_array.clear();
        dualBarcodes_arrayHelper.clear();
        dualBarcodes_pictures1.clear();
        dualBarcodes_pictures2.clear();
        dualBarcodes_picturesHelper.clear();

        // Переменная для query
        String selection = null;

        // Наши кнопочки
        final Button read_button = findViewById(R.id.beginInvent_button);
        final Button dualRead_button = findViewById(R.id.dualBarcode_button2);

        final EditText date_textView = (EditText) findViewById(R.id.dateOfInvent_editText);
        EditText comment_textView = (EditText) findViewById(R.id.commentOfInvent_editText);
        final CalendarView myCalendar = (CalendarView) findViewById(R.id.calendarView2);

        Intent intent = getIntent();
        actId = intent.getIntExtra("actId", -1);
        isUpdate = intent.getBooleanExtra("isUpdate", false);
        uniq_id = intent.getIntExtra("uniq_id", -256); //получили уникальный id

        if (actId != -1) {
            // Акт у нас уже имеется
            barcodes_array.clear();
            dualBarcodes_array.clear();
            barcodes_listViewArray.clear();

            date_textView.setText(InventoryFragment.dates_arrayList.get(actId));
            comment_textView.setText(InventoryFragment.comments_arrayList.get(actId));

            // Отключаем кнопки
            read_button.setEnabled(false);
            dualRead_button.setEnabled(false);

            long dateInMilles = Common.getTimeinMillis(InventoryFragment.dates_arrayList.get(actId));
            myCalendar.setDate(dateInMilles);

            Log.i("BARCODESACTS","Хотим добавить уже существующие значения кодов");
            Log.i("id = ", "" + InventoryFragment.id_arrayList.get(actId));
            // Должны очистить массив штрихкодов
            int id = InventoryFragment.id_arrayList.get(actId);

            selection = "_id = " + id;
            Cursor c = InventoryFragment.database.query(DBHelperInvent.TABLE_BARCODES, null, selection, null, null,
                    null, null);
            DBHelperInvent.readMyDBActsBar(c);
            c.close();

        } else {
            // Создаем новый акт
            barcodes_array.clear();
            dualBarcodes_array.clear();
            barcodes_listViewArray.clear();

            // работаем с датой
            Calendar date = Calendar.getInstance();
            // for your date format use
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            // set a string to format your current date
            String curDate = sdf.format(date.getTime()); //сегодняшняя дата
            // print the date in your log cat
            Log.d("CUR_DATE", curDate);

            date_textView.setText(curDate);

            InventoryFragment.dates_arrayList.add(curDate);
            InventoryFragment.comments_arrayList.add("");
            InventoryFragment.uniq_arrayList.add(uniq_id);


            actId = InventoryFragment.id_arrayList.size(); //вот тут поменяла
        }

        myCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String newDate = dayOfMonth+"."+month+"."+year;
                date_textView.setText(newDate);
                InventoryFragment.dates_arrayList.set(actId, newDate);

                Log.i("DATE IS ", "" + newDate);

                long dateInMilles = Common.getTimeinMillis(newDate);
                myCalendar.setDate(dateInMilles);
                Log.i("NEW DATE IS ", "" + dateInMilles);
            }
        });

        // Листенеры полей
        comment_textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                InventoryFragment.comments_arrayList.set(actId, "" + s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Считываем одинарные штрихкоды
        read_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InventEditorActivity.this, BarcodeCaptureActivityImpl.class);
                intent.putExtra("isDual", 0);
                intent.putExtra("isInvent", true);
                //startActivityForResult(intent, RC_BARCODE_CAPTURE);
                startActivity(intent);
            }
        });

        // Считываем двойные штрихкоды
        dualRead_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InventEditorActivity.this, BarcodeCaptureActivityImpl.class);
                intent.putExtra("isDual", 1);
                intent.putExtra("isInvent", true);
                //startActivityForResult(intent, RC_BARCODE_CAPTURE);
                startActivity(intent);
            }
        });

        Button showBarcodes_button = (Button) findViewById(R.id.showBarcodes_button2);
        showBarcodes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.splitDualArray(dualBarcodes_arrayHelper, dualBarcodes_picturesHelper, dualBarcodes_array, dualBarcodes_pictures1, dualBarcodes_pictures2);

                if (barcodes_array.size()!=0 | dualBarcodes_array.size()!=0) Common.updateListViewArray(barcodes_array, dualBarcodes_array, barcodes_listViewArray);

                barcodes_listView.setAdapter(barcodes_arrayAdapter);
            }
        });

        Button saveInvent_button = (Button) findViewById(R.id.saveInvent_button);
        saveInvent_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO actID = ", "" + actId);
                if (isUpdate) {

                    InventoryFragment.outputs_arrayList.set(actId, InventoryFragment.dates_arrayList.get(actId) + " " + InventoryFragment.uniq_arrayList.get(actId));
                    InventoryFragment.outputs_arrayAdapter.notifyDataSetChanged();

                    DBHelperInvent.upgradeDataAct(InventoryFragment.database, actId, uniq_id,
                            InventoryFragment.dates_arrayList.get(actId),
                            InventoryFragment.comments_arrayList.get(actId));
                    // ШТРИХКОДЫ МЕНЯТЬ НЕЛЬЗЯ!!!

                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();

                } else {

                    InventoryFragment.outputs_arrayList.add(InventoryFragment.dates_arrayList.get(actId) + " " + InventoryFragment.uniq_arrayList.get(actId));
                    InventoryFragment.outputs_arrayAdapter.notifyDataSetChanged();

                    DBHelperInvent.addToDBActs(InventoryFragment.database, actId, uniq_id,
                            InventoryFragment.dates_arrayList.get(actId),
                            InventoryFragment.comments_arrayList.get(actId));

                    // Заполняем базу штрихкодами
                    if (barcodes_array.size() != 0) {

                        for (int i = 0; i < barcodes_array.size(); i++) {
                            DBHelperInvent.addToDBBarcodes(InventoryFragment.database, actId, barcodes_array.get(i), barcodes_pictures.get(i), null);
                            Log.i("TABLE BARCODES", "хотим добавить штрихкод с номером" + InventoryFragment.id_arrayList.get(actId) + " " + barcodes_array.get(i));
                        }
                    } else Log.i( "ИНФООООО", "Ничего не добавляем");

                    if (dualBarcodes_array.size() != 0) {
                        for (int i = 0; i < dualBarcodes_array.size(); i++) {
                            DBHelperInvent.addToDBBarcodes(InventoryFragment.database, actId, dualBarcodes_array.get(i), dualBarcodes_pictures1.get(i), dualBarcodes_pictures2.get(i));
                            Log.i("TABLE BARCODES", "хотим добавить штрихкод с номером" + InventoryFragment.id_arrayList.get(actId) + " " + dualBarcodes_array.get(i));
                        }
                    } else Log.i( "ИНФООООО", "Ничего не добавляем");


                    // Выведем базы для проверки
                    Log.i("DB", "Проверка после заполнения, кнопка СОХРАНИТЬ: ");

                    Cursor cursor = InventoryFragment.database.query(DBHelperInvent.TABLE_ACTS, null, null, null, null, null, null);
                    DBHelperInvent.outputDBActs(cursor);
                    cursor.close();

                    Cursor cursor1 = InventoryFragment.database.query(DBHelperInvent.TABLE_BARCODES, null, null, null, null, null, null);
                    DBHelperInvent.outputDBBarcodes(cursor1);
                    cursor1.close();

                    barcodes_listViewArray.clear();
                    barcodes_array.clear();
                    dualBarcodes_array.clear();
                    dualBarcodes_arrayHelper.clear();
                    dualBarcodes_pictures1.clear();
                    dualBarcodes_pictures2.clear();
                    dualBarcodes_picturesHelper.clear();

                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();

                }
            }
        });
    }
}
