package com.example.pw2;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ConfirmationActivity extends AppCompatActivity {

    private TextView txtName;
    private TextView txtRegNo;
    private TextView txtEmail;
    private TextView txtPhone;
    private TextView txtProgramme;

    private TextView txtEvent;
    private TextView txtStatus;

    private Button btnHome;
    private Button btnUpdate;
    private Button btnDelete;

    private String name, regNo, email, phone, programme, eventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        // 1. HUBUNGKAN KOMPONEN XML
        txtName = findViewById(R.id.txtName);
        txtRegNo = findViewById(R.id.txtRegNo);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);
        txtProgramme = findViewById(R.id.txtProgramme);

        txtEvent = findViewById(R.id.txtEvent);
        txtStatus = findViewById(R.id.txtStatus);

        btnHome = findViewById(R.id.btnHome);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        // 2. AMBIL DATA DARI INTENT
        name = getIntent().getStringExtra("name");
        regNo = getIntent().getStringExtra("regNo");
        email = getIntent().getStringExtra("email");
        phone = getIntent().getStringExtra("phone");
        programme = getIntent().getStringExtra("programme");
        eventName = getIntent().getStringExtra("eventName");

        // SHAREDPREFERENCES FALLBACK (JIKA INTENT KOSONG)
        SharedPreferences preferences = getSharedPreferences("RegistrationData", MODE_PRIVATE);
        if (name == null || name.isEmpty()) name = preferences.getString("name", "N/A");
        if (regNo == null || regNo.isEmpty()) regNo = preferences.getString("regNo", "N/A");
        if (eventName == null || eventName.isEmpty()) eventName = preferences.getString("eventName", "TECH INNOVATION DAY 2026");
        if (email == null || email.isEmpty()) email = "N/A";
        if (phone == null || phone.isEmpty()) phone = "N/A";
        if (programme == null || programme.isEmpty()) programme = "N/A";

        // KEMASKINI PAPARAN SKRIN
        updateUI();

        // =======================================================
        // 3. BUTTON UPDATE - POP-UP EDIT SEMUA DATA
        // =======================================================
        if (btnUpdate != null) {
            btnUpdate.setOnClickListener(v -> showUpdateDialog());
        }

        // =======================================================
        // 4. BUTTON DELETE - POP-UP DELETE & PADAM KEKAL
        // =======================================================
        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmationActivity.this);
                builder.setTitle("Delete Registration");
                builder.setMessage("Adakah anda pasti ingin memadam data pendaftaran ini secara kekal?");

                builder.setPositiveButton("Ya, Padam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Padam dari Content Provider
                        String selection = "reg_no = ?";
                        String[] selectionArgs = new String[]{regNo};

                        int deletedRows = getContentResolver().delete(
                                CampusContentProvider.CONTENT_URI,
                                selection,
                                selectionArgs
                        );

                        // Padam dari SharedPreferences
                        SharedPreferences preferences = getSharedPreferences("RegistrationData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.apply();

                        // Pop-up pemberitahuan data sudah delete
                        AlertDialog.Builder successBuilder = new AlertDialog.Builder(ConfirmationActivity.this);
                        successBuilder.setTitle("Berjaya Dipadam");
                        successBuilder.setMessage("Data sudah delete secara kekal dari database!");
                        successBuilder.setCancelable(false);
                        successBuilder.setPositiveButton("OK", (dialogSuccess, whichSuccess) -> finish());
                        successBuilder.show();
                    }
                });

                builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());
                builder.show();
            });
        }

        // =======================================================
        // 5. BUTTON HOME
        // =======================================================
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(ConfirmationActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    // =======================================================
    // FUNGSI POP-UP FORM DIALOG UNTUK UPDATE DATA
    // =======================================================
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update All Registration Details");

        ScrollView scrollView = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText edtNewName = new EditText(this);
        edtNewName.setHint("Full Name");
        edtNewName.setInputType(InputType.TYPE_CLASS_TEXT);
        edtNewName.setText(name);
        layout.addView(edtNewName);

        final EditText edtNewRegNo = new EditText(this);
        edtNewRegNo.setHint("Registration Number");
        edtNewRegNo.setInputType(InputType.TYPE_CLASS_TEXT);
        edtNewRegNo.setText(regNo);
        layout.addView(edtNewRegNo);

        final EditText edtNewEmail = new EditText(this);
        edtNewEmail.setHint("Email Address");
        edtNewEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        edtNewEmail.setText(email);
        layout.addView(edtNewEmail);

        final EditText edtNewPhone = new EditText(this);
        edtNewPhone.setHint("Phone Number");
        edtNewPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        edtNewPhone.setText(phone);
        layout.addView(edtNewPhone);

        final EditText edtNewProgramme = new EditText(this);
        edtNewProgramme.setHint("Programme");
        edtNewProgramme.setInputType(InputType.TYPE_CLASS_TEXT);
        edtNewProgramme.setText(programme);
        layout.addView(edtNewProgramme);

        scrollView.addView(layout);
        builder.setView(scrollView);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = edtNewName.getText().toString().trim();
                String newRegNo = edtNewRegNo.getText().toString().trim();
                String newEmail = edtNewEmail.getText().toString().trim();
                String newPhone = edtNewPhone.getText().toString().trim();
                String newProgramme = edtNewProgramme.getText().toString().trim();

                if (!newName.isEmpty() && !newRegNo.isEmpty() && !newEmail.isEmpty() && !newPhone.isEmpty() && !newProgramme.isEmpty()) {
                    ContentValues values = new ContentValues();
                    values.put("name", newName);
                    values.put("reg_no", newRegNo);
                    values.put("email", newEmail);
                    values.put("phone", newPhone);
                    values.put("programme", newProgramme);

                    String selection = "reg_no = ?";
                    String[] selectionArgs = new String[]{regNo};

                    getContentResolver().update(
                            CampusContentProvider.CONTENT_URI,
                            values,
                            selection,
                            selectionArgs
                    );

                    // Kemaskini pembolehubah tempatan
                    name = newName;
                    regNo = newRegNo;
                    email = newEmail;
                    phone = newPhone;
                    programme = newProgramme;

                    // Kemaskini UI skrin
                    updateUI();

                    Toast.makeText(ConfirmationActivity.this, "Semua Rekod Berjaya Dikemaskini! (UPDATE)", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ConfirmationActivity.this, "Sila pastikan semua ruangan diisi", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // =======================================================
    // FUNGSI UNTUK MENGEMASKINI PAPARAN SKRIN (UI)
    // =======================================================
    private void updateUI() {
        txtName.setText("Name: " + name);
        txtRegNo.setText("Registration No: " + regNo);
        txtEmail.setText("Email: " + email);
        txtPhone.setText("Phone: " + phone);
        txtProgramme.setText("Programme: " + programme);
        txtEvent.setText("Event: " + eventName);
        txtStatus.setText("Status: Registration Successful");
    }
}