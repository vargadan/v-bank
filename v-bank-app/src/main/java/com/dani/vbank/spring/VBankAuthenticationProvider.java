package com.dani.vbank.spring;

import com.dani.vbank.auth.JDBCUserDetailsService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Log
public class VBankAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private JDBCUserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getPrincipal().toString());
        if (authentication.getCredentials().equals(userDetails.getPassword())) {
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
                authentication.getPrincipal(), authentication.getCredentials(),
                user.getAuthorities());
        result.setDetails(authentication.getDetails());
        return result;
    }

}
