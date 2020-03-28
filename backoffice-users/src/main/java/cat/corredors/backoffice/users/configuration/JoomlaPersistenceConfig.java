package cat.corredors.backoffice.users.configuration;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.mysql.cj.jdbc.MysqlXADataSource;

@Configuration
@EnableTransactionManagement
//@EnableJpaRepositories(
//		entityManagerFactoryRef = "joomlaEntityManagerFactory", 
//		transactionManagerRef = "joomlaTransactionManager", 
//		basePackages = {"cat.corredors.backoffice.joomla.repository" })
@EnableConfigurationProperties(JoomlaDatasourceProperties.class)
public class JoomlaPersistenceConfig {

	@Autowired
	private JoomlaDatasourceProperties joomlaDatasourceProperties;
	
//	@Bean(name = "joomlaDataSource")
//	@ConfigurationProperties(prefix = "joomla.datasource")
//	public DataSource dataSource() {
//		return DataSourceBuilder
//				.create()
//				.build();
//	}

	@Bean(name = "joomlaDataSource", initMethod = "init", destroyMethod = "close")
	@ConfigurationProperties(prefix = "joomla.datasource")
	public DataSource customerDataSource() throws SQLException {
		MysqlXADataSource mysqlXaDataSource = new MysqlXADataSource();
		mysqlXaDataSource.setUrl(joomlaDatasourceProperties.getJdbcUrl());
		mysqlXaDataSource.setPassword(joomlaDatasourceProperties.getPassword());
		mysqlXaDataSource.setUser(joomlaDatasourceProperties.getUsername());
		mysqlXaDataSource.setPinGlobalTxToPhysicalConnection(true);
		
		AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
		xaDataSource.setXaDataSource(mysqlXaDataSource);
		xaDataSource.setUniqueResourceName("xads2");
		xaDataSource.setTestQuery("SELECT 1");
		return xaDataSource;
	}
	
//	@Bean(name = "joomlaEntityManagerFactory")
//	public LocalContainerEntityManagerFactoryBean joomlaEntityManagerFactory(EntityManagerFactoryBuilder builder,
//			@Qualifier("joomlaDataSource") DataSource dataSource) {
//		return builder
//				.dataSource(dataSource)
//				.packages("cat.corredors.backoffice.joomla.domain")
//				.persistenceUnit("joomla")
//				.build();
//	}

//	@Bean(name = "joomlaTransactionManager")
//	public PlatformTransactionManager joomlaTransactionManager(
//			@Qualifier("joomlaEntityManagerFactory") EntityManagerFactory joomlaEntityManagerFactory) {
//		return new JpaTransactionManager(joomlaEntityManagerFactory);
//	}
	
	@Bean(name = "joomlaJdbcTemplate")
	public JdbcTemplate jdbcTemplate(@Qualifier("joomlaDataSource") DataSource ds) {
		return new JdbcTemplate(ds);
	}
}