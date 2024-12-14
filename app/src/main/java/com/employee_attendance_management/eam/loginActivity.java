package com.employee_attendance_management.eam;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class loginActivity extends AppCompatActivity {

    EditText etName;
    TextView tvNext;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etName = findViewById(R.id.etName);
        tvNext = findViewById(R.id.tvNext);

        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etName.getText().toString().equals("")) {
                    Toast.makeText(loginActivity.this, "Please enter the name", Toast.LENGTH_SHORT).show();
                } else {
                    String userName = etName.getText().toString();
                    SharedPreferences.Editor editor = getSharedPreferences("CNB", MODE_PRIVATE).edit();
                    editor.putBoolean("registered", true);
                    editor.putString("userName", userName);
                    editor.apply();

                    Log.e("LOGIN", userName);
                    Intent intent = new Intent(loginActivity.this, MainActivity.class);
                    intent.putExtra("userName", userName);
                    startActivity(intent);
                    finish();
                }
            }
        });


    }
}