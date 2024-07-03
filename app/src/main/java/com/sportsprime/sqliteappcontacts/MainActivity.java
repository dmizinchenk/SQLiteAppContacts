package com.sportsprime.sqliteappcontacts;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final String LOG_TAG = "myLogs";
    Button btnAdd,btnRead,btnClear;
    EditText etName,etEmail;
    RecyclerView recyclerView;
    ArrayList<Person> persons = new ArrayList<>();

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAdd = findViewById(R.id.btnAdd);
        btnRead = findViewById(R.id.btnRead);
        btnClear = findViewById(R.id.btnClear);

        btnAdd.setOnClickListener(this);
        btnRead.setOnClickListener(this);
        btnClear.setOnClickListener(this);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);

        dbHelper = new DBHelper(this);

        PersonAdapter pa = new PersonAdapter(this, persons);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setAdapter(pa);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("mytable",null,null,null,null,null,null);

        if (c.moveToFirst()) {
            int nameColIndex = c.getColumnIndex("name");
            int emailColIndex = c.getColumnIndex("email");

            do {
                persons.add(new Person(c.getString(nameColIndex), c.getString(emailColIndex)));
            } while (c.moveToNext());
        }

        dbHelper.close();
    }

    @Override
    public void onClick(View v) {
        ContentValues cv = new ContentValues();
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        Person p = new Person(name, email);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (v.getId()){
            case(R.id.btnAdd):
                Log.d(LOG_TAG,"----- Insert in mytable: ---");
                persons.add(p);
                recyclerView.getAdapter().notifyDataSetChanged();
//                PersonAdapter pa = new PersonAdapter(this, persons);
//                recyclerView.setAdapter(pa);
              cv.put("name",p.getName());
              cv.put("email",p.getEmail());
              long rowId = db.insert("mytable",null,cv);
                Log.d(LOG_TAG,"----- row inserted, ID = " + rowId);
                break;
            case (R.id.btnRead):
                Log.d(LOG_TAG,"----- Rows in mytable: ---");
                Cursor c = db.query("mytable",null,null,null,null,null,null);
                if (c.moveToFirst()){
                    int idColIndex = c.getColumnIndex("id");
                    int nameColIndex = c.getColumnIndex("name");
                    int emailColIndex = c.getColumnIndex("email");

                    do {
                        Log.d(LOG_TAG,"ID = " + c.getInt(idColIndex)+
                                ", name = " + c.getString(nameColIndex)+
                                ", email = " + c.getString(emailColIndex));
                    }while (c.moveToNext());
                }else
                    Log.d(LOG_TAG,"0 rows");
                c.close();
                break;
            case(R.id.btnClear):
                Log.d(LOG_TAG,"---- Clear mytable: ---");
                int clearCount = db.delete("mytable",null,null);
                Log.d(LOG_TAG,"deleted rows count = " + clearCount);
                persons.clear();
                recyclerView.getAdapter().notifyDataSetChanged();
                break;
        }
        dbHelper.close();
    }
}