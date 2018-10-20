#!/bin/bash
./gradlew build
java -jar ./build/libs/website-0.1-all.jar course-reader &&
python ./tfidf.py &&
java -jar ./build/libs/website-0.1-all.jar weighting
