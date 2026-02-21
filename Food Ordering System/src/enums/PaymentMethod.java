package enums;

public enum PaymentMethod {
    CREDIT_CARD(2.5),
    DEBIT_CARD(1.5),
    UPI(0.5),
    NET_BANKING(1.0),
    WALLET(0.0),
    CASH_ON_DELIVERY(0.0);

    private final double feePercentage;

    PaymentMethod(double feePercentage) {
        this.feePercentage = feePercentage;
    }

    public double getFeePercentage() {
        return feePercentage;
    }

    public double calculateFee(double amount) {
        return (amount * feePercentage) / 100.0;
    }

    public double getTotalAmount(double orderAmount) {
        return orderAmount + calculateFee(orderAmount);
    }

    public boolean hasFee() {
        return feePercentage > 0.0;
    }

    public String getDisplayName() {
        switch (this) {
            case CREDIT_CARD:
                return "Credit Card";
            case DEBIT_CARD:
                return "Debit Card";
            case UPI:
                return "UPI";
            case NET_BANKING:
                return "Net Banking";
            case WALLET:
                return "Digital Wallet";
            case CASH_ON_DELIVERY:
                return "Cash on Delivery";
            default:
                return this.name();
        }
    }

    public String getDescription() {
        switch (this) {
            case CREDIT_CARD:
                return "Pay using your credit card";
            case DEBIT_CARD:
                return "Pay using your debit card";
            case UPI:
                return "Pay using UPI (PhonePe, Google Pay, Paytm)";
            case NET_BANKING:
                return "Pay using net banking";
            case WALLET:
                return "Pay using digital wallet";
            case CASH_ON_DELIVERY:
                return "Pay cash when your order is delivered";
            default:
                return "Payment method";
        }
    }

    public boolean isOnlinePayment() {
        return this != CASH_ON_DELIVERY;
    }

    public boolean isInstantPayment() {
        return this == UPI || this == WALLET;
    }

    public boolean requiresCardDetails() {
        return this == CREDIT_CARD || this == DEBIT_CARD;
    }

    public boolean requiresBankDetails() {
        return this == NET_BANKING;
    }

    public boolean requiresUPIId() {
        return this == UPI;
    }

    public boolean isCashPayment() {
        return this == CASH_ON_DELIVERY;
    }

    public static PaymentMethod fromString(String method) {
        try {
            return PaymentMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static PaymentMethod[] getOnlinePaymentMethods() {
        return new PaymentMethod[]{CREDIT_CARD, DEBIT_CARD, UPI, NET_BANKING, WALLET};
    }

    public static PaymentMethod[] getInstantPaymentMethods() {
        return new PaymentMethod[]{UPI, WALLET};
    }

    public static PaymentMethod[] getCardPaymentMethods() {
        return new PaymentMethod[]{CREDIT_CARD, DEBIT_CARD};
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}

