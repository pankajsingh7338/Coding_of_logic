package com.actolap.wse.payment.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.actolap.wse.request.PaymentRequest;
import com.google.gson.Gson;

public class HttpPaymentVerifier {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(HttpPaymentVerifier.class);

	public static boolean verify(GatewayMeta gatewayMeta,
			PaymentRequest request, String userAgent) throws IOException {

		Map<String, String> data = gatewayMeta.getMeta();
		String url = data.get("paymentResponse");

		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		// add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", userAgent);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("authorization",
				data.get("header_authorization"));
		con.setRequestProperty("cache-control", "no-cache");

		String urlParameters = "merchantKey=" + data.get("key")
				+ "&merchantTransactionIds=" + request.getId();

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		LOGGER.info("Sending 'POST' request to URL : " + url);
		LOGGER.info("Post parameters : " + urlParameters);
		LOGGER.info("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		LOGGER.info(response.toString());
		Gson gson = new Gson();
		PayUMoneyResponse payUresponse = gson.fromJson(response.toString(),
				PayUMoneyResponse.class);
		PayUMoneyObj payUobj = payUresponse.getResult().get(0);
		if (payUobj != null) {
			Map<String, String> result = payUobj.getPostBackParam();
			if (result != null) {
				return ("success".equals(result.get("status")));
			}
		}
		return false;
	}

}

