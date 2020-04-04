package cat.corredors.backoffice.users.configuration;

import static cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants.REST.Endpoints.API_BASE;

import java.net.URI;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Configuration
public class BackOfficeUsersConfiguration implements WebMvcConfigurer {

//    private static final String DATEFORMAT = "dd/MM/yyyy";
//    private static final String DATETIMEFORMAT = "dd/MM/yyyy HH:mm:ss";
//    private static final String DATETIMEFORMAT2 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
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
//    @Bean
//    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
//        return builder -> {
//            builder.simpleDateFormat(DATEFORMAT);
//            builder.simpleDateFormat(DATETIMEFORMAT);
//            builder.simpleDateFormat(DATETIMEFORMAT2);
//            
//            builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(DATEFORMAT)));
//            builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(DATETIMEFORMAT)));
//            builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(DATETIMEFORMAT2)));
//            
//            builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATETIMEFORMAT)));
//            builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATETIMEFORMAT2)));;
//        };
//    }

	@Bean
	public Function<String, URI> internalIdToURI() {
		return (String id) -> null == id ? URI.create("")
				: ServletUriComponentsBuilder.fromCurrentContextPath().path(API_BASE + "/{id}").build(id);
	}
}
