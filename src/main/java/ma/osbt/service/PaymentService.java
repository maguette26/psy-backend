package ma.osbt.service;

public interface PaymentService {
	String createPaymentIntent(Long amountInCents, String currency, String successUrl, String cancelUrl) throws Exception;
	 
}
