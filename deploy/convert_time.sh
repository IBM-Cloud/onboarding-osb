#!/bin/bash
source deploy/colorcodes.sh

secs=$1
stage=$2

echo -e "${Gre}"
printf '%s took: %dh:%dm:%ds\n' $2 $((secs/3600)) $((secs%3600/60)) $((secs%60))
echo -e "${RCol}"
