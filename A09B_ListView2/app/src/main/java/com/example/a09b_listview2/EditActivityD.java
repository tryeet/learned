package com.example.a09b_listview2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditActivityD extends AppCompatActivity {

    EditText editTitle, editDate, editPlace, editDetail;
    int iItem = -1;
    DBHelper dbHelper;
    int iID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        editTitle = findViewById(R.id.editTextName);
        editDate = findViewById(R.id.editTextDate);
        editPlace = findViewById(R.id.editTextPlace);
        editDetail = findViewById(R.id.editTextDetail);

        dbHelper = new DBHelper(this);
        Intent intentR = getIntent();
        iItem = intentR.getIntExtra("item", -1);
        iID = intentR.getIntExtra("id", 0);
        if(iItem != -1)
        {
            editTitle.setText(intentR.getStringExtra("schedule"));
            editDate.setText(intentR.getStringExtra("date"));
            editPlace.setText(intentR.getStringExtra("place"));
            editDetail.setText(intentR.getStringExtra("detail"));
        }

        Button btnSave, btnCancel;
        btnSave = findViewById(R.id.buttonSave);
        btnCancel = findViewById(R.id.buttonCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sName = editTitle.getText().toString().trim();
                String sDate = editDate.getText().toString().trim();
                String sPlace = editPlace.getText().toString().trim();
                String sDetail = editDetail.getText().toString().trim();

                if(sName.isEmpty() || sDate.isEmpty() || sPlace.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "????????? ???????????????.", Toast.LENGTH_LONG).show();
                    return;
                }

                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery(DBContract.SQL_SELECT_ID, new String[] {sName, sDate});
                if(cursor.getCount() != 0) // ????????? ???????????? ?????? ???????????? ??????.
                {
                    cursor.moveToNext();
                    if(iItem == -1) // ???????????? ?????? ???????????? ????????? ??????
                    {
                        Toast.makeText(getApplicationContext(), "????????? ????????? ????????????.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(iID != cursor.getInt(0)) // ?????? ?????? ?????? ????????? ??????, ??? ?????? ????????????.
                    {
                        Toast.makeText(getApplicationContext(), "????????? ????????? ????????????.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Intent intent = new Intent();
                intent.putExtra("item", iItem);
                intent.putExtra("id", iID);
                intent.putExtra("schedule", sName);
                intent.putExtra("date", sDate);
                intent.putExtra("place", sPlace);
                intent.putExtra("detail", sDetail);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    } // onCreate
}