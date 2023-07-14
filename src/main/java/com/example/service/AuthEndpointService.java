package com.example.service;

import com.example.LoginReply;
import com.example.LoginRequest;
import com.example.ReactorAuthServiceGrpc;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.security.JwtProvider;
import io.grpc.Status;
import io.micronaut.grpc.annotation.GrpcService;
import java.util.Optional;
import reactor.core.publisher.Mono;

@GrpcService
public class AuthEndpointService extends ReactorAuthServiceGrpc.AuthServiceImplBase {

  private final JwtProvider jwtProvider;
  private final UserRepository userRepository;

  public AuthEndpointService(JwtProvider jwtProvider, UserRepository userRepository) {
    this.jwtProvider = jwtProvider;
    this.userRepository = userRepository;
  }

  @Override
  public Mono<LoginReply> login(Mono<LoginRequest> request) {
    return request
        .flatMap(req -> {
          Optional<User> userOptional = userRepository.getUserByEmail(req.getLogin());
          if (userOptional.isPresent() && validateCredentials(req, userOptional.get())) {
            return Mono.just(
                LoginReply.newBuilder().setJwt(jwtProvider.generateJwt(userOptional.get()))
                    .build());
          } else {
            return Mono.error(Status.UNAUTHENTICATED.withDescription("Invalid email or password").asRuntimeException());
          }
        });
  }

  private boolean validateCredentials(LoginRequest request, User user) {
    return user.email().equals(request.getLogin()) && user.password().equals(request.getPassword());
  }
}
