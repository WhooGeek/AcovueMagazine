package com.AcovueMagazine.Member.Service;

import com.AcovueMagazine.Member.Entity.MemberLoginStatus;
import com.AcovueMagazine.Member.Entity.MemberRole;
import com.AcovueMagazine.Member.Entity.MemberStatus;
import com.AcovueMagazine.Member.Entity.Members;
import com.AcovueMagazine.Member.Repository.MembersRepository;
import com.AcovueMagazine.Member.Util.MemberDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MembersRepository membersRepository;
    private final PasswordEncoder passwordEncoder;

    // 구글에서 데이터 받아올 때 이 메서드가 자동 실행
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 1. 부모 클래스한테 구글에서 유저 정보 받아오라고 지시
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2. 어디서 로그인했는지 확인 (구글, 네이버, 카카오)
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // 3. 받아온 원본 데이터 꺼내기
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 4. 필요한 데이터 추출
        String providerId = "";
        String email = "";
        String name = "";

        if ("google". equals(provider)) {
            // 구글 로그인 데이터 핸들링
            providerId = oAuth2User.getAttribute("sub");
            email = oAuth2User.getAttribute("email");
            name = oAuth2User.getAttribute("name");
        } else if ("naver".equals(provider)) {
            //네이버 로그인 데이터 핸들링
            Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttribute("response");
            if (response == null) {
                throw new OAuth2AuthenticationException(new OAuth2Error("naver_response_missing"),
                        "네이버 사용자 정보를 불러오지 못했습니다.");
            }
            providerId = (String) response.get("id");
            email = (String) response.get("email");
            name = (String) response.get("name");
        }

        if (providerId == null || providerId.isBlank()) {
            throw new OAuth2AuthenticationException(new OAuth2Error("provider_id_missing"),
                    "소셜 로그인 사용자 식별값이 없습니다.");
        }

        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException(new OAuth2Error("email_missing"),
                    "소셜 로그인 계정의 이메일 정보를 확인할 수 없습니다.");
        }

        if (name == null || name.isBlank()) {
            name = provider + "_" + providerId;
        }

        log.info("소셜 로그인 시도 : provider{} email{}", provider, email );

        // 5. 우리 디비에서 해당 데이터 있는지 확인
        Optional<Members> optionalMember = membersRepository.findByMemberEmail(email);
        Members member;

        if(optionalMember.isEmpty()){
            // 해당 데이터가 DB에 없다면 -> 회원가입으로 진행
            log.info("새로운 소셜 로그인 유저, 회원가입 진행");

            // 더미 비밀번호 생성(비밀번호 엔티티가 NotNull 이어서 필요)
            String dummyPassword = passwordEncoder.encode(UUID.randomUUID().toString());
            String nickname = createUniqueNickname(email, provider, providerId);

            member = Members.builder()
                    .memberName(name)
                    .memberNickname(nickname)
                    .memberEmail(email)
                    .memberPassword(dummyPassword)
                    .memberRole(MemberRole.USER)
                    .memberStatus(MemberStatus.ACTIVE)
                    .memberLoginStatus(MemberLoginStatus.LOGIN)
                    .provider(provider)
                    .providerId(providerId)
                    .build();

            member = membersRepository.save(member);
        } else{
            // 해당 데이터 있으면 -> 로그인으로 진행
            log.info("기존 소셜 로그인 유저, 로그인 진행");
            member = optionalMember.get();
        }

        // 이제 이 데이터들을 일반 로그인, 소셜 로그인 합쳐놓은 하이브리드 MemberDetail로 리턴
        return new MemberDetail(member, attributes);
    }

    private String createUniqueNickname(String email, String provider, String providerId) {
        String emailPrefix = email.split("@")[0].toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_]", "");
        String baseNickname = emailPrefix.isBlank() ? provider + "_" + providerId.substring(0, Math.min(providerId.length(), 8)) : emailPrefix;
        String nickname = baseNickname;
        int suffix = 1;

        while (membersRepository.existsByMemberNickname(nickname)) {
            nickname = baseNickname + suffix;
            suffix++;
        }

        return nickname;
    }
}
