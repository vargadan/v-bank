package com.dani.vbank.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Component
@Slf4j
public class VBankAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private DataSource dataSource;

    private UserDetails loadUserByUsername(String uname) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            String sql = "SELECT USERNAME, PASSWORD " +
                    "FROM USER U WHERE U.USERNAME = '" + uname + "'";
            log.warn(sql);
            ResultSet resultSet = stmt.executeQuery(sql);
            if (resultSet.next()) {
                final String username = resultSet.getString("USERNAME");
                final String password = resultSet.getString("PASSWORD");
                return User.withUsername(username).password(password).roles("USER").build();
            } else {
                log.info("No user found with name : " + uname);
                return null;
            }
        }
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails userDetails = null;
        try {
            userDetails = this.loadUserByUsername(authentication.getPrincipal().toString());
        } catch (Exception e) {
            MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
            throw new AuthenticationServiceException(messages.getMessage(
                    "VBankAuthenticationProvider.authenticationError",
                    "Authentication Error" ));
        }
        if (authentication != null && authentication.getCredentials() != null && userDetails != null &&
                authentication.getCredentials().equals(userDetails.getPassword())) {
            return createSuccessAuthentication(authentication, userDetails);
        } else {
            MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
            throw new BadCredentialsException(messages.getMessage(
                    "VBankAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    protected Authentication createSuccessAuthentication(Authentication authentication, UserDetails user) {
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
                user.getUsername(), null,
                user.getAuthorities());
        result.setDetails(authentication.getDetails());
        return result;
    }

}
