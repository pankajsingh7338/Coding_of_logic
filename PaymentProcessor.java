package com.actolap.wse.payment.service;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.naming.NameNotFoundException;

import com.actolap.wse.dao.ForumDao;
import com.actolap.wse.dao.PaymentDao;
import com.actolap.wse.dao.PaymentGatewayDao;
import com.actolap.wse.dao.PlayerDao;
import com.actolap.wse.model.Common;
import com.actolap.wse.model.payment.Payment;
import com.actolap.wse.model.payment.Payment.PaymentConsumer;
import com.actolap.wse.model.payment.Payment.PaymentStatus;
import com.actolap.wse.model.payment.Payment.PaymentType;
import com.actolap.wse.model.payment.PaymentGateway;
import com.actolap.wse.model.player.Player;
import com.actolap.wse.request.PaymentRequest;

public class PaymentProcessor {

	private PaymentRequest request;
	private Map<String, String> merchantData;
	private GatewayMeta gatewayMeta;

	public PaymentProcessor() {
	}

	public PaymentProcessor(PaymentRequest paymentRequest)
			throws NameNotFoundException, InterruptedException, ExecutionException, IOException {
		request = paymentRequest;
		gatewayInit(paymentRequest.getGatewayId(), PaymentType.valueOf(paymentRequest.getPaymentType()));
	}

	public boolean initiate(Process<PaymentRequest, GatewayMeta> process) {
		merchantData = process.run(request, gatewayMeta);
		return merchantData != null;
	}

	public Map<String, String> getFormData(WebData data, String contextPath) {
		return data.get(merchantData, gatewayMeta, contextPath);
	}

	// public String getFormFullURL() {
	// URL url = new URL();
	// if (this.merchantData != null) {
	// if (PaymentGateways.payu_paisa.equals(gatewayMeta.getGateway())) {
	// url.addLast(gatewayMeta.getMeta().get("paymentURL") + "?");
	// merchantData.forEach((k, v) -> url.addLast(k + "=" + v + "&"));
	// }
	// }
	// return url.getUrl().substring(0, url.getUrl().length() - 1);
	// }

	public void verifiyTransaction(String userAgent, Payment payment)
			throws IOException, InterruptedException, ExecutionException {
		String h = null;
		String additionalCharge = merchantData.get("additionalCharge");
		String hash = merchantData.get("hash");
		String status = merchantData.get("status");
		if (additionalCharge != null)
			h = HashGenerator.getHashWithAdditionalCharge(merchantData, gatewayMeta.getMeta().get("salt"));
		else
			h = HashGenerator.getHash(merchantData, gatewayMeta.getMeta().get("salt"));
		if (hash.equals(h)) {
			if (status.equals("success")) {
				if (request.getPaymentType().equals(PaymentType.prod)) {
					if (HttpPaymentVerifier.verify(gatewayMeta, request, userAgent)) {
						payment.setStatus(PaymentStatus.completed);
					} else {
						payment.setStatus(PaymentStatus.verification_pending);
					}
				} else {
					payment.setStatus(PaymentStatus.completed);
				}
			} else {
				payment.setStatus(PaymentStatus.failed);
			}
		} else {
			payment.setStatus(PaymentStatus.hash_not_match);
		}
		payment.setResponseData(merchantData);
	}

	public void saveTransaction(Meta meta) throws InterruptedException, ExecutionException, IOException {
		String displayId = displayTransaction();
		
		request.setStatus(PaymentStatus.initiated.toString());
		if (meta != null)
			request.setMeta(meta.get(merchantData));
		Payment payment = new Payment();
		payment.setId(request.getId());
		payment.setAmount(request.getAmount());
		payment.setFirstname(request.getFirstname());
		payment.setLastname(request.getLastname());
		payment.setEmail(request.getEmail());
		payment.setPhone(request.getPhone());
		payment.setProductinfo(request.getProductinfo());
		payment.setAddress(request.getAddress());
		payment.setUserId(request.getUserId());
		payment.setGatewayId(request.getGatewayId());
		payment.setGameName(request.getGameName());
		payment.setMeta(request.getMeta());
		payment.setIp(request.getIp());
		if(displayId != null)
			payment.setDisplayTransaxId(displayId);
		if(request.getUniqueId() != null)
			payment.setUniqueId(request.getUniqueId());
		payment.setPaymentType((PaymentType.valueOf(request.getPaymentType())));
		payment.setStatus(PaymentStatus.valueOf(request.getStatus()));
		payment.setConsumer(PaymentConsumer.valueOf(request.getConsumer()));
		if(request.getCode() != null)
			payment.setUniqueCode(request.getCode());
		PaymentDao.persist(payment);

	}

	public GatewayMeta getGatwayMeta() {
		return this.gatewayMeta;
	}

	public void setMerchantData(Map<String, String> data) {
		merchantData = data;
	}

	private void gatewayInit(String gatewayId, PaymentType type)
			throws NameNotFoundException, InterruptedException, ExecutionException, IOException {
		PaymentGateway paymentGateway = PaymentGatewayDao.getById(gatewayId);
		gatewayMeta = new GatewayMeta(paymentGateway, type);
	}
	
	private String displayTransaction() {
		String displayId;
		Date now = new Date();     
		int year = now.getYear();
		int month = now.getMonth() +1;
		int date = now.getDate();
		String displayMonth = null;
		String displayDay = null;
		int digitInMonth = NumberofDigits(month);
		if(digitInMonth == 1)
			displayMonth = "0"+month;
		else
			displayMonth = String.valueOf(month);
			
		int digitInDate = NumberofDigits(date);
		if(digitInDate == 1)
			displayDay = "0"+date;
		else
			displayDay = String.valueOf(date);
		
		year = year%100;
		int nextYear = year +1;
		nextYear = nextYear%100;
		displayId = Integer.toString(year) + Integer.toString(nextYear);
		displayId = displayId+"B"+displayMonth+date+"-";
		Common common = ForumDao.getCommon();
		
		int uniqueId = common.getTransactionId();
		uniqueId = uniqueId + 1;
		String displayUniqueId;
		ForumDao.updateUniqueId(common.getId(),uniqueId);
		int digitInUniqueId = NumberofDigits(uniqueId);
		if(digitInUniqueId ==1)
			displayUniqueId = "00000"+uniqueId;
		else if(digitInUniqueId ==2)
			displayUniqueId = "0000"+uniqueId;
		else if(digitInUniqueId ==3)
			displayUniqueId = "000"+uniqueId;
		else if(digitInUniqueId ==4)
			displayUniqueId = "00"+uniqueId;
		else if(digitInUniqueId ==5)
			displayUniqueId = "0"+uniqueId;
		else
			displayUniqueId = String.valueOf(uniqueId);
		displayId = displayId +displayUniqueId;
		
		return displayId;
	}
	
	public static int NumberofDigits(int Number) {
		int Count;
		for (Count = 0; Number > 0; Number = Number/10) {
			Count = Count + 1; 
		}
		return Count;
	}
}
