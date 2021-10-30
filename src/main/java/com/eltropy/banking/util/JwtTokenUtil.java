package com.eltropy.banking.util;

import com.eltropy.banking.service.RoleAccessService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60l;
    public static final String ROLE = "role";

    @Value("${jwt.secret.key}")
    private String secret;

    @Autowired
    RoleAccessService roleAccessService;

//    Read user details
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

//    Fetch token expiry
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

//    Get all info from token. Will be returned as map
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

//    Check token expiry
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

//    Generate token
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        Optional<? extends GrantedAuthority> authority = userDetails.getAuthorities().stream().findFirst();
        authority.ifPresent(grantedAuthority -> claims.put(ROLE, grantedAuthority.getAuthority()));
        return doGenerateToken(claims, userDetails.getUsername());
    }

//    Create token.
//    Add details like issuer, expiry, user, id
//    Sign with chosen algorithm and a secret key
//    Compact method needs to be called
    private String doGenerateToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    //validate token
    public Boolean validateToken(String token, UserDetails userDetails, HttpServletRequest request) {
        final String username = getUsernameFromToken(token);

//        Claims is a Map of strings. In this case has the roles
        Claims claims = getAllClaimsFromToken(token);

//        Use the role in the Claim to get all access the user has
        List<String> accessesForRole = roleAccessService.getRolesToAccessMap().get(claims.get(ROLE, String.class));
        String accessRequested = request.getServletPath().split("/")[1];

//        If the user does NOT have access based on token, then refuse access
        if (! accessesForRole.contains(accessRequested)) { return Boolean.FALSE; }

//        All looks good. Allow access
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}