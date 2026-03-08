# 빌드는 Github Actions 에서만 돌리는거니깐 가벼운 이미지 사용
FROM eclipse-temurin:21-jre-alpine

# 작업 디렉토리 생성
WORKDIR /app

# 빌드된 JAR 파일을 컨테이너 안으로 복사, Plain.jar 는 제외
COPY build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080

# 한국 시간 맞추기
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]