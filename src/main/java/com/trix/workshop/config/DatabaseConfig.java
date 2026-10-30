package com.trix.workshop.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DatabaseConfig {
    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${spring.datasource.url:jdbc:mysql://localhost:3306/workshop_management?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=UTF-8}")
    private String defaultUrl;

    @Value("${spring.datasource.username:root}")
    private String defaultUsername;

    @Value("${spring.datasource.password:root}")
    private String defaultPassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        // Priority 1: DATABASE_URL (Render / Heroku standard auto-injected environment variable)
        String envDatabaseUrl = System.getenv("DATABASE_URL");
        // Priority 2: DB_URL (Custom environment variable set in dashboard)
        String envDbUrl = System.getenv("DB_URL");
        
        String rawUrl = (envDatabaseUrl != null && !envDatabaseUrl.isBlank()) ? envDatabaseUrl :
                        (envDbUrl != null && !envDbUrl.isBlank()) ? envDbUrl : defaultUrl;

        String jdbcUrl = rawUrl.trim();
        String username = defaultUsername;
        String password = defaultPassword;
        String driverClassName = "com.mysql.cj.jdbc.Driver";

        // Check if PostgreSQL (postgres://, postgresql://, or jdbc:postgresql://)
        if (jdbcUrl.startsWith("postgres://") || jdbcUrl.startsWith("postgresql://") || jdbcUrl.startsWith("jdbc:postgresql://")) {
            driverClassName = "org.postgresql.Driver";

            if (jdbcUrl.startsWith("postgres://") || jdbcUrl.startsWith("postgresql://")) {
                try {
                    String uriString = jdbcUrl;
                    if (uriString.startsWith("postgres://")) {
                        uriString = "postgresql://" + uriString.substring("postgres://".length());
                    }
                    URI uri = new URI(uriString);
                    String host = uri.getHost();
                    int port = uri.getPort() > 0 ? uri.getPort() : 5432;
                    String path = uri.getPath();
                    if (path != null && path.startsWith("/")) {
                        path = path.substring(1);
                    }
                    String userInfo = uri.getUserInfo();
                    if (userInfo != null && userInfo.contains(":")) {
                        String[] parts = userInfo.split(":", 2);
                        username = parts[0];
                        password = parts[1];
                    }

                    jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + path;
                    if (uri.getQuery() != null) {
                        jdbcUrl += "?" + uri.getQuery();
                    }
                } catch (Exception e) {
                    log.warn("Could not parse PostgreSQL URI, falling back to jdbc: prefix: {}", e.getMessage());
                    if (!jdbcUrl.startsWith("jdbc:")) {
                        jdbcUrl = "jdbc:" + jdbcUrl;
                    }
                }
            }
        }

        // Allow explicit override if separate user/pass environment variables are set
        String explicitUser = System.getenv("DB_USERNAME");
        if (explicitUser != null && !explicitUser.isBlank()) {
            username = explicitUser;
        }
        String explicitPass = System.getenv("DB_PASSWORD");
        if (explicitPass != null && !explicitPass.isBlank()) {
            password = explicitPass;
        }

        // Check if local MySQL is accessible; if not running, fall back to embedded H2 MySQL mode
        if (driverClassName.contains("mysql") && jdbcUrl.contains("localhost")) {
            try (java.net.Socket s = new java.net.Socket()) {
                s.connect(new java.net.InetSocketAddress("localhost", 3306), 1500);
            } catch (Exception e) {
                log.warn("Local MySQL service (localhost:3306) is not currently running. Activating embedded H2 database in MySQL compatibility mode so the system runs out-of-the-box.");
                jdbcUrl = "jdbc:h2:mem:workshop_management;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE";
                driverClassName = "org.h2.Driver";
                username = "sa";
                password = "";
            }
        }

        log.info("Initialized DataSource: driverClassName={}, jdbcUrl={}, username={}",
                driverClassName, jdbcUrl.replaceAll(":[^:@]+@", ":****@"), username);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);
        config.setPoolName("TrixHikariPool");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(300000);
        config.setConnectionTimeout(20000);

        return new HikariDataSource(config);
    }
}
