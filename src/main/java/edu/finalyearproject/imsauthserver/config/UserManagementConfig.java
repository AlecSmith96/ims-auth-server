/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
package edu.finalyearproject.imsauthserver.config;

import edu.finalyearproject.imsauthserver.services.JPAUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * Configuration class for creating a UserDeatilsService for communicating with a User database, specifying the encoder
 * for User passwords and specifying to serve a Login form when /oauth/authorize endpoint is accessed from an allowed
 * client.
 */
@Configuration
public class UserManagementConfig extends WebSecurityConfigurerAdapter {

    @Value("${client.app.origin}")
    private String clientOrigin;

    /**
     * Adds custom UserDetailsService to the Spring context for the application.
     * @return UserDetailsService - Custome service for retrieving Users from the user database,
     */
    @Bean
    public UserDetailsService userDetailsService()
    {
        return new JPAUserDetailsService();
    }

    /**
     * Sets the PasswordEncoder in the spring context for the application
     * @return PasswordEncoder - Class for encoding/decoding passwords based on type of encoding.
     */
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
//        return NoOpPasswordEncoder.getInstance();         //used for testing, provides no encryption
    }

    /**
     * Config method to set the AuthenticationManager for the server.
     * @return AuthenticationManager - the authentication manager for the server.
     * @throws Exception
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception
    {
        return super.authenticationManagerBean();
    }

    /**
     * Config method for configuring WebSecurity options.
     * @param web - the WebSecurity object,
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/resources/**"); // ignore requests that start with '/resources/"
    }

    /**
     * Configuration method for configuring security settings for all HTTP requests.
     * @param http - the HttpSecurity object.
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http.csrf().disable();  // lesson 9 explanation, just to focus on CORS

        http.formLogin()                            // use Spring Security login form to authenticate
                .and()
                .logout()                           // allow logout of user
                .logoutSuccessUrl(clientOrigin)     // redirect to client app after logout
                .and()
                .authorizeRequests()
                .antMatchers("/login*", "/users/**")
                .permitAll()                        // allow unauthenticated access to '/login'
                .anyRequest().authenticated();      // force authentication for all other requests
    }

    /**
     * Config method for setting up the CORS filter for the server.
     * @return FilterRegistrationBean<CorsFilter> - the CORS filter.
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> simpleCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}

