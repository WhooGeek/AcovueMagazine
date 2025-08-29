package com.AcovueMagazine.User.Service;

import com.AcovueMagazine.User.Dto.JoinUserReqDTO;
import com.AcovueMagazine.User.Dto.LoginUserReqDTO;
import com.AcovueMagazine.User.Entity.Member;
import com.AcovueMagazine.User.Repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public boolean checkLoginIdDuplicate(String loginId){
        return memberRepository.existsByLoginId(loginId);
    }

    public void join(JoinUserReqDTO joinUserDto) {
        memberRepository.save(joinUserDto.toEntity());
    }

    public Member login(LoginUserReqDTO loginUserDto) {
        Member findMember = memberRepository.findByLoginId(loginUserDto.getLoginId());

        if (findMember == null) {
            return null;
        }

        if (!findMember.getPassword().equals(loginUserDto.getPassword())) {
            return null;
        }

        return findMember;
    }

    public Member getLoginMemberById(Long memberId){
        if(memberId == null) return null;

        Optional<Member> findMember = memberRepository.findById(memberId);
        return findMember.orElse(null);
    }


    public Member getLoginMemberByLoginId(String loginId) {
        if(loginId == null) return null;

        return memberRepository.findByLoginId(loginId);

    }

    public void securityJoin(JoinUserReqDTO joinUserDto) {
        if(memberRepository.existsByLoginId(joinUserDto.getLoginId())){
            return;
        }

    }
}
