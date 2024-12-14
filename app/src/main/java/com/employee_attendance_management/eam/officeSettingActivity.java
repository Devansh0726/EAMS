package com.employee_attendance_management.eam;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class officeSettingActivity extends AppCompatActivity {


    EditText etLatitude, etLongitude;
    TextView tvModify;
    ImageView ivBackBtn;

    double userLongitude;// Get longitude from user input
    double userLatitude;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_setting);

        etLatitude = findViewById(R.id.etLatitude);
        etLongitude = findViewById(R.id.etLongitude);
        tvModify = findViewById(R.id.tvModify);
        ivBackBtn = findViewById(R.id.ivBackBtn);


        tvModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLongitude = Double.parseDouble(etLongitude.getText().toString());
                userLatitude = Double.parseDouble(etLatitude.getText().toString());

                if (etLongitude.getText().toString().equals("") || etLatitude.getText().toString().equals("")){
                    Toast.makeText(officeSettingActivity.this, "Please enter the required credentials", Toast.LENGTH_SHORT).show();
                }
                else if (isValidLongitudeLatitude(userLongitude, userLatitude)) {
                    // Input is valid
                    SharedPreferences.Editor editor = getSharedPreferences("CNB", MODE_PRIVATE).edit();
                    editor.putString("officeLatitude", etLatitude.getText().toString());
                    editor.putString("officeLongitude", etLongitude.getText().toString());
                    editor.apply();
                    Toast.makeText(officeSettingActivity.this, "Office Location Updated Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(officeSettingActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Input is invalid
                    Toast.makeText(officeSettingActivity.this, "Invalid longitude or latitude", Toast.LENGTH_SHORT).show();
                }


            }
        });


        ivBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    public boolean isValidLongitudeLatitude(double longitude, double latitude) {
        return longitude >= -180 && longitude <= 180 && latitude >= -90 && latitude <= 90;
    }

}