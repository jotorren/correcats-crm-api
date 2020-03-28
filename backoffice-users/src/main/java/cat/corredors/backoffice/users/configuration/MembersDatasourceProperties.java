package cat.corredors.backoffice.users.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "spring.datasource")
@Getter @Setter
public class MembersDatasourceProperties {

	private String jdbcUrl;
	private String username;
	private String password;
}
