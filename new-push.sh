#!/bin/bash

read -p "Enter new branch name SI LMSATEK: " BRANCH_NAME

if [ -z "$BRANCH_NAME" ]; then
    echo "Error: Branch name cannot be empty."
    exit 1
fi

echo ""
echo "Creating and switching to branch: $BRANCH_NAME"
git checkout -b "$BRANCH_NAME"

if [ $? -ne 0 ]; then
    echo "Error: Failed to create branch."
    exit 1
fi
git add .
git commit -m "Initial commit on $BRANCH_NAME"

echo ""
echo "Pushing branch to origin..."
git push origin "$BRANCH_NAME"

if [ $? -ne 0 ]; then
    echo "Error: Failed to push branch."
    exit 1
fi

echo ""
echo "Done! Branch \"$BRANCH_NAME\" created and pushed to origin."
