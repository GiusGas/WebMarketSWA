
package it.univaq.swa.webmarket.jackson;

import java.util.Calendar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

	private final ObjectMapper mapper;

	public ObjectMapperContextResolver() {
		this.mapper = createObjectMapper();
	}

	@Override
	public ObjectMapper getContext(Class<?> type) {
		return mapper;
	}

	private ObjectMapper createObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();

		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		SimpleModule customSerializer = new SimpleModule("CustomSerializersModule");

		customSerializer.addSerializer(Calendar.class, new JavaCalendarSerializer());
		customSerializer.addDeserializer(Calendar.class, new JavaCalendarDeserializer());

		objectMapper.registerModule(customSerializer);

		objectMapper.findAndRegisterModules();
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return objectMapper;
	}
}
