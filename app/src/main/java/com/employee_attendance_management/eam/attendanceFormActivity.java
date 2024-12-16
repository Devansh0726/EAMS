package com.employee_attendance_management.eam;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class attendanceFormActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    double userLatitude;
    double userLongitude;

    String userName;
    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_form);

        databaseHelper = new DatabaseHelper(this);

        ImageView capturedImage = findViewById(R.id.civUserImage);
        ImageView ivBackBtn = findViewById(R.id.ivBackBtn);
        TextView dateTextView = findViewById(R.id.tvDate);
        TextView tvName = findViewById(R.id.tvName);
        TextView timeTextView = findViewById(R.id.tvTime);
        TextView tvConfirm = findViewById(R.id.tvConfirm);
        TextView tvCheckInOut = findViewById(R.id.tvCheckInOut);

        Bitmap imageBitmap = (Bitmap) getIntent().getParcelableExtra("imageBitmap");
        capturedImage.setImageBitmap(imageBitmap);
        userLongitude = getIntent().getDoubleExtra("userLongitude", 0);
        userLatitude = getIntent().getDoubleExtra("userLatitude", 0);
        userName = getIntent().getStringExtra("userName");

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageByte = stream.toByteArray();

//        byte[] imageByte = getIntent().getByteArrayExtra("imageBitmap");
        Log.e("FORM", userName);

        tvName.setText(userName);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());


        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        String formattedTime = timeFormat.format(calendar.getTime());

        dateTextView.setText(formattedDate);
        timeTextView.setText(formattedTime);

        String currentDat = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(new Date());

        if (databaseHelper.isAttendanceCompleted(userName, currentDat)) {

            tvCheckInOut.setText("Check In");

        } else if (databaseHelper.isCheckedIn(userName, currentDat)) {


            tvCheckInOut.setText("Check Out");



        }

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = userName; // Replace with actual user name
                byte[] image = imageByte;      // Replace with actual image bytes
                double latitude = userLatitude; // Replace with actual latitude
                double longitude = userLongitude; // Replace with actual longitude

                String currentDate = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(new Date());
                String currentTime = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());

                // Check if attendance is already completed
                if (databaseHelper.isAttendanceCompleted(name, currentDate)) {
                    Toast.makeText(attendanceFormActivity.this, "Your attendance is already completed for today.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(attendanceFormActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }

                // Check if user is already checked in
                if (databaseHelper.isCheckedIn(name, currentDate)) {
                    // Check-out
                    databaseHelper.checkOut(name, currentDate, currentTime, latitude, longitude);
                    Toast.makeText(attendanceFormActivity.this, "Checked Out Successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(attendanceFormActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Check-in
                    long result = databaseHelper.insertAttendance(name, image, currentDate, currentTime, latitude, longitude, "check-in");
                    if (result != -1) {
                        Toast.makeText(attendanceFormActivity.this, "Checked In Successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(attendanceFormActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(attendanceFormActivity.this, "Check-in Failed", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(attendanceFormActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
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
}