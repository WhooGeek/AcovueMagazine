package com.AcovueMagazine.User.Repository;

import com.AcovueMagazine.User.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {

    //로그인할때 로그인 ID 갖는 객체가 존재 -> O : true (중복 검사시 필요)
    boolean existsByLoginId(String loginId);

    //로그인 ID를 갖는 객체 반환
    Member findByLoginId(String loginId);


}
