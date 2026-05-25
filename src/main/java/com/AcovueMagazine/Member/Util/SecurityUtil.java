package com.AcovueMagazine.Member.Util;

import com.AcovueMagazine.Common.Response.ErrorCode;
import com.AcovueMagazine.Common.Response.RestApiException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    //어떤 회원이 API 요청 했는지 조회하는 메서드
    public static String getCurrentUsername(){
        // 현재 실행 중인 스레드에 저장했던 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || authentication.getName() == null){
            throw new RestApiException(ErrorCode.INVALID_TOKEN);
        }

        return authentication.getName();
    }
}
