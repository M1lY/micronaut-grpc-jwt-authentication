package com.example.service;

import com.example.ManagerPing;
import com.example.ReactorManagerServiceGrpc;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

@Singleton
public class ManagerEndpointService extends ReactorManagerServiceGrpc.ManagerServiceImplBase {

  @Override
  public Mono<ManagerPing> manager(Mono<ManagerPing> request) {
    return request;
  }
}
