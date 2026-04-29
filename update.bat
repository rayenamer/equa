@echo off
setlocal

echo Switching to main branch... big boss
git switch main

if %ERRORLEVEL% neq 0 (
    echo Error: Failed to switch to main branch.big boss
    exit /b 1
)

echo.
echo Pulling latest changes from origin/main...LMSATEK
git pull origin main

if %ERRORLEVEL% neq 0 (
    echo Error: Failed to pull from origin/main. big boss
    exit /b 1
)

echo.
echo Done! You are now on main with the latest changes. YA BHIM
echo =========================================================
echo Updating branch for big boss
echo =========================================================
endlocal

