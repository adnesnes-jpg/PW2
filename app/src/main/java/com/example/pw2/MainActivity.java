package com.example.pw2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect Register button
        btnRegister = findViewById(R.id.btnRegister);

        // Open RegistrationActivity
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    RegistrationActivity.class
            );

            // Pass event information
            intent.putExtra("eventName", "Campus Technology Day");

            startActivity(intent);
        });

        // PANGGIL FUNGSI READ CONTENT PROVIDER
        readAllRegistrations();
    }

    // FUNGSI UNTUK BACA DATA (READ / QUERY)
    private void readAllRegistrations() {
        try {
            Cursor cursor = getContentResolver().query(
                    CampusContentProvider.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        int nameIndex = cursor.getColumnIndex("name");
                        int regNoIndex = cursor.getColumnIndex("reg_no");

                        if (nameIndex != -1 && regNoIndex != -1) {
                            String name = cursor.getString(nameIndex);
                            String regNo = cursor.getString(regNoIndex);
                            Log.d("READ_DATA", "Name: " + name + " | RegNo: " + regNo);
                        }
                    } while (cursor.moveToNext());

                    Toast.makeText(this, "Data Berjaya Dibaca! (READ)", Toast.LENGTH_SHORT).show();
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("READ_ERROR", "Error reading data: " + e.getMessage());
        }
    }
}