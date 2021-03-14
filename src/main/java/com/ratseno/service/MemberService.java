package com.ratseno.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface MemberService {

    //동기 호출 - Blocking
    int getMemberId(String name);

    CompletableFuture<Void> getMemberIdAsync_runAsync(String name);

    CompletableFuture<Integer> getMemberIdAsync_supplyAsync(String name);

    CompletableFuture<Integer> getMemberAgeAsync_supplyAsync(String name);

    CompletableFuture<Integer> getMemberIdAsync(String name);
}
