#!/bin/bash

# Create (everytime) a new gh-pages branch not based in our current branch (with empty content except the .gitignore'd files)
# So, only run this script at the end!
git switch --orphan gh-pages

# Move /docs/dokka-generated/* to root
mv /docs/dokka-generated/* .

git add .
git commit -m "Deploy to gh-pages"
git push origin gh-pages --force
