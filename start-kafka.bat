@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

set "KAFKA_VERSION=3.6.1"
set "SCALA_VERSION=2.13"
set "KAFKA_DIR=kafka_%SCALA_VERSION%-%KAFKA_VERSION%"
set "KAFKA_URL=https://downloads.apache.org/kafka/%KAFKA_VERSION%/%KAFKA_DIR%.tgz"
set "DOWNLOAD_DIR=%cd%"
set "KAFKA_HOME=%DOWNLOAD_DIR%\%KAFKA_DIR%"

echo ==============================================
echo  自动下载并启动Apache Kafka
echo ==============================================

if not exist "%KAFKA_HOME%" (
    echo [1/3] 下载 Kafka %KAFKA_VERSION%...
    powershell -Command "Invoke-WebRequest -Uri '%KAFKA_URL%' -OutFile '%DOWNLOAD_DIR%\kafka.tgz'"
    
    echo [2/3] 解压 Kafka...
    powershell -Command "tar -xzf '%DOWNLOAD_DIR%\kafka.tgz' -C '%DOWNLOAD_DIR%'"
    del "%DOWNLOAD_DIR%\kafka.tgz"
) else (
    echo [1/2] Kafka已存在，跳过下载
)

echo [2/2] 修改server.properties配置...
set "SERVER_CONFIG=%KAFKA_HOME%\config\server.properties"
powershell -Command "(Get-Content '%SERVER_CONFIG%') -replace 'listeners=PLAINTEXT://:9092', 'listeners=PLAINTEXT://localhost:9092' | Set-Content '%SERVER_CONFIG%'"
powershell -Command "(Get-Content '%SERVER_CONFIG%') -replace 'advertised.listeners=', 'advertised.listeners=PLAINTEXT://localhost:9092' | Set-Content '%SERVER_CONFIG%'"

echo.
echo ==============================================
echo  启动Kafka...
echo ==============================================

start "Zookeeper" cmd /k "%KAFKA_HOME%\bin\windows\zookeeper-server-start.bat %KAFKA_HOME%\config\zookeeper.properties"
timeout /t 5 /nobreak >nul
start "Kafka" cmd /k "%KAFKA_HOME%\bin\windows\kafka-server-start.bat %KAFKA_HOME%\config\server.properties"

echo.
echo Kafka服务正在启动，请等待10-15秒后再启动collector-service
echo Zookeeper端口: 2181
echo Kafka端口: 9092
echo.
pause