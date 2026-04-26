@echo off
setlocal

echo Switching to main branch...
git switch main

if %ERRORLEVEL% neq 0 (
    echo Error: Failed to switch to main branch.
    exit /b 1
)

echo.
echo Pulling latest changes from origin/main...
git pull origin main

if %ERRORLEVEL% neq 0 (
    echo Error: Failed to pull from origin/main.
    exit /b 1
)

echo.
echo Done! You are now on main with the latest changes.
endlocal