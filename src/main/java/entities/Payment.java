package entities;

public class Payment {
    private int paymentId;
    private String transactionId; // ✅ Kept as String
    private double amount;
    private String email; // ✅ Added email field

    public Payment() {}

    public Payment(int paymentId, String transactionId, double amount, String email) {
        this.paymentId = paymentId;
        this.transactionId = transactionId;
        this.amount = amount;
        this.email = email;
    }

    public Payment(String transactionId, double amount, String email) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.email = email;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "transactionId='" + transactionId + '\'' +
                ", amount=" + amount +
                ", email='" + email + '\'' +
                '}';
    }
}