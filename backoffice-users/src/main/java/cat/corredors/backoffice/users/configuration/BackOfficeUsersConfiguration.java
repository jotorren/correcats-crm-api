package cat.corredors.backoffice.users.configuration;

import static cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants.DATE_FORMAT;
import static cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants.REST.Endpoints.API_BASE;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import cat.corredors.backoffice.users.controller.SearchCriteriaDeSerializer;
import cat.corredors.backoffice.users.controller.StringToSearchOperationEnumConverter;
import cat.corredors.backoffice.users.controller.StringToSearchOperationEnumDeserializer;
import cat.corredors.backoffice.users.domain.Associada;
import cat.corredors.backoffice.users.domain.SearchCriteria;
import cat.corredors.backoffice.users.domain.SearchOperation;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableConfigurationProperties(BackOfficeUsersConfigurationProperties.class)
@EnableAsync
@Slf4j
public class BackOfficeUsersConfiguration implements WebMvcConfigurer, AsyncConfigurer {

//  private static final String DATEFORMAT = "dd/MM/yyyy";
//  private static final String DATETIMEFORMAT = "dd/MM/yyyy HH:mm:ss";
//  private static final String DATETIMEFORMAT2 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
//  
//	@Bean
//	public FormattingConversionService conversionService() {
//		DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService(false);
//
//		DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
//		registrar.setDateFormatter(DateTimeFormatter.ofPattern(DATEFORMAT));
//		registrar.setDateFormatter(DateTimeFormatter.ofPattern(DATETIMEFORMAT));
//		registrar.setDateFormatter(DateTimeFormatter.ofPattern(DATETIMEFORMAT2));
//		
//		registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern(DATETIMEFORMAT));
//		registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern(DATETIMEFORMAT2));
//		registrar.registerFormatters(conversionService);
//
//		// other desired formatters
//
//		return conversionService;
//	}
//
//  @Bean
//  public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
//      return builder -> {
//          builder.simpleDateFormat(DATEFORMAT);
//          builder.simpleDateFormat(DATETIMEFORMAT);
//          builder.simpleDateFormat(DATETIMEFORMAT2);
//          
//          builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(DATEFORMAT)));
//          builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(DATETIMEFORMAT)));
//          builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(DATETIMEFORMAT2)));
//          
//          builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATETIMEFORMAT)));
//          builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATETIMEFORMAT2)));;
//      };
//  }

	
	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new StringToSearchOperationEnumConverter());
	}

	@Bean
	public ObjectMapper objectMapper() {
		SimpleModule module = new SimpleModule();
		module.addDeserializer(SearchOperation.class, new StringToSearchOperationEnumDeserializer());
		module.addDeserializer(SearchCriteria.class, new SearchCriteriaDeSerializer());
		
		return new ObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
				.setTimeZone(TimeZone.getDefault()).registerModule(module)
				.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
	}

	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
		jsonConverter.setObjectMapper(objectMapper());
		return jsonConverter;
	}

	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(3);
		executor.setMaxPoolSize(3);
		executor.setQueueCapacity(100);
		executor.setThreadNamePrefix("AsynchThread-");
		executor.initialize();
		return executor;
	}

	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		configurer.setTaskExecutor((ThreadPoolTaskExecutor) getAsyncExecutor());
	}

	@Bean
	public Function<String, URI> internalIdToURI() {
		return (String id) -> null == id ? URI.create("")
				: ServletUriComponentsBuilder.fromCurrentContextPath().path(API_BASE + "/{id}").build(id);
	}
	
	@Bean
	public Supplier<List<String>> allAssociadaProperties() {
		return () -> {
			
			List<String> all = Collections.emptyList();
			try {
				all = BeanUtils.describe(new Associada()).keySet().stream().collect(Collectors.toList());
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				log.error(e.getMessage());
			}
			
			return all;
		};
	}
}
