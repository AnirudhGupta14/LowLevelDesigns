package Alerts;

public interface WareHouseObserver {
    void onStockRequest();
    void onStockThreshholdBreach();
    void onStockClear();
    void onStockTransfer();
}
