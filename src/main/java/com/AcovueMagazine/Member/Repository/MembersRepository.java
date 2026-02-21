package com.AcovueMagazine.Member.Repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.AcovueMagazine.Member.Entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembersRepository extends JpaRepository<Members,Long> {

    boolean existsByMemberEmail(String member_email);
    boolean existsByMemberNickname(String member_nickname);
    Optional<Members> findByMemberNickname(String member_nickname);

    // Memeber Email로 유저 데이터 찾기
    // OAuth 로그인 시 이메일로 유저 찾기
    Optional<Members> findByMemberEmail(String member_email);
}
