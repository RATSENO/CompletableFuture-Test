package com.ratseno.service;

import com.ratseno.repository.MemberRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        MemberServiceImpl.class,
        MemberRepository.class
})
public class MemberServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(MemberServiceImplTest.class);

    @Autowired
    private MemberService memberService;

    @Test
    public void 동기_방식으로_ID조회하기(){
        log.info("시작");
        int memberId = memberService.getMemberId("철수");
        log.info("끝");

        Assert.assertEquals(1, memberId);
    }

    @Test
    public void 비동기_방식_잠시_블록킹(){
        CompletableFuture<Integer> completableFuture = memberService.getMemberIdAsync("철수");
        log.info("아직 최종 데이터를 전달 받지는 않았지만, 다른작업 수행가능");
        Integer join = completableFuture.join();
        if(join>0){
            log.info("/*메인 스레드가 끝나는것을 방지하기 위한 코드*/");
        }
    }

    @Test
    public void 비동기_방식_블록킹_되는_ID조회하기_반환값_없는_runAsync(){

        CompletableFuture<Void> completableFuture = memberService.getMemberIdAsync_runAsync("철수");
        log.info("아직 최종 데이터를 전달 받지는 않았지만, 다른작업 수행가능");
        completableFuture.join();
    }

    @Test
    public void 비동기_방식_블록킹_되는_ID조회하기_반환값_있는_supplyAsync(){

        log.info("비동기 방식으로 호출했기 때문에 3초동안 다른것 할수있다.");
        CompletableFuture<Integer> completableFuture = memberService.getMemberIdAsync_supplyAsync("철수");

        log.info("아직 최종 데이터를 전달 받지는 않았지만, 다른작업 수행가능");

        Integer memberId = completableFuture.join();
        log.info("completableFuture.join()하는 순간 블록킹 발생");

        Assert.assertEquals(1, memberId.intValue());
    }

    @Test
    public void 비동기_방식_논블록킹_되는_콜백방식_ID조회하기_thenAccept(){

        CompletableFuture<Void> completableFuture = memberService.getMemberIdAsync_supplyAsync("철수")
                .thenAccept(integer -> {
                    log.info("리턴값이 없는 thenAccept");
                    log.info("회원 ID:" + integer);
                });

        log.info("아직 최종 데이터를 전달 받지는 않았지만, 다른작업 수행가능");
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "다른 작업 실행 중");
            }
        };
        timer.schedule(timerTask, 1000, 1000);


        /*메인 스레드가 끝나는것을 방지하기 위한 코드*/
        Assert.assertNull(completableFuture.join());
    }

    @Test
    public void 비동기_방식_논블록킹_되는_콜백방식_ID조회하기_thenApply(){

        CompletableFuture<Void> completableFuture = memberService.getMemberIdAsync_supplyAsync("철수")
                .thenApply(integer -> {
                    log.info("리턴값이 있는 thenApply");
                    log.info("회원 ID:" + integer);
                    return integer;
                })
                .thenAccept(result -> {
                    log.info("리턴값이 없는 thenAccept");
                    log.info("회원 ID:" + result);
                    Assert.assertEquals(1, result.intValue());
                });

        log.info("아직 최종 데이터를 전달 받지는 않았지만, 다른작업 수행가능");
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "다른 작업 실행 중");
            }
        };
        timer.schedule(timerTask, 1000, 1000);

        /*메인 스레드가 끝나는것을 방지하기 위한 코드*/
        Assert.assertNull(completableFuture.join());
    }

    @Test
    public void 비동기_방식_논블록킹_되는_콜백방식_나이_조회하기_allOf(){
        //8초 걸리는 메인 스레드 종료 방지용
        CompletableFuture<Integer> temp = memberService.getMemberIdAsync("철수");

        //3초 걸림
        CompletableFuture memberIdAsyncSupplyAsync = memberService.getMemberIdAsync_supplyAsync("철수");
        //6초 걸림
        CompletableFuture memberAgeAsyncSupplyAsync = memberService.getMemberAgeAsync_supplyAsync("철수");

        List<CompletableFuture> completableFutures = Arrays.asList(memberIdAsyncSupplyAsync, memberAgeAsyncSupplyAsync);
        //전부 완료되었을 때
        CompletableFuture.allOf(memberIdAsyncSupplyAsync, memberAgeAsyncSupplyAsync)
                            .thenAccept(aVoid -> {
                                List<Object> collect = completableFutures.stream()
                                        .map(completableFuture -> {
                                            return completableFuture.join();
                                        })
                                        .collect(Collectors.toList());
                                log.info(collect.toString());
                            });

        log.info("아직 최종 데이터를 전달 받지는 않았지만, 다른작업 수행가능");
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "다른 작업 실행 중");
            }
        };
        timer.schedule(timerTask, 1000, 1000);

        /*메인 스레드가 끝나는것을 방지하기 위한 코드*/
        Assert.assertEquals(1, temp.join().intValue());
    }

    @Test
    public void test(){
        CompletableFuture<Integer> temp = memberService.getMemberIdAsync("철수");

        CompletableFuture completableFuture1 = CompletableFuture.supplyAsync(() -> {
            log.info("future-1");
            return (Integer)1;
        });

        CompletableFuture completableFuture2 = CompletableFuture.supplyAsync(() -> {
            log.info("future-2");
            return (Integer)2;
        });

        CompletableFuture completableFuture3 = CompletableFuture.supplyAsync(() -> {
            log.info("future-3");
            return (Integer)3;
        });

        List<CompletableFuture> futures = Arrays.asList(completableFuture1,
                completableFuture2,
                completableFuture3);
        CompletableFuture.allOf(completableFuture1, completableFuture2, completableFuture3)
                .thenAccept(s -> {
                    List<Object> result = futures.stream()
                            .map(pageContentFuture -> pageContentFuture.join())
                            .collect(Collectors.toList());
                    log.info(result.toString());
                });

        /*메인 스레드가 끝나는것을 방지하기 위한 코드*/
        Assert.assertEquals(1, temp.join().intValue());
    }

}