package com.actolap.wse.rest.serialization;

import org.restexpress.serialization.json.JacksonJsonProcessor;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class JsonSerializationProcessor extends JacksonJsonProcessor {

	public JsonSerializationProcessor(boolean override) {
		super(override);
	}

	@Override
	protected void initializeModule(SimpleModule module) {
		super.initializeModule(module);
	}

	@Override
	protected void initializeMapper(ObjectMapper mapper) {
		mapper.configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, false);
		mapper.setSerializationInclusion(Include.NON_NULL);
		// mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
				true);
	}

}

