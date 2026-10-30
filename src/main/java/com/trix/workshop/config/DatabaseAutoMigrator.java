package com.trix.workshop.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
@Order(1)
public class DatabaseAutoMigrator implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DatabaseAutoMigrator.class);

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public DatabaseAutoMigrator(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        try (Connection con = dataSource.getConnection()) {
            String dbProduct = con.getMetaData().getDatabaseProductName().toLowerCase();
            log.info("DatabaseAutoMigrator: Connected to database [{}]", dbProduct);

            boolean allTablesExist = false;
            try {
                jdbcTemplate.queryForObject("SELECT count(*) FROM users", Integer.class);
                jdbcTemplate.queryForObject("SELECT count(*) FROM workshops", Integer.class);
                jdbcTemplate.queryForObject("SELECT count(*) FROM registrations", Integer.class);
                jdbcTemplate.queryForObject("SELECT count(*) FROM attendance", Integer.class);
                jdbcTemplate.queryForObject("SELECT count(*) FROM certificates", Integer.class);
                allTablesExist = true;
            } catch (Exception e) {
                allTablesExist = false;
            }

            if (!allTablesExist) {
                log.info("DatabaseAutoMigrator: Missing tables detected. Executing schema script for [{}]...", dbProduct);
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                populator.setContinueOnError(true);

                if (dbProduct.contains("postgres")) {
                    populator.addScript(new ClassPathResource("schema-postgres.sql"));
                } else {
                    populator.addScript(new ClassPathResource("schema.sql"));
                }

                populator.execute(dataSource);
                log.info("DatabaseAutoMigrator: Schema script executed successfully!");
            } else {
                log.info("DatabaseAutoMigrator: All core tables verified.");
            }

            // Ensure workshops and certificates have new certificate columns for premium templates & verification
            try {
                jdbcTemplate.execute("ALTER TABLE workshops ADD COLUMN IF NOT EXISTS certificate_template VARCHAR(50) DEFAULT 'classic_gold';");
            } catch (Exception e) {
                try { jdbcTemplate.execute("ALTER TABLE workshops ADD COLUMN certificate_template VARCHAR(50) DEFAULT 'classic_gold';"); } catch (Exception ignored) {}
            }

            try {
                jdbcTemplate.execute("ALTER TABLE certificates ADD COLUMN IF NOT EXISTS certificate_code VARCHAR(30);");
            } catch (Exception e) {
                try { jdbcTemplate.execute("ALTER TABLE certificates ADD COLUMN certificate_code VARCHAR(30);"); } catch (Exception ignored) {}
            }

            try {
                jdbcTemplate.execute("ALTER TABLE certificates ADD COLUMN IF NOT EXISTS template_id VARCHAR(30) DEFAULT 'classic_gold';");
            } catch (Exception e) {
                try { jdbcTemplate.execute("ALTER TABLE certificates ADD COLUMN template_id VARCHAR(30) DEFAULT 'classic_gold';"); } catch (Exception ignored) {}
            }

            try {
                jdbcTemplate.execute("ALTER TABLE certificates ADD COLUMN IF NOT EXISTS certificate_type VARCHAR(30) DEFAULT 'Participation';");
            } catch (Exception e) {
                try { jdbcTemplate.execute("ALTER TABLE certificates ADD COLUMN certificate_type VARCHAR(30) DEFAULT 'Participation';"); } catch (Exception ignored) {}
            }

            try {
                jdbcTemplate.execute("ALTER TABLE certificates ADD COLUMN IF NOT EXISTS issued_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP;");
            } catch (Exception e) {
                try { jdbcTemplate.execute("ALTER TABLE certificates ADD COLUMN issued_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP;"); } catch (Exception ignored) {}
            }

            try {
                jdbcTemplate.execute("ALTER TABLE certificates ADD COLUMN IF NOT EXISTS issued_by BIGINT NULL;");
            } catch (Exception e) {
                try { jdbcTemplate.execute("ALTER TABLE certificates ADD COLUMN issued_by BIGINT NULL;"); } catch (Exception ignored) {}
            }

            // For PostgreSQL: Ensure any older schema versions have updated column definitions
            if (dbProduct.contains("postgres")) {
                try {
                    jdbcTemplate.execute("ALTER TABLE certificates ADD COLUMN IF NOT EXISTS eligibility_verified BOOLEAN NOT NULL DEFAULT TRUE;");
                    jdbcTemplate.execute("ALTER TABLE certificates ADD COLUMN IF NOT EXISTS issue_date DATE NOT NULL DEFAULT CURRENT_DATE;");
                    jdbcTemplate.execute("ALTER TABLE audit_logs ADD COLUMN IF NOT EXISTS actor_email VARCHAR(150);");
                    jdbcTemplate.execute("ALTER TABLE audit_logs ADD COLUMN IF NOT EXISTS action VARCHAR(50);");
                    jdbcTemplate.execute("ALTER TABLE audit_logs ADD COLUMN IF NOT EXISTS entity_type VARCHAR(50);");
                    log.info("DatabaseAutoMigrator: PostgreSQL schema column migrations verified.");
                } catch (Exception e) {
                    log.warn("DatabaseAutoMigrator: Schema column migration note: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("DatabaseAutoMigrator error: ", e);
        }
    }
}
