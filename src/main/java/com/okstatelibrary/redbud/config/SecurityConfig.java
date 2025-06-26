package com.okstatelibrary.redbud.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.okstatelibrary.redbud.service.impl.UserSecurityService;

import java.security.SecureRandom;

/**
 * {@code SecurityConfig} is a Spring Security configuration class that defines access control
 * rules, password encoding strategy, and authentication settings for the application.
 *
 * <p>It enables web security and method-level security using Spring Security annotations.</p>
 *
 * <p>The class extends {@link WebSecurityConfigurerAdapter}, which provides hooks to customize
 * security settings such as HTTP authorization rules and global authentication manager.</p>
 * 
 * <p><strong>Note:</strong> As of Spring Security 5.7, {@code WebSecurityConfigurerAdapter} is deprecated
 * in favor of bean-based configuration. Migration to a component-based approach is recommended
 * for future projects.</p>
 * 
 * @author Damith Perera
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Salt used for secure password encoding. In production, store this securely and don't hard code.
     */
    private static final String SALT = "salt"; // WARNING: avoid hard coding salt in real-world apps

    /**
     * List of publicly accessible URL patterns.
     */
    private static final String[] PUBLIC_MATCHERS = {
            "/webjars/**",
            "/css/**",
            "/js/**",
            "/images/**",
            "/",
            "/index",
            "/reports/*",
            "/stat",
            "/error/**/*",
            "/console/**"
    };

    /**
     * Custom implementation of Spring Security's {@link org.springframework.security.core.userdetails.UserDetailsService}
     * to load user-specific data for authentication.
     */
    @Autowired
    private UserSecurityService userSecurityService;

    /**
     * Bean definition for the {@link BCryptPasswordEncoder} with custom strength and salt.
     * 
     * @return a BCryptPasswordEncoder instance for encoding passwords
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12, new SecureRandom(SALT.getBytes()));
    }

    /**
     * Configures HTTP security including route-level access, login/logout rules, CSRF, and form login settings.
     * 
     * @param http the {@link HttpSecurity} to configure
     * @throws Exception in case of any configuration error
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers(PUBLIC_MATCHERS).permitAll()  // Public URLs
                    .anyRequest().authenticated()              // All other requests require authentication
                .and()
                .csrf().disable()
                .cors().disable()
                .formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/index")
                    .failureUrl("/login?error")
                    .permitAll()
                .and()
                .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/")
                    .deleteCookies("remember-me")
                    .permitAll()
                .and()
                .rememberMe(); // Enables persistent login
    }

    /**
     * Configures the global authentication manager with the custom user details service and password encoder.
     * 
     * @param auth the {@link AuthenticationManagerBuilder} to configure
     * @throws Exception if an error occurs while setting up authentication
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userSecurityService).passwordEncoder(passwordEncoder());
    }
}
