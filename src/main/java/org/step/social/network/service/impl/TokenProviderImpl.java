package org.step.social.network.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.step.social.network.configuration.AppProperties;
import org.step.social.network.configuration.security.UserDetailsImpl;
import org.step.social.network.service.TokenProvider;

import java.util.Date;
import java.util.UUID;

@Service
public class TokenProviderImpl implements TokenProvider<UUID, Authentication> {

    private final AppProperties appProperties;

    @Autowired
    public TokenProviderImpl(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public String createToken(Authentication authentication) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();

        Date now = new Date();
        Date expireDate = new Date(now.getTime() + appProperties.getAuth().getExpiration());

        return Jwts.builder()
                .setSubject(principal.getUser().getId().toString())
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, appProperties.getAuth().getToken())
                .compact();
    }

    @Override
    public UUID getSubject(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(appProperties.getAuth().getToken())
                .parseClaimsJws(token)
                .getBody();

        return UUID.fromString(claims.getSubject());
    }

    @Override
    public boolean validateToken(String token) {
        Jwts.parser()
                .setSigningKey(appProperties.getAuth().getToken())
                .parseClaimsJws(token);

        return true;
    }
}
