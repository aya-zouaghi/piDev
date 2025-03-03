package services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

public class APIpayment {
    private static final String STRIPE_API_KEY = "sk_test_51QyZf8GC5kHdZarFOvjXGf94Or1WDR5CcJDhxQdawzXLpKNVpjpNle443aBef7mt2Ps0EVV2wKyckJ65qXmwVk4q00eg8JhyfX";

    static {
        Stripe.apiKey = STRIPE_API_KEY;
    }
    public static PaymentIntent createPayment(double montant) {
        try {
            long amountInCents = (long) (montant * 100);

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency("gbp")
                    .setPaymentMethod("pm_card_visa")
                    .addPaymentMethodType("card")
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            if (paymentIntent.getStatus().equals("requires_payment_method")) {
                System.out.println("✅ PaymentIntent created. Sending email...");

            }

            return paymentIntent;
        } catch (StripeException e) {
            e.printStackTrace();
            return null;
        }
    }



//    public static void main(String[] args) {
//        System.out.println("🔄 Starting Stripe Payment Test...");
//        double montant = 5.00;
//
//        PaymentIntent paymentIntent = createPayment(montant);
//
//        if (paymentIntent != null) {
//            System.out.println("✅ PaymentIntent created successfully!");
//            System.out.println("🆔 ID: " + paymentIntent.getId());
//            System.out.println("💰 Amount: " + paymentIntent.getAmount() / 100.0 + " GBP");
//            System.out.println("🔗 Status: " + paymentIntent.getStatus());
//        } else {
//            System.out.println("❌ Failed to create PaymentIntent.");
//        }
//    }
}