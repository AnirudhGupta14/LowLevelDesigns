package service;

import models.Ride;

/**
 * Notification hub — dispatches system-level alerts
 * (separate from the Observer-pattern ride-level notifications).
 * In production: integrates with SMS, Email, and push notification providers.
 */
public class NotificationService {

    public void sendSms(String phone, String message) {
        System.out.printf("[SMS → %s]: %s%n", phone, message);
    }

    public void sendEmail(String email, String subject, String body) {
        System.out.printf("[EMAIL → %s | Subject: %s]: %s%n", email, subject, body);
    }

    public void sendRideReceipt(Ride ride) {
        String receipt = String.format(
                "Ride Receipt\nRide ID: %s\nFrom: %s\nTo: %s\nFare: ₹%.2f\nStatus: %s",
                ride.getId(),
                ride.getPickupLocation(),
                ride.getDropoffLocation(),
                ride.getFare(),
                ride.getStatus());
        sendEmail(ride.getRider().getEmail(), "Your Ride Receipt", receipt);
    }
}
