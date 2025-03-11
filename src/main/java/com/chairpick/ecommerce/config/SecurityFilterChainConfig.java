package com.chairpick.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       return http
               .csrf(AbstractHttpConfigurer::disable)
               .authorizeHttpRequests( auth ->
                       auth
                               .requestMatchers("/**")
                               .permitAll()
                               .requestMatchers(HttpMethod.POST, "/**")
                               .permitAll()
                               .requestMatchers(HttpMethod.PUT, "/**")
                               .permitAll()
                               .requestMatchers(HttpMethod.PATCH, "/**")
                               .permitAll()
                               .requestMatchers(HttpMethod.DELETE, "/**")
                               .permitAll())
               .formLogin(AbstractHttpConfigurer::disable).build();

    }
}
