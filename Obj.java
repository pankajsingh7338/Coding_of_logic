package com.actolap.wse;

import org.restexpress.Request;
import org.restexpress.Response;
import org.restexpress.pipeline.MessageObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Obj extends MessageObserver {

	private static final Logger LOG = LoggerFactory.getLogger(Obj.class);
	private static final String USER_AGENT = "User-Agent";

	@Override
	protected void onReceived(Request request, Response response) {

	}

	@Override
	protected void onException(Throwable exception, Request request, Response response) {
		LOG.error("Method {}, {}  threw exception:  {} message {} responded {}  IP {}",
				request.getEffectiveHttpMethod().toString(), request.getUrl(), exception.getClass().getSimpleName(),
				exception.getMessage(), System.currentTimeMillis() - request.getStartTime());
		LOG.error(exception.getMessage(), exception);
	}

	@Override
	protected void onSuccess(Request request, Response response) {
	}

	@Override
	protected void onComplete(Request request, Response response) {
		LOG.info(" {} responded with {} in {} ua {} ", request.getUrl(), response.getResponseStatus().toString(),
				System.currentTimeMillis() - request.getStartTime(), request.getHeader(USER_AGENT));
	}
}

