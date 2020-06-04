package org.step.social.network.service;

public interface TokenProvider<T, U> {

    String createToken(U u);

    T getSubject(String token);

    boolean validateToken(String token);
}
