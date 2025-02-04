package com.absencemanager;

public interface AttendanceCallback {
    void onAttendanceReceived(int attendance, int position);
}