package com.actolap.wse.rest.serialization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.restexpress.Request;
import org.restexpress.Response;
import org.restexpress.common.exception.ConfigurationException;
import org.restexpress.common.util.StringUtils;
import org.restexpress.contenttype.MediaRange;
import org.restexpress.contenttype.MediaTypeParser;
import org.restexpress.exception.BadRequestException;
import org.restexpress.exception.NotAcceptableException;
import org.restexpress.response.ErrorResponseWrapper;
import org.restexpress.response.ResponseProcessor;
import org.restexpress.response.ResponseWrapper;
import org.restexpress.serialization.Aliasable;
import org.restexpress.serialization.SerializationProcessor;
import org.restexpress.serialization.SerializationSettings;

public class MbXSerializationProvider implements
		org.restexpress.serialization.SerializationProvider {
	private Map<String, ResponseProcessor> processorsByFormat = new HashMap<String, ResponseProcessor>();
	private Map<String, ResponseProcessor> processorsByMediaType = new HashMap<String, ResponseProcessor>();
	private static final SerializationProcessor JSON_SERIALIZER = new JsonSerializationProcessor(
			false);

	private static final ResponseWrapper RESPONSE_WRAPPER = new ErrorResponseWrapper();

	private List<MediaRange> supportedMediaRanges = new ArrayList<MediaRange>();
	private ResponseProcessor defaultProcessor;
	private List<Alias> aliases = new ArrayList<Alias>();

	public MbXSerializationProvider() {
		add(JSON_SERIALIZER, RESPONSE_WRAPPER, true);
	}

	@Override
	public void add(SerializationProcessor arg0, ResponseWrapper arg1) {
		add(arg0, arg1, false);
	}

	public void add(SerializationProcessor processor, ResponseWrapper wrapper,
			boolean isDefault) {
		addMediaRanges(processor.getSupportedMediaRanges());
		ResponseProcessor responseProcessor = new ResponseProcessor(processor,
				wrapper);
		assignAliases(responseProcessor);
		for (String format : processor.getSupportedFormats()) {
			if (processorsByFormat.containsKey(format)) {
				throw new ConfigurationException("Duplicate supported format: "
						+ format);
			}

			processorsByFormat.put(format, responseProcessor);
		}

		for (MediaRange mediaRange : processor.getSupportedMediaRanges()) {
			String mediaType = mediaRange.asMediaType();

			if (!processorsByMediaType.containsKey(mediaType)) {
				processorsByMediaType.put(mediaRange.asMediaType(),
						responseProcessor);
			}
		}

		if (isDefault) {
			defaultProcessor = responseProcessor;
		}
	}

	@Override
	public void alias(String name, Class<?> type) {
		Alias a = new Alias(name, type);
		if (!aliases.contains(a)) {
			aliases.add(a);
		}

		assignAlias(a);
	}

	@Override
	public void setDefaultFormat(String format) {
		ResponseProcessor processor = processorsByFormat.get(format);
		if (processor == null) {
			throw new RuntimeException(
					"No serialization processor found for requested response format: "
							+ format);
		}

		defaultProcessor = processor;
	}

	/**
	 * Provided for testing so that UTs can specify and format and compare the
	 * resolver-based results.
	 * 
	 * @param format
	 * @return
	 */
	public SerializationProcessor getSerializer(String format) {
		ResponseProcessor p = processorsByFormat.get(format);
		if (p != null) {
			return p.getSerializer();
		}

		return null;
	}

	@Override
	public SerializationSettings resolveRequest(Request request) {
		ResponseProcessor processor = null;
		String format = request.getFormat();
		String bestMatch = null;

		if (format != null) {
			processor = processorsByFormat.get(format);

			if (processor == null) {
				throw new NotAcceptableException(format);
			}
		}

		if (processor == null) {
			List<MediaRange> requestedMediaRanges = MediaTypeParser
					.parse(request.getHeader(HttpHeaders.Names.CONTENT_TYPE));
			bestMatch = MediaTypeParser.getBestMatch(supportedMediaRanges,
					requestedMediaRanges);

			if (bestMatch != null) {
				processor = processorsByMediaType.get(bestMatch);
			}
		}

		if (processor == null) {
			processor = defaultProcessor;
		}

		return new SerializationSettings(
				(bestMatch == null ? request.getHeader(HttpHeaders.Names.CONTENT_TYPE)
						: bestMatch), processor);
	}

	@Override
	public SerializationSettings resolveResponse(Request request,
			Response response, boolean shouldForce) {
		String bestMatch = null;
		ResponseProcessor processor = null;
		String format = null;

		String version = null;
		if (request.getQueryStringMap() != null) {
			version = request.getHeader("v");
			if (version != null && version.equals("1")) {
				format = "xml";
			}
		}

		if (format != null) {
			processor = processorsByFormat.get(format);
			if (processor != null) {
				bestMatch = processor.getSupportedMediaRanges().get(0)
						.asMediaType();
			} else if (!shouldForce) {
				throw new BadRequestException(
						"Requested representation format not supported: "
								+ format
								+ ". Supported formats: "
								+ StringUtils.join(", ",
										processorsByFormat.keySet()));
			}
		}

		if (processor == null) {
			processor = defaultProcessor;
			bestMatch = processor.getSupportedMediaRanges().get(0)
					.asMediaType();
		}
		return new SerializationSettings(bestMatch, processor);
	}

	private void addMediaRanges(List<MediaRange> mediaRanges) {
		if (mediaRanges == null)
			return;

		for (MediaRange mediaRange : mediaRanges) {
			if (!supportedMediaRanges.contains(mediaRange)) {
				supportedMediaRanges.add(mediaRange);
			}
		}
	}

	private void assignAlias(Alias a) {
		for (ResponseProcessor processor : processorsByFormat.values()) {
			if (Aliasable.class.isAssignableFrom(processor.getSerializer()
					.getClass())) {
				((Aliasable) processor.getSerializer()).alias(a.name, a.type);
			}
		}
	}

	private void assignAliases(ResponseProcessor processor) {
		if (Aliasable.class.isAssignableFrom(processor.getClass())) {
			for (Alias a : aliases) {
				((Aliasable) processor).alias(a.name, a.type);
			}
		}
	}

	// SECTION: INNER CLASS

	private class Alias {
		private String name;
		private Class<?> type;

		public Alias(String name, Class<?> type) {
			super();
			this.name = name;
			this.type = type;
		}

		@Override
		public boolean equals(Object that) {
			return equals((Alias) that);
		}

		private boolean equals(Alias that) {
			if (this.name.equals(that.name) && this.type.equals(that.type)) {
				return true;
			}

			return false;
		}
	}

}
