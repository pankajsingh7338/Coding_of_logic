package com.actolap.wse.payment.service;

import java.util.HashMap;
import java.util.Map;

import javax.naming.NameNotFoundException;

import com.actolap.wse.model.payment.Payment.PaymentType;
import com.actolap.wse.model.payment.PaymentGateway;

public class GatewayMeta {

	private PaymentGateways gateway;

	private String successURL;

	private String failedURL;

	private Map<String, String> meta;

	public GatewayMeta() {
	};

	public GatewayMeta(PaymentGateway paymentGateway, PaymentType type)
			throws NameNotFoundException {
		if (PaymentGateways.payu_paisa.toString().equals(
				paymentGateway.getName()))
			this.gateway = PaymentGateways.payu_paisa;
		else
			throw new NameNotFoundException("payment gate type not found");
		this.successURL = paymentGateway.getSuccessURL();
		this.failedURL = paymentGateway.getFailedURL();
		this.meta = new HashMap<String, String>();
		this.meta.putAll(paymentGateway.getMeta().get(type.toString()));
	}

	public PaymentGateways getGateway() {
		return gateway;
	}

	public void setGateway(PaymentGateways gateway) {
		this.gateway = gateway;
	}

	public Map<String, String> getMeta() {
		return meta;
	}

	public void setMeta(Map<String, String> meta) {
		this.meta = meta;
	}

	public String getSuccessURL() {
		return successURL;
	}

	public void setSuccessURL(String successURL) {
		this.successURL = successURL;
	}

	public String getFailedURL() {
		return failedURL;
	}

	public void setFailedURL(String failedURL) {
		this.failedURL = failedURL;
	}

}

