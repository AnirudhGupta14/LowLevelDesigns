package Alerts;

public interface StockObserver {
    void onProductThreshholdBreach();
    void onAddProduct();
    void onRemoveProduct();
    void onExpiryDatePassed();
    void onRequestProductFromSuppliers();
}