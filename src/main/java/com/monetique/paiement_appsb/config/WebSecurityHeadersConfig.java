package com.monetique.paiement_appsb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class WebSecurityHeadersConfig implements WebMvcConfigurer {

    @Bean
    public SecurityFilterChain securityHeadersFilterChain(HttpSecurity http) throws Exception {
        http
            .headers(headers -> headers
                .xssProtection(xss -> xss
                    .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
                )
                // Désactivation de CSP pour le développement
                .contentSecurityPolicy(csp -> {}
                    // Pour la production, il faudrait réactiver avec une politique appropriée
                    /*.policyDirectives(
                        "default-src 'self' 'unsafe-inline' 'unsafe-eval' data: https:; " +
                        "script-src 'self' 'unsafe-inline' 'unsafe-eval' https: http:; " +
                        "style-src 'self' 'unsafe-inline' https: http:; " +
                        "img-src 'self' data: https: http:; " +
                        "font-src 'self' data: https: http:;"
                    )*/
                )
                .frameOptions(frame -> frame
                    .deny()
                )
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000) // 1 year
                )
                .contentTypeOptions(contentType -> {})
                .referrerPolicy(referrer -> referrer
                    .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                )
            );

        return http.build();
    }
}
