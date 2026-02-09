package com.example.project1;

public interface OnBookingActionListener {
    void onConfirmBooking(Booking booking);
    void onCompleteBooking(Booking booking);
    void onCancelBooking(Booking booking);
}
