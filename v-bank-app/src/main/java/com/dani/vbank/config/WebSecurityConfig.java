package com.dani.vbank.config;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.RequestRejectedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Configuration
@EnableWebSecurity
@Log
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/js/**", "/css/**").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll();
//        http.csrf().disable();
        http.cors().disable();
        http.headers().xssProtection().xssProtectionEnabled(false);
    }

    @Bean
    @Override
    @SneakyThrows
    public UserDetailsService userDetailsService() {

        return new UserDetailsService() {

            // for logging in as bob with SQLi use
            // username : ' UNION ALL SELECT 'bob' AS USERNAME,'x' AS PASSWORD #
            // password : password

            @Override
            @SneakyThrows
            public UserDetails loadUserByUsername(String uname) throws UsernameNotFoundException {
                try (Connection connection = dataSource.getConnection()) {
                    Statement stmt = connection.createStatement();
                    String sql = "SELECT USERNAME, PASSWORD " +
                            "FROM USER U WHERE U.USERNAME = '" + uname + "'";
                    log.info(sql);
                    ResultSet resultSet = stmt.executeQuery(sql);
                    if (resultSet.next()) {
                        final String username = resultSet.getString("USERNAME");
                        final String password = resultSet.getString("PASSWORD");
                        return User.withUsername(username).password("{noop}" + password).roles("USER").build();
                    } else {
                        throw new UsernameNotFoundException("No user found with name : " + uname);
                    }
                }
            }
        };
    }

    @Override
    public void init(WebSecurity web) throws Exception {
        super.init(web);
        web.httpFirewall(new HttpFirewall() {
            @Override
            public FirewalledRequest getFirewalledRequest(HttpServletRequest request) throws RequestRejectedException {
                return new FirewalledRequest(request) {
                    @Override
                    public void reset() {
                    }
                };
            }

            @Override
            public HttpServletResponse getFirewalledResponse(HttpServletResponse response) {
                return response;
            }
        });
    }

}