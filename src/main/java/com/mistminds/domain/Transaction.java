package com.mistminds.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

import javax.persistence.Transient;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "transaction")
public class Transaction implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	@Field("order_id")
	private String order_id;
	@Field("tracking_id")
	private String tracking_id;
	@Field("bank_ref_no")
	private String bank_ref_no;
	@Field("order_status")
	private String order_status;
	@Field("failure_message")
	private String failure_message;
	@Field("payment_mode")
	private String payment_mode;
	@Field("card_name")
	private String card_name;
	@Field("status_code")
	private String status_code;
	@Field("status_message")
	private String status_message;
	@Field("amount")
	private double amount;
	@Field("billing_name")
	private String billing_name;
	@Field("billing_address")
	private String billing_address;
	@Field("billing_city")
	private String billing_city;
	@Field("billing_state")
	private String billing_state;
	@Field("billing_zip")
	private String billing_zip;
	@Field("billing_country")
	private String billing_country;
	@Field("billing_tel")
	private String billing_tel;
	@Field("billing_email")
	private String billing_email;
	@Field("delivery_name")
	private String delivery_name;
	@Field("delivery_address")
	private String delivery_address;
	@Field("delivery_city")
	private String delivery_city;
	@Field("delivery_state")
	private String delivery_state;
	@Field("delivery_zip")
	private String delivery_zip;
	@Field("delivery_country")
	private String delivery_country;
	@Field("delivery_tel")
	private String delivery_tel;
	@Field("merchant_param2")
	private String merchant_param2;
	@Field("provider_id")
	private String provider_id;
	@Field("vault")
	private String vault;
	@Field("offer_type")
	private String offer_type;
	@Field("offer_code")
	private String offer_code;
	@Field("discount_value")
	private String discount_value;
	@Field("mer_amount")
	private double mer_amount;
	@Field("eci_value")
	private String eci_value;
	@Field("retry")
	private String retry;
	@Field("transaction_date")
	private ZonedDateTime transaction_date;
	@Field("response_code")
	private String response_code;
	public ZonedDateTime getTransaction_date() {
		return transaction_date;
	}

	public void setTransaction_date(ZonedDateTime transaction_date) {
		this.transaction_date = transaction_date;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getTracking_id() {
		return tracking_id;
	}

	public void setTracking_id(String tracking_id) {
		this.tracking_id = tracking_id;
	}

	public String getBank_ref_no() {
		return bank_ref_no;
	}

	public void setBank_ref_no(String bank_ref_no) {
		this.bank_ref_no = bank_ref_no;
	}

	public String getOrder_status() {
		return order_status;
	}

	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}

	public String getFailure_message() {
		return failure_message;
	}

	public void setFailure_message(String failure_message) {
		this.failure_message = failure_message;
	}

	public String getPayment_mode() {
		return payment_mode;
	}

	public void setPayment_mode(String payment_mode) {
		this.payment_mode = payment_mode;
	}

	public String getCard_name() {
		return card_name;
	}

	public void setCard_name(String card_name) {
		this.card_name = card_name;
	}

	public String getStatus_code() {
		return status_code;
	}

	public void setStatus_code(String status_code) {
		this.status_code = status_code;
	}

	public String getStatus_message() {
		return status_message;
	}

	public void setStatus_message(String status_message) {
		this.status_message = status_message;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getBilling_name() {
		return billing_name;
	}

	public void setBilling_name(String billing_name) {
		this.billing_name = billing_name;
	}

	public String getBilling_address() {
		return billing_address;
	}

	public void setBilling_address(String billing_address) {
		this.billing_address = billing_address;
	}

	public String getBilling_city() {
		return billing_city;
	}

	public void setBilling_city(String billing_city) {
		this.billing_city = billing_city;
	}

	public String getBilling_state() {
		return billing_state;
	}

	public void setBilling_state(String billing_state) {
		this.billing_state = billing_state;
	}

	public String getBilling_zip() {
		return billing_zip;
	}

	public void setBilling_zip(String billing_zip) {
		this.billing_zip = billing_zip;
	}

	public String getBilling_country() {
		return billing_country;
	}

	public void setBilling_country(String billing_country) {
		this.billing_country = billing_country;
	}

	public String getBilling_tel() {
		return billing_tel;
	}

	public void setBilling_tel(String billing_tel) {
		this.billing_tel = billing_tel;
	}

	public String getBilling_email() {
		return billing_email;
	}

	public void setBilling_email(String billing_email) {
		this.billing_email = billing_email;
	}

	public String getDelivery_name() {
		return delivery_name;
	}

	public void setDelivery_name(String delivery_name) {
		this.delivery_name = delivery_name;
	}

	public String getDelivery_address() {
		return delivery_address;
	}

	public void setDelivery_address(String delivery_address) {
		this.delivery_address = delivery_address;
	}

	public String getDelivery_city() {
		return delivery_city;
	}

	public void setDelivery_city(String delivery_city) {
		this.delivery_city = delivery_city;
	}

	public String getDelivery_state() {
		return delivery_state;
	}

	public void setDelivery_state(String delivery_state) {
		this.delivery_state = delivery_state;
	}

	public String getDelivery_zip() {
		return delivery_zip;
	}

	public void setDelivery_zip(String delivery_zip) {
		this.delivery_zip = delivery_zip;
	}

	public String getDelivery_country() {
		return delivery_country;
	}

	public void setDelivery_country(String delivery_country) {
		this.delivery_country = delivery_country;
	}

	public String getDelivery_tel() {
		return delivery_tel;
	}

	public void setDelivery_tel(String delivery_tel) {
		this.delivery_tel = delivery_tel;
	}

	public String getProvider_id() {
		return provider_id;
	}

	public void setProvider_id(String provider_id) {
		this.provider_id = provider_id;
	}
	public String getMerchant_param2() {
		return merchant_param2;
	}
	
	public void setMerchant_param2(String merchant_param2) {
		this.merchant_param2 = merchant_param2;
	}

	public String getVault() {
		return vault;
	}

	public void setVault(String vault) {
		this.vault = vault;
	}

	public String getOffer_type() {
		return offer_type;
	}

	public void setOffer_type(String offer_type) {
		this.offer_type = offer_type;
	}

	public String getOffer_code() {
		return offer_code;
	}

	public void setOffer_code(String offer_code) {
		this.offer_code = offer_code;
	}

	public String getDiscount_value() {
		return discount_value;
	}

	public void setDiscount_value(String discount_value) {
		this.discount_value = discount_value;
	}

	public double getMer_amount() {
		return mer_amount;
	}

	public void setMer_amount(double mer_amount) {
		this.mer_amount = mer_amount;
	}

	public String getEci_value() {
		return eci_value;
	}

	public void setEci_value(String eci_value) {
		this.eci_value = eci_value;
	}

	public String getRetry() {
		return retry;
	}

	public void setRetry(String retry) {
		this.retry = retry;
	}

	public String getResponse_code() {
		return response_code;
	}

	public void setResponse_code(String response_code) {
		this.response_code = response_code;
	}

	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Transaction transaction = (Transaction) o;
        return Objects.equals(id, transaction.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
    	return "Transaction{" +
                "id=" + id +
                 ", order_id='" + order_id + "'" +
                 ", tracking_id='" + tracking_id + "'" +
                 ", bank_ref_no='" + bank_ref_no + "'" +
                 ", order_status='" + order_status + "'" +
                 ", failure_message='" + failure_message + "'" +
                 ", payment_mode='" + payment_mode + "'" +
                 ", card_name='" + card_name + "'" +
                 ", status_code='" + status_code + "'" +
                 ", status_message='" + status_message + "'" +
                 ", amount='" + amount + "'" +
                 ", billing_name='" + billing_name + "'" +
                 ", billing_address='" + billing_address + "'" +
                 ", billing_city='" + billing_city + "'" +
                 ", billing_state='" + billing_state + "'" +
                 ", billing_zip='" + billing_zip + "'" +
                 ", billing_country='" + billing_country + "'" +
                 ", billing_tel='" + billing_tel + "'" +
                 ", billing_email='" + billing_email + "'" +
                 ", delivery_name='" + delivery_name + "'" +
                 ", delivery_address='" + delivery_address + "'" +
                 ", delivery_city='" + delivery_city + "'" +
                 ", delivery_state='" + delivery_state + "'" +
                 ", delivery_zip='" + delivery_zip + "'" +
                 ", delivery_country='" + delivery_country + "'" +
                 ", delivery_tel='" + delivery_tel + "'" +
                 ", provider_id='" + provider_id + "'" +
                 ", vault='" + vault + "'" +
                 ", offer_type='" + offer_type + "'" +
                 ", offer_code='" + offer_code + "'" +
                 ", discount_value='" + discount_value + "'" +
                 ", mer_amount='" + mer_amount + "'" +
                 ", eci_value='" + eci_value + "'" +
                 ", retry='" + retry + "'" +
                 ", response_code='" + response_code + "'" +
                 ", transaction_date='" + transaction_date + "'" +
          
      '}';
    }
}