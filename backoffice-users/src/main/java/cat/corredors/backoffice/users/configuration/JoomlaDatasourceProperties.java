package cat.corredors.backoffice.users.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "joomla.datasource")
@Getter @Setter
public class JoomlaDatasourceProperties {

	private String jdbcUrl;
	private String username;
	private String password;
}
