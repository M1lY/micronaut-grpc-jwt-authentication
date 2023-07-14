package com.example.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.model.Role;
import com.example.model.User;
import io.micronaut.context.annotation.Property;
import jakarta.inject.Singleton;
import java.time.Instant;
import java.util.List;

@Singleton
public class JwtProvider {

  private static final String ROLE_CLAIM = "roles";
  private final Algorithm algorithm;
  private final JWTVerifier verifier;

  public JwtProvider(@Property(name = "jwt.secret") String secret) {
    algorithm = Algorithm.HMAC256(secret);
    verifier = JWT.require(algorithm)
        .build();
  }

  public String generateJwt(User user) {
    return JWT.create()
        .withIssuedAt(Instant.now())
        .withSubject(user.email())
        .withClaim(ROLE_CLAIM, user.getRoleString())
        .sign(algorithm);
  }

  public String getEmailFromJwt(String jwt) throws JWTVerificationException {
    return verifier.verify(jwt).getSubject();
  }

  public List<Role> getRolesFromJwt(String jwt) throws JWTVerificationException {
    return verifier.verify(jwt).getClaim(ROLE_CLAIM).asList(String.class).stream()
        .map(Role::valueOf).toList();
  }
}
