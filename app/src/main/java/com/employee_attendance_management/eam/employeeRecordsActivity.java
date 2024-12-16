package com.employee_attendance_management.eam;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.employee_attendance_management.eam.adapters.AttendanceRecordAdapter;
import com.employee_attendance_management.eam.models.AttendanceRecord;

import java.util.List;

public class employeeRecordsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    ImageView ivBackBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_records);

        dbHelper = new DatabaseHelper(this);


        RecyclerView recyclerView = findViewById(R.id.rvRecords);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ivBackBtn = findViewById(R.id.ivBackBtn);

        ivBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        List<AttendanceRecord> records = dbHelper.getAllAttendanceRecords();
        Log.e("RECORDS", records.toString());

        AttendanceRecordAdapter adapter = new AttendanceRecordAdapter(records);
        recyclerView.setAdapter(adapter);

    }
}