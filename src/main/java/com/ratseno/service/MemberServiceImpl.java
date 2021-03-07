package com.ratseno.service;

import com.ratseno.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class MemberServiceImpl implements MemberService{

    private final static Logger log = LoggerFactory.getLogger(MemberServiceImpl.class);

    @Autowired
    private MemberRepository memberRepository;

    Executor executor = Executors.newFixedThreadPool(10);

    @Override
    public int getMemberId(String name) {
        return memberRepository.getMemberIdByMemberName(name);
    }

    @Override
    public CompletableFuture<Void> getMemberIdAsync_runAsync(String name) {
        log.info("비동기 호출 방식으로 회원 ID 조회 시작");

        //runAsync는 CompletableFuture<Void>를 return 한다.
        return CompletableFuture.runAsync(() -> {
            log.info("새로운 쓰레드로 작업 시작");
            int memberIdByMemberName = memberRepository.getMemberIdByMemberName(name);
            log.info("회원 ID:"+memberIdByMemberName);
        }, executor);
    }

    @Override
    public CompletableFuture<Integer> getMemberIdAsync_supplyAsync(String name) {
        log.info("비동기 호출 방식으로 회원 ID 조회 시작");

        return CompletableFuture.supplyAsync(() -> {
            log.info("새로운 쓰레드로 작업 시작");
            int memberIdByMemberName = memberRepository.getMemberIdByMemberName(name);
            log.info("회원 ID:"+memberIdByMemberName);
            return memberIdByMemberName;
        },executor);
    }

    @Override
    public CompletableFuture<Integer> getMemberAgeAsync_supplyAsync(String name) {
        log.info("비동기 호출 방식으로 회원 나이 조회 시작");

        return CompletableFuture.supplyAsync(() -> {
            log.info("새로운 쓰레드로 작업 시작");
            int memberAgeByMemberName = memberRepository.getMemberAgeByMemberName(name);
            log.info("회원 나이:"+memberAgeByMemberName);
            return memberAgeByMemberName;
        },executor);
    }

    @Override
    public CompletableFuture<Integer> temp() {
        CompletableFuture<Integer> future = new CompletableFuture<>();

        new Thread(() -> {

            log.info("새로운 쓰레드로 작업 시작");
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            future.complete(1);
        }).start();

        return future;
    }

}
