@echo off
echo Starting application...
set JAVA_OPTS=-Xmx512m -Dspring.profiles.active=dev
mvn clean spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx512m -Dspring.profiles.active=dev -Dlogging.level.org.springframework=DEBUG -Dlogging.level.org.hibernate=DEBUG -Dlogging.level.com.monetique=TRACE"
pause
