#!/bin/sh
secs=$1
stage=$2
printf '%s took: %dh:%dm:%ds\n' $2 $((secs/3600)) $((secs%3600/60)) $((secs%60))
