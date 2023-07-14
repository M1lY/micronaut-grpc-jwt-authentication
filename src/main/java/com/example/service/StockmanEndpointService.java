package com.example.service;

import com.example.ReactorStockmanServiceGrpc;
import com.example.StockmanPing;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

@Singleton
public class StockmanEndpointService extends ReactorStockmanServiceGrpc.StockmanServiceImplBase {

  @Override
  public Mono<StockmanPing> stockman(Mono<StockmanPing> request) {
    return request;
  }
}
