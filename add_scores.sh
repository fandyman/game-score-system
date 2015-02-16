#!/bin/bash

for i in `seq 1 10000`; do
   USERID=$[ ( $RANDOM % 1000 )  + 1 ]
   #echo "USERID=$USERID"
   TOKEN=$(curl -s http://localhost:8081/$USERID/login)
   SCORE=$[ ( $RANDOM % 10000 )  + 1 ]
   #echo "SCORE=$SCORE"
   LEVEL=$[ ( $RANDOM % 10 )  + 1 ]
   #echo "LEVEL=$LEVEL"
   curl -s --data "$SCORE" http://localhost:8081/$LEVEL/score?sessionkey=$TOKEN
   if !((i % 1000)); then
      echo "Another 1000 requests processed"
   fi
done

for i in `seq 1 10`; do
   echo "Scores for level $i: "
   curl http://localhost:8081/$i/highscorelist?sessionkey=$TOKEN
   printf "\n"
done