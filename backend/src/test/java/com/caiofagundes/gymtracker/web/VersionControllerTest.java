package com.caiofagundes.gymtracker.web;

import com.caiofagundes.gymtracker.config.SecurityConfig;
import com.caiofagundes.gymtracker.security.JwtAuthFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = VersionController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthFilter.class}))
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "app.version.sha=test-git-sha-0123456789abcdef",
        "app.version.built-at=2026-08-25T00:00:00Z"
})
class VersionControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void versionReportsBuildSha() throws Exception {
        this.mvc.perform(get("/version"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.backend.sha").value("test-git-sha-0123456789abcdef"))
                .andExpect(jsonPath("$.backend.shortSha").value("test-gi"))
                .andExpect(jsonPath("$.backend.builtAt").value("2026-08-25T00:00:00Z"));
    }
}
