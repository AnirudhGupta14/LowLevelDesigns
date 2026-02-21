package Alerts;

public interface OrderObserver {
    void onOrderCancel();
    void onOrderReturned();
    void onOrderPlaced();
}
