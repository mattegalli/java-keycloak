@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script (Windows)
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set MAVEN_WRAPPER_PROPERTIES=%~dp0.mvn\wrapper\maven-wrapper.properties
set MAVEN_HOME_PARENT=%USERPROFILE%\.m2\wrapper\dists

for /f "tokens=2 delims==" %%a in ('findstr "distributionUrl" "%MAVEN_WRAPPER_PROPERTIES%"') do set DISTRIBUTION_URL=%%a

for %%a in ("%DISTRIBUTION_URL%") do set MAVEN_ZIP_NAME=%%~na
set MAVEN_HOME=%MAVEN_HOME_PARENT%\%MAVEN_ZIP_NAME%

if not exist "%MAVEN_HOME%" (
  echo Downloading Apache Maven from %DISTRIBUTION_URL%
  if not exist "%MAVEN_HOME_PARENT%" mkdir "%MAVEN_HOME_PARENT%"
  powershell -Command "Invoke-WebRequest -Uri '%DISTRIBUTION_URL%' -OutFile '%MAVEN_HOME_PARENT%\mvn.zip'"
  powershell -Command "Expand-Archive -Path '%MAVEN_HOME_PARENT%\mvn.zip' -DestinationPath '%MAVEN_HOME_PARENT%'"
  del "%MAVEN_HOME_PARENT%\mvn.zip"
  for /d %%d in ("%MAVEN_HOME_PARENT%\apache-maven-*") do move "%%d" "%MAVEN_HOME%"
)

"%MAVEN_HOME%\bin\mvn.cmd" %*
