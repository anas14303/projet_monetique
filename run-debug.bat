@echo off
set JAVA_OPTS=-Xmx512m -Dspring.profiles.active=dev -Dlogging.level.org.springframework=DEBUG -Dlogging.level.org.hibernate=DEBUG -Dlogging.level.com.monetique=TRACE -Dlogging.file=logs/application.log
mvn spring-boot:run
pause
