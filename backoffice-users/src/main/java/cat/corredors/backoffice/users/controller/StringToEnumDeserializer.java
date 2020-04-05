package cat.corredors.backoffice.users.controller;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringToEnumDeserializer<T extends Enum<T>> extends JsonDeserializer<T> {
	
	private Class<T> enumClass;
	
	public StringToEnumDeserializer(Class<T> clazz) {
		this.enumClass = clazz;
	}
	
	@Override
	public T deserialize(final JsonParser jp, final DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		if (JsonToken.VALUE_STRING.equals(jp.getCurrentToken())) {
			String source = jp.getText().toString();
			try {
				return Enum.valueOf(enumClass, source.toUpperCase());
			} catch (IllegalArgumentException ie) {
				log.error("Invalid enumeration  value", ie);
			}
		}
		
		return null;
	}

}
