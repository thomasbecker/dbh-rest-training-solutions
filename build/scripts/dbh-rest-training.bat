@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  dbh-rest-training startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and DBH_REST_TRAINING_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\dbh-rest-training-1.0.0.jar;%APP_HOME%\lib\jersey-container-jetty-http-2.35.jar;%APP_HOME%\lib\jersey-container-servlet-2.35.jar;%APP_HOME%\lib\jersey-container-servlet-core-2.35.jar;%APP_HOME%\lib\jersey-bean-validation-2.35.jar;%APP_HOME%\lib\jersey-server-2.35.jar;%APP_HOME%\lib\jersey-hk2-2.35.jar;%APP_HOME%\lib\jersey-media-json-jackson-2.35.jar;%APP_HOME%\lib\jersey-client-2.35.jar;%APP_HOME%\lib\jersey-common-2.35.jar;%APP_HOME%\lib\swagger-jaxrs2-2.2.8.jar;%APP_HOME%\lib\swagger-integration-2.2.8.jar;%APP_HOME%\lib\swagger-core-2.2.8.jar;%APP_HOME%\lib\swagger-models-2.2.8.jar;%APP_HOME%\lib\jackson-jaxrs-json-provider-2.14.3.jar;%APP_HOME%\lib\jackson-module-jaxb-annotations-2.14.3.jar;%APP_HOME%\lib\jackson-annotations-2.14.3.jar;%APP_HOME%\lib\jackson-jaxrs-base-2.14.3.jar;%APP_HOME%\lib\jackson-dataformat-yaml-2.14.3.jar;%APP_HOME%\lib\jackson-core-2.14.3.jar;%APP_HOME%\lib\jackson-datatype-jsr310-2.14.3.jar;%APP_HOME%\lib\jjwt-jackson-0.11.5.jar;%APP_HOME%\lib\jackson-databind-2.14.3.jar;%APP_HOME%\lib\jetty-servlet-9.4.51.v20230217.jar;%APP_HOME%\lib\jetty-security-9.4.51.v20230217.jar;%APP_HOME%\lib\jetty-server-9.4.51.v20230217.jar;%APP_HOME%\lib\jetty-http-9.4.51.v20230217.jar;%APP_HOME%\lib\jetty-io-9.4.51.v20230217.jar;%APP_HOME%\lib\jetty-util-ajax-9.4.51.v20230217.jar;%APP_HOME%\lib\jetty-util-9.4.51.v20230217.jar;%APP_HOME%\lib\logback-classic-1.2.12.jar;%APP_HOME%\lib\slf4j-api-1.7.36.jar;%APP_HOME%\lib\validation-api-2.0.1.Final.jar;%APP_HOME%\lib\hibernate-validator-6.2.5.Final.jar;%APP_HOME%\lib\javax.el-3.0.0.jar;%APP_HOME%\lib\jjwt-impl-0.11.5.jar;%APP_HOME%\lib\jjwt-api-0.11.5.jar;%APP_HOME%\lib\jbcrypt-0.4.jar;%APP_HOME%\lib\javax.annotation-api-1.3.2.jar;%APP_HOME%\lib\swagger-jaxrs2-servlet-initializer-2.2.8.jar;%APP_HOME%\lib\swagger-ui-4.15.5.jar;%APP_HOME%\lib\jersey-entity-filtering-2.35.jar;%APP_HOME%\lib\jakarta.ws.rs-api-2.1.6.jar;%APP_HOME%\lib\jakarta.annotation-api-1.3.5.jar;%APP_HOME%\lib\hk2-locator-2.6.1.jar;%APP_HOME%\lib\hk2-api-2.6.1.jar;%APP_HOME%\lib\hk2-utils-2.6.1.jar;%APP_HOME%\lib\jakarta.inject-2.6.1.jar;%APP_HOME%\lib\jakarta.validation-api-2.0.2.jar;%APP_HOME%\lib\osgi-resource-locator-1.0.3.jar;%APP_HOME%\lib\javassist-3.25.0-GA.jar;%APP_HOME%\lib\jetty-continuation-9.4.42.v20210604.jar;%APP_HOME%\lib\javax.servlet-api-3.1.0.jar;%APP_HOME%\lib\logback-core-1.2.12.jar;%APP_HOME%\lib\jakarta.el-api-3.0.3.jar;%APP_HOME%\lib\jakarta.el-3.0.4.jar;%APP_HOME%\lib\jboss-logging-3.4.1.Final.jar;%APP_HOME%\lib\classmate-1.5.1.jar;%APP_HOME%\lib\classgraph-4.8.150.jar;%APP_HOME%\lib\snakeyaml-1.33.jar;%APP_HOME%\lib\swagger-annotations-2.2.8.jar;%APP_HOME%\lib\aopalliance-repackaged-2.6.1.jar;%APP_HOME%\lib\jakarta.xml.bind-api-2.3.3.jar;%APP_HOME%\lib\jakarta.activation-api-1.2.2.jar;%APP_HOME%\lib\commons-lang3-3.12.0.jar


@rem Execute dbh-rest-training
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %DBH_REST_TRAINING_OPTS%  -classpath "%CLASSPATH%" com.dbh.training.rest.Application %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable DBH_REST_TRAINING_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%DBH_REST_TRAINING_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
