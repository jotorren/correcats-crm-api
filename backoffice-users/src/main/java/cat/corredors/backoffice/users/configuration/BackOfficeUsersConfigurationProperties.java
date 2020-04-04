package cat.corredors.backoffice.users.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "backoffice.users")
@Getter @Setter
public class BackOfficeUsersConfigurationProperties {

	String exportDirectory;
}
