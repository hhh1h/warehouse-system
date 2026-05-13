@echo off
chcp 65001 >nul
title 仓库管理系统 - Docker 部署

echo.
echo  ██████╗ ███████╗██╗   ██╗    ███████╗██╗   ██╗███████╗
echo  ██╔══██╗██╔════╝██║   ██║    ╚════██║╚██╗ ██╔╝╚════██║
echo  ██║  ██║█████╗  ██║   ██║ █╗ █████╗  ╚████╔╝   █████╔╝
echo  ██║  ██║██╔══╝  ╚██╗ ██╔╝ ██║██╔══╝   ╚██╔╝    ██╔══╝
echo  ██████╔╝███████╗ ╚████╔╝    ███████╗   ██║     ███████╗
echo  ╚═════╝ ╚══════╝  ╚═══╝     ╚══════╝   ╚═╝     ╚══════╝
echo.
echo  ================================================
echo   仓库管理系统 - Docker 一键部署
echo  ================================================
echo.

:: 检查 Docker
echo [1/3] 检查 Docker...
docker --version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到 Docker，请先安装 Docker Desktop
    echo 下载地址: https://www.docker.com/products/docker-desktop/
    pause
    exit /b 1
)
echo [OK] Docker 已安装

:: 检查 Docker 是否运行
docker info >nul 2>&1
if errorlevel 1 (
    echo [错误] Docker 未运行，请启动 Docker Desktop
    pause
    exit /b 1
)
echo [OK] Docker 已运行

:: 创建 .env 文件（如果不存在）
echo.
echo [2/3] 配置文件检查...
if not exist "backend\.env" (
    echo 创建默认配置文件...
    copy "backend\.env.example" "backend\.env" >nul 2>&1
)

:: 启动 Docker Compose
echo.
echo [3/3] 启动服务...
echo ================================================
docker-compose up -d --build
echo ================================================

echo.
echo.
echo  ================================================
echo   启动完成！
echo  ================================================
echo.
echo  访问地址: http://localhost
echo  API 地址:  http://localhost:9090
echo.
echo  默认账号: admin
echo  默认密码: 123456
echo.
echo  常用命令:
echo    查看日志: docker-compose logs -f
echo    停止服务: docker-compose down
echo    重启服务: docker-compose restart
echo.
pause
