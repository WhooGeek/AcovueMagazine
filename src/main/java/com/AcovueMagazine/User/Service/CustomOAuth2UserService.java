package com.AcovueMagazine.User.Service;

import com.AcovueMagazine.User.Dto.GoogleUserDetail;
import com.AcovueMagazine.User.Entity.CustomOauth2UserDetails;
import com.AcovueMagazine.User.Entity.Member;
import com.AcovueMagazine.User.Entity.MemberRole;
import com.AcovueMagazine.User.Repository.MemberRepository;
import com.AcovueMagazine.User.Repository.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2user = super.loadUser(userRequest);
        log.info("getAttributes : {}", oAuth2user.getAttributes());

        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo = null;

        if(provider.equals("google")){
            log.info("구글 로그인");
            oAuth2UserInfo = new GoogleUserDetail(oAuth2user.getAttributes());

        }
        String providerId = oAuth2UserInfo.getProviderId();
        String email = oAuth2UserInfo.getEmail();
        String loginId = provider + "_" + providerId;
        String name = oAuth2UserInfo.getName();
        Member member;

        Member findmember = memberRepository.findByLoginId(loginId);
        if(findmember == null){
            member = Member.builder()
                    .loginId(loginId)
                    .name(name)
                    .provider(provider)
                    .providerId(providerId)
                    .role(MemberRole.USER)
                    .build();
            memberRepository.save(member);
        } else{
            member = findmember;
        }

        return new CustomOauth2UserDetails(member, oAuth2user.getAttributes());
    }
}
