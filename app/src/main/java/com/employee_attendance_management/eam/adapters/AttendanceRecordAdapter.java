package com.employee_attendance_management.eam.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.employee_attendance_management.eam.R;
import com.employee_attendance_management.eam.models.AttendanceRecord;

import java.util.List;

public class AttendanceRecordAdapter extends RecyclerView.Adapter<AttendanceRecordAdapter.ViewHolder> {

    private List<AttendanceRecord> records;

    public AttendanceRecordAdapter(List<AttendanceRecord> records) {
        this.records = records;
    }


    @NonNull
    @Override
    public AttendanceRecordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_record_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceRecordAdapter.ViewHolder holder, int position) {


        AttendanceRecord record = records.get(position);

        // Set data to views
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(record.getImage(), 0, record.getImage().length);
        holder.imageView.setImageBitmap(imageBitmap);
        holder.nameTextView.setText(record.getName());
        holder.checkInTextView.setText("Check-in: " + record.getCheckInTime());
        holder.checkOutTextView.setText("Check-out: " + record.getCheckOutTime());
        holder.locationTextView.setText("Location: " + record.getLatitude() + ", " + record.getLongitude());
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView checkInTextView;
        TextView checkOutTextView;
        TextView locationTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            checkInTextView = itemView.findViewById(R.id.checkInTextView);
            checkOutTextView = itemView.findViewById(R.id.checkOutTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);

        }
    }
}
