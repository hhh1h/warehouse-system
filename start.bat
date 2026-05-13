@echo off
chcp 65001 >nul
title 仓库管理系统

echo.
echo  ██████╗ ███████╗██╗   ██╗    ███████╗██╗   ██╗███████╗
echo  ██╔══██╗██╔════╝██║   ██║    ╚════██║╚██╗ ██╔╝╚════██║
echo  ██║  ██║█████╗  ██║   ██║ █╗ █████╗  ╚████╔╝   █████╔╝
echo  ██║  ██║██╔══╝  ╚██╗ ██╔╝ ██║██╔══╝   ╚██╔╝    ██╔══╝
echo  ██████╔╝███████╗ ╚████╔╝    ███████╗   ██║     ███████╗
echo  ╚═════╝ ╚══════╝  ╚═══╝     ╚══════╝   ╚═╝     ╚══════╝
echo.
echo  ================================================
echo   仓库管理系统 - 一键启动
echo  ================================================
echo.

:: 检查 Java
echo [1/4] 检查 Java 环境...
java -version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到 Java，请先安装 JDK 17+
    echo 下载地址: https://adoptium.net/
    pause
    exit /b 1
)
echo [OK] Java 已安装

:: 检查 Maven
echo.
echo [2/4] 检查 Maven 环境...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到 Maven，请先安装 Maven
    echo 下载地址: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)
echo [OK] Maven 已安装

:: 检查 MySQL
echo.
echo [3/4] 检查 MySQL 服务...
set MYSQL_RUNNING=0
sc query MySQL >nul 2>&1
if not errorlevel 1 set MYSQL_RUNNING=1
sc query MySQL80 >nul 2>&1
if not errorlevel 1 set MYSQL_RUNNING=1
sc query "MySQL 8.0" >nul 2>&1
if not errorlevel 1 set MYSQL_RUNNING=1

if "%MYSQL_RUNNING%"=="0" (
    echo [警告] MySQL 服务未运行，尝试启动...
    net start MySQL >nul 2>&1
    net start MySQL80 >nul 2>&1
    timeout /t 3 >nul
    sc query MySQL >nul 2>&1
    if not errorlevel 1 set MYSQL_RUNNING=1
    sc query MySQL80 >nul 2>&1
    if not errorlevel 1 set MYSQL_RUNNING=1

    if "%MYSQL_RUNNING%"=="0" (
        echo [错误] 无法启动 MySQL，请手动启动 MySQL 服务
        echo 提示：在 Windows 服务中找到 MySQL 并启动
        pause
        exit /b 1
    )
)
echo [OK] MySQL 已运行

:: 初始化数据库
echo.
echo [4/4] 初始化数据库...
mysql -uroot -p123456 -e "CREATE DATABASE IF NOT EXISTS warehouse;" >nul 2>&1
mysql -uroot -p123456 warehouse < sql\init.sql >nul 2>&1
if errorlevel 1 (
    echo [警告] 数据库初始化可能需要手动执行
    echo 请确保 MySQL 用户名: root, 密码: 123456
) else (
    echo [OK] 数据库已初始化
)

:: 启动后端
echo.
echo ================================================
echo  启动后端服务...
echo ================================================
start "仓库管理-后端服务" cmd /k "cd /d %~dp0backend && mvn spring-boot:run"

:: 等待后端启动
echo  等待后端启动（约30秒）...
timeout /t 30 /nobreak >nul

:: 检查后端是否启动成功
curl -s http://localhost:9090/user/stats >nul 2>&1
if errorlevel 1 (
    echo [警告] 后端可能未启动完成，请查看后端窗口
) else (
    echo [OK] 后端启动成功
)

echo.
echo ================================================
echo  启动完成！
echo ================================================
echo.
echo  访问地址: http://localhost:9090
echo  默认账号: admin
echo  默认密码: 123456
echo.
echo  提示：如果前端显示异常，请先构建前端：
echo    cd frontend ^&^& npm install ^&^& npm run build
echo.
pause
