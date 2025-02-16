# İlk Aşama: Gereksinimleri doğrulayın ve projeyi oluşturun
FROM eclipse-temurin:17-jdk AS builder

# Paketleri güncelleyin ve gerekli araçları yükleyin
RUN apt-get update && apt-get install -y maven nodejs npm postgresql-client && rm -rf /var/lib/apt/lists/*

# Çalışma dizinini ayarla
WORKDIR /app

# Java, Maven ve PostgreSQL'in kurulu olduğundan emin olun
RUN java -version && javac -version && mvn -version && psql --version

# Derlemeyi hızlandırmak için bağımlılıkları önceden indirin
COPY pom.xml .
RUN mvn dependency:go-offline

# Kaynak kodunu kopyala ve derle
COPY src ./src
RUN mvn clean package -Pproduction -DskipTests -Dvaadin.ignoreVersionChecks=true

# İkinci Aşama: Çalıştırma ortamı
FROM openjdk:17-slim

WORKDIR /app

# Derlenmiş JAR dosyasını kopyala
COPY --from=builder /app/target/spring-vaadin-crud-1.0-SNAPSHOT.jar /app.jar

# Uygulama için bağlantı noktası
EXPOSE 8081

# Uygulamayı başlat
CMD ["sh", "-c", "java -jar /app.jar --db.enabled=${DB_ENABLED} --spring.profiles.active=${SPRING_PROFILE} --debug"]
