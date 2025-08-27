package com.AcovueMagazine.User.Service;

import com.AcovueMagazine.User.Dto.JoinUserDto;
import com.AcovueMagazine.User.Dto.LoginUserDto;
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

    public void join(JoinUserDto joinUserDto) {
        memberRepository.save(joinUserDto.toEntity());
    }

    public Member login(LoginUserDto loginUserDto) {
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


}
