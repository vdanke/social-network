package org.step.social.network.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.step.social.network.service.TokenProvider;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider<UUID, Authentication> tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public TokenAuthenticationFilter(TokenProvider<UUID, Authentication> tokenProvider,
                                     UserDetailsServiceImpl userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        String bearerFreeToken = getBearerFreeToken(authorization);

        if (!StringUtils.isEmpty(bearerFreeToken) && tokenProvider.validateToken(bearerFreeToken)) {
            UUID id = tokenProvider.getSubject(bearerFreeToken);

            UserDetails userDetails = userDetailsService.loadUserById(id.toString());

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, "", userDetails.getAuthorities()
            );
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
    }

    private String getBearerFreeToken(String token) {
        if (token == null) {
            return "";
        }
        return token.substring(7, token.length());
    }
}
