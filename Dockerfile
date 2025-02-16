#İlk Aşama: Gereksinimleri doğrulayın ve projeyi oluşturun
FROM eclipse-temurin:17-jdk AS builder

# Paketleri güncelleyin ve gerekli araçları yükleyin
RUN apt-get update && apt-get install -y maven nodejs npm postgresql-client

#Eylem rotasını belirleyin
WORKDIR /app

# Java, Maven ve PostgreSQL'in kurulu olduğundan emin olun
RUN java -version && javac -version && mvn -version && psql --version

# Derlemeyi hızlandırmak için 'pom.xml' dosyasını kopyalayın ve bağımlılıkları önceden indirin
COPY pom.xml .
RUN mvn dependency:go-offline

# Derlemeyi hızlandırmak için 'pom.xml' dosyasını kopyalayın ve bağımlılıkları önceden indirin
COPY src ./src
RUN mvn clean package -Pproduction -DskipTests -Dvaadin.ignoreVersionChecks=true

# İkinci Aşama: Uygulamayı çalıştırma
FROM openjdk:17-slim

WORKDIR /app

# Kaynak kodu ve yapılandırma dosyalarını kopyala
COPY --from=builder /app/target/spring-vaadin-crud-1.0-SNAPSHOT.jar /app.jar

EXPOSE 8081

# Uygulamayı DB_ENABLED ortam değişkenine göre çalıştırın
CMD ["sh", "-c", "java -jar /app.jar --db.enabled=${DB_ENABLED}"]
