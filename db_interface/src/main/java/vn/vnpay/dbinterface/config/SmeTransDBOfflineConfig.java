package vn.vnpay.dbinterface.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "vn.vnpay.dbinterface.reposotorydboffline", entityManagerFactoryRef = "dbOffEntityManager", transactionManagerRef = "dbOffTransactionManager")
public class SmeTransDBOfflineConfig {

    @Autowired
    private Environment env;

    @Bean(name = "dbOffEntityManager")
    public LocalContainerEntityManagerFactoryBean dbOffEntityManager(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(dbOffDataSource()).packages("vn.vnpay.dbinterface.entitydboffline").persistenceUnit("smepuoff").build();
    }

    @Bean
    public DataSource dbOffDataSource() {
        HikariDataSource ds = DataSourceBuilder.create().type(HikariDataSource.class).build();
        ds.setDriverClassName(env.getProperty("spring.offline.datasource.driver-class-name"));
        ds.setJdbcUrl(env.getProperty("spring.offline.datasource.jdbcUrl"));
        ds.setUsername(env.getProperty("spring.offline.datasource.username"));
        ds.setPassword(env.getProperty("spring.offline.datasource.password"));
        ds.setMinimumIdle(Integer.parseInt(env.getProperty("spring.offline.datasource.minimum-idle")));
        ds.setMaximumPoolSize(Integer.parseInt(env.getProperty("spring.offline.datasource.maximum-pool-size")));
        ds.setIdleTimeout(Long.parseLong(env.getProperty("spring.offline.datasource.idle-timeout")));
        ds.setMaxLifetime(Long.parseLong(env.getProperty("spring.offline.datasource.max-lifetime")));
        ds.setConnectionTimeout(Long.parseLong(env.getProperty("spring.offline.datasource.connection-timeout")));
        return ds;
    }

    @Bean(name = "dbOffTransactionManager")
    public PlatformTransactionManager dbOffTransactionManager(@Qualifier("dbOffEntityManager") EntityManagerFactory smbEntityManagerFactory) {
        return new JpaTransactionManager(smbEntityManagerFactory);
    }
}
