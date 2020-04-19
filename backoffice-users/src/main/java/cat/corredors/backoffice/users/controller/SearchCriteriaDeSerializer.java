package cat.corredors.backoffice.users.controller;

import static cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants.DATE_FORMAT;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import cat.corredors.backoffice.users.domain.Associada;
import cat.corredors.backoffice.users.domain.SearchCriteria;
import cat.corredors.backoffice.users.domain.SearchOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SearchCriteriaDeSerializer extends JsonDeserializer<SearchCriteria> {
	private static final long serialVersionUID = -2285975254676451930L;

	private final SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
	
	@Override
	public SearchCriteria deserialize(JsonParser parser, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		SearchCriteria sc = new SearchCriteria();
		
		ObjectCodec codec = parser.getCodec();
		JsonNode node = codec.readTree(parser);
		
		// key
		JsonNode keyNode = node.get("key");
		sc.setKey(keyNode.asText());
		
		// operation
		JsonNode opNode = node.get("operation");
		try {
			sc.setOperation(SearchOperation.valueOf(opNode.asText()));
		} catch (IllegalArgumentException ie) {
			log.error(String.format("Invalid enumeration value %s", opNode.asText()), ie);
			sc.setOperation(null);
		}
		
		// value
		try {
			Class<?> type = PropertyUtils.getPropertyType(new Associada(), sc.getKey());	        
			sc.setValue(getTypedValue(node.get("value"), type));
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			log.error(String.format("Error trying to discover type of ", sc.getKey()), e);
			sc.setValue(null);
		}
		
		return sc;
	}

	private Object getTypedValue(JsonNode valueNode, Class<?> type) {
		if (valueNode.isNull()) {
			return null;
		}
		
		if (type.isAssignableFrom(Date.class)) {
			try {
				return valueNode.isArray()?getArrayValue(valueNode, type):formatter.parse(valueNode.asText());
			} catch (ParseException e) {
				log.error(String.format("Invalid date format %s. Supported format is ", valueNode.asText(), DATE_FORMAT), e);
				return null;
			}
		} else if (type.isAssignableFrom(Boolean.class)) {
			return valueNode.isArray()?getArrayValue(valueNode, type):valueNode.booleanValue();
		} else if (type.isAssignableFrom(Float.class)) {
			return valueNode.isArray()?getArrayValue(valueNode, type):valueNode.floatValue();
		} else if (type.isAssignableFrom(Double.class)) {
			return valueNode.isArray()?getArrayValue(valueNode, type):valueNode.doubleValue();
		} else if (type.isAssignableFrom(Integer.class)) {
			return valueNode.isArray()?getArrayValue(valueNode, type):valueNode.intValue();
		} else if (type.isAssignableFrom(Long.class)) {
			return valueNode.isArray()?getArrayValue(valueNode, type):valueNode.longValue();
		} else if (type.isAssignableFrom(Short.class)) {
			return valueNode.isArray()?getArrayValue(valueNode, type):valueNode.shortValue();
		} else if (type.isAssignableFrom(BigInteger.class)) {
			return valueNode.isArray()?getArrayValue(valueNode, type):valueNode.bigIntegerValue();
		} else if (type.isAssignableFrom(String.class)) {
			return valueNode.isArray()?getArrayValue(valueNode, type):valueNode.textValue();
		} else {
			return valueNode;
		}		
	}
	
	private List<Object> getArrayValue(JsonNode valueNode, Class<?> type) {
		List<Object> list = new ArrayList<Object>();
		
		Iterator<JsonNode> it = valueNode.elements();
		while(it.hasNext()) {
			list.add(getTypedValue(it.next(), type));
		}
		
		return list;
	}
}
