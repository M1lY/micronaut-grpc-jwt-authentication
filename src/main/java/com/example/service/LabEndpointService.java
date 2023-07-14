package com.example.service;

import com.example.LabPing;
import com.example.ReactorLabServiceGrpc;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

@Singleton
public class LabEndpointService extends ReactorLabServiceGrpc.LabServiceImplBase {

  @Override
  public Mono<LabPing> lab(Mono<LabPing> request) {
    return request;
  }
}
