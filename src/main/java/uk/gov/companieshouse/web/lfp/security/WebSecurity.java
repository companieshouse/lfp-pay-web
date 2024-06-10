package uk.gov.companieshouse.web.lfp.security;

import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import uk.gov.companieshouse.auth.filter.HijackFilter;
import uk.gov.companieshouse.auth.filter.UserAuthFilter;
import uk.gov.companieshouse.session.handler.SessionHandler;

@SuppressWarnings("java:S1118")  // Constructor is required for Spring Application
@EnableWebSecurity
public class WebSecurity {

    @Order(1)
    public SecurityFilterChain temporaryStartPageSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .antMatcher("/late-filing-penalty")
                .authorizeRequests(authorize -> authorize
                        .anyRequest().authenticated()
                );
        return http.build();
    }



    @Order(2)
    protected SecurityFilterChain accessibilityStatementPageSecurityConfig(HttpSecurity http) throws Exception {
        http
                .antMatcher("/late-filing-penalty/accessibility-statement")
                .authorizeRequests(authorize -> authorize
                        .anyRequest().authenticated()
                );
        return http.build();
    }


    @Order(3)
    protected SecurityFilterChain healthcheckSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .antMatcher("/late-filing-penalty/healthcheck")
                .authorizeRequests(authorize -> authorize
                        .anyRequest().authenticated()
                );
        return http.build();
    }


    @Order(4)
    public SecurityFilterChain lFPWebSecurityFilterConfig (HttpSecurity http) throws Exception {
        http
                .antMatcher("/late-filing-penalty/**")
                .addFilterBefore(new SessionHandler(), BasicAuthenticationFilter.class)
                .addFilterBefore(new HijackFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new UserAuthFilter(), BasicAuthenticationFilter.class)
                .authorizeRequests(authorize -> authorize
                        .anyRequest().authenticated()
                );
        return http.build();
    }

}

