package com.eltropy.banking.service;

import com.eltropy.banking.main.JwtRequestFilter;
import com.eltropy.banking.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    public static final String CLASS_NAME = JwtUserDetailsService.class.getName();
    private static final Logger logger = LoggerFactory.getLogger(JwtUserDetailsService.class);

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        com.eltropy.banking.entity.User localuser = userRepository.findByUsername(username);
        if (localuser != null) {
            return new User(localuser.getUsername(), localuser.getPassword(), List.of((GrantedAuthority) localuser::getType));
        } else {
            logger.error("User not found with username: " + username, CLASS_NAME);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}