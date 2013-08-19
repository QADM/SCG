@echo off
REM ************************************************************************************************
REM   BATCH SCRIPT TO START ADMIN CONSOLE
REM ************************************************************************************************
REM If you find that your system encoding isn't UTF-8, but the platform
REM web.xml is set for UTF-8, you need to add an additional -D parameter to the java line below:
REM -Dfile.encoding="UTF-8"
REM ************************************************************************************************

setlocal

REM necessary for loop below to find JARs and because PAC looks for config using relative path

cd /D %~dp0

set CLASSPATH=.;resource\config
FOR %%F IN (lib\*.jar) DO call :updateClassPath %%F
FOR %%F IN (jdbc\*.jar) DO call :updateClassPath %%F

goto :startjava

:updateClassPath
set CLASSPATH=%CLASSPATH%;%1
goto :eof

:startjava

call set-pentaho-env.bat "%~dp0..\biserver-ce\jre"

call "%_PENTAHO_JAVA%" -Xmx512M -XX:PermSize=64M -XX:MaxPermSize=128M -DCONSOLE_HOME=. -Dlog4j.configuration=resource/config/log4j.xml -cp %CLASSPATH%  org.pentaho.pac.server.JettyServer
