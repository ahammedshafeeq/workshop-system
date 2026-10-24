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

            boolean tablesExist = false;
            try {
                jdbcTemplate.queryForObject("SELECT count(*) FROM users", Integer.class);
                tablesExist = true;
            } catch (Exception e) {
                tablesExist = false;
            }

            if (!tablesExist) {
                log.info("DatabaseAutoMigrator: No tables found. Creating schema automatically for [{}]...", dbProduct);
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                populator.setContinueOnError(true);

                if (dbProduct.contains("postgres")) {
                    populator.addScript(new ClassPathResource("schema-postgres.sql"));
                } else {
                    populator.addScript(new ClassPathResource("schema.sql"));
                }

                populator.execute(dataSource);
                log.info("DatabaseAutoMigrator: Schema created successfully!");
            } else {
                log.info("DatabaseAutoMigrator: Tables already exist. Schema creation skipped.");
            }
        } catch (Exception e) {
            log.error("DatabaseAutoMigrator error: ", e);
        }
    }
}
