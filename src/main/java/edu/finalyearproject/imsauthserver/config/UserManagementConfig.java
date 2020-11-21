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

    @Value("${resource.server.origin}")
    private String resourceServerOrigin;

    private static final String GET = "GET";
    private static final String POST = "POST";

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

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception
    {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/resources/**"); // ignore requests that start with '/resources/"
    }

    /**
     * Configures the OAuth login page.
     * Can be overriden for custom login page:
     * https://docs.spring.io/spring-security/site/docs/5.0.7.RELEASE/reference/html/oauth2login-advanced.html#:~:text=By%20default%2C%20the%20OAuth%202.0,(or%20OAuth%202.0%20Login).
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http.csrf().disable();  // lesson 9 explanation, just to focus on CORS
//        configureCors(http);

        // use Spring Security login form to authenticate
        http.formLogin()/*.loginProcessingUrl("/login")*/;
//                        .defaultSuccessUrl("http://localhost:3000/oauth_callback",true);

        // forces any request to be authenticated
        http.authorizeRequests().antMatchers("/login*").permitAll()
                .anyRequest().authenticated();
    }

//    private void configureCors(HttpSecurity http) throws Exception
//    {
//        http.cors(customizer -> {   // configures allowed origins and allowed request types
//            CorsConfigurationSource cs = request -> {
//                CorsConfiguration configuration = new CorsConfiguration();
//                configuration.setAllowedOrigins(List.of("*"));
//                configuration.setAllowedMethods(List.of("*"));
//                configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
//                configuration.setAllowCredentials(true);
//                return configuration;
//            };
//            customizer.configurationSource(cs);
//        });
//    }

    @Bean
    public FilterRegistrationBean<CorsFilter> simpleCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("*"));         // This needs to only allow react app and resource server
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}

