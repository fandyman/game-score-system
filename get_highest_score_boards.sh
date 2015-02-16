#!/bin/bash

for i in `seq 1 10000`; do
   USERID=$[ ( $RANDOM % 1000 )  + 1 ]
   #echo "USERID=$USERID"
   TOKEN=$(curl -s http://localhost:8081/$USERID/login)
   LEVEL=$[ ( $RANDOM % 10 )  + 1 ]
   curl -s -o highscores.txt http://localhost:8081/$LEVEL/highscorelist?sessionkey=$TOKEN
   if !((i % 1000)); then
      echo "Another 1000 requests processed"
   fi
done