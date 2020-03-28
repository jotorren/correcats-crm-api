package cat.corredors.backoffice.users.configuration;

import static cat.corredors.backoffice.users.crosscutting.BOUsersConstants.REST.Endpoints.API_BASE;

import java.net.URI;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Configuration
public class BOUsersConfiguration implements WebMvcConfigurer {
	
	@Bean
	public Function<String, URI> internalIdToURI() {
		return (String id) -> null == id? URI.create("") : 
			ServletUriComponentsBuilder.fromCurrentContextPath().path(API_BASE+"/{id}").build(id);
	}
	
}
