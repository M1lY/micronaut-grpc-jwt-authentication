package com.example.security;

import com.example.model.Role;
import io.grpc.Context;
import io.grpc.Metadata;
import java.util.List;

public class GrpcConstants {

  public static final Metadata.Key<String> JWT_METADATA = Metadata.Key.of("jwt", Metadata.ASCII_STRING_MARSHALLER);

  public static final Context.Key<String> EMAIL_CONTEXT = Context.key("email");
  public static final Context.Key<List<Role>> ROLES_CONTEXT = Context.key("roles");

}
