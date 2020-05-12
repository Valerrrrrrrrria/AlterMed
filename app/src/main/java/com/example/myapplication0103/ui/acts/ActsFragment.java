package com.example.myapplication0103.ui.acts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication0103.Common;
import com.example.myapplication0103.DBHelper;
import com.example.myapplication0103.R;
import com.example.myapplication0103.SendRequest;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ActsFragment extends Fragment {

    private ActsViewModel actsViewModel;
    public static ArrayList<Integer> id_arrayList; // id документа в базе
    public static ArrayList<Integer> uniq_arrayList; // Список уникальных номеров документа поле KEY_NUMB
    public static ArrayList<String> patName_arrayList; // Список фамилий пациентов
    public static ArrayList<String> dates_arrayList; // Список дат актов
    public static ArrayList<String> comments_arrayList; // Список комментариев к акту
    public static ArrayList<String> drName_arrayList; // Список врачей
    public static ArrayList<String> historyNumb_arrayList; // Список историй болезни
    public static String photoOfAct;

    public static ArrayList<String> outputs_arrayList; // Список для вывода в ListView

    // Для JSON
    public static ArrayList<String> photo1_arrayList;
    public static ArrayList<String> photo2_arrayList;
    public static ArrayList<String> barcodes_arrayList;

    // Для querry
    String selection = null;

    public static ArrayAdapter outputs_arrayAdapter;
    DBHelper dbHelper;
    static SQLiteDatabase database;

    // Ответ сервера
    public static int responseCode;

    public static TextView info_textView;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        actsViewModel = ViewModelProviders.of(this).get(ActsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_acts, container, false);

        // Считываем hospitalID
        SharedPreferences sharedPref = root.getContext().getSharedPreferences(getString(R.string.logIn_pref_file), Context.MODE_PRIVATE);
        final String hospitalId = Common.getHospitalId(sharedPref);
        Log.i("hospitalId ", "" + hospitalId);

        final ListView acts_listView = (ListView) root.findViewById(R.id.acts_listView);
        info_textView = (TextView) root.findViewById(R.id.info_textView);
        id_arrayList = new ArrayList<>();
        uniq_arrayList = new ArrayList<>();
        patName_arrayList = new ArrayList<>();
        dates_arrayList = new ArrayList<>();
        comments_arrayList = new ArrayList<>();
        outputs_arrayList = new ArrayList<>();
        drName_arrayList = new ArrayList<>();
        historyNumb_arrayList = new ArrayList<>();


        photo1_arrayList = new ArrayList<>();
        photo2_arrayList = new ArrayList<>();
        barcodes_arrayList = new ArrayList<>();

        // --- Работа с базой ---
        dbHelper = new DBHelper(getContext()); //создали экземпляр
        database = dbHelper.getWritableDatabase();

        // Считываем БД, получаем массивы: acts_arrayList - с именами пациента, dates_arrayList - датами операций,
        // comments_arrayList - комментариями
        Cursor cursor = database.query(DBHelper.TABLE_ACTS, null,null,null,null,null,null); //пока без сортировок и группировок, поэтому null
        DBHelper.readMyDatabaseActs(cursor);
        cursor.close();
        // --- --- --- --- ---

        // ЗАГРУЖЕННЫЕ ШТРИХКОДЫ (НОМЕРА) Будут загружаться при открытии определенного акта по ID

        //acts_arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, acts_arrayList);
        //acts_listView.setAdapter(acts_arrayAdapter);
        outputs_arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, outputs_arrayList);
        acts_listView.setAdapter(outputs_arrayAdapter);


        acts_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), ActsEditorActivity.class);
                intent.putExtra("actId", position);
                intent.putExtra("uniq_id", uniq_arrayList.get(position));
                intent.putExtra("isUpdate", true);
                startActivityForResult(intent, 1);

            }
        });

        // --- Сейчас будем удалять по длинному клику ---
        acts_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int itemToDelete = position;
                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Вы уверены?")
                        .setMessage("Удалить выбранный акт?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DBHelper.deleteFromDatabase(database, DBHelper.TABLE_ACTS, itemToDelete);
                                DBHelper.deleteFromDatabase(database, DBHelper.TABLE_BARCODESACTS, itemToDelete);
                                patName_arrayList.remove(itemToDelete);
                                dates_arrayList.remove(itemToDelete);
                                comments_arrayList.remove(itemToDelete);
                                id_arrayList.remove(itemToDelete);
                                drName_arrayList.remove(itemToDelete);
                                historyNumb_arrayList.remove(itemToDelete);
                                uniq_arrayList.remove(itemToDelete);
                                outputs_arrayList.remove(itemToDelete);
                                photoOfAct = null;
                                outputs_arrayAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Отмена", null)
                        .show();
                return true;
            }
        });


        Button add_button = (Button) root.findViewById(R.id.add_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //создаем уникальный id и сохраняем
                SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.logIn_pref_file), Context.MODE_PRIVATE);

                int uniq_id = Common.getAndUpdateUniqueId(sharedPref);

                Intent intent = new Intent (getContext(), ActsEditorActivity.class);
                intent.putExtra("isUpdate", false);
                intent.putExtra("uniq_id", uniq_id);
                startActivityForResult(intent, 1);
            }
        });

        //final ArrayList<Integer> id_arrayListCopy = id_arrayList;
        // Очищаем БД
        Button clear_button = (Button) root.findViewById(R.id.clear_button);
        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper.clearDatabase(database, DBHelper.TABLE_ACTS);
                DBHelper.clearDatabase(database, DBHelper.TABLE_BARCODESACTS);
                patName_arrayList.clear();
                dates_arrayList.clear();
                comments_arrayList.clear();
                id_arrayList.clear();
                drName_arrayList.clear();
                historyNumb_arrayList.clear();
                uniq_arrayList.clear();
                outputs_arrayList.clear();
                outputs_arrayAdapter.notifyDataSetChanged();
                photoOfAct = null;
            }
        });

        final Button send_button = (Button) root.findViewById(R.id.send_button);
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                responseCode = 0;
                if (id_arrayList.size() != 0) {
                    // И тогда что же мы делаем?
                    // 0. блокируем кнопку отправки
                    send_button.setEnabled(false);
                    // --- И так по кругу
                    for (int i = 0; i< id_arrayList.size(); i++) {
                        // Скопировать id_arrayList и отправлять его
                        barcodes_arrayList.clear();
                        photo1_arrayList.clear();
                        photo2_arrayList.clear();

                        // Считываем коды и фото с базы
                        selection = "_id = " + id_arrayList.get(i);
                        Cursor c = database.query(DBHelper.TABLE_BARCODESACTS, null, selection, null, null,
                                null, null);
                        DBHelper.readDBActsBarForJSON(c);
                        c.close();

                        Log.i("ID = ", "" + id_arrayList.get(i));
                        for (int j = 0; j < barcodes_arrayList.size(); j++) {
                            Log.i("Barcode", barcodes_arrayList.get(j));
                            Log.i("Photo1", "" + photo1_arrayList.get(j));
                            Log.i("Photo2", "" + photo2_arrayList.get(j));
                        }

                        // 1. Создаем JSON объект
                        String message = null;
                        message = Common.createJSONForImpl(hospitalId, patName_arrayList.get(i), historyNumb_arrayList.get(i),
                                dates_arrayList.get(i), drName_arrayList.get(i), comments_arrayList.get(i),
                                photoOfAct, barcodes_arrayList, photo1_arrayList, photo2_arrayList);
                        Log.i("JSON", message);

                        // 2. Отправляем JSON объект
                        SendRequest sendRequest = new SendRequest(i, database, true, false, "valeria", "AZdWD89Ej6HNCUmV");
                        sendRequest.execute("https://test.4lpu.ru/upload_lua/", message);

                        // 3. Ждем ответа сервера
                        // 4. При 200 удаляем с базы!
                        // --- Делаем это до тех пор, пока не отправим все
                    }

                    // 5. Разблокируем кнопку
                    send_button.setEnabled(true);
                } else Log.i("INFO", "Нечего отправлять");
            }
        });


        //dbHelper.close();
        return root;
    }

}
