package com.example.a09b_listview2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivityD extends AppCompatActivity {
    ListView listView;
    ArrayList<HashMap<String,String>> listData;
    SimpleAdapter simpleAdapter;
    int iSelectedItem = -1; // 선택된 항목이 없다. (중요)

    int iSelectedID = 0;
    int iMaxID = 0; // 레코드들 중 제일 큰 값, iMaxID + 1

    DBHelper dbHelper;
    SQLiteDatabase db;

    ActivityResultContract<Intent, ActivityResult> contract;
    ActivityResultCallback<ActivityResult> callback;
    ActivityResultLauncher<Intent> launcher;

    private void loadTable() {
        listData.clear();
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(DBContract.SQL_LOAD, null);
        while (cursor.moveToNext()) {
            int nID = cursor.getInt(0);
            HashMap<String, String> hitem = new HashMap<>();
            hitem.put("id", String.valueOf(nID));
            hitem.put("schedule", cursor.getString(1));
            hitem.put("date", cursor.getString(2));
            hitem.put("place", cursor.getString(3));
            hitem.put("detail", cursor.getString(4));
            listData.add(hitem);
            iMaxID = Math.max(nID, iMaxID);
        }
        simpleAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         listView = findViewById(R.id.listView);
         listData = new ArrayList<>();
         simpleAdapter = new SimpleAdapter(this, listData,
                 R.layout.simple_list_item_activated_3,
                 new String[] {"schedule", "date", "place"},
                 new int[] {R.id.text1, R.id.text2, R.id.text3});
         listView.setAdapter(simpleAdapter);

         dbHelper = new DBHelper(this);
         loadTable();

         contract = new ActivityResultContracts.StartActivityForResult();
         callback = new ActivityResultCallback<ActivityResult>() {
             @Override
             public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK)
                {
                    Intent intentR = result.getData();
                    int iItem = intentR.getIntExtra("item", -1);
                    int iID = intentR.getIntExtra("id", 0);
                    HashMap<String,String> hitem = new HashMap<>();
                    hitem.put("schedule", intentR.getStringExtra("schedule"));
                    hitem.put("date", intentR.getStringExtra("date"));
                    hitem.put("place", intentR.getStringExtra("place"));
                    hitem.put("detail", intentR.getStringExtra("detail"));
                    ContentValues values = new ContentValues();
                    values.put("schedule", intentR.getStringExtra("schedule"));
                    values.put("date", intentR.getStringExtra("date"));
                    values.put("place", intentR.getStringExtra("place"));
                    values.put("detail", intentR.getStringExtra("detail"));

                    if(iItem == -1) // 추가
                    {
                        iMaxID++;
                        hitem.put("id", String.valueOf(iMaxID));
                        values.put("id", String.valueOf(iMaxID));
                        listData.add(hitem);
                        db = dbHelper.getWritableDatabase();
                        db.insert(DBContract.TABLE_NAME, null, values);
                    }
                    else // 수정
                    {
                        hitem.put("id",String.valueOf(iID));
                        values.put("id", String.valueOf(iID));
                        listData.set(iItem, hitem);
                        db = dbHelper.getWritableDatabase();
                        db.update(DBContract.TABLE_NAME, values, "id=" + iID, null);
                    }
                    simpleAdapter.notifyDataSetChanged();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show();
                }
             }
         };
         launcher = registerForActivityResult(contract, callback);

         listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 iSelectedItem = i;
                 HashMap<String,String> hitem = (HashMap<String, String>) simpleAdapter.getItem(iSelectedItem);
                 String sName = hitem.get("schedule");
                 Toast.makeText(getApplicationContext(), sName, Toast.LENGTH_LONG).show();
                 iSelectedID = Integer.parseInt(hitem.get("id"));
             }
         });

        Button btnInfo = findViewById(R.id.buttonInfo);
        btnInfo.setOnClickListener(new View.OnClickListener() { // Toast 구현
            @Override
            public void onClick(View view) {
                
            }
        });
    } // oncreate

    public void onClickAdd(View v)
    {
        Intent intent = new Intent(getApplicationContext(), EditActivityD.class);
        intent.putExtra("item", -1);
        intent.putExtra("id", 0);
        launcher.launch(intent);
    }

    public void onClickEdit(View v)
    {
        if(iSelectedItem == -1)
        {
            Toast.makeText(this, "선택한 항목이 없습니다.", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(getApplicationContext(), EditActivityD.class);
        intent.putExtra("item", iSelectedItem);
        intent.putExtra("id", iSelectedID);
        HashMap<String,String> hitem = (HashMap<String, String>) simpleAdapter.getItem(iSelectedItem);
        intent.putExtra("schedule", hitem.get("schedule"));
        intent.putExtra("date", hitem.get("date"));
        intent.putExtra("place", hitem.get("place"));
        intent.putExtra("detail", hitem.get("detail"));
        launcher.launch(intent);
    }
}