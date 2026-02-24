import enums.SeatCategory;
import enums.PaymentStatus;
import models.*;
import services.*;
import payment.*;
import observer.*;
import factory.SeatFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("==========================================================");
        System.out.println("       🎬 MOVIE SEAT BOOKING SYSTEM — DEMO");
        System.out.println("==========================================================\n");

        // ─── STEP 1: Create Theatres & Screens ────────────────────
        System.out.println("── STEP 1: Setting up Theatres & Screens ──\n");

        TheatreManager theatreManager = new TheatreManager();

        // Theatre 1: PVR Cinemas with 2 screens
        Theatre pvr = new Theatre("T1", "PVR Cinemas", "Mumbai");
        Screen screen1 = createScreen("SCR1", "Screen 1", 3, 5); // 15 seats mixed
        Screen screen2 = createScreen("SCR2", "Screen 2", 2, 4); // 8 seats mixed
        pvr.addScreen(screen1);
        pvr.addScreen(screen2);
        theatreManager.addTheatre(pvr);

        // Theatre 2: INOX in Delhi
        Theatre inox = new Theatre("T2", "INOX Delhi", "Delhi");
        Screen screen3 = createScreen("SCR3", "Audi 1", 4, 6); // 24 seats mixed
        inox.addScreen(screen3);
        theatreManager.addTheatre(inox);

        System.out.println("\nTheatres in Mumbai: " + theatreManager.getTheatresByCity("Mumbai"));
        System.out.println("Theatres in Delhi: " + theatreManager.getTheatresByCity("Delhi"));

        // ─── STEP 2: Create Movies & Shows ────────────────────────
        System.out.println("\n── STEP 2: Creating Movies & Shows ──\n");

        Movie avengers = new Movie("M1", "Avengers: Endgame", 181, "Action");
        Movie inception = new Movie("M2", "Inception", 148, "Sci-Fi");

        ShowManager showManager = new ShowManager();

        Show show1 = new Show("S1", avengers, screen1, LocalDateTime.now().plusHours(2));
        Show show2 = new Show("S2", inception, screen2, LocalDateTime.now().plusHours(3));
        Show show3 = new Show("S3", avengers, screen3, LocalDateTime.now().plusHours(4));
        showManager.addShow(show1);
        showManager.addShow(show2);
        showManager.addShow(show3);

        System.out.println("\nShows for Avengers: " + showManager.getShowsForMovie(avengers));
        System.out.println("Available seats for Show 1: " + show1.getAvailableSeats().size());

        // ─── STEP 3: Setup Booking System ─────────────────────────
        System.out.println("\n── STEP 3: Setting up Booking System ──\n");

        // Using a short timeout (10 seconds) for demo purposes
        SeatLockManager seatLockManager = SeatLockManager.getInstance(Duration.ofSeconds(10));
        BookingManager bookingManager = BookingManager.getInstance(seatLockManager);

        // Register observer
        NotificationService notificationService = new NotificationService();
        bookingManager.addObserver(notificationService);

        // Create users
        User alice = new User("U1", "Alice", "alice@email.com");
        User bob = new User("U2", "Bob", "bob@email.com");
        User charlie = new User("U3", "Charlie", "charlie@email.com");

        // ─── STEP 4: Normal Booking Flow ──────────────────────────
        System.out.println("\n── STEP 4: Alice books 2 seats (Normal Flow) ──\n");

        List<Seat> aliceSeats = show1.getAvailableSeats().subList(0, 2);
        System.out.println("Alice wants to book: " + aliceSeats);

        Booking aliceBooking = bookingManager.initiateBooking(show1, aliceSeats, alice);
        System.out.println("Booking status: " + aliceBooking.getStatus());

        // Alice pays with credit card
        Payment creditCard = new CreditCardPayment("4111222233334444");
        boolean confirmed = bookingManager.confirmBooking(aliceBooking, creditCard);
        System.out.println("Confirmed: " + confirmed);
        System.out.println("Available seats after Alice's booking: " + show1.getAvailableSeats().size());

        // ─── STEP 5: Conflict — Bob tries same show ──────────────
        System.out.println("\n── STEP 5: Bob books different seats (same show) ──\n");

        List<Seat> bobSeats = show1.getAvailableSeats().subList(0, 3);
        System.out.println("Bob wants to book: " + bobSeats);

        Booking bobBooking = bookingManager.initiateBooking(show1, bobSeats, bob);
        Payment upi = new UPIPayment("bob@upi");
        bookingManager.confirmBooking(bobBooking, upi);

        System.out.println("Available seats after Bob's booking: " + show1.getAvailableSeats().size());

        // ─── STEP 6: Seat Lock Timeout Demo ───────────────────────
        System.out.println("\n── STEP 6: Charlie's Booking — TIMEOUT DEMO ──\n");

        List<Seat> charlieSeats = show1.getAvailableSeats().subList(0, 2);
        System.out.println("Charlie wants to book: " + charlieSeats);

        Booking charlieBooking = bookingManager.initiateBooking(show1, charlieSeats, charlie);
        System.out.println("Charlie's booking status: " + charlieBooking.getStatus());

        // Check lock time
        Seat firstSeat = charlieSeats.get(0);
        System.out.println("Lock remaining for Charlie's seat: "
                + seatLockManager.getRemainingLockSeconds(show1, firstSeat) + "s");

        // Simulate Charlie taking too long — force-expire for demo
        System.out.println("\n⏳ Simulating timeout... (force-expiring locks for demo)");
        seatLockManager.forceExpireAllLocks(show1);

        // Now Charlie tries to pay — too late!
        Payment cash = new CashPayment();
        boolean charliePaid = bookingManager.confirmBooking(charlieBooking, cash);
        System.out.println("Charlie's payment accepted: " + charliePaid);
        System.out.println("Charlie's booking status: " + charlieBooking.getStatus());

        // ─── STEP 7: Those seats are now available again ──────────
        System.out.println("\n── STEP 7: Seats released — another user books them ──\n");

        // Bob now books those released seats in a different show
        List<Seat> bobSeats2 = show2.getAvailableSeats().subList(0, 2);
        Booking bobBooking2 = bookingManager.initiateBooking(show2, bobSeats2, bob);
        bookingManager.confirmBooking(bobBooking2, new CashPayment());

        // ─── STEP 8: Cancellation Demo ────────────────────────────
        System.out.println("\n── STEP 8: Bob cancels his first booking ──\n");

        bookingManager.cancelBooking(bobBooking);
        System.out.println("Available seats after Bob's cancellation: " + show1.getAvailableSeats().size());

        // ─── STEP 9: Multiple Theatres Demo ───────────────────────
        System.out.println("\n── STEP 9: Alice books at a different theatre (INOX Delhi) ──\n");

        List<Seat> aliceSeats2 = show3.getAvailableSeats().subList(0, 4);
        Booking aliceBooking2 = bookingManager.initiateBooking(show3, aliceSeats2, alice);
        bookingManager.confirmBooking(aliceBooking2, new UPIPayment("alice@upi"));

        // ─── SUMMARY ──────────────────────────────────────────────
        System.out.println("\n==========================================================");
        System.out.println("                  📊 BOOKING SUMMARY");
        System.out.println("==========================================================");
        for (Booking b : bookingManager.getAllBookings()) {
            System.out.println("  " + b);
        }

        // Cleanup
        seatLockManager.shutdown();
        System.out.println("\n✅ Demo completed successfully!");
    }

    /**
     * Helper to create a Screen with mixed seat categories.
     * Row 0 → PLATINUM, last row → SILVER, middle rows → GOLD
     */
    private static Screen createScreen(String id, String name, int rows, int cols) {
        Screen screen = new Screen(id, name);
        for (int r = 0; r < rows; r++) {
            SeatCategory category;
            if (r == 0)
                category = SeatCategory.PLATINUM;
            else if (r == rows - 1)
                category = SeatCategory.SILVER;
            else
                category = SeatCategory.GOLD;

            for (int c = 0; c < cols; c++) {
                String seatId = id + "-R" + r + "C" + c;
                Seat seat = SeatFactory.createSeat(category, seatId, r, c);
                screen.addSeat(seat);
            }
        }
        return screen;
    }
}