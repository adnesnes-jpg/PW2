package com.example.pw2;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {

    private EditText edtName;
    private EditText edtRegNo;
    private EditText edtEmail;
    private EditText edtPhone;
    private EditText edtProgramme;

    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration);

        // ==========================================
        // 1. CONNECT XML FIELDS
        // ==========================================

        edtName = findViewById(R.id.edtName);
        edtRegNo = findViewById(R.id.edtRegNo);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtProgramme = findViewById(R.id.edtProgramme);

        btnSubmit = findViewById(R.id.btnSubmit);


        // ==========================================
        // 2. SUBMIT BUTTON
        // ==========================================

        btnSubmit.setOnClickListener(v -> {

            // ==========================================
            // 3. READ INPUT VALUES
            // ==========================================

            String name = edtName.getText().toString().trim();
            String regNo = edtRegNo.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String programme = edtProgramme.getText().toString().trim();


            // ==========================================
            // 4. VALIDATE INPUT
            // ==========================================

            if (!validateInput(name, regNo, email, phone, programme)) {
                return;
            }


            // ==========================================
            // 5. GET EVENT NAME
            // ==========================================

            String eventName = getIntent().getStringExtra("eventName");

            if (eventName == null || eventName.isEmpty()) {
                eventName = "Unknown Event";
            }


            // ==========================================
            // 6. SIMPAN KE CONTENT PROVIDER (CREATE / INSERT)
            // ==========================================

            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("reg_no", regNo);
            values.put("email", email);
            values.put("phone", phone);
            values.put("programme", programme);

            try {
                Uri newUri = getContentResolver().insert(CampusContentProvider.CONTENT_URI, values);
                if (newUri != null) {
                    Toast.makeText(RegistrationActivity.this, "Data Disimpan ke Content Provider! (CREATE)", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            // ==========================================
            // 7. SAVE DATA LOCALLY (SHAREDPREFERENCES)
            // ==========================================

            SharedPreferences preferences =
                    getSharedPreferences("RegistrationData", MODE_PRIVATE);

            SharedPreferences.Editor editor = preferences.edit();

            editor.putString("name", name);
            editor.putString("regNo", regNo);
            editor.putString("eventName", eventName);

            editor.apply();


            // ==========================================
            // 8. SEND DATA TO CONFIRMATION ACTIVITY
            // ==========================================

            Intent intent = new Intent(
                    RegistrationActivity.this,
                    ConfirmationActivity.class
            );

            intent.putExtra("name", name);
            intent.putExtra("regNo", regNo);
            intent.putExtra("email", email);
            intent.putExtra("phone", phone);
            intent.putExtra("programme", programme);
            intent.putExtra("eventName", eventName);

            startActivity(intent);
        });
    }


    // ==============================================
    // VALIDATION METHOD
    // ==============================================
    private boolean validateInput(
            String name,
            String regNo,
            String email,
            String phone,
            String programme) {

        if (name.isEmpty()) {
            edtName.setError("Name is required");
            edtName.requestFocus();
            return false;
        }

        if (regNo.isEmpty()) {
            edtRegNo.setError("Registration number is required");
            edtRegNo.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            edtEmail.setError("Email is required");
            edtEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Enter a valid email address");
            edtEmail.requestFocus();
            return false;
        }

        if (phone.isEmpty()) {
            edtPhone.setError("Phone number is required");
            edtPhone.requestFocus();
            return false;
        }

        if (!Patterns.PHONE.matcher(phone).matches()) {
            edtPhone.setError("Enter a valid phone number");
            edtPhone.requestFocus();
            return false;
        }

        if (programme.isEmpty()) {
            edtProgramme.setError("Programme is required");
            edtProgramme.requestFocus();
            return false;
        }

        return true;
    }
}