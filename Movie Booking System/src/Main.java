import enums.PaymentStatus;
import models.*;
import services.*;
import enums.SeatCategory;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final MovieService movieService = new MovieService();
    private static final TheatreService theatreService = new TheatreService();
    private static final ShowService showService = new ShowService();
    private static final SeatLockService seatLockService = new SeatLockService();
    private static final BookingService bookingService = new BookingService(seatLockService);
    private static final PaymentService paymentService = new PaymentService();
    
    // Store current user session
    private static User currentUser = null;
    
    public static void main(String[] args) {
        System.out.println("🎬 Welcome to Movie Ticket Booking System! 🎬\n");
        
        // Initialize sample data
        initializeSampleData();
        
        // Start interactive menu
        startInteractiveMenu();
    }
    
    private static void initializeSampleData() {
        System.out.println("Initializing sample data...\n");
        
        // Create movies
        Movie movie1 = movieService.createMovie(
            "Avengers: Endgame", 
            Duration.ofMinutes(181), 
            "Action", 
            "English", 
            "UA", 
            "The epic conclusion to the Infinity Saga"
        );
        
        Movie movie2 = movieService.createMovie(
            "The Lion King", 
            Duration.ofMinutes(118), 
            "Animation", 
            "English", 
            "U", 
            "A remake of the classic animated film"
        );
        
        Movie movie3 = movieService.createMovie(
            "Spider-Man: No Way Home", 
            Duration.ofMinutes(148), 
            "Action", 
            "English", 
            "UA", 
            "Spider-Man faces the multiverse"
        );
        
        // Create theatres
        Theatre theatre1 = theatreService.createTheatre("PVR Cinemas", "Phoenix Mills, Lower Parel");
        Theatre theatre2 = theatreService.createTheatre("INOX", "Select City Walk, Saket");
        Theatre theatre3 = theatreService.createTheatre("Cinepolis", "Andheri West");
        
        // Create screens
        Screen screen1 = theatreService.createScreen("Screen 1", theatre1);
        Screen screen2 = theatreService.createScreen("Screen 2", theatre1);
        Screen screen3 = theatreService.createScreen("Screen 1", theatre2);
        Screen screen4 = theatreService.createScreen("Screen 1", theatre3);
        
        // Create seats for screens
        createSeatsForScreen(screen1, 10, 10); // 100 seats
        createSeatsForScreen(screen2, 8, 8);   // 64 seats
        createSeatsForScreen(screen3, 12, 10); // 120 seats
        createSeatsForScreen(screen4, 6, 8);   // 48 seats
        
        // Create shows
        LocalDateTime now = LocalDateTime.now();
        Show show1 = showService.createShow(movie1, screen1, now.plusHours(2));
        Show show2 = showService.createShow(movie2, screen2, now.plusHours(4));
        Show show3 = showService.createShow(movie1, screen3, now.plusHours(6));
        Show show4 = showService.createShow(movie3, screen4, now.plusHours(8));
        
        System.out.println("✅ Sample data initialized successfully!");
        System.out.println("📊 Created: 3 movies, 3 theatres, 4 screens, 332 seats, 4 shows\n");
    }
    
    private static void createSeatsForScreen(Screen screen, int rows, int columns) {
        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= columns; col++) {
                SeatCategory category = determineSeatCategory(row, rows);
                theatreService.createSeat(row, col, category, screen);
            }
        }
    }
    
    private static SeatCategory determineSeatCategory(int row, int totalRows) {
        if (row <= totalRows / 3) {
            return SeatCategory.SILVER;
        } else if (row <= (2 * totalRows) / 3) {
            return SeatCategory.GOLD;
        } else {
            return SeatCategory.PLATINUM;
        }
    }

    private static void displayMenu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎬 MOVIE TICKET BOOKING SYSTEM");
        System.out.println("👤 User: " + (currentUser != null ? currentUser.getName() : "Not logged in"));
        System.out.println("=".repeat(60));
        System.out.println("1. 🔍 Search Movies");
        System.out.println("2. 🎭 Search Shows");
        System.out.println("3. 🎫 Book Tickets");
        System.out.println("4. 📋 View My Bookings");
        System.out.println("5. ✅ Confirm Booking");
        System.out.println("6. ❌ Cancel Booking");
        System.out.println("7. 💺 View Available Seats");
        System.out.println("8. 🧹 Cleanup Expired Locks");
        System.out.println("9. 📊 System Statistics");
        System.out.println("10. 🔄 Switch User");
        System.out.println("0. 🚪 Exit");
        System.out.println("=".repeat(60));
    }
    
    private static void startInteractiveMenu() {
        Scanner scanner = new Scanner(System.in);
        
        // Login or create user
        loginUser(scanner);
        
        while (true) {
            displayMenu();
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            try {
                switch (choice) {
                    case 1:
                        searchMovies(scanner);
                        break;
                    case 2:
                        searchShows(scanner);
                        break;
                    case 3:
                        bookTickets(scanner);
                        break;
                    case 4:
                        viewBookings();
                        break;
                    case 5:
                        confirmBooking(scanner);
                        break;
                    case 6:
                        cancelBooking(scanner);
                        break;
                    case 7:
                        viewAvailableSeats(scanner);
                        break;
                    case 8:
                        cleanupExpiredLocks();
                        break;
                    case 9:
                        switchUser(scanner);
                        break;
                    case 0:
                        System.out.println("Thank you for using Movie Ticket Booking System! 👋");
                        return;
                    default:
                        System.out.println("❌ Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
            
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }
    
    private static void loginUser(Scanner scanner) {
        System.out.println("👤 USER LOGIN");
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your phone: ");
        String phone = scanner.nextLine();
        
        currentUser = new User(name, email, phone);
        System.out.println("✅ Welcome, " + name + "!\n");
    }
    
    private static void switchUser(Scanner scanner) {
        System.out.println("🔄 SWITCH USER");
        loginUser(scanner);
    }
    
    private static void searchMovies(Scanner scanner) {
        System.out.println("\n🔍 SEARCH MOVIES");
        System.out.println("1. Search by title");
        System.out.println("2. Search by genre");
        System.out.println("3. Search by language");
        System.out.println("4. View all movies");
        System.out.print("Enter choice: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        List<Movie> movies;
        
        switch (choice) {
            case 1:
                System.out.print("Enter movie title: ");
                String title = scanner.nextLine();
                movies = movieService.searchMoviesByTitle(title);
                break;
            case 2:
                System.out.print("Enter genre: ");
                String genre = scanner.nextLine();
                movies = movieService.searchMoviesByGenre(genre);
                break;
            case 3:
                System.out.print("Enter language: ");
                String language = scanner.nextLine();
                movies = movieService.searchMoviesByLanguage(language);
                break;
            case 4:
                movies = movieService.getAllMovies();
                break;
            default:
                System.out.println("❌ Invalid choice");
                return;
        }
        
        if (movies.isEmpty()) {
            System.out.println("❌ No movies found");
        } else {
            System.out.println("\n📽️ FOUND MOVIES:");
            movies.forEach(movie -> {
                System.out.printf("🎬 %s (%s) - %s - %s - %s\n", 
                    movie.getTitle(), movie.getLanguage(), movie.getGenre(), 
                    movie.getCertification(), movie.getDuration().toMinutes() + " min");
            });
        }
    }
    
    private static void searchShows(Scanner scanner) {
        System.out.println("\n🎭 SEARCH SHOWS");
        System.out.println("1. Search by movie");
        System.out.println("2. Search by theatre");
        System.out.println("3. View all shows");
        System.out.print("Enter choice: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        List<Show> shows = List.of();
        
        switch (choice) {
            case 1:
                System.out.print("Enter movie title: ");
                String title = scanner.nextLine();
                List<Movie> movies = movieService.searchMoviesByTitle(title);
                if (!movies.isEmpty()) {
                    shows = showService.getShowsByMovie(movies.getFirst());
                }
                break;
            case 2:
                System.out.print("Enter theatre name: ");
                String theatreName = scanner.nextLine();
                // For simplicity, get first theatre with matching name
                shows = showService.getAllShows().stream()
                    .filter(show -> show.getScreen().getTheatre().getName().toLowerCase().contains(theatreName.toLowerCase()))
                    .collect(Collectors.toList());
                break;
            case 3:
                shows = showService.getAllShows();
                break;
            default:
                System.out.println("❌ Invalid choice");
                return;
        }
        
        if (shows.isEmpty()) {
            System.out.println("❌ No shows found");
        } else {
            System.out.println("\n🎭 FOUND SHOWS:");
            shows.forEach(show -> {
                System.out.printf("🎬 %s at %s - %s\n   📅 %s | 🕐 %s\n   💺 Available: %d seats\n\n",
                    show.getMovie().getTitle(),
                    show.getScreen().getTheatre().getName(),
                    show.getScreen().getName(),
                    show.getStartTime().toLocalDate(),
                    show.getStartTime().toLocalTime(),
                    bookingService.getAvailableSeats(show).size()
                );
            });
        }
    }
    
    private static void bookTickets(Scanner scanner) {
        System.out.println("\n🎫 BOOK TICKETS");
        
        // Show available shows
        List<Show> shows = showService.getAllShows();
        if (shows.isEmpty()) {
            System.out.println("❌ No shows available");
            return;
        }
        
        System.out.println("Available shows:");
        for (int i = 0; i < shows.size(); i++) {
            Show show = shows.get(i);
            System.out.printf("%d. %s at %s - %s (%d available seats)\n",
                i + 1, show.getMovie().getTitle(), show.getScreen().getTheatre().getName(),
                show.getStartTime().toString(), bookingService.getAvailableSeats(show).size());
        }
        
        System.out.print("Select show (number): ");
        int showIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        
        if (showIndex < 0 || showIndex >= shows.size()) {
            System.out.println("❌ Invalid show selection");
            return;
        }
        
        Show selectedShow = shows.get(showIndex);
        
        // Show available seats
        List<Seat> availableSeats = bookingService.getAvailableSeats(selectedShow);
        if (availableSeats.isEmpty()) {
            System.out.println("❌ No seats available for this show");
            return;
        }
        
        System.out.println("\n💺 Available seats:");
        displaySeatLayout(availableSeats, selectedShow.getScreen());
        
        System.out.print("Enter seat numbers (comma-separated, e.g., 1,1;2,3): ");
        String seatInput = scanner.nextLine();
        
        List<Seat> selectedSeats = parseSeatSelection(seatInput, availableSeats);
        if (selectedSeats.isEmpty()) {
            System.out.println("❌ Invalid seat selection");
            return;
        }
        
        try {
            Booking booking = bookingService.createBooking(currentUser, selectedShow, selectedSeats);
            System.out.println("✅ Booking created successfully!");
            System.out.println("📋 Booking ID: " + booking.getBookingId());
            System.out.println("💰 Total Amount: ₹" + booking.calculateTotalAmount());
            System.out.println("⏰ You have 5 minutes to confirm your booking");
            
            // Process payment
            processPayment(booking, scanner);
            
        } catch (Exception e) {
            System.out.println("❌ Booking failed: " + e.getMessage());
        }
    }
    
    private static void displaySeatLayout(List<Seat> seats, Screen screen) {
        // Group seats by row
        Map<Integer, List<Seat>> seatsByRow = seats.stream()
            .collect(Collectors.groupingBy(Seat::getRow));
        
        int maxRow = seatsByRow.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
        int maxCol = seats.stream().mapToInt(Seat::getColumn).max().orElse(0);
        
        System.out.println("   " + " ".repeat(maxCol * 3));
        for (int row = 1; row <= maxRow; row++) {
            System.out.printf("%2d ", row);
            List<Seat> rowSeats = seatsByRow.getOrDefault(row, List.of());
            for (int col = 1; col <= maxCol; col++) {
                final int finalCol = col;
                boolean hasSeat = rowSeats.stream().anyMatch(seat -> seat.getColumn() == finalCol);
                if (hasSeat) {
                    System.out.print("[ ] ");
                } else {
                    System.out.print("    ");
                }
            }
            System.out.println();
        }
        System.out.println("   " + " ".repeat(maxCol * 3));
    }
    
    private static List<Seat> parseSeatSelection(String input, List<Seat> availableSeats) {
        List<Seat> selectedSeats = new ArrayList<>();
        String[] seatPairs = input.split(";");
        
        for (String pair : seatPairs) {
            String[] coords = pair.trim().split(",");
            if (coords.length == 2) {
                try {
                    int row = Integer.parseInt(coords[0].trim());
                    int col = Integer.parseInt(coords[1].trim());

                    availableSeats.stream()
                            .filter(s -> s.getRow() == row && s.getColumn() == col)
                            .findFirst().ifPresent(selectedSeats::add);

                } catch (NumberFormatException e) {
                    // Invalid format, skip
                }
            }
        }
        
        return selectedSeats;
    }
    
    private static void processPayment(Booking booking, Scanner scanner) {
        System.out.println("\n💳 PAYMENT PROCESSING");
        System.out.println("1. Credit Card");
        System.out.println("2. Debit Card");
        System.out.println("3. UPI");
        System.out.println("4. Net Banking");
        System.out.print("Select payment method: ");
        
        int paymentMethod = scanner.nextInt();
        scanner.nextLine();
        
        String[] methods = {"Credit Card", "Debit Card", "UPI", "Net Banking"};
        String selectedMethod = methods[paymentMethod - 1];
        
        Payment payment = paymentService.createPayment(booking, selectedMethod);
        System.out.println("💳 Processing payment...");
        
        try {
            Thread.sleep(2000); // Simulate payment processing
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        PaymentStatus status = paymentService.processPayment(payment.getPaymentId());
        
        if (status == PaymentStatus.SUCCESS) {
            bookingService.confirmBooking(booking.getBookingId(), currentUser);
            System.out.println("✅ Payment successful! Booking confirmed.");
            System.out.println("🎫 Your tickets are ready!");
        } else {
            System.out.println("❌ Payment failed. Booking will expire in 5 minutes.");
        }
    }
    
    private static void viewBookings() {
        System.out.println("\n📋 MY BOOKINGS");
        
        List<Booking> bookings = bookingService.getBookingsByUser(currentUser);
        if (bookings.isEmpty()) {
            System.out.println("❌ No bookings found");
            return;
        }
        
        for (Booking booking : bookings) {
            System.out.println("📋 Booking ID: " + booking.getBookingId());
            System.out.println("🎬 Movie: " + booking.getShow().getMovie().getTitle());
            System.out.println("🏢 Theatre: " + booking.getShow().getScreen().getTheatre().getName());
            System.out.println("📅 Date: " + booking.getShow().getStartTime().toLocalDate());
            System.out.println("🕐 Time: " + booking.getShow().getStartTime().toLocalTime());
            System.out.println("💺 Seats: " + booking.getSeats().stream()
                .map(seat -> "[" + seat.getRow() + "," + seat.getColumn() + "]")
                .collect(Collectors.joining(", ")));
            System.out.println("💰 Amount: ₹" + booking.calculateTotalAmount());
            System.out.println("📊 Status: " + booking.getStatus());
            if (booking.getPayment() != null) {
                System.out.println("💳 Payment: " + booking.getPayment().getStatus());
            }
            System.out.println("─".repeat(50));
        }
    }
    
    private static void confirmBooking(Scanner scanner) {
        System.out.println("\n✅ CONFIRM BOOKING");
        System.out.print("Enter booking ID: ");
        String bookingId = scanner.nextLine();
        
        try {
            bookingService.confirmBooking(bookingId, currentUser);
            System.out.println("✅ Booking confirmed successfully!");
        } catch (Exception e) {
            System.out.println("❌ Failed to confirm booking: " + e.getMessage());
        }
    }
    
    private static void cancelBooking(Scanner scanner) {
        System.out.println("\n❌ CANCEL BOOKING");
        System.out.print("Enter booking ID: ");
        String bookingId = scanner.nextLine();
        
        try {
            bookingService.cancelBooking(bookingId, currentUser);
            System.out.println("✅ Booking cancelled successfully!");
        } catch (Exception e) {
            System.out.println("❌ Failed to cancel booking: " + e.getMessage());
        }
    }
    
    private static void viewAvailableSeats(Scanner scanner) {
        System.out.println("\n💺 VIEW AVAILABLE SEATS");
        
        List<Show> shows = showService.getAllShows();
        if (shows.isEmpty()) {
            System.out.println("❌ No shows available");
            return;
        }
        
        System.out.println("Available shows:");
        for (int i = 0; i < shows.size(); i++) {
            Show show = shows.get(i);
            System.out.printf("%d. %s at %s - %s\n",
                i + 1, show.getMovie().getTitle(), show.getScreen().getTheatre().getName(),
                show.getStartTime().toString());
        }
        
        System.out.print("Select show (number): ");
        int showIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        
        if (showIndex < 0 || showIndex >= shows.size()) {
            System.out.println("❌ Invalid show selection");
            return;
        }
        
        Show selectedShow = shows.get(showIndex);
        List<Seat> availableSeats = bookingService.getAvailableSeats(selectedShow);
        
        System.out.println("\n💺 Available seats for " + selectedShow.getMovie().getTitle() + ":");
        System.out.println("Total available: " + availableSeats.size() + " seats");
        
        displaySeatLayout(availableSeats, selectedShow.getScreen());
        
        // Show seat categories
        Map<SeatCategory, Long> categoryCount = availableSeats.stream()
            .collect(Collectors.groupingBy(Seat::getCategory, Collectors.counting()));
        
        System.out.println("\nSeat categories:");
        categoryCount.forEach((category, count) -> {
            System.out.printf("  %s: %d seats\n", category, count);
        });
    }
    
    private static void cleanupExpiredLocks() {
        System.out.println("\n🧹 CLEANUP EXPIRED LOCKS");
        
        List<SeatLock> expiredLocks = seatLockService.getExpiredLocks();
        if (expiredLocks.isEmpty()) {
            System.out.println("✅ No expired locks found");
        } else {
            System.out.println("🔓 Found " + expiredLocks.size() + " expired locks");
            seatLockService.cleanupExpiredLocks();
            System.out.println("✅ Cleanup completed");
        }
    }
}