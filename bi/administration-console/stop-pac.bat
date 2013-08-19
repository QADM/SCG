@echo off

REM ***************************************
REM   BATCH SCRIPT TO STOP ADMIN CONSOLE
REM ***************************************

setlocal

REM necessary for loop below to find JARs and because PAC looks for config using relative path

cd /D %~dp0

set PATH=%path%
set CLASSPATH=.;./resource;./bin;./classes;./lib;

FOR %%F IN (lib\*.jar) DO call :updateClassPath %%F

goto :startjava

:updateClassPath
set CLASSPATH=%CLASSPATH%;%1
goto :eof

:startjava

call set-pentaho-env.bat "%~dp0..\biserver-ce\jre"

"%_PENTAHO_JAVA%" -Djava.io.tmpdir=temp -cp %CLASSPATH% org.pentaho.pac.server.StopJettyServer
