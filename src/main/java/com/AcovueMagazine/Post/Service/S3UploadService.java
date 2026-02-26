package com.AcovueMagazine.Post.Service;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String saveFile(MultipartFile multipartFile) throws IOException {

        String originalFilename = multipartFile.getOriginalFilename();

        // 파일 이름 겹치지 않게 UUID 걸기
        String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;

        // S3에 파일 형식, 사이즈 전송
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        // S3로 전송, PublicRead 권한 부여
        amazonS3.putObject(new PutObjectRequest(bucket, uniqueFileName, multipartFile.getInputStream(), metadata));

        // S3 올린 파일 URL 꺼내서 반환
        return amazonS3.getUrl(bucket, uniqueFileName).toString();
    }

    public List<String> saveFile(List<MultipartFile> multipartFiles){
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : multipartFiles) {
            try{
                String url = saveFile(file);
                imageUrls.add(url);
            } catch (IOException e){
                throw new RuntimeException("S3 다중 이미지 업로드에 실패하였습니다.", e);

            }
        }
        return imageUrls;
    }


}
