package com.sleepy.manager.web.core.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gehoubao
 * @create 2021-11-07 19:22
 **/
@Configuration
public class FlywayConfig {

    /**
     * ddl文件目录
     */
    private static final String DDL_DIRECTORY = "db/migration/ddl";
    /**
     * dml文件目录前缀
     */
    private static final String DML_PREFIX_DIRECTORY = "db/migration/dml/";
    /**
     * 公共dml文件目录
     */
    private static final String DML_PUBLIC_DIRECTORY = DML_PREFIX_DIRECTORY + "public/";
    @Value("${spring.profiles.active}")
    private String env;
    @Autowired
    @Qualifier("flywayDataSource")
    private DataSource dataSource;

    @Bean
    DataSource flywayDataSource(
            @Value("${spring.datasource.driverClassName}") String driverClassName,
            @Value("${flyway.datasource.initializationFailTimeout}") long initializationFailTimeout,
            @Value("${flyway.datasource.idleTimeout}") long idleTimeout,
            @Value("${flyway.datasource.autoCommit}") boolean autoCommit,
            @Value("${flyway.datasource.connectionTimeout}") long connectionTimeout,
            @Value("${spring.datasource.druid.master.password}") String password,
            @Value("${spring.datasource.druid.master.username}") String username,
            @Value("${flyway.datasource.maxLifeTime}") long maxLifeTime,
            @Value("${flyway.datasource.minimumIdle}") int minimumIdle,
            @Value("${flyway.datasource.maximumPoolSize}") int maximumPoolSize,
            @Value("${spring.datasource.druid.master.url}") String jdbcUrl,
            @Value("${flyway.datasource.allowPoolSuspension}") boolean allowPoolSuspension,
            @Value("${flyway.datasource.readOnly}") boolean readOnly,
            @Value("${flyway.datasource.registerMbeans}") boolean registerMbeans,
            @Value("${flyway.datasource.isolationLevel}") String isolationLevel) throws IOException {


        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setConnectionTimeout(connectionTimeout);
        config.setIdleTimeout(idleTimeout);
        config.setAutoCommit(autoCommit);
        config.setMinimumIdle(minimumIdle);
        config.setMaximumPoolSize(maximumPoolSize);
        config.setMaxLifetime(maxLifeTime);
        config.setAllowPoolSuspension(allowPoolSuspension);
        config.setReadOnly(readOnly);
        config.setRegisterMbeans(registerMbeans);
        config.setInitializationFailTimeout(initializationFailTimeout);
        config.setDriverClassName(driverClassName);

        if (StringUtils.hasText(isolationLevel)) {
            config.setTransactionIsolation(isolationLevel);
        }

        return new HikariDataSource(config);
    }

    @PostConstruct
    Flyway migrate() {
        List<String> locations = new ArrayList<>();
        locations.add(DDL_DIRECTORY);
        locations.add(DML_PREFIX_DIRECTORY);
        locations.add(DML_PUBLIC_DIRECTORY);
        locations.add(DML_PREFIX_DIRECTORY + env + "/");
        String[] locationsArray = new String[locations.size()];
        locations.toArray(locationsArray);

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(locationsArray)
                .baselineOnMigrate(true)
                .cleanOnValidationError(false)
                .encoding("UTF-8")
                .load();
        flyway.migrate();
        return flyway;
    }


}
