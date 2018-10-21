#!/bin/bash

set -x

# Build frontend
#cd frontend
#npm run build
#cd ../

cd backend
# Staging
./gradlew build
mkdir -p build/staging
cp build/libs/website-0.1-all.jar build/staging
cp src/main/docker/* build/staging

# Cloud Build
container_name='gcr.io/brh-fa18/website-container'
container_tag=`date +%s`
full_container_tag="${container_name}:${container_tag}"
echo "The container tag will be: ${full_container_tag}"
cd build/staging; \
gcloud config set project brh-fa18; \
gcloud builds submit -t ${full_container_tag} .

# Rolling Update
kubectl set image deployment website-workload *=${full_container_tag}
