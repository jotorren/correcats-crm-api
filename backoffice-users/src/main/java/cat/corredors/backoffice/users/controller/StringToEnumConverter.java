package cat.corredors.backoffice.users.controller;

import org.springframework.core.convert.converter.Converter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringToEnumConverter<T extends Enum<T>> implements Converter<String, T> {

	private Class<T> enumClass;
	
	public StringToEnumConverter(Class<T> clazz) {
		this.enumClass = clazz;
	}
	
	@Override
	public T convert(String source) {
		try {
			return Enum.valueOf(enumClass, source.toUpperCase());
		} catch (IllegalArgumentException ie) {
			log.error("Invalid enumeration  value", ie);
		}
		
		return null;
	}

}
