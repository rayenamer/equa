@echo off

set /p BRANCH_NAME=Enter new branch name SI ZEBI: 

if "%BRANCH_NAME%"=="" (
    echo Error: Branch name cannot be empty.
    exit /b 1
)

echo.
echo Creating and switching to branch: %BRANCH_NAME%
git checkout -b "%BRANCH_NAME%"

if errorlevel 1 (
    echo Error: Failed to create branch.
    exit /b 1
)

git add .
git commit -m "Initial commit on %BRANCH_NAME%"

echo.
echo Pushing branch to origin...
git push origin "%BRANCH_NAME%"

if errorlevel 1 (
    echo Error: Failed to push branch.
    exit /b 1
)

echo.
echo Done! Branch "%BRANCH_NAME%" created and pushed to origin.