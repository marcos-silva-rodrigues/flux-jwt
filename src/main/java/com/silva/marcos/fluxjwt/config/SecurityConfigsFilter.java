package com.silva.marcos.fluxjwt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfigsFilter {

    @Autowired
    private AuthManager authManager;

    @Autowired
    private SecurityContext securityContext;

    public SecurityConfigsFilter(AuthManager authManager, SecurityContext securityContext) {
        this.authManager = authManager;
        this.securityContext = securityContext;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .cors().disable()
                .csrf().disable()
                .authenticationManager(this.authManager)
                .securityContextRepository(this.securityContext)
                .authorizeExchange()
                .pathMatchers(new String[]{"/sign-up/**"}).permitAll()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .anyExchange().authenticated()
                .and().build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
