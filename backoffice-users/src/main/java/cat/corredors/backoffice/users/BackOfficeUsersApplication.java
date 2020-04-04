package cat.corredors.backoffice.users;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;

@SpringBootApplication(exclude = JmxAutoConfiguration.class)
public class BackOfficeUsersApplication {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		SpringApplication.run(BackOfficeUsersApplication.class, args);
	}
}
