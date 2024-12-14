package com.employee_attendance_management.eam.models;

public class AttendanceRecord {
    private byte[] image;
    private String checkInTime;
    private String checkOutTime;
    private String name;
    private double latitude;
    private double longitude;

    public AttendanceRecord(){

    }

    public AttendanceRecord(byte[] image, String checkInTime, String checkOutTime, String name, double latitude, double longitude) {
        this.image = image;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
