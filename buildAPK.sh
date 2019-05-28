#!/bin/bash
./gradlew :app:clean
./gradlew assembleUatMobile30Debug
./gradlew assembleProdMobile30Release
./gradlew assembleProdPremobile30Release