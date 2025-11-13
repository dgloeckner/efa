@echo off

IF "%1" == "" GOTO USAGE
IF "%2" == "run" GOTO RUN
IF "%2" == "runcli" GOTO RUN

REM # ##########################################
REM # Test for Operating System                #
REM # ##########################################
IF "%OS%" == "Windows_NT" GOTO WINNT
GOTO WIN9X

:WIN9X
command.com /e:1024 /crunefa.bat %1 run %2 %3 %4 %5 %6 %7 %8
GOTO END

:WINNT
call runefa.bat %1 run %2 %3 %4 %5 %6 %7 %8
GOTO END


REM # ##########################################
REM # Show Usage                               #
REM # ##########################################
:USAGE
echo usage: runefa.bat mainclass [run] [arguments]
goto EXIT

REM # ##########################################
REM # Prepare to run Program                   #
REM # ##########################################
:RUN
REM Preparing to run ...


REM Prefer Getdown launcher if available (non-breaking fallback below)
IF EXIST program\getdown.jar IF EXIST program\getdown.txt GOTO STARTGETDOWN

REM # ##########################################
REM # JVM Settings                             #
REM # ##########################################

REM Java Heap
REM A higher Java Heaps helps to speed up efa on slower computers
REM As garbage collection needs to run at lower frequencies
SET EFA_JAVA_HEAP=192m
SET EFA_NEW_SIZE=32m
IF EXIST javaheap.bat CALL javaheap.bat

REM JVM Options
SET JVMOPTIONS=-Xmx%EFA_JAVA_HEAP% -XX:NewSize=%EFA_NEW_SIZE% -XX:MaxNewSize=%EFA_NEW_SIZE%
REM Enable remote debugging if requested
IF "%EFA_JVM_DEBUG%"=="1" SET JVMOPTIONS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005 %JVMOPTIONS%


REM # ##########################################
REM # Run Program                              #
REM # ##########################################

REM Java Arguments
SET EFA_JAVA_ARGUMENTS=%JVMOPTIONS% -cp program\efa.jar %1 -javaRestart %3 %4 %5 %6 %7 %8 %9

SET EFA_RUN_CLI=0
SET EFA_RUN_DEBUG=0
IF "%2" == "runcli" SET EFA_RUN_CLI=1
IF "%EFA_RUN_CLI%" == "1" GOTO STARTCLI
IF "%EFA_RUN_DEBUG%" == "1" GOTO STARTCLIDBG
IF "%OS%" == "Windows_NT" GOTO STARTNT
GOTO START9X

:STARTGETDOWN
ECHO launching via Getdown ...
START /B javaw -cp program\* io.github.bekoenig.getdown.launcher.GetdownApp program %*
GOTO END

:STARTNT
REM Path for Windows 7 (64 Bit)
SET PATH=%PATH%;C:\Windows\SysWOW64
echo starting %1 (Windows NT) ...
start /b javaw %EFA_JAVA_ARGUMENTS%
GOTO END

:START9X
echo starting %1 (Windows 9x) ...
javaw %EFA_JAVA_ARGUMENTS%
GOTO END

:STARTCLIDBG
echo EFA_JAVA_ARGUMENTS=%EFA_JAVA_ARGUMENTS%
:STARTCLI
java %EFA_JAVA_ARGUMENTS%
goto EXIT

:END
IF "%EFA_RUN_CLI%" == "1" GOTO EXIT
IF "%EFA_RUN_DEBUG%" == "1" GOTO EXIT
@CLS
:EXIT
