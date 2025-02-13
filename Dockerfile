# استخدم صورة من OpenJDK لتشغيل تطبيق Java
FROM eclipse-temurin:17-jre

# نسخ JAR الخاص بالتطبيق إلى الحاوية
COPY target/*.jar app.jar

# تعريض البورت 8080 لتطبيقك
EXPOSE 8080

# تشغيل التطبيق باستخدام Java
ENTRYPOINT ["java", "-jar", "/app.jar"]
