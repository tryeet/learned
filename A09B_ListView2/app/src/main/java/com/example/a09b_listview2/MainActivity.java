package com.example.a09b_listview2;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<HashMap<String,String>> listData;
    SimpleAdapter simpleAdapter;
    int iSelectedItem = -1; // 선택된 항목이 없다. (중요)

    ActivityResultContract<Intent, ActivityResult> contract;
    ActivityResultCallback<ActivityResult> callback;
    ActivityResultLauncher<Intent> launcher;

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

         contract = new ActivityResultContracts.StartActivityForResult();
         callback = new ActivityResultCallback<ActivityResult>() {
             @Override
             public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK)
                {
                    Intent intentR = result.getData();
                    int iItem = intentR.getIntExtra("item", -1);
                    HashMap<String,String> hitem = new HashMap<>();
                    hitem.put("schedule", intentR.getStringExtra("schedule"));
                    hitem.put("date", intentR.getStringExtra("date"));
                    hitem.put("place", intentR.getStringExtra("place"));
                    hitem.put("detail", intentR.getStringExtra("detail"));
                    if(iItem == -1)
                        listData.add(hitem);
                    else
                        listData.set(iItem, hitem);
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
             }
         });
    } // oncreate

    public void onClickAdd(View v)
    {
        Intent intent = new Intent(getApplicationContext(), EditActivity.class);
        intent.putExtra("item", -1);
        launcher.launch(intent);
    }

    public void onClickEdit(View v)
    {
        if(iSelectedItem == -1)
        {
            Toast.makeText(this, "선택한 항목이 없습니다.", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(getApplicationContext(), EditActivity.class);
        intent.putExtra("item", iSelectedItem);
        HashMap<String,String> hitem = (HashMap<String, String>) simpleAdapter.getItem(iSelectedItem);
        intent.putExtra("schedule", hitem.get("schedule"));
        intent.putExtra("date", hitem.get("date"));
        intent.putExtra("place", hitem.get("place"));
        intent.putExtra("detail", hitem.get("detail"));
        launcher.launch(intent);
    }
}