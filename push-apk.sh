#!/bin/sh

# Checkout gh-pages branch
git clone https://github.com/FireCat1987/QRmoduleApp.git --branch gh-pages --single-branch repo_ghpages
cd repo_ghpages

# Copy newly created APK into the target directory
mv $TRAVIS_BUILD_DIR/qrmodule.apk ./build

# Setup git for commit and push
git config --global user.email "travis@travis-ci.org"
git config --global user.name "Travis CI"
git remote add gh-pages https://${AUTOBUILD_TOKEN}@github.com/FireCat1987/QRmoduleApp > /dev/null 2>&1
git add ./build/qrmodule.apk

# We don’t want to run a build for a this commit in order to avoid circular builds:
# add [ci skip] to the git commit message
git commit --message "Snapshot build N.$TRAVIS_BUILD_NUMBER [ci skip]"
git push gh-pages
