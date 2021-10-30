package com.eltropy.banking.controller;

import com.eltropy.banking.entity.JwtResponse;
import com.eltropy.banking.entity.User;
import com.eltropy.banking.service.JwtUserDetailsService;
import com.eltropy.banking.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @PostMapping("/authenticate")
    public ResponseEntity<Object> createAuthenticationToken(@RequestBody User authenticationRequest) throws Exception {

//        Verify passed credentials with ones in the DB
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

//        Get user and his accesses
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

//        Generate token with the user details
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException e) {
//            Do NOT print password
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}