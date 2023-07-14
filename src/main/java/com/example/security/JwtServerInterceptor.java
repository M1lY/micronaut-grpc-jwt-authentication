package com.example.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.Auth;
import com.example.Lab;
import com.example.Manager;
import com.example.Stockman;
import com.example.model.Role;
import com.google.protobuf.Descriptors.FileDescriptor;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import jakarta.inject.Singleton;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Singleton
public class JwtServerInterceptor implements ServerInterceptor {

  private final JwtProvider jwtProvider;
  private final Map<String, List<Role>> accessibleRoles;

  public JwtServerInterceptor(JwtProvider jwtProvider) {
    this.jwtProvider = jwtProvider;
    this.accessibleRoles = new HashMap<>();

    appendAccessibleRoles(Auth.getDescriptor(), "AuthService", "login", null);
    appendAccessibleRoles(Manager.getDescriptor(), "ManagerService", "manager", List.of(Role.ROLE_MANAGER));
    appendAccessibleRoles(Lab.getDescriptor(), "LabService", "lab", List.of(Role.ROLE_LAB));
    appendAccessibleRoles(Stockman.getDescriptor(), "StockmanService", "stockman", List.of(Role.ROLE_STOCKMAN));
  }

  private void appendAccessibleRoles(FileDescriptor fileDescriptor, String serviceRegex,
      String methodRegex, List<Role> accessibleRoles) {
    fileDescriptor.getServices().stream()
        .filter(serviceDescriptor -> serviceDescriptor.getName().matches(serviceRegex)).forEach(
            serviceDescriptor -> serviceDescriptor.getMethods().stream()
                .filter(methodDescriptor -> methodDescriptor.getName().matches(methodRegex)).forEach(
                    methodDescriptor -> this.accessibleRoles.put(
                        serviceDescriptor.getFullName() + "/" + methodDescriptor.getName(),
                        accessibleRoles)));
  }

  @Override
  public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
      ServerCallHandler<ReqT, RespT> next) {
    if (accessibleRoles.get(call.getMethodDescriptor().getFullMethodName()) == null) {
      return Contexts.interceptCall(Context.current(), call, headers, next);
    } else {
      String jwt = headers.get(GrpcConstants.JWT_METADATA);
      if (jwt == null) {
        call.close(Status.UNAUTHENTICATED.withDescription("Missing JWT Token"), headers);
        return new Listener<>() {};
      }
      try {
        String email = jwtProvider.getEmailFromJwt(jwt);
        List<Role> roles = jwtProvider.getRolesFromJwt(jwt);
        if (!new HashSet<>(roles).containsAll(accessibleRoles.get(call.getMethodDescriptor().getFullMethodName()))) {
          call.close(Status.PERMISSION_DENIED, headers);
          return new Listener<>() {};
        }
        Context context = Context.current()
            .withValue(GrpcConstants.EMAIL_CONTEXT, email)
            .withValue(GrpcConstants.ROLES_CONTEXT, roles);
        return Contexts.interceptCall(context, call, headers, next);
      } catch (JWTVerificationException e) {
        call.close(Status.UNAUTHENTICATED.withDescription(e.getMessage()), headers);
        return new Listener<>() {};
      }
    }
  }
}
