/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.boot.test.context.SpringBootTest
 *  org.springframework.test.context.ActiveProfiles
 *  org.springframework.test.context.DynamicPropertyRegistry
 *  org.springframework.test.context.DynamicPropertySource
 *  org.testcontainers.containers.MySQLContainer
 */
package com.caiofagundes.gymtracker;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

@SpringBootTest
@ActiveProfiles(value={"test"})
public abstract class AbstractIntegrationTest {
    static final MySQLContainer<?> MYSQL = (MySQLContainer)new MySQLContainer("mysql:8.4").withDatabaseName("gym_tracker").withReuse(true);

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> MYSQL.getJdbcUrl());
        registry.add("spring.datasource.username", () -> MYSQL.getUsername());
        registry.add("spring.datasource.password", () -> MYSQL.getPassword());
    }

    static {
        MYSQL.start();
    }
}

