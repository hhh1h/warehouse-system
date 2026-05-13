@echo off
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot"
echo Java Version:
"%JAVA_HOME%\bin\java.exe" -version
echo.
echo Maven Version:
call "E:\apache-maven-3.9.14\bin\mvn.cmd" -version
pause
