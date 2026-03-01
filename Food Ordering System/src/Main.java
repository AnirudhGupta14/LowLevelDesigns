import Observer.OrderStatusNotifier;
import Services.OrderService;
import Services.PaymentService;
import Services.RestaurantService;
import Strategy.CashOnDelivery;
import Strategy.CreditCardPayment;
import Strategy.UpiPayment;
import entities.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        // ─── 1. Initialize services ────────────────────────────────────
        RestaurantService restaurantService = RestaurantService.getInstance();
        OrderService orderService = new OrderService();
        PaymentService paymentService = new PaymentService(new CreditCardPayment("4111222233334444"));

        // Register order-status observer
        orderService.addObserver(new OrderStatusNotifier());

        System.out.println("═══════════════════════════════════════════════");
        System.out.println("   🍔 FOOD ORDERING SYSTEM — DEMO");
        System.out.println("═══════════════════════════════════════════════\n");

        // ─── 2. Create restaurants & their menus ───────────────────────
        System.out.println("── Registering Restaurants ─────────────────\n");

        Restaurant pizzaPalace = new Restaurant("R001", "Pizza Palace", "MG Road, Bangalore");
        pizzaPalace.addMenuItem(new MenuItem("M001", "Margherita Pizza", MenuCategory.MAIN_COURSE, 299.00, true));
        pizzaPalace.addMenuItem(new MenuItem("M002", "Garlic Bread", MenuCategory.STARTER, 149.00, true));
        pizzaPalace.addMenuItem(new MenuItem("M003", "Chocolate Lava Cake", MenuCategory.DESSERT, 199.00, true));
        pizzaPalace.addMenuItem(new MenuItem("M004", "Cold Coffee", MenuCategory.BEVERAGE, 129.00, true));
        pizzaPalace.addMenuItem(new MenuItem("M005", "Pepperoni Pizza", MenuCategory.MAIN_COURSE, 449.00, false)); // unavailable

        Restaurant biryaniHouse = new Restaurant("R002", "Biryani House", "Koramangala, Bangalore");
        biryaniHouse.addMenuItem(new MenuItem("M010", "Chicken Biryani", MenuCategory.MAIN_COURSE, 349.00, true));
        biryaniHouse.addMenuItem(new MenuItem("M011", "Paneer Tikka", MenuCategory.STARTER, 249.00, true));
        biryaniHouse.addMenuItem(new MenuItem("M012", "Gulab Jamun", MenuCategory.DESSERT, 99.00, true));
        biryaniHouse.addMenuItem(new MenuItem("M013", "Masala Chai", MenuCategory.BEVERAGE, 49.00, true));
        biryaniHouse.addMenuItem(new MenuItem("M014", "Veg Fried Rice", MenuCategory.MAIN_COURSE, 199.00, true));

        restaurantService.addRestaurant(pizzaPalace);
        restaurantService.addRestaurant(biryaniHouse);

        System.out.println();
        restaurantService.displayAllRestaurants();

        // ─── 3. Display menus ──────────────────────────────────────────
        System.out.println("── Restaurant Menus ────────────────────────\n");
        pizzaPalace.displayMenu();
        biryaniHouse.displayMenu();

        // ─── 4. Create customers ───────────────────────────────────────
        Customer anirudh = new Customer("C001", "Anirudh", "anirudh@email.com", "HSR Layout, Bangalore");
        Customer priya = new Customer("C002", "Priya", "priya@email.com", "Indiranagar, Bangalore");

        // ─── 5. Place orders ───────────────────────────────────────────
        System.out.println("── Placing Orders ─────────────────────────\n");

        // Order 1: Anirudh orders from Pizza Palace
        Map<String, Integer> order1Items = new LinkedHashMap<>();
        order1Items.put("M001", 2); // 2x Margherita Pizza
        order1Items.put("M002", 1); // 1x Garlic Bread
        order1Items.put("M004", 2); // 2x Cold Coffee
        Order order1 = orderService.placeOrder(anirudh, pizzaPalace, order1Items);

        System.out.println();

        // Order 2: Priya orders from Biryani House
        Map<String, Integer> order2Items = new LinkedHashMap<>();
        order2Items.put("M010", 1); // 1x Chicken Biryani
        order2Items.put("M011", 1); // 1x Paneer Tikka
        order2Items.put("M013", 2); // 2x Masala Chai
        Order order2 = orderService.placeOrder(priya, biryaniHouse, order2Items);

        System.out.println();

        // ─── 6. Process payments ───────────────────────────────────────
        System.out.println("── Processing Payments ─────────────────────\n");

        // Pay order 1 with Credit Card
        paymentService.processPayment(order1);
        System.out.println();

        // Pay order 2 with UPI (switch strategy)
        paymentService.setStrategy(new UpiPayment("priya@upi"));
        paymentService.processPayment(order2);
        System.out.println();

        // ─── 7. Order lifecycle — full flow for Order 1 ────────────────
        System.out.println("── Order Lifecycle (ORD-001) ───────────────\n");

        orderService.confirmOrder("ORD-001");
        orderService.prepareOrder("ORD-001");
        orderService.outForDelivery("ORD-001");
        orderService.deliverOrder("ORD-001");

        System.out.println();

        // ─── 8. Cancel Order 2 ─────────────────────────────────────────
        System.out.println("── Cancelling Order (ORD-002) ──────────────\n");

        orderService.cancelOrder("ORD-002");

        System.out.println();

        // ─── 9. Edge case: unavailable item ────────────────────────────
        System.out.println("── Edge Case: Unavailable Item ─────────────\n");

        Map<String, Integer> failOrder = new LinkedHashMap<>();
        failOrder.put("M005", 1); // Pepperoni Pizza — unavailable
        orderService.placeOrder(anirudh, pizzaPalace, failOrder);

        System.out.println();

        // ─── 10. Edge case: invalid order ID ───────────────────────────
        System.out.println("── Edge Case: Invalid Order ID ─────────────\n");

        orderService.confirmOrder("ORD-999");

        System.out.println();

        // ─── 11. Order 3: Cash on Delivery ─────────────────────────────
        System.out.println("── Order with Cash on Delivery ─────────────\n");

        Map<String, Integer> order3Items = new LinkedHashMap<>();
        order3Items.put("M014", 2); // 2x Veg Fried Rice
        order3Items.put("M012", 3); // 3x Gulab Jamun
        Order order3 = orderService.placeOrder(anirudh, biryaniHouse, order3Items);

        System.out.println();

        paymentService.setStrategy(new CashOnDelivery());
        paymentService.processPayment(order3);

        System.out.println();

        // ─── 12. Final state ───────────────────────────────────────────
        orderService.displayOrders();

        System.out.println("═══════════════════════════════════════════════");
        System.out.println("   ✅ DEMO COMPLETE");
        System.out.println("═══════════════════════════════════════════════");
    }
}