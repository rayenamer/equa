#!/bin/bash

echo "Switching to main branch...lel BOSS"
git switch main

if [ $? -ne 0 ]; then
    echo "Error: Failed to switch to main branch. BISS"
    exit 1
fi

echo ""
echo "Pulling latest changes from origin/main... FOR BIG BOSS"
git pull origin main

if [ $? -ne 0 ]; then
    echo "Error: Failed to pull from origin/main. SORRY BOSS"
    exit 1
fi

echo ""
echo "Done! You are now on main with the latest changes. FOR BIG BOSS"
echo "Done! Branch \"$BRANCH_NAME\" created and pushed to origin. FOR BIG BOSS"
echo "========================================================================"
echo "Respect Rayen , optimizing and automating every detail."
echo "========================================================================"
