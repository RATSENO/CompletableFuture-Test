package com.ratseno.repository;

import com.ratseno.model.MemberVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Repository
public class MemberRepository {

    private static final Logger log = LoggerFactory.getLogger(MemberRepository.class);

    private Map<String, MemberVO> memberVOMap = new HashMap<>();

    @PostConstruct
    public void init(){
        memberVOMap.put("철수", new MemberVO(1, "철수", 20));
        memberVOMap.put("영희", new MemberVO(2, "영희", 21));
        memberVOMap.put("미애", new MemberVO(3, "미애", 22));
    }

    public int getMemberIdByMemberName(String name){
        try {
            //조회하는데 3초 걸림
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int id = memberVOMap.get(name).getId();
        return memberVOMap.get(name).getId();
    }

    public int getMemberAgeByMemberName(String name){
        try {
            //조회하는데 6초 걸림
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int id = memberVOMap.get(name).getAge();
        return memberVOMap.get(name).getId();
    }
}
