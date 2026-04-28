#!/bin/bash

echo "Switching to main branch...l si LMSATEK"
git switch main

if [ $? -ne 0 ]; then
    echo "Error: Failed to switch to main branch."
    exit 1
fi

echo ""
echo "Pulling latest changes from origin/main..."
git pull origin main

if [ $? -ne 0 ]; then
    echo "Error: Failed to pull from origin/main."
    exit 1
fi

echo ""
echo "Done! You are now on main with the latest changes."
