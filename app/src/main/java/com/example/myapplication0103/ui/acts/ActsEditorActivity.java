package com.example.myapplication0103.ui.acts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication0103.BarcodeCaptureActivityImpl;
import com.example.myapplication0103.Common;
import com.example.myapplication0103.DBHelper;
import com.example.myapplication0103.R;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.sql.CommonDataSource;

public class ActsEditorActivity extends AppCompatActivity {
    private ActsViewModel actsViewModel;
    //private CompoundButton autoFocus;
    //private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView barcodeValue;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    int actId;
    int uniq_id;
    boolean isUpdate;

    // список для вывода в ListView
    public static ArrayList<String> barcodes_listViewArray = new ArrayList<String>();


    // списки для обычных штрихкодов
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
    public static ImageView test_imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acts_editor);

        barcodes_listView = (ListView) findViewById(R.id.barcodes_listView);
        barcodes_arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, barcodes_listViewArray);

        barcodes_listViewArray.clear();
        barcodes_array.clear();
        dualBarcodes_array.clear();
        dualBarcodes_arrayHelper.clear();
        dualBarcodes_pictures1.clear();
        dualBarcodes_pictures2.clear();
        dualBarcodes_picturesHelper.clear();
        ActsFragment.photoOfAct = null;


        // Переменная для query
        String selection = null;

        //test_imageView = (ImageView) findViewById(R.id.test_imageView);


        EditText patName_textView = (EditText) findViewById(R.id.patName_editText);
        EditText historyNum_textView = (EditText) findViewById(R.id.historyNumb_editText);
        final EditText date_textView = (EditText) findViewById(R.id.date_editText);
        EditText drName_textView = (EditText) findViewById(R.id.drName_editText);
        EditText comment_textView = (EditText) findViewById(R.id.comment_editText);
        final CalendarView myCalendar = (CalendarView) findViewById(R.id.calendarView);

        // Наши кнопочки
        final Button read_button = findViewById(R.id.read_barcode);
        final Button dualRead_button = findViewById(R.id.dualBarcode_button);


        Intent intent = getIntent();
        actId = intent.getIntExtra("actId", -1);
        isUpdate = intent.getBooleanExtra("isUpdate", false);
        uniq_id = intent.getIntExtra("uniq_id", -256); //получили уникальный id

        // Получаем имя доктора из SharedPref
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.logIn_pref_file), Context.MODE_PRIVATE);
        String drName = Common.getDrName(sharedPref);
        String hospitalId = Common.getHospitalId(sharedPref);


        if (actId != -1) {
            barcodes_array.clear();
            dualBarcodes_array.clear();
            barcodes_listViewArray.clear();
            //barcodes_arrayAdapter.notifyDataSetChanged();

            patName_textView.setText(ActsFragment.patName_arrayList.get(actId));
            date_textView.setText(ActsFragment.dates_arrayList.get(actId));
            comment_textView.setText(ActsFragment.comments_arrayList.get(actId));
            historyNum_textView.setText(ActsFragment.historyNumb_arrayList.get(actId));
            drName_textView.setText(ActsFragment.drName_arrayList.get(actId));

            // Отключаем кнопки
            read_button.setEnabled(false);
            dualRead_button.setEnabled(false);

            long dateInMilles = Common.getTimeinMillis(ActsFragment.dates_arrayList.get(actId));
            myCalendar.setDate(dateInMilles);
            //uniq_id = ActsFragment.uniq_arrayList.get(actId);
            // Вот тут нам нужно вывести наш массив листВью, для этого нам нужно считать данные с таблицы BARCODESACTS по ID = ID документа,
            // Это по id = id_arrayList(actID)


            Log.i("BARCODESACTS","Хотим добавить уже существующие значения кодов");
            Log.i("id = ", "" + ActsFragment.id_arrayList.get(actId));
            // Должны очистить массив штрихкодов
            int id = ActsFragment.id_arrayList.get(actId);

            selection = "_id = " + id;
            //selectionArgs = new String[]{Integer.toString(ActsFragment.id_arrayList.get(actId))};
            Cursor c = ActsFragment.database.query(DBHelper.TABLE_BARCODESACTS, null, selection, null, null,
                    null, null);
            DBHelper.readMyDBActsBar(c);
            c.close();


        } else {
            barcodes_array.clear();
            dualBarcodes_array.clear();
            barcodes_listViewArray.clear();
            //barcodes_arrayAdapter.notifyDataSetChanged();

            drName_textView.setText(drName); // Заносим имя доктора в поле ФИО Доктора

            // работаем с датой
            Calendar date = Calendar.getInstance();
            // for your date format use
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            // set a string to format your current date
            String curDate = sdf.format(date.getTime()); //сегодняшняя дата
            // print the date in your log cat
            Log.d("CUR_DATE", curDate);

            date_textView.setText(curDate);

            ActsFragment.patName_arrayList.add("");
            ActsFragment.dates_arrayList.add(curDate);
            ActsFragment.comments_arrayList.add("");
            ActsFragment.historyNumb_arrayList.add("");
            ActsFragment.drName_arrayList.add(drName);
            ActsFragment.uniq_arrayList.add(uniq_id);


            //ActsFragment.id_arrayList.add(null);
            actId = ActsFragment.patName_arrayList.size() - 1; // FIXME Странно, что не выдает ошибку при создании первого нового акта
            //ActsFragment.outputs_arrayAdapter.notifyDataSetChanged();

        }

        myCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String newDate = dayOfMonth+"."+month+"."+year;
                date_textView.setText(newDate);
                ActsFragment.dates_arrayList.set(actId, newDate);

                Log.i("DATE IS ", "" + newDate);

                long dateInMilles = Common.getTimeinMillis(newDate);
                myCalendar.setDate(dateInMilles);
                Log.i("NEW DATE IS ", "" + dateInMilles);
            }
        });



        // Листенеры полей

        patName_textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ActsFragment.patName_arrayList.set(actId, "" + s);
                //ActsFragment.outputs_arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        historyNum_textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ActsFragment.historyNumb_arrayList.set(actId,"" + s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        drName_textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ActsFragment.drName_arrayList.set(actId, "" + s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        comment_textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ActsFragment.comments_arrayList.set(actId, "" + s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Считываем одинарные штрихкоды
        read_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    // launch barcode activity.
                    Intent intent = new Intent(ActsEditorActivity.this, BarcodeCaptureActivityImpl.class);
                    intent.putExtra("isDual", 0);
                    intent.putExtra("isImplant", true);
                    //startActivityForResult(intent, RC_BARCODE_CAPTURE);
                    startActivity(intent);
            }
        });

        // Считываем двойные штрихкоды
        dualRead_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActsEditorActivity.this, BarcodeCaptureActivityImpl.class);
                intent.putExtra("isDual", 1);
                intent.putExtra("isImplant", true);
                //startActivityForResult(intent, RC_BARCODE_CAPTURE);
                startActivity(intent);
            }
        });


        Button showBarcodes_button = (Button) findViewById(R.id.showBarcodes_button);
        showBarcodes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.splitDualArray(dualBarcodes_arrayHelper, dualBarcodes_picturesHelper, dualBarcodes_array, dualBarcodes_pictures1, dualBarcodes_pictures2);

                if (barcodes_array.size()!=0 | dualBarcodes_array.size()!=0) Common.updateListViewArray(barcodes_array, dualBarcodes_array, barcodes_listViewArray);

                barcodes_listView.setAdapter(barcodes_arrayAdapter);
            }
        });

        final ImageView showPhoto_imageView = (ImageView) findViewById(R.id.showPhoto_imageView);

        final Button showPhoto_button = findViewById(R.id.showPhoto_button);
        showPhoto_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActsFragment.photoOfAct = null;
                int id = ActsFragment.id_arrayList.get(actId);

                Log.i("ID = ", "" + id);

                String selection1;
                selection1 = "_id = " + id;
                Cursor c = ActsFragment.database.query(DBHelper.TABLE_ACTS, null, selection1, null, null,
                        null, null);
                DBHelper.readMyDatabaseActs(c);
                c.close();

                byte[] newImg3 = Base64.decode(ActsFragment.photoOfAct, 0);
                InputStream inputStream2  = new ByteArrayInputStream(newImg3);
                Bitmap bitmap2  = BitmapFactory.decodeStream(inputStream2);
                Log.i("", "" + bitmap2.getWidth() + ", " + bitmap2.getHeight());

                showPhoto_imageView.setImageBitmap(bitmap2);


            }
        });

        final Button saveToActs_button = findViewById(R.id.saveToActs);
        saveToActs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("INFO actID = ", "" + actId);
                    if (isUpdate) {

                        ActsFragment.outputs_arrayList.set(actId, ActsFragment.dates_arrayList.get(actId) + " " + ActsFragment.uniq_arrayList.get(actId) + " " + ActsFragment.patName_arrayList.get(actId));
                        ActsFragment.outputs_arrayAdapter.notifyDataSetChanged();

                        DBHelper.upgradeDataAct(ActsFragment.database, actId, uniq_id, ActsFragment.historyNumb_arrayList.get(actId),
                                ActsFragment.dates_arrayList.get(actId), ActsFragment.patName_arrayList.get(actId), ActsFragment.drName_arrayList.get(actId),
                                ActsFragment.comments_arrayList.get(actId));
                        // ШТРИХКОДЫ МЕНЯТЬ НЕЛЬЗЯ!!!
                        
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();

                    } else {

                        ActsFragment.outputs_arrayList.add(ActsFragment.dates_arrayList.get(actId) + " " + ActsFragment.uniq_arrayList.get(actId) + " " + ActsFragment.patName_arrayList.get(actId));
                        ActsFragment.outputs_arrayAdapter.notifyDataSetChanged();

                        DBHelper.addToDatabaseActs(ActsFragment.database, actId, uniq_id, ActsFragment.historyNumb_arrayList.get(actId),
                                ActsFragment.dates_arrayList.get(actId), ActsFragment.patName_arrayList.get(actId), ActsFragment.drName_arrayList.get(actId), ActsFragment.photoOfAct,
                                ActsFragment.comments_arrayList.get(actId));

                        // Заполняем базу штрихкодами
                        if (barcodes_array.size() != 0) {

                            for (int i = 0; i < barcodes_array.size(); i++) {
                                DBHelper.addToDBActsBar(ActsFragment.database, actId, barcodes_array.get(i), barcodes_pictures.get(i), null);
                                Log.i("TABLE BARCODES", "хотим добавить штрихкод с номером" + ActsFragment.id_arrayList.get(actId) + " " + barcodes_array.get(i));
                            }
                        } else Log.i( "ИНФООООО", "Ничего не добавляем");

                        if (dualBarcodes_array.size() != 0) {
                            for (int i = 0; i < dualBarcodes_array.size(); i++) {
                                DBHelper.addToDBActsBar(ActsFragment.database, actId, dualBarcodes_array.get(i), dualBarcodes_pictures1.get(i), dualBarcodes_pictures2.get(i));
                                Log.i("TABLE BARCODES", "хотим добавить штрихкод с номером" + ActsFragment.id_arrayList.get(actId) + " " + dualBarcodes_array.get(i));
                            }
                        } else Log.i( "ИНФООООО", "Ничего не добавляем");


                            // Выведем базы для проверки
                            Log.i("DB", "Проверка после заполнения, кнопка СОХРАНИТЬ: ");

                            Cursor cursor = ActsFragment.database.query(DBHelper.TABLE_ACTS, null, null, null, null, null, null);
                            DBHelper.outputMyDatabaseActs(cursor);
                            cursor.close();

                            Cursor cursor1 = ActsFragment.database.query(DBHelper.TABLE_BARCODESACTS, null, null, null, null, null, null);
                            DBHelper.outputMyDBActsBar(cursor1);
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
