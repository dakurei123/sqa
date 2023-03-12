package com.example.sqa.service;

import com.example.sqa.entity.Account;
import com.example.sqa.exception.ApiInputException;
import com.example.sqa.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws ApiInputException {
        Account account = accountRepository.findByUsername(username);
        if (account == null)
            throw new ApiInputException("LOGIN_FAIL");
        else if (account.getStatus() != 1)
            throw new ApiInputException("ACCOUNT_NOT_ACTIVE", account.getStatus());
        else {
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(account.getRole()));
            return new org.springframework.security.core.userdetails.User(account.getUsername(), account.getPassword(),
                    authorities);
        }
    }
}
