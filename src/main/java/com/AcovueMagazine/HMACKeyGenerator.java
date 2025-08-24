package com.AcovueMagazine;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

public class HMACKeyGenerator {

    public static void main(String[] args){
        try{
            // MS512를 위한 KeyGenerator 생성
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA512");
            keyGen.init(512);

            //비밀키 생성
            SecretKey secretKey = keyGen.generateKey();

            //키 -> Base64로 인코딩해서 문자열로 변환
            String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

            System.out.println("HS512 Key:" + encodedKey);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
