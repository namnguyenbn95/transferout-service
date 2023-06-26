package vn.vnpay.dbinterface.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Optional;

@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "vn.vnpay.dbinterface.repository", entityManagerFactoryRef = "dbOnEntityManager", transactionManagerRef = "dbOnTransactionManager")
public class JpaAuditingConfiguration {

    @Bean
    public AuditorAware<String> auditorProvider() {
        /*
          if you are using spring security, you can get the currently logged username with following code segment.
          SecurityContextHolder.getContext().getAuthentication().getName()
         */
        return () -> Optional.ofNullable("sme-auditor");
    }

    @Autowired
    private Environment env;

    @Primary
    @Bean(name = "dbOnEntityManager")
    public LocalContainerEntityManagerFactoryBean dbOffEntityManager(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(dbOnDataSource()).packages("vn.vnpay.dbinterface.entity").persistenceUnit("smepu").build();
    }

    @Primary
    @Bean
    public DataSource dbOnDataSource() {
        HikariDataSource ds = DataSourceBuilder.create().type(HikariDataSource.class).build();
        ds.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        ds.setJdbcUrl(env.getProperty("spring.datasource.url"));
        ds.setUsername(env.getProperty("spring.datasource.username"));
        ds.setPassword(env.getProperty("spring.datasource.password"));
        ds.setMinimumIdle(Integer.parseInt(env.getProperty("spring.datasource.hikari.minimum-idle")));
        ds.setMaximumPoolSize(Integer.parseInt(env.getProperty("spring.datasource.hikari.maximum-pool-size")));
        ds.setIdleTimeout(Long.parseLong(env.getProperty("spring.datasource.hikari.idle-timeout")));
        ds.setMaxLifetime(Long.parseLong(env.getProperty("spring.datasource.hikari.max-lifetime")));
        ds.setConnectionTimeout(Long.parseLong(env.getProperty("spring.datasource.hikari.connection-timeout")));
        ds.setPoolName(env.getProperty("spring.datasource.hikari.pool-name"));
        return ds;
    }

    @Primary
    @Bean(name = "dbOnTransactionManager")
    public PlatformTransactionManager dbOnTransactionManager(@Qualifier("dbOnEntityManager") EntityManagerFactory smbEntityManagerFactory) {
        return new JpaTransactionManager(smbEntityManagerFactory);
    }

}
