#!/bin/sh

TZ='America/Toronto'
NEWURL=https://s3.amazonaws.com/${ARTIFACTS_BUCKET}/${TRAVIS_REPO_SLUG}/${TRAVIS_BUILD_NUMBER}/${TRAVIS_JOB_NUMBER}/`ls build/libs/*.jar`
TEXT="Build #${TRAVIS_BUILD_NUMBER} - `date`"

LINE="[${TEXT}](${NEWURL}) - [Commit](https://github.com/${TRAVIS_REPO_SLUG}/commit/${TRAVIS_COMMIT})"

git clone --depth=10 --branch=master https://pkmnfrk:${GIT_TOKEN}@github.com/pkmnfrk/pkmnfrk.github.io.git site

cd site

sed -ie "/^<!-- Top -->/a * ${LINE}" megacorp-builds.md

git add megacorp-builds.md
git commit -m "Add reference to new build" --author="Build Bot <caron.mike@gmail.com>"
git push origin master

cd ..

rm -rf site