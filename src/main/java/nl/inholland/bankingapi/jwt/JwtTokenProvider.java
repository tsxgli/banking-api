package nl.inholland.bankingapi.jwt;

import io.jsonwebtoken.*;
import nl.inholland.bankingapi.model.UserType;
import nl.inholland.bankingapi.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    @Value("${application.token.validity}")
    private long validityInMicroseconds;
    private  final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JwtKeyProvider jwtKeyProvider;

    public JwtTokenProvider(UserDetailsServiceImpl userDetailsServiceImpl, JwtKeyProvider jwtKeyProvider) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.jwtKeyProvider = jwtKeyProvider;
    }

    public String createToken(String username, UserType roles) {
        Claims claims = Jwts.claims().setSubject(username);

        // And we add an array of the roles to the auth element of the Claims
        // Note that we only provide the role as information to the frontend
        // The actual role based authorization should always be done in the backend code
        claims.put("auth", roles.name());

        // We decide on an expiration date
        Date now = new Date();
        Date expiration = new Date(now.getTime() + validityInMicroseconds);

        // And finally, generate the token and sign it. .compact() then turns it into a string that we can return.
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(jwtKeyProvider.getPrivateKey())
                .compact();
    }

    public Authentication getAuthentication(String token) {
        // We will get the email from the token
        // And then get the UserDetails for this user from our service
        // We can then pass the UserDetails back to the caller
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(jwtKeyProvider.getPrivateKey()).build().parseClaimsJws(token);
            String email = claims.getBody().getSubject();
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(email);
            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Bearer token not valid");
        }
    }
    public void validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtKeyProvider.getPrivateKey()).build().parseClaimsJws(token);
        } catch (JwtException | IllegalArgumentException e) {
            throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, "Expired or invalid JWT token");
        }
    }
}