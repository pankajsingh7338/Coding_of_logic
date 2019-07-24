package com.actolap.wse.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ning.http.client.AsyncHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.HttpResponseBodyPart;
import com.ning.http.client.HttpResponseHeaders;
import com.ning.http.client.HttpResponseStatus;

public class NingClient {

	public static final Logger logger = LoggerFactory.getLogger(NingClient.class);
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String UTF_8 = "UTF-8";
	private AsyncHttpClient asyncHttpClient;
	private static NingClient client = new NingClient();

	private NingClient() {
		AsyncHttpClientConfig.Builder bc = new AsyncHttpClientConfig.Builder();
	//	bc.setMaxConnectionsPerHost(-1);
	//	bc.setMaxConnections(-1);
		//bc.setConnectTimeout(350);
		bc.setDisableUrlEncodingForBoundedRequests(true);
		bc.setFollowRedirect(true);
		asyncHttpClient = new AsyncHttpClient(bc.build());

	}

	public static NingClient getInstance() {
		return client;
	}

	public String postData(String url, String data) throws InterruptedException, ExecutionException, IOException {
		BoundRequestBuilder postRequest = asyncHttpClient.preparePost(url);
		postRequest.setHeader(CONTENT_TYPE, "application/json");
		postRequest.setBody(data);
		Future<String> f = postRequest.execute(new AsyncHandler<String>() {
			private ByteArrayOutputStream bytes = new ByteArrayOutputStream();

			public STATE onStatusReceived(HttpResponseStatus status) throws Exception {
				int statusCode = status.getStatusCode();
				if (statusCode >= 500) {
					return STATE.ABORT;
				}
				return STATE.CONTINUE;
			}

			public STATE onHeadersReceived(HttpResponseHeaders h) throws Exception {
				return STATE.CONTINUE;
			}

			public STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
				bytes.write(bodyPart.getBodyPartBytes());
				return STATE.CONTINUE;
			}

			public String onCompleted() throws Exception {
				return bytes.toString(UTF_8);
			}

			public void onThrowable(Throwable t) {
			}
		});
		String response = f.get();

		return response;
	}

}

