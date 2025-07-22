package com.monetique.paiement_appsb.config;

import com.monetique.paiement_appsb.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.util.Arrays;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class JwtSecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider() {
            @Override
            protected void additionalAuthenticationChecks(UserDetails userDetails, 
                UsernamePasswordAuthenticationToken authentication) {
                // Skip password check for our simplified authentication
                if (authentication.getCredentials() == null || authentication.getCredentials().toString().isEmpty()) {
                    return;
                }
                super.additionalAuthenticationChecks(userDetails, authentication);
            }
        };
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(new BCryptPasswordEncoder() {
            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                // Always return true for empty passwords in our simplified model
                if (rawPassword == null || rawPassword.toString().isEmpty()) {
                    return true;
                }
                return super.matches(rawPassword, encodedPassword);
            }
        });
        return authProvider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Autoriser les origines spécifiques (à adapter en production)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:8080",
            "http://localhost:8081",
            "http://localhost:3000",
            "http://127.0.0.1:3000",
            "http://127.0.0.1:8080"
        ));
        
        // Autoriser les en-têtes nécessaires
        configuration.setAllowedHeaders(Arrays.asList(
            "Origin", "Content-Type", "Accept", "Authorization", 
            "X-Requested-With", "X-XSRF-TOKEN", "X-Auth-Token"
        ));
        
        // Exposer les en-têtes de réponse
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", "X-XSRF-TOKEN", "X-Auth-Token"
        ));
        
        // Autoriser les méthodes HTTP
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"
        ));
        
        // Autoriser les cookies d'authentification
        configuration.setAllowCredentials(true);
        
        // Temps de mise en cache des pré-vérifications CORS (1 heure)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public FilterRegistrationBean<Filter> contentLengthFilter() {
        FilterRegistrationBean<Filter> filter = new FilterRegistrationBean<>();
        filter.setFilter((request, response, chain) -> {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader("Connection", "close");
            httpResponse.setHeader("Content-Length", String.valueOf(httpResponse.getBufferSize()));
            chain.doFilter(request, response);
        });
        filter.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation().migrateSession()
                .invalidSessionUrl("/auth/login?expired")
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .expiredUrl("/auth/login?expired")
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/vendor/**",
                    "/webfonts/**",
                    "/i18n/**",
                    "/favicon.ico",
                    "/error",
                    "/api/auth/**",
                    "/static/**",
                    "/webjars/**"
                ).permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> {})
                .frameOptions(frame -> frame.disable())
                .httpStrictTransportSecurity(hsts -> hsts.disable())
                .contentTypeOptions(contentType -> contentType.disable())
                .xssProtection(xss -> xss.disable())
                .cacheControl(cache -> cache.disable())
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/auth/login?error=true")
                .usernameParameter("email")
                .successHandler((request, response, authentication) -> {
                    // Rediriger en fonction du rôle après une connexion réussie
                    boolean isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    
                    String redirectUrl = request.getParameter("redirect");
                    if (redirectUrl != null && !redirectUrl.isEmpty() && !redirectUrl.contains("error")) {
                        response.sendRedirect(redirectUrl);
                    } else if (isAdmin) {
                        response.sendRedirect("/admin/dashboard");
                    } else {
                        response.sendRedirect("/user/dashboard");
                    }
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .invalidSessionUrl("/auth/login?expired")
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .expiredUrl("/auth/login?expired")
            )
            .headers(headers -> headers.disable())
            .authenticationProvider(authenticationProvider())
            // Ne pas appliquer le filtre JWT aux requêtes de formulaire de connexion
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
