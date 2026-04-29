@echo off
setlocal

echo Switching to main branch... BIG BOSS
git switch main

if %ERRORLEVEL% neq 0 (
    echo Error: Failed to switch to main branch.BIG BOSS
    exit /b 1
)

echo.
echo Pulling latest changes from origin/main...BIG BOSS
git pull origin main

if %ERRORLEVEL% neq 0 (
    echo Error: Failed to pull from origin/main. BIG BOSS
    exit /b 1
)

echo.
echo Done! You are now on main with the latest changes. BIG BOSS
echo =========================================================
echo Updating branch for BIG BOSS
echo =========================================================
endlocal

