package com.chairpick.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfig {

    private final BeforeRequestFilter filter;

    public SecurityFilterChainConfig(BeforeRequestFilter filter) {
        this.filter = filter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/chairs/**, /login", "/customers/new")
                                .permitAll()
                                .requestMatchers(HttpMethod.POST, "/customers")
                                .permitAll()
                                .requestMatchers("/customers/**")
                                .authenticated()
                                .requestMatchers(HttpMethod.PATCH,"/admin/chairs/*/price-change-requests/*")
                                .hasAuthority("SALES_MANAGER")
                                .requestMatchers("/admin/chairs/*/edit")
                                .hasAuthority("ADMIN")
                                .requestMatchers("/admin/chairs/new")
                                .hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/admin/chairs/**")
                                .hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/admin/chairs")
                                .hasAuthority("ADMIN")
                                .requestMatchers("/admin/chairs/new")
                                .hasAuthority("ADMIN")
                                .requestMatchers("/admin/chairs/**")
                                .hasAnyAuthority("ADMIN", "SALES_MANAGER")
                                .requestMatchers("/admin/**")
                                .hasAnyAuthority("ADMIN")
                                .anyRequest().permitAll()
                )
                .exceptionHandling(handler ->
                        handler.authenticationEntryPoint((request, response, authException) -> {
                            response.sendRedirect("/login");
                        })
                                .accessDeniedHandler(((request, response, accessDeniedException) ->
                                        response.sendRedirect("/404"))))
                .logout(logout -> logout.deleteCookies("token")
                        .logoutSuccessUrl("/"))
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
