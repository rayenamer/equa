#!/bin/bash

read -p "Enter new branch name BOSS: " BRANCH_NAME

if [ -z "$BRANCH_NAME" ]; then
    echo "Error: Branch name cannot be empty BOSS"
    exit 1
fi

echo ""
echo "Creating and switching to branch: $BRANCH_NAME FOR BIG BOSS"
git checkout -b "$BRANCH_NAME"

if [ $? -ne 0 ]; then
    echo "Error: Failed to create branch. BOSS"
    exit 1
fi
git add .
git commit -m "Initial commit on $BRANCH_NAME"

echo ""
echo "Pushing branch to origin... FOR BIG BOSS"
git push origin "$BRANCH_NAME"

if [ $? -ne 0 ]; then
    echo "Error: Failed to push branch."
    exit 1
fi

echo ""
echo "Done! Branch \"$BRANCH_NAME\" created and pushed to origin. FOR BIG BOSS"
echo "========================================================================"
echo "Respect Rayen , optimizing and automating every detail."
echo "========================================================================"
